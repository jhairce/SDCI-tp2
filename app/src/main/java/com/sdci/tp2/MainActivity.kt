package com.sdci.tp2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnCerrarSesion = findViewById<ImageView>(R.id.btnCerrarSesion)
        val db = Firebase.firestore
        val tvDistrito = findViewById<TextView>(R.id.tvShowDistrito)
        val tvZonaControl = findViewById<TextView>(R.id.tvShowZC)

        var idDistrito = ""
        var idZonaControl = ""
        var sessionId = ""

        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        val currentuser: String = auth.currentUser?.uid.toString()

        var docRef = db.collection("session")



        docRef.whereEqualTo("userId",currentuser).whereEqualTo("active",true).get().addOnSuccessListener { documents ->
            for (document in documents) {
                Log.d("success 1", "Se recupero la sesion. ${document.id} => ${document.data}")
                sessionId = document.id
                idZonaControl = document.getString("zonaId").toString()
                idDistrito = document.getString("distId").toString()
            }

            db.collection("districts").document(idDistrito).get()
                .addOnSuccessListener {
                    Log.d("cargadist","Se cargo el distrito: ${it.id} => ${it.data}")
                    tvDistrito.text = it.getString("nombreDistrito").toString()
                }
            db.collection("districts").document(idDistrito).collection("control_zones").document(idZonaControl).get()
                .addOnSuccessListener {
                    Log.d("cargazoncon","Se cargo la zona: ${it.id} => ${it.data}")
                    tvZonaControl.text = it.getString("nombreZona").toString()
                }
        }


        btnCerrarSesion.setOnClickListener {
            db.collection("session").document(sessionId).update("active",false)
                .addOnSuccessListener {
                    Log.d("successLogOut","Se modifico el estado de la sesion $sessionId a false")
                }
            auth.signOut()
            startActivity(Intent(applicationContext,IniciarSesion::class.java))
            finish()
        }
    }
}