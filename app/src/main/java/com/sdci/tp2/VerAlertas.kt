package com.sdci.tp2

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

class VerAlertas : AppCompatActivity() {
    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_alertas)

        val db = Firebase.firestore
        val auth = Firebase.auth

        val sessionId = intent.getStringExtra("SesionID").toString()
        val zonaCtrlId = intent.getStringExtra("ZonaCtrlID").toString()

        val idsAlertas: MutableList<String> = ArrayList()
        val horasAlertas: MutableList<Date> = ArrayList()
        val imgsAlertas: MutableList<String> = ArrayList()
        val intervAlertas: MutableList<Boolean?> = ArrayList()

        var idAlertaActual: String
        var imgAlertaActual: String
        var horaAlertaActual: String
        var intervAlertaActual: Boolean?

        val formatter: DateFormat = SimpleDateFormat("HH:mm:ss")
        var localfile: File = File.createTempFile("tempImage","jpg")

        var counter = 0
        var max = 0

        val btnCerrarSesion = findViewById<ImageView>(R.id.btnCerrarSesion)
        val btnMenuPrincipal = findViewById<ImageView>(R.id.btnMenuPrincipal)
        val tvMax = findViewById<TextView>(R.id.tvMaximo)
        val tvConteo = findViewById<TextView>(R.id.tvConteo)
        val tvHora = findViewById<TextView>(R.id.tvHora)
        val tvInterv = findViewById<TextView>(R.id.tvIntervenido)
        val ivAlerta = findViewById<ImageView>(R.id.iv_Alerta)
        val btnPrevious = findViewById<Button>(R.id.btnPrevious)
        val btnNext = findViewById<Button>(R.id.btnNext)

        fun cargarAlerta(count: Int){
            idAlertaActual = idsAlertas[count-1]
            imgAlertaActual = imgsAlertas[count-1]
            horaAlertaActual = formatter.format(horasAlertas[count-1])
            intervAlertaActual = intervAlertas[count-1]


            btnPrevious.isEnabled = count != 1
            btnNext.isEnabled = count != max
            tvConteo.text = count.toString()
            tvHora.text = horaAlertaActual
            tvInterv.text = if (intervAlertaActual!!) "Si" else "No"
            val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imgAlertaActual)
            storageRef.getFile(localfile).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                ivAlerta.setImageBitmap(bitmap)
            }
        }

        val ref: CollectionReference = db.collection("Alertas")
        ref.orderBy("horaAlerta",Query.Direction.ASCENDING).get().addOnSuccessListener {
            for (document in it) {
                Log.d("successAlerta", "se recupero la data ${document.id} => ${document.data} ")
            }
        }.addOnCompleteListener {
            if (it.isSuccessful) {
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
            }
        }


        btnPrevious.setOnClickListener {
            counter--
            cargarAlerta(counter)
        }

        btnNext.setOnClickListener {
            counter++
            cargarAlerta(counter)
        }

        btnCerrarSesion.setOnClickListener{
            db.collection("session").document(sessionId).update("active",false).addOnSuccessListener{
                    Log.d("successLogOut","Se modifico el estado de la sesion $sessionId a false")
                    auth.signOut()
                    startActivity(Intent(applicationContext,IniciarSesion::class.java))
                    finish()
            }
        }
        btnMenuPrincipal.setOnClickListener{
            finish()
        }


    }


}
