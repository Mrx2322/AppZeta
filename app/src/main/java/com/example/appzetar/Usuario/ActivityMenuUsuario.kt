package com.example.appzetar.Usuario

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
import android.widget.LinearLayout
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

    // ---------------------------------------------------------
    // FIREBASE
    // ---------------------------------------------------------

    private val db = FirebaseFirestore.getInstance()

    // ---------------------------------------------------------
    // PEDIDO
    // ---------------------------------------------------------

    private val pedido = mutableListOf<PedidoItem>()

    // ---------------------------------------------------------
    // LISTAS
    // ---------------------------------------------------------

    private val entradas = mutableListOf<TaskEntradas>()
    private val listaMenu = mutableListOf<TaskMenu>()

    // ---------------------------------------------------------
    // RECYCLERVIEW ENTRADAS
    // ---------------------------------------------------------

    private lateinit var rvEntradas: RecyclerView
    private lateinit var entradasAdapter: EntradasUsuarioAdapter

    // ---------------------------------------------------------
    // RECYCLERVIEW MENÚ
    // ---------------------------------------------------------

    private lateinit var rvMenu: RecyclerView
    private lateinit var menuAdapter: MenuUsuarioAdapter

    // ---------------------------------------------------------
    // OTROS COMPONENTES
    // ---------------------------------------------------------

    private lateinit var progressBarMenu: ProgressBar
    private lateinit var tvCantidadCarrito: TextView
    private lateinit var btnCarrito: FloatingActionButton


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

            hide(WindowInsetsCompat.Type.statusBars())

            systemBarsBehavior =
                WindowInsetsControllerCompat
                    .BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        setContentView(R.layout.activity_menu_usuario)

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

        // -----------------------------------------------------
        // CARGAR FIREBASE
        // -----------------------------------------------------

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
            EntradasUsuarioAdapter(entradas)

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
            MenuUsuarioAdapter(listaMenu) { plato ->

                mostrarAlertaAgregar(plato)
            }

        rvMenu.layoutManager =
            LinearLayoutManager(this)

        rvMenu.adapter =
            menuAdapter


        // -----------------------------------------------------
        // BOTÓN CARRITO
        // -----------------------------------------------------

        btnCarrito.setOnClickListener {

            mostrarDialogoPedido()
        }


        // -----------------------------------------------------
        // ESTADO INICIAL DEL CONTADOR
        // -----------------------------------------------------

        actualizarContadorCarrito()
    }


    // =========================================================
    // ALERTA AGREGAR AL PEDIDO
    // =========================================================

    private fun mostrarAlertaAgregar(plato: TaskMenu) {

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

        tvNombrePlato.text = plato.name

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

            agregarAlPedido(plato)

            dialog.dismiss()
        }
    }


    // =========================================================
    // AGREGAR AL PEDIDO
    // =========================================================

    private fun agregarAlPedido(plato: TaskMenu) {

        val existente =
            pedido.find {
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

        actualizarContadorCarrito()

        Log.d(
            "PEDIDO",
            "Plato agregado: ${plato.name}"
        )

        Toast.makeText(
            this,
            "${plato.name} agregado al pedido",
            Toast.LENGTH_SHORT
        ).show()
    }


    // =========================================================
    // ACTUALIZAR CONTADOR CARRITO
    // =========================================================

    private fun actualizarContadorCarrito() {

        val cantidadTotal =
            pedido.sumOf {
                it.cantidad
            }

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
    // MOSTRAR PEDIDO
    // =========================================================

    private fun mostrarDialogoPedido() {

        if (pedido.isEmpty()) {

            AlertDialog.Builder(this)
                .setTitle("Tu pedido está vacío")
                .setMessage(
                    "Agrega algunos platos del menú para comenzar tu pedido."
                )
                .setPositiveButton(
                    "Ver menú",
                    null
                )
                .show()

            return
        }

        val dialogView =
            layoutInflater.inflate(
                R.layout.dialog_pedido,
                null
            )

        val tvCantidadProductos =
            dialogView.findViewById<TextView>(
                R.id.tvCantidadProductos
            )

        val contenedorPedido =
            dialogView.findViewById<LinearLayout>(
                R.id.contenedorPedido
            )

        val tvTotalProductos =
            dialogView.findViewById<TextView>(
                R.id.tvTotalProductos
            )

        val btnContinuar =
            dialogView.findViewById<Button>(
                R.id.btnContinuarPedido
            )

        val btnSeguirComprando =
            dialogView.findViewById<TextView>(
                R.id.btnSeguirComprando
            )


        val dialog =
            AlertDialog.Builder(this)
                .setView(dialogView)
                .create()

        dialog.window?.setBackgroundDrawable(
            android.graphics.Color.TRANSPARENT.toDrawable()
        )

        dialog.show()


        // ---------------------------------------------------------
        // ACTUALIZAR RESUMEN
        // ---------------------------------------------------------

        fun actualizarResumen() {

            val cantidadTotal =
                pedido.sumOf {
                    it.cantidad
                }

            tvCantidadProductos.text =
                if (cantidadTotal == 1) {
                    "1 producto"
                } else {
                    "$cantidadTotal productos"
                }

            tvTotalProductos.text =
                "$cantidadTotal productos"
        }


        // ---------------------------------------------------------
        // DIBUJAR PRODUCTOS
        // ---------------------------------------------------------

        fun mostrarProductos() {

            contenedorPedido.removeAllViews()

            for (item in pedido) {

                val itemView =
                    layoutInflater.inflate(
                        R.layout.item_dialog_pedido,
                        contenedorPedido,
                        false
                    )

                val tvNombre =
                    itemView.findViewById<TextView>(
                        R.id.tvNombreProducto
                    )

                val tvCantidad =
                    itemView.findViewById<TextView>(
                        R.id.tvCantidadProducto
                    )

                val tvCantidadNumero =
                    itemView.findViewById<TextView>(
                        R.id.tvCantidad
                    )

                val btnRestar =
                    itemView.findViewById<TextView>(
                        R.id.btnRestar
                    )

                val btnSumar =
                    itemView.findViewById<TextView>(
                        R.id.btnSumar
                    )


                tvNombre.text =
                    item.nombre

                tvCantidad.text =
                    "Cantidad: ${item.cantidad}"

                tvCantidadNumero.text =
                    item.cantidad.toString()


                // -------------------------------------------------
                // RESTAR
                // -------------------------------------------------

                btnRestar.setOnClickListener {

                    if (item.cantidad > 1) {

                        item.cantidad--

                    } else {

                        pedido.remove(item)
                    }

                    actualizarContadorCarrito()

                    if (pedido.isEmpty()) {

                        dialog.dismiss()

                    } else {

                        mostrarProductos()
                        actualizarResumen()
                    }
                }


                // -------------------------------------------------
                // SUMAR
                // -------------------------------------------------

                btnSumar.setOnClickListener {

                    item.cantidad++

                    actualizarContadorCarrito()

                    mostrarProductos()
                    actualizarResumen()
                }


                contenedorPedido.addView(itemView)
            }
        }


        // ---------------------------------------------------------
        // CARGAR PRODUCTOS
        // ---------------------------------------------------------

        mostrarProductos()
        actualizarResumen()


        // ---------------------------------------------------------
        // SEGUIR COMPRANDO
        // ---------------------------------------------------------

        btnSeguirComprando.setOnClickListener {

            dialog.dismiss()
        }


        // ---------------------------------------------------------
        // CONTINUAR PEDIDO
        // ---------------------------------------------------------

        btnContinuar.setOnClickListener {

            Toast.makeText(
                this,
                "Próximamente: confirmar pedido",
                Toast.LENGTH_SHORT
            ).show()

            dialog.dismiss()
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


                // -------------------------------------------------
                // LEER ENTRADAS
                // -------------------------------------------------

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

                        entradas.add(entrada)
                    }
                }


                // -------------------------------------------------
                // ACTUALIZAR UI
                // -------------------------------------------------

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


                // -------------------------------------------------
                // LEER MENÚ
                // -------------------------------------------------

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


                // -------------------------------------------------
                // ACTUALIZAR UI
                // -------------------------------------------------

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
}