package com.sdci.tp2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnCerrarSesion = findViewById<Button>(R.id.botonCerrarSesion)
        val db = Firebase.firestore
        val iSessionId = findViewById<TextView>(R.id.tvSessionId)
        val iUserId = findViewById<TextView>(R.id.tvUserID)
        val iZonaId = findViewById<TextView>(R.id.tvZonaId)
        var sessionId = ""

        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        val currentuser: String = auth.currentUser?.uid.toString()
        val docRef = db.collection("session")

        docRef.whereEqualTo("userId",currentuser).whereEqualTo("active",true).get().addOnSuccessListener{ documents ->
            for (document in documents){
                Log.d("success 1","Se recupero la sesion. ${document.id} => ${document.data}")
                sessionId = document.id
                iSessionId.text = sessionId
                iUserId.text = document.getString("userId")
                iZonaId.text = document.getString("zonaId")
            }
        }
        btnCerrarSesion.setOnClickListener {

            db.collection("session").document(sessionId).update("active",false)
                .addOnSuccessListener {
                    Log.d("successLogOut","Se modifico el estado de la sesion a false")
                }
            auth.signOut()
            startActivity(Intent(applicationContext,IniciarSesion::class.java))
            finish()
        }
    }
}