package com.sdci.tp2

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Definicion de objetos de la vista
        val btnCerrarSesion = findViewById<ImageView>(R.id.btnCerrarSesion)
        val tvDistrito = findViewById<TextView>(R.id.tvShowDistrito)
        val tvZonaControl = findViewById<TextView>(R.id.tvShowZC)
        val btnAlertas = findViewById<Button>(R.id.btnAlertas)
        val btnReportes = findViewById<Button>(R.id.btnReportes)
        val btnCambiarZona = findViewById<Button>(R.id.btnCambiarZona)
        val btnReportarFallos = findViewById<Button>(R.id.btnReportarFallos)

        val intentAlertas = Intent(this, VerAlertas::class.java)
        val intentReportes = Intent(this, GenerarReportes::class.java)
        val intentCambiarZona = Intent(this, CambiarZonaControl::class.java)
        val intentFallos = Intent(this, ReportarFallos::class.java)

        val msging = Firebase.messaging
        val db = Firebase.firestore
        var idDistrito = ""
        var idZonaControl = ""
        var sessionId = ""


        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        val currentuser: String = auth.currentUser?.uid.toString()
        val docRef = db.collection("session")


        docRef.whereEqualTo("userId", currentuser).whereEqualTo("active", true).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d("success 1", "Se recupero la sesion. ${document.id} => ${document.data}")
                    sessionId = document.id
                    idZonaControl = document.getString("zonaId").toString()
                    idDistrito = document.getString("distId").toString()

                    intentAlertas.putExtra("SesionID", sessionId)
                    intentAlertas.putExtra("ZonaCtrlID", idZonaControl)
                    intentReportes.putExtra("SesionID", sessionId)
                    intentCambiarZona.putExtra("SesionID", sessionId)
                    intentCambiarZona.putExtra("ZonaCtrlID", idZonaControl)
                    intentFallos.putExtra("SesionID", sessionId)
                    val intentPendingAlertas = PendingIntent.getActivity(this,0, intentAlertas,PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

                    msging.subscribeToTopic(idZonaControl).addOnSuccessListener {
                        Log.d(
                            "successNotif",
                            "Se subscribio correctamente al topico $idZonaControl"
                        )
                    }
                }

                db.collection("districts").document(idDistrito).get()
                    .addOnSuccessListener {
                        Log.d("cargadist", "Se cargo el distrito: ${it.id} => ${it.data}")
                        tvDistrito.text = it.getString("nombreDistrito").toString()
                    }
                db.collection("districts").document(idDistrito).collection("control_zones")
                    .document(idZonaControl).get()
                    .addOnSuccessListener {
                        Log.d("cargazoncon", "Se cargo la zona: ${it.id} => ${it.data}")
                        tvZonaControl.text = it.getString("nombreZona").toString()
                    }
            }

        btnCerrarSesion.setOnClickListener {
            db.collection("session").document(sessionId).update("active", false)
                .addOnSuccessListener {
                    msging.unsubscribeFromTopic(sessionId)
                    auth.signOut()
                    startActivity(Intent(applicationContext, IniciarSesion::class.java))
                    finish()
                }
        }

        btnAlertas.setOnClickListener {
            startActivity(intentAlertas)
        }

        btnReportes.setOnClickListener {
            startActivity(intentReportes)
        }

        btnCambiarZona.setOnClickListener {
            startActivity(intentCambiarZona)
            finish()
        }

        btnReportarFallos.setOnClickListener {
            startActivity(intentFallos)
        }
    }
}