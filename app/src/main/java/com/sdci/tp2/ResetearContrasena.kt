package com.sdci.tp2

import android.os.Bundle
import android.util.Patterns
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ResetearContrasena : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resetear_contrasena)

        val etCorreo = findViewById<EditText>(R.id.tbCorreo)
        val btnResetear = findViewById<Button>(R.id.btnResetearContrasena)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        val auth = Firebase.auth

        val layout = layoutInflater.inflate(R.layout.custom_toast, null)
        val txtToast = layout.findViewById<TextView>(R.id.tv_text)
        val imgToast = layout.findViewById<ImageView>(R.id.iv_ToastIcon)

        fun resetearContrasena() {
            val email = etCorreo.text.toString().trim()
            if(email.isEmpty()){
                etCorreo.error = "Debes ingresar un correo."
                etCorreo.requestFocus()
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                etCorreo.error = "Por favor, ingrease un correo valido."
                etCorreo.requestFocus()
                return;
            }
            progressBar.visibility = View.VISIBLE
            auth.sendPasswordResetEmail(email).addOnCompleteListener{
                if (it.isSuccessful){
                    Toast(this).apply {
                        duration = Toast.LENGTH_SHORT
                        txtToast.text = "Exito. Verifique su correo para resetear su contraseña."
                        imgToast.setImageResource(R.drawable.toast_success)
                        setGravity(Gravity.FILL_HORIZONTAL,0,0)
                        view = layout
                    }.show()
                    finish()
                }
            }
        }

        btnResetear.setOnClickListener{
            resetearContrasena()
        }



    }


}