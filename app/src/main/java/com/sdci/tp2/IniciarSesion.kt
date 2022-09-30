package com.sdci.tp2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

@Suppress("DEPRECATION")
class IniciarSesion : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iniciar_sesion)

        val btnViewRegistrar = findViewById<TextView>(R.id.tvViewRegistrarse)
        val btnIniciarSesion = findViewById<Button>(R.id.botonIniciarSesion)
        val iCorreo = findViewById<EditText>(R.id.tbCorreo)
        val iContrasena = findViewById<EditText>(R.id.tbContrasena)
        var auth: FirebaseAuth = FirebaseAuth.getInstance()

        if (auth.currentUser != null){
            startActivity(Intent(applicationContext,MainActivity::class.java))
            finish()
        }

        btnViewRegistrar.setOnClickListener {
            startActivity(Intent(applicationContext, Registrar::class.java))
            finish()
        }

        val layout = layoutInflater.inflate(R.layout.custom_toast,null)
        val txtToast = layout.findViewById<TextView>(R.id.tv_text)


        btnIniciarSesion.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v:View){
                val correo = iCorreo.text.toString().trim()
                val contrasena = iContrasena.text.toString().trim()

                if(TextUtils.isEmpty(correo)){
                    iCorreo.error = ("Se requiere ingresar un correo.")
                    return
                }
                if(TextUtils.isEmpty(contrasena)){
                    iContrasena.error = "Se requiere ingreasar una contraseña."
                    return
                }
                if (contrasena.length < 7){
                    iContrasena.error = "Contraseña debe contener minimo 8 caracteres."
                    return
                }

                auth.signInWithEmailAndPassword(correo,contrasena).addOnCompleteListener(this@IniciarSesion){ task ->
                    if (task.isSuccessful){
                        //Toast.makeText(this@IniciarSesion,"Sesion iniciada con exito.", Toast.LENGTH_SHORT).show()
                        Toast(this@IniciarSesion).apply {
                            duration = Toast.LENGTH_SHORT
                            txtToast.text = "Exito. Se inicio sesion correctamente."
                            setGravity(Gravity.CENTER,0,0)
                            view = layout
                        }.show()
                        startActivity(Intent(applicationContext,MainActivity::class.java))
                        finish()
                    } else{
                        Toast(this@IniciarSesion).apply{
                            duration = Toast.LENGTH_SHORT
                            txtToast.text = "Error. Los datos ingresados son incorrectos o no existen."
                            setGravity(Gravity.CENTER,0,0)
                            view = layout
                        }.show()
                    }
                }


            }
        })

    }
}