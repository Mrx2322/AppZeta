package com.example.appzetar.Menu

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appzetar.R

class ActivityMenu : AppCompatActivity() {

    private val entradas = mutableListOf(
        TaskEntradas.Ceviche(),
        TaskEntradas.Huancaina(),
        TaskEntradas.Otros()
    )

    private lateinit var rvEntradas: RecyclerView
    private lateinit var entradasAdapter: EntradasAdapter
    private lateinit var rvMenu: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initComponent()
        // 1. Cargamos lo que guardaste previamente antes de iniciar la UI
        cargarNombresGuardados()
        initUI()
    }

    private fun initComponent() {
        rvEntradas = findViewById(R.id.rvEntradas)
        rvMenu = findViewById(R.id.rvMenu)
    }

    private fun initUI() {
        entradasAdapter = EntradasAdapter(entradas) { posicionClickeada ->
            mostrarDialogoEdicion(posicionClickeada)
        }

        rvEntradas.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvEntradas.adapter = entradasAdapter
    }

    private fun mostrarDialogoEdicion(posicion: Int) {
        val entradaActual = entradas[posicion]

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_editar_entrada, null)
        val etNombre = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etNombrePlato)

        etNombre.setText(entradaActual.nombre)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Guardar", null)
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.window?.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val nuevoNombre = etNombre.text.toString().trim()

            if (nuevoNombre.isNotEmpty()) {
                entradaActual.nombre = nuevoNombre
                entradasAdapter.notifyItemChanged(posicion)

                // 2. Guardamos permanentemente en la memoria del celular
                guardarNombresEnDisco()

                dialog.dismiss()
            } else {
                etNombre.error = "Escribe un nombre válido"
            }
        }
    }

    // GUARDA LOS NOMBRES EN EL CELULAR
    private fun guardarNombresEnDisco() {
        val prefs = getSharedPreferences("MisEntradasStorage", MODE_PRIVATE)
        val editor = prefs.edit()

        for (i in entradas.indices) {
            editor.putString("plato_nombre_$i", entradas[i].nombre)
        }
        editor.apply()
    }

    // CARGA LOS NOMBRES CADA VEZ QUE ENTRAS A LA PANTALLA
    private fun cargarNombresGuardados() {
        val prefs = getSharedPreferences("MisEntradasStorage", MODE_PRIVATE)

        for (i in entradas.indices) {
            val nombreGuardado = prefs.getString("plato_nombre_$i", null)
            if (!nombreGuardado.isNullOrEmpty()) {
                entradas[i].nombre = nombreGuardado
            }
        }
    }
}