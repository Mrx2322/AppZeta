package com.deiapp.appzetar.usuario.carrito

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deiapp.appzeta.R
import com.deiapp.appzetar.usuario.ActivityMenuUsuario
import com.deiapp.appzetar.usuario.entregas.ActivityEntrega
import com.deiapp.appzetar.usuario.extras.ActivityExtras
import com.deiapp.appzetar.usuario.navegacionUsuario.NavegacionUsuario
import com.deiapp.appzetar.usuario.pedidos.ActivityPedidosUsuario
import com.deiapp.appzetar.usuario.perfilusuario.ActivityPerfilUsuario
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore

class ActivityPedido : AppCompatActivity() {

    private lateinit var rvPedido: RecyclerView
    private lateinit var tvTotalProductos: TextView
    private lateinit var tvTotalPedido: TextView
    private lateinit var tvCantidadCarrito: TextView
    private lateinit var tvMensajeVacio: View
    private lateinit var btnContinuar: MaterialButton

    private lateinit var navInicio: LinearLayout
    private lateinit var navExtras: LinearLayout
    private lateinit var navPedidos: LinearLayout
    private lateinit var navCarrito: LinearLayout
    private lateinit var navPerfil: LinearLayout

    private lateinit var pedidoAdapter: PedidoAdapter

    private val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    // Evita varias verificaciones simultáneas del mismo producto.
    private val productosVerificando = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(
            window,
            false
        )

        setContentView(R.layout.activity_pedido)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { view, insets ->

            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        initComponent()
        initUI()
        actualizarResumenPedido()
    }

    private fun initComponent() {
        rvPedido = findViewById(R.id.rvPedido)
        tvTotalProductos = findViewById(R.id.tvTotalProductos)
        tvTotalPedido = findViewById(R.id.tvTotalPedido)
        tvCantidadCarrito = findViewById(R.id.tvCantidadCarrito)
        tvMensajeVacio = findViewById(R.id.tvMensajeVacio)
        btnContinuar = findViewById(R.id.btnContinuar)

        navInicio = findViewById(R.id.navInicio)
        navExtras = findViewById(R.id.navExtras)
        navPedidos = findViewById(R.id.navPedidos)
        navCarrito = findViewById(R.id.navCarrito)
        navPerfil = findViewById(R.id.navPerfil)
    }

    private fun initUI() {
        pedidoAdapter = PedidoAdapter(
            PedidoManager.pedido,

            onAumentar = { item ->
                aumentarProducto(item)
            },

            onDisminuir = { item ->
                disminuirProducto(item)
            },

            onEliminar = { item ->
                eliminarProducto(item)
            }
        )

        rvPedido.layoutManager = LinearLayoutManager(this)
        rvPedido.adapter = pedidoAdapter

        btnContinuar.setOnClickListener {
            continuarCompra()
        }

        configurarNavegacion()
        configurarAnimacionesBarra()
        marcarCarritoActivo()
    }

    private fun configurarNavegacion() {

        navInicio.setOnClickListener {
            NavegacionUsuario.abrir(
                this,
                ActivityMenuUsuario::class.java
            )
        }

        navExtras.setOnClickListener {
            NavegacionUsuario.abrir(
                this,
                ActivityExtras::class.java
            )
        }

        navPedidos.setOnClickListener {
            NavegacionUsuario.abrir(
                this,
                ActivityPedidosUsuario::class.java
            )
        }

        navCarrito.setOnClickListener {
            // Ya estás en Carrito.
        }

        navPerfil.setOnClickListener {
            NavegacionUsuario.abrir(
                this,
                ActivityPerfilUsuario::class.java
            )
        }
    }

    private fun configurarAnimacionesBarra() {
        val opciones = listOf(
            navInicio,
            navExtras,
            navPedidos,
            navCarrito,
            navPerfil
        )

        opciones.forEach { opcion ->
            opcion.stateListAnimator =
                crearAnimadorDePresion(opcion)
        }
    }

    private fun crearAnimadorDePresion(
        vista: View
    ): StateListAnimator {

        val animacionPresionada = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(
                    vista,
                    View.SCALE_X,
                    1.12f
                ),
                ObjectAnimator.ofFloat(
                    vista,
                    View.SCALE_Y,
                    1.12f
                ),
                ObjectAnimator.ofFloat(
                    vista,
                    View.TRANSLATION_Y,
                    -9f
                )
            )

