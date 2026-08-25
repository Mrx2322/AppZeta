package com.example.appzetar.Menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appzetar.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class ActivityMenu : AppCompatActivity() {

    private lateinit var progressBarMenu: ProgressBar

    private val db = FirebaseFirestore.getInstance()

    private val prefs by lazy {
        getSharedPreferences("menu_prefs", MODE_PRIVATE)
    }

    private val entradas = mutableListOf(
        TaskEntradas.Ceviche(),
        TaskEntradas.Huancaina(),
        TaskEntradas.Otros()
    )

    // Empieza vacía. Los datos vienen de Firebase.
    private val listaMenu = mutableListOf<TaskMenu>()

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

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        initComponent()
        initUI()
        cargarDatosDesdeFirebase()
    }

    private fun initComponent() {
        rvEntradas = findViewById(R.id.rvEntradas)
        rvMenu = findViewById(R.id.rvMenu)
        fabAgregarMenu = findViewById(R.id.fabAgregarMenu)
        progressBarMenu = findViewById(R.id.progressBarMenu)
    }

    private fun initUI() {

        // Adaptador Entradas
        entradasAdapter = EntradasAdapter(entradas) { posicion ->
            mostrarDialogoEdicionEntrada(posicion)
        }

        rvEntradas.layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )

        rvEntradas.adapter = entradasAdapter

        // Adaptador Menú
        menuAdapter = MenuAdapter(
            listaMenu,

            onEditClick = { posicion ->
                mostrarDialogoEdicionMenu(posicion)
            },

            onDeleteClick = { posicion ->
                eliminarElementoMenu(posicion)
            }
        )

        rvMenu.layoutManager = LinearLayoutManager(this)
        rvMenu.adapter = menuAdapter

        // Botón agregar
        fabAgregarMenu.setOnClickListener {
            mostrarDialogoAgregarMenu()
        }
    }

    // ---------------------------------------------------------
    // AGREGAR PLATO
    // ---------------------------------------------------------

    private fun mostrarDialogoAgregarMenu() {

        val dialogView = LayoutInflater.from(this)
            .inflate(R.layout.dialog_editar_entrada, null)

        val etNombre =
            dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(
                R.id.etNombrePlato
            )

        etNombre.hint = "Nombre del nuevo plato"

        val dialog = AlertDialog.Builder(this)
            .setTitle("Nuevo Plato")
            .setView(dialogView)
            .setPositiveButton("Agregar", null)
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.window?.setBackgroundDrawable(
            android.graphics.Color.TRANSPARENT.toDrawable()
        )

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {

            val nuevoNombre = etNombre.text.toString().trim()

            if (nuevoNombre.isNotEmpty()) {

                val nuevoId =
                    (listaMenu.maxOfOrNull { it.id } ?: 0) + 1

                val nuevoPlato =
                    TaskMenu(nuevoId, nuevoNombre)

                listaMenu.add(nuevoPlato)

                menuAdapter.notifyItemInserted(
                    listaMenu.size - 1
                )

                guardarMenuEnFirebase()

                dialog.dismiss()

            } else {
                etNombre.error = "Escribe un nombre válido"
            }
        }
    }

    // ---------------------------------------------------------
    // EDITAR ENTRADA
    // ---------------------------------------------------------

    private fun mostrarDialogoEdicionEntrada(posicion: Int) {

        val entradaActual = entradas[posicion]

        val dialogView = LayoutInflater.from(this)
            .inflate(R.layout.dialog_editar_entrada, null)

        val etNombre =
            dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(
                R.id.etNombrePlato
            )

        etNombre.setText(entradaActual.nombre)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Guardar", null)
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.window?.setBackgroundDrawable(
            android.graphics.Color.TRANSPARENT.toDrawable()
        )

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {

            val nuevoNombre = etNombre.text.toString().trim()

            if (nuevoNombre.isNotEmpty()) {

                entradaActual.nombre = nuevoNombre

                entradasAdapter.notifyItemChanged(posicion)

                // Por ahora no guardamos entradas en Firebase
                // porque tu colección actual es "menu".

                dialog.dismiss()

            } else {
                etNombre.error = "Escribe un nombre válido"
            }
        }
    }

    // ---------------------------------------------------------
    // EDITAR MENÚ
    // ---------------------------------------------------------

    private fun mostrarDialogoEdicionMenu(posicion: Int) {

        val itemActual = listaMenu[posicion]

        val dialogView = LayoutInflater.from(this)
            .inflate(R.layout.dialog_editar_entrada, null)

        val etNombre =
            dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(
                R.id.etNombrePlato
            )

        etNombre.setText(itemActual.name)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Guardar", null)
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.window?.setBackgroundDrawable(
            android.graphics.Color.TRANSPARENT.toDrawable()
        )

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {

            val nuevoNombre = etNombre.text.toString().trim()

            if (nuevoNombre.isNotEmpty()) {

                itemActual.name = nuevoNombre

                menuAdapter.notifyItemChanged(posicion)

                guardarMenuEnFirebase()

                dialog.dismiss()

            } else {
                etNombre.error = "Escribe un nombre válido"
            }
        }
    }

    // ---------------------------------------------------------
    // ELIMINAR MENÚ
    // ---------------------------------------------------------

    private fun eliminarElementoMenu(posicion: Int) {

        if (posicion != RecyclerView.NO_POSITION) {

            val plato = listaMenu[posicion]

            db.collection("menu")
                .document(plato.id.toString())
                .delete()
                .addOnSuccessListener {

                    listaMenu.removeAt(posicion)

                    menuAdapter.notifyItemRemoved(posicion)
                }
                .addOnFailureListener { error ->

                    android.util.Log.e(
                        "FIREBASE",
                        "Error eliminando plato",
                        error
                    )
                }
        }
    }

    // ---------------------------------------------------------
    // GUARDAR MENÚ EN FIREBASE
    // ---------------------------------------------------------

    private fun guardarMenuEnFirebase() {

        for (plato in listaMenu) {

            val datos = hashMapOf(
                "id" to plato.id,
                "nombre" to plato.name
            )

            db.collection("menu")
                .document(plato.id.toString())
                .set(datos)
                .addOnSuccessListener {

                    android.util.Log.d(
                        "FIREBASE",
                        "Plato guardado: ${plato.name}"
                    )
                }
                .addOnFailureListener { error ->

                    android.util.Log.e(
                        "FIREBASE",
                        "Error guardando plato",
                        error
                    )
                }
        }
    }

    // ---------------------------------------------------------
    // CARGAR MENÚ DESDE FIREBASE
    // ---------------------------------------------------------

    private fun cargarDatosDesdeFirebase() {

        // Mostrar carga
        progressBarMenu.visibility = View.VISIBLE

        // Ocultar RecyclerView mientras carga
        rvMenu.visibility = View.GONE

        db.collection("menu")
            .get()
            .addOnSuccessListener { resultado ->

                listaMenu.clear()

                if (
                    resultado.isEmpty &&
                    !prefs.getBoolean(
                        "menu_inicializado",
                        false
                    )
                ) {

                    // Primera ejecución
                    val platosIniciales = listOf(
                        TaskMenu(1, "Lomo Saltado"),
                        TaskMenu(2, "Arroz con Mariscos"),
                        TaskMenu(3, "Ají de Gallina")
                    )

                    listaMenu.addAll(platosIniciales)

                    guardarMenuEnFirebase()

                    prefs.edit()
                        .putBoolean(
                            "menu_inicializado",
                            true
                        )
                        .apply()

                } else {

                    // Cargar datos existentes
                    for (documento in resultado) {

                        val id =
                            documento.getLong("id")?.toInt()
                                ?: 0

                        val nombre =
                            documento.getString("nombre")
                                ?: ""

                        if (nombre.isNotEmpty()) {

                            listaMenu.add(
                                TaskMenu(
                                    id,
                                    nombre
                                )
                            )
                        }
                    }
                }

                menuAdapter.notifyDataSetChanged()

                // Ocultar ProgressBar
                progressBarMenu.visibility = View.GONE

                // Mostrar RecyclerView
                rvMenu.visibility = View.VISIBLE
            }
            .addOnFailureListener { error ->

                android.util.Log.e(
                    "FIREBASE",
                    "Error cargando menú",
                    error
                )

                // Ocultar ProgressBar
                progressBarMenu.visibility = View.GONE

                // Mostrar RecyclerView
                rvMenu.visibility = View.VISIBLE
            }
    }
}