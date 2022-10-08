package com.sdci.tp2

import android.app.DatePickerDialog
import android.app.DownloadManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import java.time.format.DateTimeFormatter
import java.util.*

class GenerarReportes : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generar_reportes)

        val db = Firebase.firestore
        val auth = Firebase.auth
        val msging = Firebase.messaging

        val layout = layoutInflater.inflate(R.layout.custom_toast, null)
        val txtToast = layout.findViewById<TextView>(R.id.tv_text)
        val imgToast = layout.findViewById<ImageView>(R.id.iv_ToastIcon)

        val sessionId = intent.getStringExtra("SesionID").toString()
        val btnCerrarSesion = findViewById<ImageView>(R.id.btnCerrarSesion)
        val btnMenuPrincipal = findViewById<ImageView>(R.id.btnMenuPrincipal)
        val btnDescargarReporte = findViewById<Button>(R.id.btnDescargarReporte)
        val etFecha = findViewById<EditText>(R.id.etFecha)
        var fechaSeleccion = ""

        btnDescargarReporte.setOnClickListener{
            val refReporte = db.collection("reports").whereEqualTo("fechaReporte",fechaSeleccion.toString()).get().addOnSuccessListener {
                if (it.documents.size == 0){
                    Toast(this).apply {
                        duration = Toast.LENGTH_LONG
                        txtToast.text = "Error. No existe reporte para la fecha seleccionada."
                        imgToast.setImageResource(R.drawable.toast_error)
                        setGravity(Gravity.FILL_HORIZONTAL, 0, 0)
                        view = layout
                    }.show()
                } else {
                    for (document in it.documents) {
                        Toast(this).apply {
                            duration = Toast.LENGTH_LONG
                            txtToast.text =
                                "Reporte encontrado. Se iniciara la descarga..."
                            imgToast.setImageResource(R.drawable.toast_success)
                            setGravity(Gravity.FILL_HORIZONTAL, 0, 0)
                            view = layout
                        }.show()
                        val formater = DateTimeFormatter.ofPattern("MM/d/yyyy")
                        val date = formater.parse(fechaSeleccion)
                        val desiredFormat = DateTimeFormatter.ofPattern("dd-MMM-yyyy").format(date)
                        val request = DownloadManager.Request(Uri.parse(document.getString("reportDownloadURL")))
                            .setTitle("Reporte Infractores del $desiredFormat")
                            .setDescription("Descargando...")
                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                            .setAllowedOverMetered(true)
                        val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                        dm.enqueue(request)
                    }
                }
            }
        }

        etFecha.setOnClickListener{
            val c = Calendar.getInstance()
            val minDate = Calendar.getInstance()
            minDate.set(Calendar.MONTH,9)
            minDate.set(Calendar.YEAR,2022)
            minDate.set(Calendar.DAY_OF_MONTH,1)
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this,{view, year, monthOfYear, dayOfMonth ->
                val dat = (monthOfYear+1).toString() + "/" +  dayOfMonth.toString() + "/" + year
                etFecha.setText(dat)
            }, year,month,day)

            c.add(Calendar.DATE,-1)
            datePickerDialog.datePicker.minDate = minDate.timeInMillis
            datePickerDialog.datePicker.maxDate = c.timeInMillis
            datePickerDialog.show()
        }

        etFecha.addTextChangedListener {
            btnDescargarReporte.isEnabled = true
            fechaSeleccion = etFecha.text.toString()
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
    }
}