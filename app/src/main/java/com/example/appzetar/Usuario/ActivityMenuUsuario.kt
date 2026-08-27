package com.example.appzetar.Usuario

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appzetar.Menu.TaskEntradas
import com.example.appzetar.Menu.TaskMenu
import com.example.appzetar.R
import com.google.firebase.firestore.FirebaseFirestore

class ActivityMenuUsuario : AppCompatActivity() {

    private val pedido = mutableListOf<PedidoItem>()
    private val db = FirebaseFirestore.getInstance()

    private val entradas = mutableListOf<TaskEntradas>()
    private val listaMenu = mutableListOf<TaskMenu>()

    private lateinit var rvEntradas: RecyclerView
    private lateinit var entradasAdapter: EntradasUsuarioAdapter

    private lateinit var rvMenu: RecyclerView
    private lateinit var menuAdapter: MenuUsuarioAdapter

    private lateinit var progressBarMenu: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(
            window,
            false
        )

        WindowInsetsControllerCompat(
            window,
            window.decorView
        ).apply {

            hide(WindowInsetsCompat.Type.statusBars())

            systemBarsBehavior =
                WindowInsetsControllerCompat
                    .BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        setContentView(R.layout.activity_menu_usuario)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v, insets ->

            val systemBars =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                )

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

    // ---------------------------------------------------------
    // COMPONENTES
    // ---------------------------------------------------------

    private fun initComponent() {

        rvEntradas =
            findViewById(R.id.rvEntradas)

        rvMenu =
            findViewById(R.id.rvMenu)

        progressBarMenu =
            findViewById(R.id.progressBarMenu)
    }

    // ---------------------------------------------------------
    // UI
    // ---------------------------------------------------------

    private fun initUI() {

        // ENTRADAS

        entradasAdapter =
            EntradasUsuarioAdapter(entradas)

        rvEntradas.layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )

        rvEntradas.adapter =
            entradasAdapter


        // MENÚ

        menuAdapter = MenuUsuarioAdapter(listaMenu) { plato ->

            agregarAlPedido(plato)

        }

        rvMenu.layoutManager =
            LinearLayoutManager(this)

        rvMenu.adapter =
            menuAdapter
    }

    private fun agregarAlPedido(plato: TaskMenu) {

        val existente = pedido.find {
            it.id == plato.id
        }

        if (existente != null) {

            existente.cantidad++

        } else {

            pedido.add(
                PedidoItem(
                    id = plato.id,
                    nombre = plato.name,
                    cantidad = 1
                )
            )
        }

        android.util.Log.d(
            "PEDIDO",
            "Plato agregado: ${plato.name}"
        )

        android.widget.Toast.makeText(
            this,
            "${plato.name} agregado al pedido",
            android.widget.Toast.LENGTH_SHORT
        ).show()
    }

    // ---------------------------------------------------------
    // CARGAR DATOS
    // ---------------------------------------------------------

    private fun cargarDatosDesdeFirebase() {

        progressBarMenu.visibility = View.VISIBLE

        rvEntradas.visibility = View.GONE
        rvMenu.visibility = View.GONE

        cargarEntradas()
        cargarMenu()
    }

    // ---------------------------------------------------------
    // ENTRADAS
    // ---------------------------------------------------------

    private fun cargarEntradas() {

        db.collection("entradas")
            .addSnapshotListener { resultado, error ->

                if (error != null) {

                    android.util.Log.e(
                        "USUARIO_FIREBASE",
                        "Error escuchando entradas",
                        error
                    )

                    return@addSnapshotListener
                }

                if (resultado == null) {
                    return@addSnapshotListener
                }

                entradas.clear()

                for (documento in resultado) {

                    val id =
                        documento.getLong("id")?.toInt() ?: 0

                    val nombre =
                        documento.getString("nombre") ?: ""

                    val disponible =
                        documento.getBoolean("disponible") ?: true

                    if (nombre.isNotEmpty()) {

                        val entrada =
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

                entradasAdapter.notifyDataSetChanged()

                android.util.Log.d(
                    "USUARIO_FIREBASE",
                    "Entradas actualizadas en tiempo real: ${entradas.size}"
                )
            }
    }
    // ---------------------------------------------------------
    // MENÚ -carga
    // ---------------------------------------------------------

    private fun cargarMenu() {

        db.collection("menu")
            .addSnapshotListener { resultado, error ->

                if (error != null) {

                    android.util.Log.e(
                        "USUARIO_FIREBASE",
                        "Error escuchando menú",
                        error
                    )

                    mostrarContenido()
                    return@addSnapshotListener
                }

                if (resultado == null) {
                    return@addSnapshotListener
                }

                listaMenu.clear()

                for (documento in resultado) {

                    android.util.Log.d(
                        "USUARIO_FIREBASE",
                        "Menú: ${documento.id} - ${documento.data}"
                    )

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

                mostrarContenido()

                android.util.Log.d(
                    "USUARIO_FIREBASE",
                    "Menú actualizado en tiempo real: ${listaMenu.size}"
                )
            }
    }
    // ---------------------------------------------------------
    // MOSTRAR CONTENIDO
    // ---------------------------------------------------------

    private fun mostrarContenido() {

        progressBarMenu.visibility =
            View.GONE

        rvEntradas.visibility =
            View.VISIBLE

        rvMenu.visibility =
            View.VISIBLE
    }
}