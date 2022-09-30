package com.sdci.tp2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val db = Firebase.firestore
        val iCorreo = findViewById<TextView>(R.id.tvCorreo)
        val iNombre = findViewById<TextView>(R.id.tvNombre)
        val iApellido = findViewById<TextView>(R.id.tvApellido)
        val auth: FirebaseAuth = FirebaseAuth.getInstance()

        val docRef = db.collection("users").document(auth.uid.toString())
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("existe", "DocumentSnapshot data: ${document.data}")

                    iCorreo.text = document.getString("correoUsuario")
                    iNombre.text = document.getString("nombreUsuario")
                    iApellido.text = document.getString("apellidoUsuario")


                } else {
                    Log.d("no existe", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("error de bd", "get failed with ", exception)
            }

    }

    fun logout(view: View) {
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(applicationContext,IniciarSesion::class.java))
        finish()
    }

}