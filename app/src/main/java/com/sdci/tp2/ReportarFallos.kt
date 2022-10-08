package com.sdci.tp2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging


class ReportarFallos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reportar_fallos)

        val db = Firebase.firestore
        val auth = Firebase.auth
        val msging = Firebase.messaging

        val sessionId = intent.getStringExtra("SesionID").toString()
        val btnCerrarSesion = findViewById<ImageView>(R.id.btnCerrarSesion)
        val btnMenuPrincipal = findViewById<ImageView>(R.id.btnMenuPrincipal)
        val btnReportarFallos = findViewById<Button>(R.id.btnReportarFallos)


        val spnAsunto = findViewById<Spinner>(R.id.spnAsunto)
        val tbDescripcion = findViewById<EditText>(R.id.etDescripcion)

        val arraySpinner = arrayOf("Deteccion de Falsos positivos", "No hay detecciones", "Duplicacion de detecciones", "Otros")
        val adapter = ArrayAdapter(this,android.R.layout.simple_spinner_item, arraySpinner        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnAsunto.adapter = adapter

        // Variables del toast
        val layout = layoutInflater.inflate(R.layout.custom_toast,null)
        val txtToast = layout.findViewById<TextView>(R.id.tv_text)
        val imgToast = layout.findViewById<ImageView>(R.id.iv_ToastIcon)

        val refTickets = db.collection("tickets")

        tbDescripcion.addTextChangedListener{
            btnReportarFallos.isEnabled = tbDescripcion.text.isNotEmpty()
        }

        btnReportarFallos.setOnClickListener{
            val ticket = hashMapOf(
                "asuntoProblema" to spnAsunto.selectedItem.toString(),
                "descripcionProblema" to tbDescripcion.text.toString(),
                "idSesion" to sessionId
            )

            refTickets.add(ticket).addOnSuccessListener {
                Log.d("successticket","Se agrego el reporte de falla")
                Toast(this).apply {
                    duration = Toast.LENGTH_SHORT
                    txtToast.text = "Exito! Se ha reportado la falla. El equipo tecnico lo evaluara."
                    imgToast.setImageResource(R.drawable.toast_success)
                    setGravity(Gravity.FILL_HORIZONTAL,0,0)
                    view = layout
                }.show()
                finish()
            }
        }

        btnCerrarSesion.setOnClickListener{
            db.collection("session").document(sessionId).update("active",false).addOnSuccessListener{
                msging.unsubscribeFromTopic(sessionId)
                auth.signOut()
                startActivity(Intent(applicationContext, IniciarSesion::class.java))
                finish()
            }
        }

        btnMenuPrincipal.setOnClickListener{
            finish()
        }
    }
}