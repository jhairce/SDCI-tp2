package com.sdci.tp2

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Suppress("DEPRECATION")
class IniciarSesion : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iniciar_sesion)


        val auth = Firebase.auth
        val fStore = Firebase.firestore
        val sessionRef = fStore.collection("session")

        // Si la sesion esta iniciada, mandar al usuario a la vista Main Activity.
        if (auth.currentUser != null){
            val currentuser = auth.currentUser?.uid.toString()
            sessionRef.whereEqualTo("userId",currentuser).whereEqualTo("active",true).get().addOnSuccessListener {
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            }
        //startActivity(Intent(applicationContext,ConfigZonaControl::class.java))
        //finish()
        }


        // Definicion de variables.
        val btnViewRegistrar = findViewById<TextView>(R.id.tvViewRegistrarse)
        val btnIniciarSesion = findViewById<Button>(R.id.botonIniciarSesion)
        val tvResetearContrasena = findViewById<TextView>(R.id.tvRecuperarContrasena)
        val iCorreo = findViewById<EditText>(R.id.tbCorreo)
        val iContrasena = findViewById<EditText>(R.id.tbContrasena)


        val layout = layoutInflater.inflate(R.layout.custom_toast, null)
        val txtToast = layout.findViewById<TextView>(R.id.tv_text)
        val imgToast = layout.findViewById<ImageView>(R.id.iv_ToastIcon)



        // Acciones cuando el usuario hace clic en boton "Registrate"
        btnViewRegistrar.setOnClickListener {
            startActivity(Intent(applicationContext, Registrar::class.java))
            finish()
        }

        tvResetearContrasena.setOnClickListener{
            startActivity(Intent(applicationContext,ResetearContrasena::class.java))
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
                    iCorreo.requestFocus()
                    return
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
                    iCorreo.error = "Por favor, ingrease un correo valido."
                    iCorreo.requestFocus()
                    return
                }
                // Validacion: Campo contraseña vacia.
                if(TextUtils.isEmpty(contrasena)){
                    iContrasena.error = "Se requiere ingreasar una contraseña."
                    iContrasena.requestFocus()
                    return
                }

                // Validacion: contraseña corta.
                if (contrasena.length < 7){
                    iContrasena.error = "Contraseña debe contener minimo 8 caracteres."
                    iContrasena.requestFocus()
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