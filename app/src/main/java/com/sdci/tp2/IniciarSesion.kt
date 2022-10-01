package com.sdci.tp2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

@Suppress("DEPRECATION")
class IniciarSesion : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iniciar_sesion)

        // Definicion de variables.
        val btnViewRegistrar = findViewById<TextView>(R.id.tvViewRegistrarse)
        val btnIniciarSesion = findViewById<Button>(R.id.botonIniciarSesion)
        val iCorreo = findViewById<EditText>(R.id.tbCorreo)
        val iContrasena = findViewById<EditText>(R.id.tbContrasena)
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        val fStore: FirebaseFirestore = FirebaseFirestore.getInstance()
        val sessionRef: CollectionReference = fStore.collection("session")
        val layout = layoutInflater.inflate(R.layout.custom_toast, null)
        val txtToast = layout.findViewById<TextView>(R.id.tv_text)
        val imgToast = layout.findViewById<ImageView>(R.id.iv_PtoControl)

        // Si la sesion esta iniciada, mandar al usuario a la vista Main Activity.
        if (auth.currentUser != null){
            val currentuser: String = auth.currentUser?.uid.toString()
            sessionRef.whereEqualTo("userId",currentuser).whereEqualTo("active",true).get().addOnCompleteListener{
                if (it.isSuccessful){
                    startActivity(Intent(applicationContext,MainActivity::class.java))
                    finish()
                } else{
                    startActivity(Intent(applicationContext,ConfigZonaControl::class.java))
                    finish()
                }
            }
        }

        // Acciones cuando el usuario hace clic en boton "Registrate"
        btnViewRegistrar.setOnClickListener {
            startActivity(Intent(applicationContext, Registrar::class.java))
            finish()
        }

        // Acciones cuando el usuario hace clic en boton "Iniciar sesion"
        btnIniciarSesion.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v:View){

                // Capturar valores de correo y contraseña ingresados por usuario.
                val correo = iCorreo.text.toString().trim()
                val contrasena = iContrasena.text.toString().trim()

                // Validacion: Campo correo vacio.
                if(TextUtils.isEmpty(correo)){
                    iCorreo.error = ("Se requiere ingresar un correo.")
                    return
                }

                // Validacion: Campo contraseña vacia.
                if(TextUtils.isEmpty(contrasena)){
                    iContrasena.error = "Se requiere ingreasar una contraseña."
                    return
                }

                // Validacion: contraseña corta.
                if (contrasena.length < 7){
                    iContrasena.error = "Contraseña debe contener minimo 8 caracteres."
                    return
                }

                // Iniciar sesion en el sistema. FB Auth administra.
                auth.signInWithEmailAndPassword(correo,contrasena).addOnCompleteListener(this@IniciarSesion){ task ->
                    if (task.isSuccessful){
                        // Toast de exito de inicio de sesion.
                        Toast(this@IniciarSesion).apply {
                            duration = Toast.LENGTH_SHORT
                            txtToast.text = "Exito. Se inicio sesion correctamente."
                            imgToast.setImageResource(R.drawable.toast_success)
                            setGravity(Gravity.FILL_HORIZONTAL,0,0)
                            view = layout
                        }.show()
                        // Enviar a usuario que inicia sesion a la actividad Main Activity.
                        startActivity(Intent(applicationContext,ConfigZonaControl::class.java))
                        finish()
                    } else{
                        // Toast de error de inicio de sesion.
                        Toast(this@IniciarSesion).apply{
                            duration = Toast.LENGTH_SHORT
                            txtToast.text = "Error. Los datos ingresados son incorrectos o no existen."
                            imgToast.setImageResource(R.drawable.toast_error)
                            setGravity(Gravity.FILL_HORIZONTAL,0,0)
                            view = layout
                        }.show()
                    }
                }
            }
        })
    }
}