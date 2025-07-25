package com.example.to_do_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {

    private val NOMBRE_PREFS = "PreferenciasListaTareas"
    private val CLAVE_TAREAS = "tareasGuardadas"
    private val listaTareas = mutableListOf<String>()
    private lateinit var adaptador: AdaptadorTareas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val prefs = getSharedPreferences(NOMBRE_PREFS, MODE_PRIVATE)
        val tareasGuardadas = prefs.getStringSet(CLAVE_TAREAS, mutableSetOf())?.toMutableList() ?: mutableListOf()
        listaTareas.addAll(tareasGuardadas)

        val vistaReciclada = findViewById<RecyclerView>(R.id.lista_tareas)
        vistaReciclada.layoutManager = LinearLayoutManager(this)
        adaptador = AdaptadorTareas(listaTareas)
        vistaReciclada.adapter = adaptador

        val botonAgregar = findViewById<Button>(R.id.boton_agregar)
        val campoTexto = findViewById<EditText>(R.id.campo_texto)

        botonAgregar.setOnClickListener {
            val tarea = campoTexto.text.toString()
            if (tarea.isNotEmpty()) {
                listaTareas.add(tarea)
                adaptador.notifyItemInserted(listaTareas.size - 1)
                campoTexto.text.clear()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        val prefs = getSharedPreferences(NOMBRE_PREFS, MODE_PRIVATE)
        prefs.edit().putStringSet(CLAVE_TAREAS, listaTareas.toSet()).apply()
    }

    inner class AdaptadorTareas(private val tareas: MutableList<String>) :
        RecyclerView.Adapter<AdaptadorTareas.SoporteTareas>() {

        inner class SoporteTareas(vistaItem: View) : RecyclerView.ViewHolder(vistaItem) {
            val textoVista: TextView = vistaItem.findViewById(R.id.texto_tarea)
            val casillaVerificacion: CheckBox = vistaItem.findViewById(R.id.casilla_verificacion)
        }

        override fun onCreateViewHolder(contenedor: ViewGroup, tipoVista: Int): SoporteTareas {
            val vista = LayoutInflater.from(contenedor.context)
                .inflate(R.layout.item_tarea, contenedor, false)
            return SoporteTareas(vista)
        }

        override fun onBindViewHolder(soporte: SoporteTareas, posicion: Int) {
            val tarea = tareas[posicion]
            soporte.textoVista.text = tarea



            // esto es para que cuando se marque lo del checklist despuese de un segundo se elimine asies
            soporte.casillaVerificacion.setOnCheckedChangeListener { _, estaMarcada ->
                if (estaMarcada) {
                    soporte.textoVista.apply {
                        paintFlags = paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                        setTextColor(android.graphics.Color.GRAY)
                    }
                    soporte.itemView.postDelayed({
                        tareas.removeAt(posicion)
                        notifyItemRemoved(posicion)
                    }, 1000)
                }
            }
        }

        override fun getItemCount(): Int = tareas.size
    }
}