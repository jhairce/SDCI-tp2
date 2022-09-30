package com.sdci.tp2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

@Suppress("DEPRECATION")
class Registrar : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar)

        val btnRegistrar = findViewById<Button>(R.id.botonRegistrar)
        val btnViewIniciarSesion = findViewById<TextView>(R.id.tvViewIniciarSesion)
        val iNombre = findViewById<EditText>(R.id.tbNombre)
        val iApellido = findViewById<EditText>(R.id.tbApellido)
        val iCorreo = findViewById<EditText>(R.id.tbCorreo)
        val iContrasena = findViewById<EditText>(R.id.tbContrasena)
        var userID: String
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        val fStore: FirebaseFirestore = FirebaseFirestore.getInstance()

        val layout = layoutInflater.inflate(R.layout.custom_toast,null)
        val txtToast = layout.findViewById<TextView>(R.id.tv_text)

        btnViewIniciarSesion.setOnClickListener {
            startActivity(Intent(applicationContext, IniciarSesion::class.java))
            finish()
        }

        btnRegistrar.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                val correo = iCorreo.text.toString().trim()
                val contrasena = iContrasena.text.toString().trim()
                val nombre = iNombre.text.toString().trim()
                val apellido = iApellido.text.toString().trim()

                if(TextUtils.isEmpty(correo)){
                    iCorreo.error = "Se requiere ingresar un correo."
                    return
                }
                if(TextUtils.isEmpty(contrasena)){
                    iContrasena.error = "Se requiere ingresar una contraseña."
                    return
                }
                if (contrasena.length < 7){
                    iContrasena.error = "Contraseña debe contener minimo 8 caracteres."
                    return
                }


                //registrar usuario
                auth.createUserWithEmailAndPassword(correo,contrasena).addOnCompleteListener(this@Registrar) {task ->
                    if (task.isSuccessful){
                        Toast(this@Registrar).apply {
                            duration = Toast.LENGTH_SHORT
                            txtToast.text = "Exito! Se ha creado su cuenta correctamente."
                            setGravity(Gravity.CENTER,0,0)
                            view = layout
                        }.show()

                        userID = auth.currentUser?.uid.toString()

                        var docRef: DocumentReference = fStore.collection("users").document(userID)
                        val user = hashMapOf(
                            "nombreUsuario" to iNombre.text.toString(),
                            "apellidoUsuario" to iApellido.text.toString(),
                            "correoUsuario" to iCorreo.text.toString(),
                            "contrasenaUsuario" to iContrasena.text.toString()
                        )
                        docRef.set(user).addOnSuccessListener {
                            Log.d("success","onSuccess: usuario registrado en Firestore para: $userID"                            )
                        }.addOnFailureListener{ e->
                            Log.d("failure","No se registro el usuario. $e.toString()")
                        }



                        startActivity(Intent(applicationContext,MainActivity::class.java))
                        finish()
                    } else {
                        Toast(this@Registrar).apply {
                            duration = Toast.LENGTH_SHORT
                            txtToast.text = "Error! No se ha creado su cuenta. Intentalo de nuevo en unos minutos."
                            setGravity(Gravity.CENTER,0,0)
                            view = layout
                        }.show()
                    }
                }
            }
        })


    }
}

