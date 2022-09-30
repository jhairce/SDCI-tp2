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
import androidx.annotation.NonNull
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class Registrar : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar)

        lateinit var auth: FirebaseAuth

        val btnRegistrar = findViewById<Button>(R.id.botonRegistrar)
        val btnViewIniciarSesion = findViewById<TextView>(R.id.tvViewIniciarSesion)
        val iNombre = findViewById<EditText>(R.id.tbNombre)
        val iApellido = findViewById<EditText>(R.id.tbApellido)
        val iCorreo = findViewById<EditText>(R.id.tbCorreo)
        val iContrasena = findViewById<EditText>(R.id.tbContrasena)
        auth = FirebaseAuth.getInstance()

        val layout = layoutInflater.inflate(R.layout.custom_toast,null)
        val txtToast = layout.findViewById<TextView>(R.id.tv_text)

        if (auth.currentUser != null){
            startActivity(Intent(applicationContext,IniciarSesion::class.java))
            finish()
        }

        btnViewIniciarSesion.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v:View?) {
                startActivity(Intent(applicationContext,IniciarSesion::class.java))
            }
        })

        btnRegistrar.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                val correo = iCorreo.text.toString().trim()
                val contrasena = iContrasena.text.toString().trim()

                if(TextUtils.isEmpty(correo)){
                    iCorreo.setError("Se requiere ingresar un correo.")
                    return
                }
                if(TextUtils.isEmpty(contrasena)){
                    iContrasena.setError("Se requiere ingresar una contraseña.")
                    return
                }
                if (contrasena.length < 7){
                    iContrasena.setError("Contraseña debe contener minimo 8 caracteres.")
                    return
                }

                //registrar usuario

                auth.createUserWithEmailAndPassword(correo,contrasena).addOnCompleteListener(this@Registrar) {task ->
                    if (task.isSuccessful){
                        Toast(this@Registrar).apply {
                            duration = Toast.LENGTH_SHORT
                            txtToast.setText("Exito! Se ha creado su cuenta correctamente.")
                            setGravity(Gravity.CENTER,0,0)
                            view = layout
                        }.show()
                        startActivity(Intent(applicationContext,IniciarSesion::class.java))
                    } else {
                        Toast(this@Registrar).apply {
                            duration = Toast.LENGTH_SHORT
                            txtToast.setText("Error! No se ha podido crear su cuenta. Intentalo de nuevo en unos minutos.")
                            setGravity(Gravity.CENTER,0,0)
                            view = layout
                        }.show()
                    }
                }
            }
        })


    }
}

