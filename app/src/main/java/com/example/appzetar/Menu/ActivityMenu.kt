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
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appzetar.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import android.content.Intent
import android.widget.Button
import com.example.appzetar.Usuario.ActivityMenuUsuario

class ActivityMenu : AppCompatActivity() {

    private lateinit var progressBarMenu: ProgressBar

    private val db = FirebaseFirestore.getInstance()

    private val entradas = mutableListOf<TaskEntradas>()

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
        WindowCompat.setDecorFitsSystemWindows(window, false)

        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.statusBars())
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
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
        cargarEntradasDesdeFirebase()
    }

    private fun initComponent() {
        rvEntradas = findViewById(R.id.rvEntradas)
        rvMenu = findViewById(R.id.rvMenu)
        fabAgregarMenu = findViewById(R.id.fabAgregarMenu)
        progressBarMenu = findViewById(R.id.progressBarMenu)
    }

    private fun initUI() {
        val btnUsuario = findViewById<Button>(R.id.btnUsuario)

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
            mostrarDialogoSeleccionarTipo()
        }

        btnUsuario.setOnClickListener {

            val intent = Intent(
                this,
                ActivityMenuUsuario::class.java
            )

            startActivity(intent)
        }
    }

    // ---------------------------------------------------------
    // MMOSTRAR SELECCIÓN
    // -------------------------------------------------------
    private fun mostrarDialogoSeleccionarTipo() {

        val opciones = arrayOf(
            "Plato del menú",
            "Entrada"
        )

        AlertDialog.Builder(this)
            .setTitle("¿Qué deseas agregar?")
            .setItems(opciones) { _, cual ->

                when (cual) {

                    0 -> {
                        mostrarDialogoAgregarMenu()
                    }

                    1 -> {
                        mostrarDialogoAgregarEntrada()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // ---------------------------------------------------------
    // AGREGAR PLATO
    // ---------------------------------------------------------

    private fun mostrarDialogoAgregarMenu() {

        val dialogView = LayoutInflater.from(this)
            .inflate(R.layout.dialog_agregar_menu, null)

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

            if (nuevoNombre.isEmpty()) {
                etNombre.error = "Escribe un nombre válido"
                return@setOnClickListener
            }

            // Obtener el siguiente ID
            val nuevoId =
                (listaMenu.maxOfOrNull { it.id } ?: 0) + 1

            // Datos que se guardarán en Firebase
            val datos = hashMapOf(
                "id" to nuevoId,
                "nombre" to nuevoNombre
            )

            // Guardar SOLO el nuevo plato en Firebase
            db.collection("menu")
                .document(nuevoId.toString())
                .set(datos)
                .addOnSuccessListener {

                    // Crear el objeto local
                    val nuevoPlato =
                        TaskMenu(nuevoId, nuevoNombre)

                    // Agregarlo a la lista
                    listaMenu.add(nuevoPlato)

                    // Actualizar RecyclerView
                    menuAdapter.notifyItemInserted(
                        listaMenu.size - 1
                    )

                    android.util.Log.d(
                        "FIREBASE",
                        "Plato agregado: $nuevoNombre"
                    )

                    dialog.dismiss()
                }
                .addOnFailureListener { error ->

                    android.util.Log.e(
                        "FIREBASE",
                        "Error agregando plato",
                        error
                    )

                    etNombre.error =
                        "No se pudo guardar el plato"
                }
        }
    }

    private fun mostrarDialogoAgregarEntrada() {

        val dialogView = LayoutInflater.from(this)
            .inflate(R.layout.dialog_agregar_entrada, null)

        val etNombre =
            dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(
                R.id.etNombreEntrada
            )

        val switchDisponible =
            dialogView.findViewById<com.google.android.material.switchmaterial.SwitchMaterial>(
                R.id.switchDisponible
            )

        switchDisponible.isChecked = true

        val dialog = AlertDialog.Builder(this)
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
            val disponible = switchDisponible.isChecked

            if (nuevoNombre.isEmpty()) {

                etNombre.error = "Escribe un nombre válido"
                return@setOnClickListener
            }

            // Buscar el siguiente ID
            val nuevoId =
                (entradas.maxOfOrNull { it.id } ?: 0) + 1

            val datos = hashMapOf(
                "id" to nuevoId,
                "nombre" to nuevoNombre,
                "disponible" to disponible
            )

            // Guardar en Firebase
            db.collection("entradas")
                .document(nuevoId.toString())
                .set(datos)
                .addOnSuccessListener {

                    // Crear la nueva entrada
                    val nuevaEntrada =
                        TaskEntradas.Otros(
                            nuevoId,
                            nuevoNombre,
                            disponible
                        )

                    entradas.add(nuevaEntrada)

                    // Actualizar RecyclerView
                    entradasAdapter.notifyItemInserted(
                        entradas.size - 1
                    )

                    android.util.Log.d(
                        "FIREBASE",
                        "Entrada agregada: $nuevoNombre"
                    )

                    dialog.dismiss()
                }
                .addOnFailureListener { error ->

                    android.util.Log.e(
                        "FIREBASE",
                        "Error agregando entrada",
                        error
                    )

                    etNombre.error =
                        "No se pudo guardar la entrada"
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

        val switchDisponible =
            dialogView.findViewById<com.google.android.material.switchmaterial.SwitchMaterial>(
                R.id.switchDisponible
            )

        // Mostrar datos actuales
        etNombre.setText(entradaActual.nombre)
        switchDisponible.isChecked = entradaActual.disponible

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Guardar", null)
            .setNegativeButton("Eliminar", null)
            .create()

        dialog.window?.setBackgroundDrawable(
            android.graphics.Color.TRANSPARENT.toDrawable()
        )

        dialog.show()

        // BOTÓN GUARDAR
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {

            val nuevoNombre = etNombre.text.toString().trim()
            val nuevaDisponibilidad = switchDisponible.isChecked

            if (nuevoNombre.isEmpty()) {

                etNombre.error = "Escribe un nombre válido"
                return@setOnClickListener
            }

            val datosActualizados = hashMapOf(
                "id" to entradaActual.id,
                "nombre" to nuevoNombre,
                "disponible" to nuevaDisponibilidad
            )

            db.collection("entradas")
                .document(entradaActual.id.toString())
                .set(datosActualizados)
                .addOnSuccessListener {

                    entradaActual.nombre = nuevoNombre
                    entradaActual.disponible = nuevaDisponibilidad

                    entradasAdapter.notifyItemChanged(posicion)

                    android.util.Log.d(
                        "FIREBASE",
                        "Entrada actualizada: $nuevoNombre"
                    )

                    dialog.dismiss()
                }
                .addOnFailureListener { error ->

                    android.util.Log.e(
                        "FIREBASE",
                        "Error actualizando entrada",
                        error
                    )

                    etNombre.error =
                        "No se pudo actualizar la entrada"
                }
        }

        // BOTÓN ELIMINAR
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {

            AlertDialog.Builder(this)
                .setTitle("Eliminar entrada")
                .setMessage(
                    "¿Estás seguro de que deseas eliminar \"${entradaActual.nombre}\"?"
                )
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Eliminar") { _, _ ->

                    eliminarEntradaDeFirebase(
                        entradaActual,
                        posicion,
                        dialog
                    )
                }
                .show()
        }
    }

    // ---------------------------------------------------------
    // ELIMINAR ENTRADA
    // ---------------------------------------------------------
    private fun eliminarEntradaDeFirebase(
        entrada: TaskEntradas,
        posicion: Int,
        dialog: AlertDialog
    ) {

        db.collection("entradas")
            .document(entrada.id.toString())
            .delete()
            .addOnSuccessListener {

                // Eliminar de la lista local
                entradas.removeAt(posicion)

                // Actualizar RecyclerView
                entradasAdapter.notifyItemRemoved(posicion)

                android.util.Log.d(
                    "FIREBASE",
                    "Entrada eliminada: ${entrada.nombre}"
                )

                dialog.dismiss()
            }
            .addOnFailureListener { error ->

                android.util.Log.e(
                    "FIREBASE",
                    "Error eliminando entrada",
                    error
                )
            }
    }


    // ---------------------------------------------------------
    // EDITAR MENÚ
    // ---------------------------------------------------------

    private fun mostrarDialogoEdicionMenu(posicion: Int) {

        val itemActual = listaMenu[posicion]

        val dialogView = LayoutInflater.from(this)
            .inflate(R.layout.dialog_editar_menu, null)

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

            if (nuevoNombre.isEmpty()) {

                etNombre.error = "Escribe un nombre válido"
                return@setOnClickListener
            }

            // Datos actualizados
            val datosActualizados = hashMapOf(
                "id" to itemActual.id,
                "nombre" to nuevoNombre
            )

            // Actualizar SOLO este plato en Firebase
            db.collection("menu")
                .document(itemActual.id.toString())
                .set(datosActualizados)
                .addOnSuccessListener {

                    // Actualizar el objeto local
                    itemActual.name = nuevoNombre

                    // Actualizar RecyclerView
                    menuAdapter.notifyItemChanged(posicion)

                    android.util.Log.d(
                        "FIREBASE",
                        "Plato actualizado: $nuevoNombre"
                    )

                    dialog.dismiss()
                }
                .addOnFailureListener { error ->

                    android.util.Log.e(
                        "FIREBASE",
                        "Error actualizando plato",
                        error
                    )

                    etNombre.error =
                        "No se pudo actualizar el plato"
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
    // CARGAR MENÚ DESDE FIREBASE
    // ---------------------------------------------------------

    private fun cargarDatosDesdeFirebase() {

        progressBarMenu.visibility = View.VISIBLE
        rvMenu.visibility = View.GONE

        db.collection("menu")
            .addSnapshotListener { resultado, error ->

                if (error != null) {

                    android.util.Log.e(
                        "FIREBASE",
                        "Error escuchando menú",
                        error
                    )

                    progressBarMenu.visibility = View.GONE
                    rvMenu.visibility = View.VISIBLE

                    return@addSnapshotListener
                }

                if (resultado == null) {
                    return@addSnapshotListener
                }

                listaMenu.clear()

                for (documento in resultado) {

                    val id =
                        documento.getLong("id")?.toInt() ?: 0

                    val nombre =
                        documento.getString("nombre") ?: ""

                    if (nombre.isNotEmpty()) {

                        listaMenu.add(
                            TaskMenu(
                                id,
                                nombre
                            )
                        )
                    }
                }

                menuAdapter.notifyDataSetChanged()

                progressBarMenu.visibility = View.GONE
                rvMenu.visibility = View.VISIBLE
            }
    }

    // ---------------------------------------------------------
// CARGAR ENTRADAS DESDE FIREBASE
// ---------------------------------------------------------

    // ---------------------------------------------------------
// CARGAR ENTRADAS DESDE FIREBASE EN TIEMPO REAL
// ---------------------------------------------------------

    private fun cargarEntradasDesdeFirebase() {

        db.collection("entradas")
            .addSnapshotListener { resultado, error ->

                if (error != null) {

                    android.util.Log.e(
                        "FIREBASE",
                        "Error escuchando entradas",
                        error
                    )

                    return@addSnapshotListener
                }

                if (resultado == null) {
                    return@addSnapshotListener
                }

                entradas.clear()

                // -------------------------------------------------
                // SI FIREBASE ESTÁ VACÍO
                // -------------------------------------------------

                if (resultado.isEmpty) {

                    val entradasIniciales = listOf(
                        TaskEntradas.Ceviche(),
                        TaskEntradas.Huancaina(),
                        TaskEntradas.Otros()
                    )

                    for (entrada in entradasIniciales) {

                        val datos = hashMapOf(
                            "id" to entrada.id,
                            "nombre" to entrada.nombre,
                            "disponible" to entrada.disponible
                        )

                        db.collection("entradas")
                            .document(entrada.id.toString())
                            .set(datos)
                            .addOnSuccessListener {

                                android.util.Log.d(
                                    "FIREBASE",
                                    "Entrada inicial guardada: ${entrada.nombre}"
                                )
                            }
                            .addOnFailureListener { error ->

                                android.util.Log.e(
                                    "FIREBASE",
                                    "Error guardando entrada inicial",
                                    error
                                )
                            }
                    }

                    return@addSnapshotListener
                }

                // -------------------------------------------------
                // FIREBASE YA TIENE ENTRADAS
                // -------------------------------------------------

                for (documento in resultado) {

                    val id =
                        documento.getLong("id")?.toInt()
                            ?: 0

                    val nombre =
                        documento.getString("nombre")
                            ?: ""

                    val disponible =
                        documento.getBoolean("disponible")
                            ?: true

                    if (nombre.isNotEmpty()) {

                        val entrada: TaskEntradas =
                            when (id) {

                                1 -> TaskEntradas.Ceviche(
                                    id,
                                    nombre,
                                    disponible
                                )

                                2 -> TaskEntradas.Huancaina(
                                    id,
                                    nombre,
                                    disponible
                                )

                                else -> TaskEntradas.Otros(
                                    id,
                                    nombre,
                                    disponible
                                )
                            }

                        entradas.add(entrada)
                    }
                }

                // Actualizar RecyclerView
                entradasAdapter.notifyDataSetChanged()

                android.util.Log.d(
                    "FIREBASE",
                    "Entradas actualizadas en tiempo real: ${entradas.size}"
                )
            }
    }
}