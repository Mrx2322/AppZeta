package com.example.appzetar.Menu

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appzetar.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.core.graphics.drawable.toDrawable
import androidx.core.content.edit

class ActivityMenu : AppCompatActivity() {

    private val entradas = mutableListOf(
        TaskEntradas.Ceviche(),
        TaskEntradas.Huancaina(),
        TaskEntradas.Otros()
    )

    private val listaMenu = mutableListOf(
        TaskMenu(1, "Lomo Saltado"),
        TaskMenu(2, "Arroz con Mariscos"),
        TaskMenu(3, "Ají de Gallina")
    )

    private lateinit var rvEntradas: RecyclerView
    private lateinit var entradasAdapter: EntradasAdapter

    private lateinit var rvMenu: RecyclerView
    private lateinit var menuAdapter: MenuAdapter
    private lateinit var fabAgregarMenu: FloatingActionButton

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
        cargarNombresGuardados()
        initUI()
    }

    private fun initComponent() {
        rvEntradas = findViewById(R.id.rvEntradas)
        rvMenu = findViewById(R.id.rvMenu)
        fabAgregarMenu = findViewById(R.id.fabAgregarMenu)
    }

    private fun initUI() {
        // Adaptador Entradas
        entradasAdapter = EntradasAdapter(entradas) { posicion ->
            mostrarDialogoEdicionEntrada(posicion)
        }
        rvEntradas.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvEntradas.adapter = entradasAdapter

        // Adaptador Menú
        menuAdapter = MenuAdapter(
            listaMenu,
            onEditClick = { posicion -> mostrarDialogoEdicionMenu(posicion) },
            onDeleteClick = { posicion -> eliminarElementoMenu(posicion) }
        )
        rvMenu.layoutManager = LinearLayoutManager(this)
        rvMenu.adapter = menuAdapter

        // Evento del botón Agregar
        fabAgregarMenu.setOnClickListener {
            mostrarDialogoAgregarMenu()
        }
    }

    // --- DIÁLOGO PARA AGREGAR NUEVO ELEMENTO AL MENÚ ---
    private fun mostrarDialogoAgregarMenu() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_editar_entrada, null)
        val etNombre = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etNombrePlato)

        etNombre.hint = "Nombre del nuevo plato"

        val dialog = AlertDialog.Builder(this)
            .setTitle("Nuevo Plato")
            .setView(dialogView)
            .setPositiveButton("Agregar", null)
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.window?.setBackgroundDrawable(android.graphics.Color.TRANSPARENT.toDrawable())
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val nuevoNombre = etNombre.text.toString().trim()

            if (nuevoNombre.isNotEmpty()) {
                val nuevoId = System.currentTimeMillis().toInt()
                val nuevoPlato = TaskMenu(nuevoId, nuevoNombre)

                listaMenu.add(nuevoPlato)
                menuAdapter.notifyItemInserted(listaMenu.size - 1)

                guardarNombresEnDisco()
                dialog.dismiss()
            } else {
                etNombre.error = "Escribe un nombre válido"
            }
        }
    }

    // --- EDITAR ENTRADA ---
    private fun mostrarDialogoEdicionEntrada(posicion: Int) {
        val entradaActual = entradas[posicion]
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_editar_entrada, null)
        val etNombre = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etNombrePlato)

        etNombre.setText(entradaActual.nombre)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Guardar", null)
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.window?.setBackgroundDrawable(android.graphics.Color.TRANSPARENT.toDrawable())
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val nuevoNombre = etNombre.text.toString().trim()
            if (nuevoNombre.isNotEmpty()) {
                entradaActual.nombre = nuevoNombre
                entradasAdapter.notifyItemChanged(posicion)
                guardarNombresEnDisco()
                dialog.dismiss()
            } else {
                etNombre.error = "Escribe un nombre válido"
            }
        }
    }

    // --- EDITAR MENÚ ---
    private fun mostrarDialogoEdicionMenu(posicion: Int) {
        val itemActual = listaMenu[posicion]
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_editar_entrada, null)
        val etNombre = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etNombrePlato)

        etNombre.setText(itemActual.name)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Guardar", null)
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.window?.setBackgroundDrawable(android.graphics.Color.TRANSPARENT.toDrawable())
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val nuevoNombre = etNombre.text.toString().trim()
            if (nuevoNombre.isNotEmpty()) {
                itemActual.name = nuevoNombre
                menuAdapter.notifyItemChanged(posicion)
                guardarNombresEnDisco()
                dialog.dismiss()
            } else {
                etNombre.error = "Escribe un nombre válido"
            }
        }
    }

    // --- ELIMINAR MENÚ ---
    private fun eliminarElementoMenu(posicion: Int) {
        if (posicion != RecyclerView.NO_POSITION) {
            listaMenu.removeAt(posicion)
            menuAdapter.notifyItemRemoved(posicion)
            menuAdapter.notifyItemRangeChanged(posicion, listaMenu.size - posicion)
            guardarNombresEnDisco()
        }
    }

    // --- PERSISTENCIA DE DATOS ---
    private fun guardarNombresEnDisco() {
        val prefs = getSharedPreferences("MisEntradasStorage", MODE_PRIVATE)
        prefs.edit {

            for (i in entradas.indices) {
                putString("plato_nombre_$i", entradas[i].nombre)
            }

            putInt("menu_total_count", listaMenu.size)
            for (i in listaMenu.indices) {
                putString("menu_nombre_$i", listaMenu[i].name)
            }

        }
    }

    private fun cargarNombresGuardados() {
        val prefs = getSharedPreferences("MisEntradasStorage", MODE_PRIVATE)

        for (i in entradas.indices) {
            val nombreGuardado = prefs.getString("plato_nombre_$i", null)
            if (!nombreGuardado.isNullOrEmpty()) {
                entradas[i].nombre = nombreGuardado
            }
        }

        val totalMenuGuardado = prefs.getInt("menu_total_count", -1)
        if (totalMenuGuardado != -1) {
            listaMenu.clear()
            for (i in 0 until totalMenuGuardado) {
                val nombre = prefs.getString("menu_nombre_$i", "") ?: ""
                if (nombre.isNotEmpty()) {
                    listaMenu.add(TaskMenu(i, nombre))
                }
            }
        }
    }
}