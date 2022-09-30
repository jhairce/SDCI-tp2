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
import android.widget.LinearLayout
import com.google.firebase.auth.FirebaseAuth

class IniciarSesion : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iniciar_sesion)

        lateinit var auth: FirebaseAuth
        val btnViewRegistrar = findViewById<TextView>(R.id.tvViewRegistrarse)
        val btnIniciarSesion = findViewById<Button>(R.id.botonIniciarSesion)
        val iCorreo = findViewById<EditText>(R.id.tbCorreo)
        val iContrasena = findViewById<EditText>(R.id.tbContrasena)
        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null){
            startActivity(Intent(applicationContext,MainActivity::class.java))
            finish()
        }

        btnViewRegistrar.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v:View){
                startActivity(Intent(applicationContext,Registrar::class.java))
            }
        } )

        val layout = layoutInflater.inflate(R.layout.custom_toast,null)
        val txtToast = layout.findViewById<TextView>(R.id.tv_text)


        btnIniciarSesion.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v:View){
                val correo = iCorreo.text.toString().trim()
                val contrasena = iContrasena.text.toString().trim()

                if(TextUtils.isEmpty(correo)){
                    iCorreo.setError("Se requiere ingresar un correo.")
                    return
                }
                if(TextUtils.isEmpty(contrasena)){
                    iContrasena.setError("Se requiere ingreasar una contraseña.")
                    return
                }
                if (contrasena.length < 7){
                    iContrasena.setError("Contraseña debe contener minimo 8 caracteres.")
                    return
                }

                auth.signInWithEmailAndPassword(correo,contrasena).addOnCompleteListener(this@IniciarSesion){ task ->
                    if (task.isSuccessful){
                        //Toast.makeText(this@IniciarSesion,"Sesion iniciada con exito.", Toast.LENGTH_SHORT).show()
                        Toast(this@IniciarSesion).apply {
                            duration = Toast.LENGTH_SHORT
                            txtToast.setText("Exito. Se inicio sesion correctamente.")
                            setGravity(Gravity.CENTER,0,0)
                            view = layout
                        }.show()
                        startActivity(Intent(applicationContext,MainActivity::class.java))
                    } else{
                        Toast(this@IniciarSesion).apply{
                            duration = Toast.LENGTH_SHORT
                            txtToast.setText("Error. Los datos ingresados son incorrectos o no existen.")
                            setGravity(Gravity.CENTER,0,0)
                            view = layout
                        }.show()
                        //Toast.makeText(this@IniciarSesion,"Error. Los datos ingresados son incorrectos o no existen.",Toast.LENGTH_SHORT).show()
                    }
                }


            }
        })

    }
}