            duration = 150
            interpolator = DecelerateInterpolator()
        }

        val animacionNormal = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(
                    vista,
                    View.SCALE_X,
                    1f
                ),
                ObjectAnimator.ofFloat(
                    vista,
                    View.SCALE_Y,
                    1f
                ),
                ObjectAnimator.ofFloat(
                    vista,
                    View.TRANSLATION_Y,
                    0f
                )
            )

            duration = 180
            interpolator = DecelerateInterpolator()
        }

        return StateListAnimator().apply {
            addState(
                intArrayOf(android.R.attr.state_pressed),
                animacionPresionada
            )

            addState(
                intArrayOf(),
                animacionNormal
            )
        }
    }

    private fun marcarCarritoActivo() {
        navCarrito.post {
            navCarrito.animate()
                .scaleX(1.08f)
                .scaleY(1.08f)
                .translationY(-5f)
                .setDuration(280)
                .setInterpolator(DecelerateInterpolator())
                .start()
        }
    }

    // =========================================================
    // AUMENTAR PRODUCTOS CON VALIDACIÓN DE STOCK
    // =========================================================

    private fun aumentarProducto(item: PedidoItem) {

        val clave = "${item.tipo.name}_${item.id}"

        // Evita consultas simultáneas por pulsaciones rápidas.
        if (!productosVerificando.add(clave)) {
            return
        }

        when (item.tipo) {

            TipoPedido.MENU -> {
                comprobarStockProducto(
                    coleccion = "menu",
                    id = item.id,
                    nombre = item.nombre,
                    cantidadActual = {
                        PedidoManager.cantidadMenuEnPedido(item.id)
                    },
                    cantidadAdicional = 1,

                    alValidar = {
                        // Válida las entradas asociadas al menú
                        // antes de aumentar su cantidad.
                        validarEntradasDelMenu(
                            item = item,
                            indice = 0,

                            alValidar = {
                                agregarCantidadVerificada(item)
                                productosVerificando.remove(clave)
                            },

                            alFinalizar = {
                                productosVerificando.remove(clave)
                            }
                        )
                    },

                    alFinalizar = {
                        productosVerificando.remove(clave)
                    }
                )
            }

            TipoPedido.CENA -> {
                comprobarStockProducto(
                    coleccion = "cenas",
                    id = item.id,
                    nombre = item.nombre,
                    cantidadActual = {
                        PedidoManager.cantidadCenaEnPedido(item.id)
                    },
                    cantidadAdicional = 1,

                    alValidar = {
                        agregarCantidadVerificada(item)
                        productosVerificando.remove(clave)
                    },

                    alFinalizar = {
                        productosVerificando.remove(clave)
                    }
                )
            }

            TipoPedido.ENTRADA -> {
                comprobarStockProducto(
                    coleccion = "entradas",
                    id = item.id,
                    nombre = item.nombre,
                    cantidadActual = {
                        PedidoManager.cantidadEntradaEnPedido(item.id)
                    },
                    cantidadAdicional = 1,

                    alValidar = {
                        agregarCantidadVerificada(item)
                        productosVerificando.remove(clave)
                    },

                    alFinalizar = {
                        productosVerificando.remove(clave)
                    }
                )
            }

            TipoPedido.EXTRA -> {
                productosVerificando.remove(clave)

                Toast.makeText(
                    this,
                    "Los extras todavía no tienen control de stock.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // =========================================================
    // COMPROBAR STOCK EN FIRESTORE
    // =========================================================

    /**
     * Consulta el stock del producto.
     *
     * Esta función solamente lee Firestore.
     * No descuenta ni modifica el stock.
     */
    private fun comprobarStockProducto(
        coleccion: String,
        id: Int,
        nombre: String,
        cantidadActual: () -> Int,
        cantidadAdicional: Int,
        alValidar: () -> Unit,
        alFinalizar: () -> Unit
    ) {

        db.collection(coleccion)
            .whereEqualTo("id", id)
            .limit(1)
            .get()
            .addOnSuccessListener { resultado ->

                val documento = resultado.documents.firstOrNull()

                if (documento == null) {
                    Toast.makeText(
                        this,
                        "No se encontró $nombre en el inventario.",
                        Toast.LENGTH_LONG
                    ).show()

                    alFinalizar()
                    return@addOnSuccessListener
                }

                val stock = documento.getLong("stock")

                if (stock == null || stock < 0L) {
                    Toast.makeText(
                        this,
                        "No se pudo comprobar el stock de $nombre.",
                        Toast.LENGTH_LONG
                    ).show()

                    alFinalizar()
                    return@addOnSuccessListener
                }

                // Obtiene la cantidad actual del carrito
                // cuando ya llegó la respuesta de Firestore.
                val cantidadEnCarrito = cantidadActual()
                val cantidadSolicitada =
                    cantidadEnCarrito + cantidadAdicional

                if (cantidadSolicitada.toLong() > stock) {
                    Toast.makeText(
                        this,
                        "Stock insuficiente para $nombre. " +
                                "Disponibles: $stock.",
                        Toast.LENGTH_LONG
                    ).show()

                    alFinalizar()
                    return@addOnSuccessListener
                }

                // El callback decide cómo continuar.
                alValidar()
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "No se pudo verificar el inventario. " +
                            "Inténtalo nuevamente.",
                    Toast.LENGTH_LONG
                ).show()

                alFinalizar()
            }
    }

    // =========================================================
    // VALIDAR ENTRADAS ASOCIADAS A UN MENÚ
    // =========================================================

    /**
     * Antes de aumentar un menú, verifica también el stock
     * de sus entradas asociadas.
     */
    private fun validarEntradasDelMenu(
        item: PedidoItem,
        indice: Int,
        alValidar: () -> Unit,
        alFinalizar: () -> Unit
    ) {

        if (indice >= item.entradas.size) {
            alValidar()
            return
        }

        val entrada = item.entradas[indice]

        comprobarStockProducto(
            coleccion = "entradas",
            id = entrada.id,
            nombre = entrada.nombre,
            cantidadActual = {
                PedidoManager.cantidadEntradaEnPedido(entrada.id)
            },
            cantidadAdicional = entrada.cantidad,

            alValidar = {
                validarEntradasDelMenu(
                    item = item,
                    indice = indice + 1,
                    alValidar = alValidar,
                    alFinalizar = alFinalizar
                )
            },

            alFinalizar = alFinalizar
        )
    }

    // =========================================================
    // AUMENTAR CANTIDAD DESPUÉS DE VALIDAR EL STOCK
    // =========================================================

    private fun agregarCantidadVerificada(
        item: PedidoItem
    ) {

        val posicion = PedidoManager.pedido.indexOf(item)

        // El producto pudo eliminarse mientras se consultaba
        // el inventario.
        if (posicion < 0) {
            return
        }

        PedidoManager.aumentarCantidad(item)

        pedidoAdapter.notifyItemChanged(posicion)
        actualizarResumenPedido()
    }

    // =========================================================
    // DISMINUIR PRODUCTOS
    // =========================================================

    private fun disminuirProducto(item: PedidoItem) {

        val posicion = PedidoManager.pedido.indexOf(item)
        val cantidadItemsAnterior = PedidoManager.pedido.size

        PedidoManager.disminuirCantidad(item)

        if (posicion >= 0) {
            if (PedidoManager.pedido.size < cantidadItemsAnterior) {
                pedidoAdapter.notifyItemRemoved(posicion)
            } else {
                pedidoAdapter.notifyItemChanged(posicion)
            }
        }

        actualizarResumenPedido()
    }

    // =========================================================
    // ELIMINAR PRODUCTOS
    // =========================================================

    private fun eliminarProducto(item: PedidoItem) {

        val posicion = PedidoManager.pedido.indexOf(item)

        PedidoManager.eliminarProducto(item)

        if (posicion >= 0) {
            pedidoAdapter.notifyItemRemoved(posicion)
        }

        actualizarResumenPedido()
    }

    // =========================================================
    // CONTINUAR COMPRA
    // =========================================================

    private fun continuarCompra() {

        if (PedidoManager.pedido.isEmpty()) {
            Toast.makeText(
                this,
                "Tu carrito está vacío.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        startActivity(
            Intent(
                this,
                ActivityEntrega::class.java
            )
        )
    }

    // =========================================================
    // ACTUALIZAR RESUMEN DEL CARRITO
    // =========================================================

    private fun actualizarResumenPedido() {

        val cantidad = PedidoManager.cantidadTotal()

        tvTotalProductos.text = resources.getQuantityString(
            R.plurals.pedido_cantidad_productos,
            cantidad,
            cantidad
        )

        tvCantidadCarrito.text = cantidad.toString()
        tvCantidadCarrito.isVisible = cantidad > 0

        val total = PedidoManager.totalPedido()

        tvTotalPedido.text = getString(
            R.string.pedido_total_formato,
            total
        )

        val pedidoVacio = PedidoManager.pedido.isEmpty()

        rvPedido.isVisible = !pedidoVacio
        tvMensajeVacio.isVisible = pedidoVacio

        btnContinuar.isEnabled = !pedidoVacio
        btnContinuar.alpha = if (pedidoVacio) 0.5f else 1f
    }

    override fun onResume() {
        super.onResume()
        actualizarResumenPedido()
    }
}