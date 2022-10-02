package com.sdci.tp2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class VerAlertas : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_alertas)

        val db = Firebase.firestore
        val auth = Firebase.auth

        val sessionId = intent.getStringExtra("SesionID").toString()
        val zonaCtrlId = intent.getStringExtra("ZonaCtrlID").toString()

        val btnCerrarSesion = findViewById<ImageView>(R.id.btnCerrarSesion)
        val btnMenuPrincipal = findViewById<ImageView>(R.id.btnMenuPrincipal)



        btnCerrarSesion.setOnClickListener{
            db.collection("session").document(sessionId).update("active",false).addOnSuccessListener{
                    Log.d("successLogOut","Se modifico el estado de la sesion $sessionId a false")
                    auth.signOut()
                    startActivity(Intent(applicationContext,IniciarSesion::class.java))
                    finish()
            }
        }
        btnMenuPrincipal.setOnClickListener{
            finish()
        }
    }
}