package com.sdci.tp2

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
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

        // Variables del toast
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

                // Validaciones: Campo correo vacio.
                if (TextUtils.isEmpty(nombre)){
                    iNombre.error = "Debe ingresar su nombre."
                    iNombre.requestFocus()
                    return
                }
                if (TextUtils.isEmpty(apellido)){
                    iApellido.error = "Debe ingresar su apellido."
                    iApellido.requestFocus()
                    return
                }
                if(TextUtils.isEmpty(correo)){
                    iCorreo.error = "Se requiere ingresar un correo."
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
                    iContrasena.error = "Se requiere ingresar una contraseña."
                    iContrasena.requestFocus()
                    return
                }
                // Validacion: contraseña corta.
                if (contrasena.length < 7){
                    iContrasena.error = "Contraseña debe contener minimo 8 caracteres."
                    iContrasena.requestFocus()
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
                            txtToast.text = "Error! Ya existe una cuenta con ese correo. Ve a la opcion de Olvidaste tu Contraseña. "
                            setGravity(Gravity.FILL_HORIZONTAL,0,0)
                            view = layout
                        }.show()
                    }
                }
            }
        })
    }
}

