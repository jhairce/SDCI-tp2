package com.sdci.tp2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

@Suppress("DEPRECATION")
class Registrar : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar)

        // Definicion de variables
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
        val imgToast = layout.findViewById<ImageView>(R.id.iv_ToastIcon)

        // Acciones cuando usuario hace clic en "Inicia sesion si ya tienes cuenta."
        btnViewIniciarSesion.setOnClickListener {
            startActivity(Intent(applicationContext, IniciarSesion::class.java))
            finish()
        }

        // Acciones cuando usuario hace clic en Registrar
        btnRegistrar.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                val correo = iCorreo.text.toString().trim()
                val contrasena = iContrasena.text.toString().trim()
                val nombre = iNombre.text.toString().trim()
                val apellido = iApellido.text.toString().trim()

                // Validacion: Campo correo vacio.
                if(TextUtils.isEmpty(correo)){
                    iCorreo.error = "Se requiere ingresar un correo."
                    return
                }
                // Validacion: Campo contraseña vacia.
                if(TextUtils.isEmpty(contrasena)){
                    iContrasena.error = "Se requiere ingresar una contraseña."
                    return
                }
                // Validacion: contraseña corta.
                if (contrasena.length < 7){
                    iContrasena.error = "Contraseña debe contener minimo 8 caracteres."
                    return
                }


                // Registrar usuario en FB Auth y luego registrarlo en FB Firestore
                auth.createUserWithEmailAndPassword(correo,contrasena).addOnCompleteListener(this@Registrar) {task ->
                    if (task.isSuccessful){
                        // Toast de exito de creacion de cuenta
                        Toast(this@Registrar).apply {
                            duration = Toast.LENGTH_SHORT
                            txtToast.text = "Exito! Se ha creado su cuenta correctamente."
                            imgToast.setImageResource(R.drawable.toast_success)
                            setGravity(Gravity.FILL_HORIZONTAL,0,0)
                            view = layout
                        }.show()

                        // Capturar el userID que se acaba de crear.
                        userID = auth.currentUser?.uid.toString()

                        // Crear un documento nuevo con el userID como referencia dentro de la coleccion "users".
                        val docRef: DocumentReference = fStore.collection("users").document(userID)

                        // Llenar los campos del documento con los datos ingresados durante el registro.
                        val user = hashMapOf(
                            "nombreUsuario" to iNombre.text.toString(),
                            "apellidoUsuario" to iApellido.text.toString(),
                            "correoUsuario" to iCorreo.text.toString(),
                            "contrasenaUsuario" to iContrasena.text.toString()
                        )

                        // Ingresar (put) el documento en la coleccion "users"
                        docRef.set(user).addOnSuccessListener {
                            // Evento: Exito. Ingresar al Log.
                            Log.d("success","onSuccess: usuario registrado en Firestore para: $userID"                            )
                        }.addOnFailureListener{ e->
                            // Evento: Error. Ingresar al Log.
                            Log.d("failure","No se registro el usuario. $e.toString()")
                        }

                        // Iniciar la nueva vista (ZonaControl) para el usuario una vez se registra
                        startActivity(Intent(applicationContext,ConfigZonaControl::class.java))

                        // Destruir vista actual
                        finish()
                    } else {
                        // Toast de error de creacion de cuenta
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

