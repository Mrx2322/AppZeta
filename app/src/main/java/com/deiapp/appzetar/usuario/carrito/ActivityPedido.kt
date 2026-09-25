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
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deiapp.appzeta.R
import com.deiapp.appzetar.usuario.entregas.ActivityEntrega
import com.deiapp.appzetar.usuario.extras.ActivityExtras
import com.deiapp.appzetar.usuario.ActivityMenuUsuario
import com.deiapp.appzetar.usuario.pedidos.ActivityPedidosUsuario
import com.deiapp.appzetar.usuario.perfilusuario.ActivityPerfilUsuario
import com.deiapp.appzetar.usuario.navegacionUsuario.NavegacionUsuario
import com.google.android.material.button.MaterialButton

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
            NavegacionUsuario.abrir(this, ActivityMenuUsuario::class.java)
        }

        navExtras.setOnClickListener {
            NavegacionUsuario.abrir(this, ActivityExtras::class.java)
        }

        navPedidos.setOnClickListener {
            NavegacionUsuario.abrir(this, ActivityPedidosUsuario::class.java)
        }

        navCarrito.setOnClickListener {
            // Ya estás en Carrito.
        }

        navPerfil.setOnClickListener {
            NavegacionUsuario.abrir(this, ActivityPerfilUsuario::class.java)
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
            opcion.stateListAnimator = crearAnimadorDePresion(opcion)
        }
    }

    private fun crearAnimadorDePresion(vista: View): StateListAnimator {
        val animacionPresionada = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(vista, View.SCALE_X, 1.12f),
                ObjectAnimator.ofFloat(vista, View.SCALE_Y, 1.12f),
                ObjectAnimator.ofFloat(vista, View.TRANSLATION_Y, -9f)
            )
            duration = 150
            interpolator = DecelerateInterpolator()
        }

        val animacionNormal = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(vista, View.SCALE_X, 1f),
                ObjectAnimator.ofFloat(vista, View.SCALE_Y, 1f),
                ObjectAnimator.ofFloat(vista, View.TRANSLATION_Y, 0f)
            )
            duration = 180
            interpolator = DecelerateInterpolator()
        }

        return StateListAnimator().apply {
            addState(
                intArrayOf(android.R.attr.state_pressed),
                animacionPresionada
            )
            addState(intArrayOf(), animacionNormal)
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

    private fun aumentarProducto(item: PedidoItem) {
        val posicion = PedidoManager.pedido.indexOf(item)

        PedidoManager.aumentarCantidad(item)

        if (posicion >= 0) {
            pedidoAdapter.notifyItemChanged(posicion)
        }

        actualizarResumenPedido()
    }

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

    private fun eliminarProducto(item: PedidoItem) {
        val posicion = PedidoManager.pedido.indexOf(item)

        PedidoManager.eliminarProducto(item)

        if (posicion >= 0) {
            pedidoAdapter.notifyItemRemoved(posicion)
        }

        actualizarResumenPedido()
    }

    private fun continuarCompra() {
        if (PedidoManager.pedido.isEmpty()) {
            return
        }

        startActivity(
            Intent(this, ActivityEntrega::class.java)
        )
    }

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