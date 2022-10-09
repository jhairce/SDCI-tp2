package com.sdci.tp2

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.squareup.picasso.Picasso
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class VerAlertas : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_alertas)

        val db = Firebase.firestore
        val auth = Firebase.auth
        val msging = Firebase.messaging

        var sessionId = ""
        var zonaCtrlId = ""

        val intentAlertas = Intent(this, VerAlertas::class.java)

        val layout = layoutInflater.inflate(R.layout.custom_toast, null)
        val txtToast = layout.findViewById<TextView>(R.id.tv_text)
        val imgToast = layout.findViewById<ImageView>(R.id.iv_ToastIcon)

        val idsAlertas: MutableList<String> = ArrayList()
        val horasAlertas: MutableList<Date> = ArrayList()
        val imgsAlertas: MutableList<String> = ArrayList()
        val intervAlertas: MutableList<Boolean?> = ArrayList()

        var idAlertaActual = ""
        var imgAlertaActual: String
        var horaAlertaActual: String
        var intervAlertaActual: Boolean?

        val formatter: DateFormat = SimpleDateFormat("HH:mm:ss")

        var counter = 0
        var max = 0

        val btnCerrarSesion = findViewById<ImageView>(R.id.btnCerrarSesion)
        val btnMenuPrincipal = findViewById<ImageView>(R.id.btnMenuPrincipal)
        val btnIntervencion = findViewById<Button>(R.id.btnIntervencion)
        val tvMax = findViewById<TextView>(R.id.tvMaximo)
        val tvConteo = findViewById<TextView>(R.id.tvConteo)
        val tvHora = findViewById<TextView>(R.id.tvHora)
        val tvInterv = findViewById<TextView>(R.id.tvIntervenido)
        val tvNoAlertas = findViewById<TextView>(R.id.tvNoAlertas)
        val ivAlerta = findViewById<ImageView>(R.id.iv_Alerta)
        val btnPrevious = findViewById<Button>(R.id.btnPrevious)
        val btnNext = findViewById<Button>(R.id.btnNext)
        val pbAlertas = findViewById<ProgressBar>(R.id.pbAlertas)

        fun cargarAlerta(count: Int){
            idAlertaActual = idsAlertas[count-1]
            imgAlertaActual = imgsAlertas[count-1]
            horaAlertaActual = formatter.format(horasAlertas[count-1])
            intervAlertaActual = intervAlertas[count-1]

            Picasso.get().load(imgAlertaActual).into(ivAlerta)
            tvInterv.text = if (intervAlertaActual!!) "Si" else "No"
            tvConteo.text = count.toString()
            tvHora.text = horaAlertaActual
            btnIntervencion.isEnabled = !intervAlertaActual!!
            btnPrevious.isEnabled = count != 1
            btnNext.isEnabled = count != max
        }


        db.collection("session").whereEqualTo("userId", auth.currentUser?.uid).whereEqualTo("active", true).get()
            .addOnSuccessListener { documents ->
                Log.d("LISTENER","user is: ${auth.currentUser?.uid} ")
                for (document in documents) {
                    zonaCtrlId = document.getString("zonaId").toString()
                    sessionId = document.id
                }
                val ref = db.collection("Alertas").whereEqualTo("idZonaControl",zonaCtrlId)
                ref.orderBy("horaAlerta", Query.Direction.ASCENDING).get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        if (it.result.isEmpty){
                            tvNoAlertas.visibility = View.VISIBLE
                            counter = 0
                            tvMax.text = counter.toString()
                            pbAlertas.visibility = View.GONE
                            btnPrevious.isEnabled = false
                            btnNext.isEnabled = false
                            btnIntervencion.isEnabled = false
                        } else {
                            for (document in it.result) {
                                idsAlertas.add(document.id)
                                imgsAlertas.add(document.getString("imgDownloadURL").toString())
                                horasAlertas.add(document.getTimestamp("horaAlerta")!!.toDate())
                                intervAlertas.add(document.getBoolean("intervencion"))
                            }
                            counter = idsAlertas.count()
                            max = counter
                            tvMax.text = counter.toString()
                            cargarAlerta(counter)
                            pbAlertas.visibility = View.GONE
                        }
                    }
                }
            }



        btnPrevious.setOnClickListener {
            pbAlertas.visibility = View.VISIBLE
            counter--
            cargarAlerta(counter)
            pbAlertas.visibility = View.GONE
        }

        btnNext.setOnClickListener {
            pbAlertas.visibility = View.VISIBLE
            counter++
            cargarAlerta(counter)
            pbAlertas.visibility = View.GONE
        }

        btnCerrarSesion.setOnClickListener{
            db.collection("session").document(sessionId).update("active",false).addOnSuccessListener{
                msging.unsubscribeFromTopic(sessionId)
                auth.signOut()
                startActivity(Intent(applicationContext, IniciarSesion::class.java))
                finish()
            }
        }

        btnMenuPrincipal.setOnClickListener{
            finish()
        }

        btnIntervencion.setOnClickListener{
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.intervencion_dialog,null)
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
                .setTitle("Confirmacion")
            val mAlertDialog = mBuilder.show()
            mDialogView.findViewById<TextView>(R.id.tvTextoDialog).text = "Confirma que se ha realizado la intervención de la alerta mostrada?"
            mDialogView.findViewById<Button>(R.id.btnCancelar).setOnClickListener{
                mAlertDialog.dismiss()
            }
            mDialogView.findViewById<Button>(R.id.btnConfirmar).setOnClickListener{
                db.collection("Alertas").document(idAlertaActual).update("intervencion",true).addOnSuccessListener{
                    mAlertDialog.dismiss()
                    Toast(this).apply {
                        duration = Toast.LENGTH_LONG
                        txtToast.text = "Exito! Se registró la intervención."
                        imgToast.setImageResource(R.drawable.toast_success)
                        setGravity(Gravity.FILL_HORIZONTAL, 0, 0)
                        view = layout
                    }.show()

                    intentAlertas.putExtra("SesionID", sessionId)
                    intentAlertas.putExtra("ZonaCtrlID", zonaCtrlId)
                    startActivity(intentAlertas)
                    finish()
                }
            }

        }
    }
}
