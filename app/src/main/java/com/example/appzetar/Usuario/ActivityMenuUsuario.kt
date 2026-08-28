package com.example.appzetar.Usuario

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
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
import com.example.appzetar.Menu.TaskEntradas
import com.example.appzetar.Menu.TaskMenu
import com.example.appzetar.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class ActivityMenuUsuario : AppCompatActivity() {

    // =========================================================
    // CATEGORÍAS
    // =========================================================

    private lateinit var rvCategorias: RecyclerView
    private lateinit var categoriaAdapter: CategoriaAdapter

    private val categorias =
        mutableListOf<CategoriaItem>()

    private var categoriaSeleccionadaId = 0


    // =========================================================
    // EXTRAS
    // =========================================================

    private lateinit var rvExtras: RecyclerView
    private lateinit var extraAdapter: ExtraAdapter

    private val todosLosExtras =
        mutableListOf<ExtraItem>()

    private val listaExtras =
        mutableListOf<ExtraItem>()


    // =========================================================
    // FIREBASE
    // =========================================================

    private val db =
        FirebaseFirestore.getInstance()


    // =========================================================
    // LISTAS
    // =========================================================

    private val entradas =
        mutableListOf<TaskEntradas>()

    private val listaMenu =
        mutableListOf<TaskMenu>()


    // =========================================================
    // ENTRADAS
    // =========================================================

    private lateinit var rvEntradas: RecyclerView
    private lateinit var entradasAdapter: EntradasUsuarioAdapter


    // =========================================================
    // MENÚ
    // =========================================================

    private lateinit var rvMenu: RecyclerView
    private lateinit var menuAdapter: MenuUsuarioAdapter


    // =========================================================
    // OTROS
    // =========================================================

    private lateinit var progressBarMenu: ProgressBar
    private lateinit var tvCantidadCarrito: TextView
    private lateinit var btnCarrito: FloatingActionButton


    // =========================================================
    // ON CREATE
    // =========================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // -----------------------------------------------------
        // PANTALLA COMPLETA
        // -----------------------------------------------------

        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(
            window,
            false
        )

        WindowInsetsControllerCompat(
            window,
            window.decorView
        ).apply {

            hide(
                WindowInsetsCompat.Type.statusBars()
            )

            systemBarsBehavior =
                WindowInsetsControllerCompat
                    .BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }


        setContentView(
            R.layout.activity_menu_usuario
        )


        // -----------------------------------------------------
        // INSETS
        // -----------------------------------------------------

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


        // -----------------------------------------------------
        // INICIALIZAR
        // -----------------------------------------------------

        initComponent()
        initUI()

        cargarExtrasDePrueba()

        cargarDatosDesdeFirebase()
    }


    // =========================================================
    // COMPONENTES
    // =========================================================

    private fun initComponent() {

        rvEntradas =
            findViewById(R.id.rvEntradas)

        rvMenu =
            findViewById(R.id.rvMenu)

        rvCategorias =
            findViewById(R.id.rvCategorias)

        rvExtras =
            findViewById(R.id.rvExtras)

        progressBarMenu =
            findViewById(R.id.progressBarMenu)

        tvCantidadCarrito =
            findViewById(R.id.tvCantidadCarrito)

        btnCarrito =
            findViewById(R.id.btnCarrito)
    }


    // =========================================================
    // UI
    // =========================================================

    private fun initUI() {

        // -----------------------------------------------------
        // ENTRADAS
        // -----------------------------------------------------

        entradasAdapter =
            EntradasUsuarioAdapter(
                entradas
            )

        rvEntradas.layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )

        rvEntradas.adapter =
            entradasAdapter


        // -----------------------------------------------------
        // MENÚ
        // -----------------------------------------------------

        menuAdapter =
            MenuUsuarioAdapter(
                listaMenu
            ) { plato ->

                mostrarAlertaAgregar(
                    plato
                )
            }

        rvMenu.layoutManager =
            LinearLayoutManager(this)

        rvMenu.adapter =
            menuAdapter


        // -----------------------------------------------------
        // EXTRAS
        // -----------------------------------------------------

        extraAdapter =
            ExtraAdapter(
                listaExtras
            ) { extra ->

                agregarExtraAlPedido(
                    extra
                )
            }

        rvExtras.layoutManager =
            LinearLayoutManager(this)

        rvExtras.adapter =
            extraAdapter


        // -----------------------------------------------------
        // CARRITO
        // -----------------------------------------------------

        btnCarrito.setOnClickListener {

            val intent =
                android.content.Intent(
                    this,
                    ActivityPedido::class.java
                )

            startActivity(intent)
        }


        // -----------------------------------------------------
        // CONTADOR
        // -----------------------------------------------------

        actualizarContadorCarrito()
    }


    // =========================================================
    // EXTRAS DE PRUEBA
    // =========================================================

    private fun cargarExtrasDePrueba() {

        todosLosExtras.clear()


        // -----------------------------------------------------
        // GASEOSAS
        // -----------------------------------------------------

        todosLosExtras.add(
            ExtraItem(
                101,
                "Inca Kola",
                4.00,
                1,
                R.drawable.ic_gaseosa
            )
        )

        todosLosExtras.add(
            ExtraItem(
                102,
                "Coca Cola",
                4.00,
                1,
                R.drawable.ic_gaseosa
            )
        )


        // -----------------------------------------------------
        // TORTAS
        // -----------------------------------------------------

        todosLosExtras.add(
            ExtraItem(
                201,
                "Torta de Chocolate",
                8.00,
                2,
                R.drawable.ic_torta
            )
        )

        todosLosExtras.add(
            ExtraItem(
                202,
                "Torta Tres Leches",
                8.00,
                2,
                R.drawable.ic_torta
            )
        )


        // -----------------------------------------------------
        // POSTRES
        // -----------------------------------------------------

        todosLosExtras.add(
            ExtraItem(
                301,
                "Gelatina",
                3.00,
                3,
                R.drawable.ic_postre
            )
        )


        // -----------------------------------------------------
        // BEBIDAS
        // -----------------------------------------------------

        todosLosExtras.add(
            ExtraItem(
                401,
                "Café",
                3.50,
                4,
                R.drawable.ic_bebida
            )
        )
    }


    // =========================================================
    // MOSTRAR / OCULTAR EXTRAS
    // =========================================================

    private fun mostrarExtrasPorCategoria(
        categoriaId: Int
    ) {

        listaExtras.clear()

        listaExtras.addAll(
            todosLosExtras.filter {
                it.categoriaId == categoriaId
            }
        )

        extraAdapter.notifyDataSetChanged()

        rvExtras.visibility =
            if (listaExtras.isEmpty()) {
                View.GONE
            } else {
                View.VISIBLE
            }
    }


    // =========================================================
    // AGREGAR EXTRA
    // =========================================================

    private fun agregarExtraAlPedido(
        extra: ExtraItem
    ) {

        PedidoManager.agregarProducto(

            PedidoItem(
                id = extra.id,
                nombre = extra.nombre,
                cantidad = 1
            )
        )

        actualizarContadorCarrito()

        android.widget.Toast.makeText(
            this,
            "${extra.nombre} agregado al pedido",
            android.widget.Toast.LENGTH_SHORT
        ).show()
    }


    // =========================================================
    // ALERTA DEL MENÚ
    // =========================================================

    private fun mostrarAlertaAgregar(
        plato: TaskMenu
    ) {

        val dialogView =
            layoutInflater.inflate(
                R.layout.dialog_agregar_pedido,
                null
            )


        val tvNombrePlato =
            dialogView.findViewById<TextView>(
                R.id.tvNombrePlato
            )


        val btnCancelar =
            dialogView.findViewById<Button>(
                R.id.btnCancelarPedido
            )


        val btnAgregar =
            dialogView.findViewById<Button>(
                R.id.btnAgregarPedido
            )


        tvNombrePlato.text =
            plato.name


        val dialog =
            AlertDialog.Builder(this)
                .setView(dialogView)
                .create()


        dialog.window?.setBackgroundDrawable(
            android.graphics.Color.TRANSPARENT.toDrawable()
        )


        dialog.show()


        btnCancelar.setOnClickListener {

            dialog.dismiss()
        }


        btnAgregar.setOnClickListener {

            agregarAlPedido(
                plato
            )

            dialog.dismiss()
        }
    }


    // =========================================================
    // AGREGAR MENÚ AL PEDIDO
    // =========================================================

    private fun agregarAlPedido(
        plato: TaskMenu
    ) {

        PedidoManager.agregarProducto(

            PedidoItem(
                id = plato.id,
                nombre = plato.name,
                cantidad = 1
            )
        )


        actualizarContadorCarrito()


        android.widget.Toast.makeText(
            this,
            "${plato.name} agregado al pedido",
            android.widget.Toast.LENGTH_SHORT
        ).show()
    }


    // =========================================================
    // CONTADOR DEL CARRITO
    // =========================================================

    private fun actualizarContadorCarrito() {

        val cantidadTotal =
            PedidoManager.cantidadTotal()


        tvCantidadCarrito.text =
            cantidadTotal.toString()


        tvCantidadCarrito.visibility =
            if (cantidadTotal > 0) {
                View.VISIBLE
            } else {
                View.GONE
            }
    }


    // =========================================================
    // CARGAR DATOS
    // =========================================================

    private fun cargarDatosDesdeFirebase() {

        progressBarMenu.visibility =
            View.VISIBLE

        rvEntradas.visibility =
            View.GONE

        rvMenu.visibility =
            View.GONE

        rvExtras.visibility =
            View.GONE


        // =====================================================
        // CATEGORÍAS
        // =====================================================

        categorias.clear()


        categorias.add(
            CategoriaItem(
                1,
                "Gaseosas",
                R.drawable.ic_gaseosa
            )
        )


        categorias.add(
            CategoriaItem(
                2,
                "Tortas",
                R.drawable.ic_torta
            )
        )


        categorias.add(
            CategoriaItem(
                3,
                "Postres",
                R.drawable.ic_postre
            )
        )


        categorias.add(
            CategoriaItem(
                4,
                "Bebidas",
                R.drawable.ic_bebida
            )
        )


        // =====================================================
        // ADAPTER CATEGORÍAS
        // =====================================================

        categoriaAdapter =
            CategoriaAdapter(
                categorias
            ) { categoria ->

                // ---------------------------------------------
                // SI ES LA MISMA CATEGORÍA
                // ---------------------------------------------

                if (
                    categoriaSeleccionadaId ==
                    categoria.id &&
                    rvExtras.visibility ==
                    View.VISIBLE
                ) {

                    // Ocultar extras

                    rvExtras.visibility =
                        View.GONE

                } else {

                    // -----------------------------------------
                    // NUEVA CATEGORÍA
                    // -----------------------------------------

                    categoriaSeleccionadaId =
                        categoria.id

                    mostrarExtrasPorCategoria(
                        categoria.id
                    )
                }
            }


        // =====================================================
        // RECYCLERVIEW CATEGORÍAS
        // =====================================================

        rvCategorias.layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )


        rvCategorias.adapter =
            categoriaAdapter


        // =====================================================
        // FIREBASE
        // =====================================================

        cargarEntradas()

        cargarMenu()
    }


    // =========================================================
    // ENTRADAS - FIREBASE
    // =========================================================

    private fun cargarEntradas() {

        db.collection("entradas")
            .addSnapshotListener { resultado, error ->

                if (error != null) {

                    Log.e(
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
                        documento
                            .getLong("id")
                            ?.toInt()
                            ?: 0


                    val nombre =
                        documento
                            .getString("nombre")
                            ?: ""


                    val disponible =
                        documento
                            .getBoolean("disponible")
                            ?: true


                    if (nombre.isNotEmpty()) {

                        val entrada =
                            when (id) {

                                1 ->
                                    TaskEntradas.Ceviche(
                                        id,
                                        nombre,
                                        disponible
                                    )

                                2 ->
                                    TaskEntradas.Huancaina(
                                        id,
                                        nombre,
                                        disponible
                                    )

                                else ->
                                    TaskEntradas.Otros(
                                        id,
                                        nombre,
                                        disponible
                                    )
                            }


                        entradas.add(
                            entrada
                        )
                    }
                }


                entradasAdapter.notifyDataSetChanged()


                Log.d(
                    "USUARIO_FIREBASE",
                    "Entradas actualizadas: ${entradas.size}"
                )
            }
    }


    // =========================================================
    // MENÚ - FIREBASE
    // =========================================================

    private fun cargarMenu() {

        db.collection("menu")
            .addSnapshotListener { resultado, error ->

                if (error != null) {

                    Log.e(
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

                    Log.d(
                        "USUARIO_FIREBASE",
                        "Menú: ${documento.id} - ${documento.data}"
                    )


                    val id =
                        documento
                            .getLong("id")
                            ?.toInt()
                            ?: 0


                    val nombre =
                        documento
                            .getString("nombre")
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


                menuAdapter.notifyDataSetChanged()


                mostrarContenido()


                Log.d(
                    "USUARIO_FIREBASE",
                    "Menú actualizado: ${listaMenu.size}"
                )
            }
    }


    // =========================================================
    // MOSTRAR CONTENIDO
    // =========================================================

    private fun mostrarContenido() {

        progressBarMenu.visibility =
            View.GONE

        rvEntradas.visibility =
            View.VISIBLE

        rvMenu.visibility =
            View.VISIBLE
    }


    // =========================================================
    // ACTUALIZAR AL REGRESAR
    // =========================================================

    override fun onResume() {
        super.onResume()

        actualizarContadorCarrito()
    }
}