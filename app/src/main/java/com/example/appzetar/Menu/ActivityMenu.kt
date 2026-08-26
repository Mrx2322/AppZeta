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
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.window?.setBackgroundDrawable(
            android.graphics.Color.TRANSPARENT.toDrawable()
        )

        dialog.show()

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

                    // Actualizar datos locales
                    entradaActual.nombre = nuevoNombre
                    entradaActual.disponible = nuevaDisponibilidad

                    // Actualizar RecyclerView
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

        // Mostrar carga
        progressBarMenu.visibility = View.VISIBLE

        // Ocultar RecyclerView mientras carga
        rvMenu.visibility = View.GONE

        db.collection("menu")
            .get()
            .addOnSuccessListener { resultado ->

                listaMenu.clear()

                if (resultado.isEmpty) {

                    // Firebase está vacío.
                    // Crear menú inicial.
                    val platosIniciales = listOf(
                        TaskMenu(1, "Lomo Saltado"),
                        TaskMenu(2, "Arroz con Mariscos"),
                        TaskMenu(3, "Ají de Gallina")
                    )

                    // Agregar a la lista local
                    listaMenu.addAll(platosIniciales)

                    // Guardar cada plato directamente en Firebase
                    for (plato in platosIniciales) {

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
                                    "Plato inicial guardado: ${plato.name}"
                                )
                            }
                            .addOnFailureListener { error ->

                                android.util.Log.e(
                                    "FIREBASE",
                                    "Error guardando plato inicial",
                                    error
                                )
                            }
                    }

                } else {

                    // Firebase ya tiene datos.
                    // Cargar los platos existentes.
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

                // Actualizar RecyclerView
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

    // ---------------------------------------------------------
// CARGAR ENTRADAS DESDE FIREBASE
// ---------------------------------------------------------

    private fun cargarEntradasDesdeFirebase() {

        db.collection("entradas")
            .get()
            .addOnSuccessListener { resultado ->

                entradas.clear()

                if (resultado.isEmpty) {

                    // Firebase está vacío.
                    // Crear entradas iniciales.
                    val entradasIniciales = listOf(
                        TaskEntradas.Ceviche(),
                        TaskEntradas.Huancaina(),
                        TaskEntradas.Otros()
                    )

                    entradas.addAll(entradasIniciales)

                    // Guardar cada entrada en Firebase
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

                } else {

                    // Firebase ya tiene entradas.
                    // Cargar los datos existentes.
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

                            val entrada = when (id) {

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
                }

                // Actualizar RecyclerView
                entradasAdapter.notifyDataSetChanged()

                android.util.Log.d(
                    "FIREBASE",
                    "Entradas cargadas: ${entradas.size}"
                )
            }
            .addOnFailureListener { error ->

                android.util.Log.e(
                    "FIREBASE",
                    "Error cargando entradas",
                    error
                )
            }
    }
}