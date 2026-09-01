package com.example.appzetar.Usuario

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ActivityMenuUsuario : AppCompatActivity() {

    // =========================================================
    // FIREBASE
    // =========================================================

    private val db =
        FirebaseFirestore.getInstance()

    private val auth =
        FirebaseAuth.getInstance()


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
    // ENTRADAS
    // =========================================================

    private val entradas =
        mutableListOf<TaskEntradas>()

    private lateinit var rvEntradas: RecyclerView

    private lateinit var entradasAdapter: EntradasUsuarioAdapter


    // =========================================================
    // MENÚ
    // =========================================================

    private val listaMenu =
        mutableListOf<TaskMenu>()

    private lateinit var rvMenu: RecyclerView

    private lateinit var menuAdapter: MenuUsuarioAdapter


    // =========================================================
    // OTROS
    // =========================================================

    private lateinit var progressBarMenu: ProgressBar

    private lateinit var tvCantidadCarrito: TextView

    private lateinit var btnCarrito: FloatingActionButton

    private lateinit var tvSaludo: TextView


    // =========================================================
    // ON CREATE
    // =========================================================

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
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

            hide(
                WindowInsetsCompat.Type.statusBars()
            )

            systemBarsBehavior =
                WindowInsetsControllerCompat
                    .BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }


        // =====================================================
        // LAYOUT
        // =====================================================

        setContentView(
            R.layout.activity_menu_usuario
        )


        // =====================================================
        // INSETS
        // =====================================================

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


        // =====================================================
        // INICIALIZAR
        // =====================================================

        initComponent()

        initUI()

        cargarNombreUsuario()

        cargarCategorias()

        cargarDatosDesdeFirebase()
    }


    // =========================================================
    // COMPONENTES
    // =========================================================

    private fun initComponent() {

        rvCategorias =
            findViewById(R.id.rvCategorias)

        rvEntradas =
            findViewById(R.id.rvEntradas)

        rvExtras =
            findViewById(R.id.rvExtras)

        rvMenu =
            findViewById(R.id.rvMenu)

        progressBarMenu =
            findViewById(R.id.progressBarMenu)

        tvCantidadCarrito =
            findViewById(R.id.tvCantidadCarrito)

        btnCarrito =
            findViewById(R.id.btnCarrito)

        tvSaludo =
            findViewById(R.id.tvSaludo)
    }


    // =========================================================
    // CARGAR NOMBRE DEL USUARIO
    // =========================================================

    private fun cargarNombreUsuario() {

        val usuarioActual =
            auth.currentUser

        if (usuarioActual == null) {

            tvSaludo.text =
                "¡Hola!"

            return
        }


        val uid =
            usuarioActual.uid


        db.collection("usuarios")
            .document(uid)
            .get()
            .addOnSuccessListener { documento ->

                if (documento.exists()) {

                    val nombre =
                        documento.getString("nombre")


                    if (!nombre.isNullOrEmpty()) {

                        tvSaludo.text =
                            "¡Hola, $nombre!"

                    } else {

                        tvSaludo.text =
                            "¡Hola!"
                    }

                } else {

                    tvSaludo.text =
                        "¡Hola!"
                }
            }
            .addOnFailureListener { error ->

                Log.e(
                    "USUARIO_FIREBASE",
                    "Error obteniendo nombre del usuario",
                    error
                )

                tvSaludo.text =
                    "¡Hola!"
            }
    }


    // =========================================================
    // UI
    // =========================================================

    private fun initUI() {

        // =====================================================
        // ENTRADAS
        // =====================================================

        entradasAdapter =
            EntradasUsuarioAdapter(
                entradas
            ) { entrada ->

                agregarEntradaAlPedido(
                    entrada
                )
            }

        rvEntradas.layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )

        rvEntradas.adapter =
            entradasAdapter


        // =====================================================
        // MENÚ
        // =====================================================

        menuAdapter =
            MenuUsuarioAdapter(
                listaMenu
            ) { plato ->

                mostrarAlertaAgregar(
                    plato
                )
            }

        // =====================================================
        // CARRUSEL HORIZONTAL DEL MENÚ
        // =====================================================

        rvMenu.layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )

        rvMenu.adapter =
            menuAdapter


        // =====================================================
        // EXTRAS
        // =====================================================

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


        // =====================================================
        // CARRITO
        // =====================================================

        btnCarrito.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    ActivityPedido::class.java
                )
            )
        }


        actualizarContadorCarrito()
    }


    // =========================================================
    // CATEGORÍAS
    // =========================================================

    private fun cargarCategorias() {

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


        categoriaAdapter =
            CategoriaAdapter(
                categorias
            ) { categoria ->

                if (
                    categoriaSeleccionadaId ==
                    categoria.id &&
                    rvExtras.visibility ==
                    View.VISIBLE
                ) {

                    rvExtras.visibility =
                        View.GONE

                } else {

                    categoriaSeleccionadaId =
                        categoria.id

                    mostrarExtrasPorCategoria(
                        categoria.id
                    )
                }
            }


        rvCategorias.layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )

        rvCategorias.adapter =
            categoriaAdapter
    }


    // =========================================================
    // MOSTRAR EXTRAS POR CATEGORÍA
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


        Log.d(
            "USUARIO_FIREBASE",
            "Categoría $categoriaId: ${listaExtras.size} extras"
        )
    }


    // =========================================================
    // AGREGAR EXTRA AL PEDIDO
    // =========================================================

    private fun agregarExtraAlPedido(
        extra: ExtraItem
    ) {

        PedidoManager.agregarProducto(

            PedidoItem(
                id = extra.id,
                nombre = extra.nombre,
                precio = extra.precio,
                cantidad = 1,
                tipo = TipoPedido.EXTRA
            )
        )

        actualizarContadorCarrito()

        Toast.makeText(
            this,
            "${extra.nombre} agregado al pedido",
            Toast.LENGTH_SHORT
        ).show()
    }


    // =========================================================
    // AGREGAR ENTRADA AL PEDIDO
    // =========================================================

    private fun agregarEntradaAlPedido(
        entrada: TaskEntradas
    ) {

        PedidoManager.agregarProducto(

            PedidoItem(
                id = entrada.id,
                nombre = entrada.nombre,
                precio = 0.0,
                cantidad = 1,
                tipo = TipoPedido.ENTRADA
            )
        )

        actualizarContadorCarrito()

        Toast.makeText(
            this,
            "${entrada.nombre} agregado al pedido",
            Toast.LENGTH_SHORT
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

        dialog.show()

        dialog.window?.setBackgroundDrawable(
            android.graphics.Color.TRANSPARENT.toDrawable()
        )


        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }


        btnAgregar.setOnClickListener {

            agregarAlPedido(plato)

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
                precio = plato.precio,
                cantidad = 1,
                tipo = TipoPedido.MENU
            )
        )

        actualizarContadorCarrito()

        Toast.makeText(
            this,
            "${plato.name} agregado al pedido",
            Toast.LENGTH_SHORT
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


        cargarEntradas()

        cargarMenu()

        cargarExtrasDesdeFirebase()
    }


    // =========================================================
    // EXTRAS - FIREBASE
    // =========================================================

    private fun cargarExtrasDesdeFirebase() {

        db.collection("extras")
            .addSnapshotListener { resultado, error ->

                if (error != null) {

                    Log.e(
                        "USUARIO_FIREBASE",
                        "Error escuchando extras",
                        error
                    )

                    return@addSnapshotListener
                }

                if (resultado == null) {
                    return@addSnapshotListener
                }


                todosLosExtras.clear()


                for (documento in resultado) {

                    Log.d(
                        "USUARIO_FIREBASE",
                        "Extra Firebase: ${documento.id} - ${documento.data}"
                    )


                    val id =
                        documento
                            .getLong("id")
                            ?.toInt()
                            ?: documento.id.toIntOrNull()
                            ?: 0


                    val nombre =
                        documento
                            .getString("nombre")
                            ?: ""


                    val precio =
                        documento
                            .getDouble("precio")
                            ?: 0.0


                    val categoriaId =
                        documento
                            .getLong("categoriaId")
                            ?.toInt()
                            ?: 0


                    // =================================================
                    // ICONO SEGÚN CATEGORÍA
                    // =================================================

                    val icono =
                        when (categoriaId) {

                            1 ->
                                R.drawable.ic_gaseosa

                            2 ->
                                R.drawable.ic_torta

                            3 ->
                                R.drawable.ic_postre

                            4 ->
                                R.drawable.ic_bebida

                            else ->
                                R.drawable.ic_bebida
                        }


                    // =================================================
                    // AGREGAR EXTRA
                    // =================================================

                    if (
                        id > 0 &&
                        nombre.isNotEmpty() &&
                        categoriaId > 0
                    ) {

                        todosLosExtras.add(

                            ExtraItem(
                                id = id,
                                nombre = nombre,
                                precio = precio,
                                categoriaId = categoriaId,
                                icono = icono
                            )
                        )
                    }
                }


                // =================================================
                // ACTUALIZAR CATEGORÍA
                // =================================================

                if (
                    categoriaSeleccionadaId != 0
                ) {

                    mostrarExtrasPorCategoria(
                        categoriaSeleccionadaId
                    )
                }


                Log.d(
                    "USUARIO_FIREBASE",
                    "Extras actualizados: ${todosLosExtras.size}"
                )
            }
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
                            ?: documento.id.toIntOrNull()
                            ?: 0

                    val nombre =
                        documento
                            .getString("nombre")
                            ?: ""

                    val disponible =
                        documento
                            .getBoolean("disponible")
                            ?: true


                    if (
                        id > 0 &&
                        nombre.isNotEmpty()
                    ) {

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

                    val id =
                        documento
                            .getLong("id")
                            ?.toInt()
                            ?: documento.id.toIntOrNull()
                            ?: 0

                    val nombre =
                        documento
                            .getString("nombre")
                            ?: ""

                    val precio =
                        documento
                            .getDouble("precio")
                            ?: 0.0


                    if (
                        id > 0 &&
                        nombre.isNotEmpty()
                    ) {

                        listaMenu.add(

                            TaskMenu(
                                id = id,
                                name = nombre,
                                precio = precio
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
    // AL REGRESAR
    // =========================================================

    override fun onResume() {
        super.onResume()

        actualizarContadorCarrito()
    }
}