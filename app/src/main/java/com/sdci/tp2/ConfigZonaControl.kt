package com.sdci.tp2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

@Suppress("DEPRECATION")
class ConfigZonaControl : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_zona_control)

        val btnConfirmarZona = findViewById<Button>(R.id.btnConfirmarZC)
        val layout = layoutInflater.inflate(R.layout.custom_toast, null)
        val txtToast = layout.findViewById<TextView>(R.id.tv_text)
        val imgToast = layout.findViewById<ImageView>(R.id.iv_ToastIcon)


        // Declaracion de las LISTAS donde se guardan los datos de los documentos
        val distritos: MutableList<String> = ArrayList()
        val zonasControl: MutableList<String> = ArrayList()
        val idsDistritos: MutableList<String> = ArrayList()
        val idsZonasControl: MutableList<String> = ArrayList()
        val ptsControl: MutableList<String> = ArrayList()
        val imgsControl: MutableList<String> = ArrayList()

        // Declaracion de la variable necesaria para sacar la referencia de FireStore
        var idDistritoSeleccionado = ""
        var idZCSeleccionada: String
        var indexDoc = 0

        // Declaracion de las variables necesarias para mostrar la imagen
        var imgZonaControl: String

        // Declaracion de los objetos que cambian su contenido basado en las elecciones de usuario
        val tPuntoControldeZona = findViewById<TextView>(R.id.tvDistrito)
        val imPtoCtrldeZona = findViewById<ImageView>(R.id.iv_PtoControl)

        // Declaracion de los spinners
        val spinDistrito = findViewById<Spinner>(R.id.spnDistrito)
        val spinZonaControl = findViewById<Spinner>(R.id.spnZonaControl)

        // Declaracion e instanciamiento de los adaptadores de los spinners
        val adaptadorDistrito = ArrayAdapter(this, android.R.layout.simple_spinner_item, distritos)
        adaptadorDistrito.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinDistrito.adapter = adaptadorDistrito

        val adaptadorZonaControl = ArrayAdapter(this,android.R.layout.simple_spinner_item, zonasControl)
        adaptadorZonaControl.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinZonaControl.adapter = adaptadorZonaControl

        // Declaracion de las referencias necesarias en la creacion de esta vista
        val fStore = Firebase.firestore
        val auth = Firebase.auth
        val ref1 = fStore.collection("districts")

        // Obtener los Distritos y llenar el spinner correspondiente
        ref1.get().addOnSuccessListener { result ->
            for (document in result) {
                Log.d("success","${document.id} => ${document.data}")
            }
        }.addOnCompleteListener {
            if (it.isSuccessful) {
                for (document in it.result) {
                    val district = document.getString("nombreDistrito")
                    val distId = document.id
                    distritos.add(district.toString())
                    idsDistritos.add(distId)
                }
                adaptadorDistrito.notifyDataSetChanged()
            }
        }

        // Acciones al seleccionar item en spinner de Distritos
        spinDistrito.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                // Instanciamiento del ID del distrito seleccionado
                idDistritoSeleccionado = idsDistritos[spinDistrito.selectedItemPosition]

                // Acciones a tomar SIEMPRE al cambiar la eleccion de distrito
                spinZonaControl.setSelection(0)
                zonasControl.clear()
                idsZonasControl.clear()
                ptsControl.clear()
                imgsControl.clear()
                imPtoCtrldeZona.setImageBitmap(null)
                tPuntoControldeZona.text = ""
                zonasControl.add(0,"Escoge...")

                // Definicion de referencia necesaria para recuperar los datos de los documentos de zonas de control
                val ref2: CollectionReference = fStore.collection("districts").document(idDistritoSeleccionado).collection("control_zones")

                // Obtener los documentos de zonas de control del distrito seleccionado. Llenado de las listas de cada documento
                ref2.get().addOnSuccessListener{
                    for (document in it) {
                        Log.d("success2", "se recupero la data ${document.id} => ${document.data} ")
                    }
                }.addOnCompleteListener{
                    if (it.isSuccessful){
                        for (document in it.result){
                            val zonacontrol = document.getString("nombreZona")
                            val zcId = document.id
                            val imgZonaCtrl = document.getString("addressZona")
                            val ptoCtrl = document.getString("puntoControl")

                            // Llenado de las listas
                            zonasControl.add(zonacontrol.toString())
                            idsZonasControl.add(zcId)
                            ptsControl.add(ptoCtrl.toString())
                            imgsControl.add(imgZonaCtrl.toString())
                        }
                        adaptadorZonaControl.notifyDataSetChanged()
                    }
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
        // Acciones al seleccionar una zona de control del spinner 2
        spinZonaControl.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                // Resetear la habilitacion del boton "Confirmar Zona Control"
                if (spinZonaControl.selectedItemPosition == 0){
                    btnConfirmarZona.isEnabled = false
                }
                // Validar que no se haga nada cuando la seleccion sea 0 (Item sintetico)
                if (spinZonaControl.selectedItemPosition != 0) {
                    btnConfirmarZona.isEnabled = true
                    // Declaracion de variable "indice" para diferenciar los documentos en las listas
                    indexDoc = spinZonaControl.selectedItemPosition-1
                    tPuntoControldeZona.text = ptsControl[indexDoc]
                    imgZonaControl = imgsControl[indexDoc]
                    Picasso.get().load(imgZonaControl).into(imPtoCtrldeZona)
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }



        btnConfirmarZona.setOnClickListener {
            // Crear la sesion combinando UID de usuario y Zona de Control
            // primero se captura el UID y la zcID
            val userID: String = auth.currentUser?.uid.toString()
            idZCSeleccionada = idsZonasControl[indexDoc]
            val sesionRef: CollectionReference = fStore.collection("session")

            val session = hashMapOf(
                "userId" to userID,
                "zonaId" to idZCSeleccionada,
                "distId" to idDistritoSeleccionado,
                "active" to true
            )
            sesionRef.add(session).addOnSuccessListener {
                Log.d("success 4", "Se creo la sesion con el id: ${it.id}")
            }.addOnFailureListener{
                Log.d("failure 4", "No se creo la sesion por el error.", it)
            }

            // Toast de que se creo correctamente la sesion.
            Toast(this@ConfigZonaControl).apply {
                duration = Toast.LENGTH_LONG
                txtToast.text = "Exito. Se configuro la zona de control correctamente."
                imgToast.setImageResource(R.drawable.toast_success)
                setGravity(Gravity.FILL_HORIZONTAL, 0, 0)
                view = layout
            }.show()

            // Enviar a usuario que inicia sesion a la actividad Main Activity.
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }
    }
}