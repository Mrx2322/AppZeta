package com.deiapp.appzetar.usuario.pedidos

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deiapp.appzeta.R
import com.deiapp.appzetar.usuario.ActivityMenuUsuario
import com.deiapp.appzetar.usuario.carrito.ActivityPedido
import com.deiapp.appzetar.usuario.carrito.PedidoManager
import com.deiapp.appzetar.usuario.extras.ActivityExtras
import com.deiapp.appzetar.usuario.navegacionUsuario.NavegacionUsuario
import com.deiapp.appzetar.usuario.perfilusuario.ActivityPerfilUsuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class ActivityPedidosUsuario : AppCompatActivity() {

    // =========================================================
    // FIREBASE
    // =========================================================

    private val db =
        FirebaseFirestore.getInstance()

    private val auth =
        FirebaseAuth.getInstance()

    private var pedidosListener:
            ListenerRegistration? = null

    // =========================================================
    // COMPONENTES
    // =========================================================

    private lateinit var rvPedidos: RecyclerView

    private lateinit var progressBarPedidos:
            ProgressBar

    private lateinit var layoutSinPedidos:
            LinearLayout

    private lateinit var tvCantidadCarrito:
            TextView

    // =========================================================
    // NAVEGACIÓN
    // =========================================================

    private lateinit var navInicio:
            LinearLayout

    private lateinit var navExtras:
            LinearLayout

    private lateinit var navPedidos:
            LinearLayout

    private lateinit var navCarrito:
            LinearLayout

    private lateinit var navPerfil:
            LinearLayout

    // =========================================================
    // ADAPTER
    // =========================================================

    private lateinit var pedidoAdapter:
            PedidoUsuarioAdapter

    private val listaPedidos =
        mutableListOf<PedidoUsuarioItem>()

    // =========================================================
    // CONTROL TIEMPO
    // =========================================================

    private val handler =
        Handler(
            Looper.getMainLooper()
        )

    private val intervaloRevision =
        60_000L

    private val revisarPedidosRunnable =
        object : Runnable {

            override fun run() {

                revisarPedidosVisibles()

                handler.postDelayed(
                    this,
                    intervaloRevision
                )
            }
        }

    // =========================================================
    // ON CREATE
    // =========================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(
            window,
            false
        )

        setContentView(
            R.layout.activity_pedidos_usuario
        )

        configurarInsets()

        inicializarComponentes()
        configurarRecyclerView()
        configurarNavegacion()
        configurarAnimacionesBarra()
        marcarPedidosActivo()
        actualizarContadorCarrito()
    }

    // =========================================================
    // INSETS
    // =========================================================

    private fun configurarInsets() {

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { view, insets ->

            val systemBars =
                insets.getInsets(
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
    }

    // =========================================================
    // ON RESUME
    // =========================================================

    override fun onResume() {
        super.onResume()

        actualizarContadorCarrito()
    }

    // =========================================================
    // COMPONENTES
    // =========================================================

    private fun inicializarComponentes() {

        rvPedidos =
            findViewById(
                R.id.rvPedidos
            )

        progressBarPedidos =
            findViewById(
                R.id.progressBarPedidos
            )

        layoutSinPedidos =
            findViewById(
                R.id.layoutSinPedidos
            )

        tvCantidadCarrito =
            findViewById(
                R.id.tvCantidadCarrito
            )

        navInicio =
            findViewById(
                R.id.navInicio
            )

        navExtras =
            findViewById(
                R.id.navExtras
            )

        navPedidos =
            findViewById(
                R.id.navPedidos
            )

        navCarrito =
            findViewById(
                R.id.navCarrito
            )

        navPerfil =
            findViewById(
                R.id.navPerfil
            )
    }

    // =========================================================
    // RECYCLER VIEW
    // =========================================================

    private fun configurarRecyclerView() {

        pedidoAdapter =
            PedidoUsuarioAdapter()

        rvPedidos.apply {

            layoutManager =
                LinearLayoutManager(
                    this@ActivityPedidosUsuario
                )

            adapter =
                pedidoAdapter

            setHasFixedSize(
                false
            )

            itemAnimator =
                null
        }
    }

    // =========================================================
    // NAVEGACIÓN
    // =========================================================

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
            // Ya estás en Pedidos.
        }

        navCarrito.setOnClickListener {

            NavegacionUsuario.abrir(
                this,
                ActivityPedido::class.java
            )
        }

        navPerfil.setOnClickListener {

            NavegacionUsuario.abrir(
                this,
                ActivityPerfilUsuario::class.java
            )
        }
    }

    // =========================================================
    // ANIMACIÓN BARRA
    // =========================================================

    @SuppressLint("ClickableViewAccessibility")
    private fun configurarAnimacionesBarra() {

        val opciones =
            listOf(
                navInicio,
                navExtras,
                navPedidos,
                navCarrito,
                navPerfil
            )

        opciones.forEach { opcion ->

            opcion.setOnTouchListener { vista, evento ->

                when (evento.actionMasked) {

                    MotionEvent.ACTION_DOWN -> {

                        vista.animate()
                            .scaleX(
                                ESCALA_PRESIONADA
                            )
                            .scaleY(
                                ESCALA_PRESIONADA
                            )
                            .translationY(
                                TRASLACION_PRESIONADA
                            )
                            .setDuration(
                                DURACION_PRESION
                            )
                            .setInterpolator(
                                DecelerateInterpolator()
                            )
                            .start()
                    }

                    MotionEvent.ACTION_UP,
                    MotionEvent.ACTION_CANCEL -> {

                        val esActivo =
                            vista === navPedidos

                        vista.animate()
                            .scaleX(
                                if (esActivo) {
                                    ESCALA_ACTIVA
                                } else {
                                    ESCALA_NORMAL
                                }
                            )
                            .scaleY(
                                if (esActivo) {
                                    ESCALA_ACTIVA
                                } else {
                                    ESCALA_NORMAL
                                }
                            )
                            .translationY(
                                if (esActivo) {
                                    TRASLACION_ACTIVA
                                } else {
                                    TRASLACION_NORMAL
                                }
                            )
                            .setDuration(
                                DURACION_RETORNO
                            )
                            .setInterpolator(
                                DecelerateInterpolator()
                            )
                            .start()
                    }
                }

                false
            }
        }
    }

    // =========================================================
    // PEDIDOS ACTIVO
    // =========================================================

    private fun marcarPedidosActivo() {

        navPedidos.post {

            navPedidos.animate()
                .scaleX(
                    ESCALA_ACTIVA
                )
                .scaleY(
                    ESCALA_ACTIVA
                )
                .translationY(
                    TRASLACION_ACTIVA
                )
                .setDuration(
                    DURACION_ACTIVA
                )
                .setInterpolator(
                    DecelerateInterpolator()
                )
                .start()
        }
    }

    // =========================================================
    // CONTADOR CARRITO
    // =========================================================

    private fun actualizarContadorCarrito() {

        val cantidad =
            PedidoManager.cantidadTotal()

        tvCantidadCarrito.text =
            cantidad.toString()

        tvCantidadCarrito.visibility =
            if (cantidad > 0) {
                View.VISIBLE
            } else {
                View.GONE
            }
    }

    // =========================================================
    // ESCUCHAR PEDIDOS
    // =========================================================

    private fun escucharPedidos() {

        val usuarioActual =
            auth.currentUser

        if (usuarioActual == null) {

            Toast.makeText(
                this,
                "Debes iniciar sesión para ver tus pedidos",
                Toast.LENGTH_SHORT
            ).show()

            finish()

            return
        }

        mostrarCargando()

        pedidosListener?.remove()

        pedidosListener =
            db.collection(
                "pedidos"
            )
                .whereEqualTo(
                    "usuarioId",
                    usuarioActual.uid
                )
                .addSnapshotListener { resultado, error ->

                    progressBarPedidos.visibility =
                        View.GONE

                    if (error != null) {

                        Log.e(
                            "PEDIDOS_USUARIO",
                            "Error escuchando pedidos",
                            error
                        )

                        limpiarPedidos()

                        mostrarSinPedidos()

                        Toast.makeText(
                            this,
                            "No se pudieron cargar los pedidos",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@addSnapshotListener
                    }

                    if (resultado == null) {

                        limpiarPedidos()

                        mostrarSinPedidos()

                        return@addSnapshotListener
                    }

                    listaPedidos.clear()

                    val ahora =
                        System.currentTimeMillis()

                    for (documento in resultado.documents) {

                        val pedido =
                            convertirDocumentoEnPedido(
                                documento
                            )

                        if (
                            pedido.debeMostrarse(
                                ahora
                            )
                        ) {

                            listaPedidos.add(
                                pedido
                            )
                        }
                    }

                    listaPedidos.sortByDescending { pedido ->

                        pedido.fecha
                    }

                    actualizarLista()
                }
    }

    // =========================================================
    // DOCUMENTO A PEDIDO
    // =========================================================

    private fun convertirDocumentoEnPedido(
        documento: DocumentSnapshot
    ): PedidoUsuarioItem {

        val numeroPedido =
            documento.getLong(
                "numeroPedido"
            ) ?: 0L

        val estado =
            documento.getString(
                "estadoPedido"
            )
                ?.trim()
                .orEmpty()
                .ifBlank {
                    "Pendiente"
                }

        val tipoEntrega =
            documento.getString(
                "tipoEntrega"
            )
                ?.trim()
                .orEmpty()
                .ifBlank {
                    "Delivery"
                }

        val total =
            documento.getDouble(
                "total"
            )
                ?: documento.getLong(
                    "total"
                )
                    ?.toDouble()
                ?: 0.0

        val fecha =
            obtenerTimestamp(
                documento,
                "fecha"
            )

        val fechaEntrega =
            obtenerTimestamp(
                documento,
                "fechaEntrega"
            )

        val fechaActualizacion =
            obtenerTimestamp(
                documento,
                "fechaActualizacion"
            )

        return PedidoUsuarioItem(
            id =
                documento.id,
            numeroPedido =
                numeroPedido,
            estado =
                estado,
            tipoEntrega =
                tipoEntrega,
            total =
                total,
            fecha =
                fecha,
            fechaEntrega =
                fechaEntrega,
            fechaActualizacion =
                fechaActualizacion
        )
    }

    // =========================================================
    // TIMESTAMP
    // =========================================================

    private fun obtenerTimestamp(
        documento: DocumentSnapshot,
        campo: String
    ): Long {

        return documento
            .getTimestamp(
                campo
            )
            ?.toDate()
            ?.time
            ?: 0L
    }

    // =========================================================
    // OCULTAR PEDIDOS VENCIDOS
    // =========================================================

    private fun revisarPedidosVisibles() {

        if (
            listaPedidos.isEmpty()
        ) {
            return
        }

        val ahora =
            System.currentTimeMillis()

        val seEliminoAlgunPedido =
            listaPedidos.removeAll { pedido ->

                !pedido.debeMostrarse(
                    ahora
                )
            }

        if (
            seEliminoAlgunPedido
        ) {

            actualizarLista()

            Log.d(
                "PEDIDOS_USUARIO",
                "Se ocultaron pedidos vencidos"
            )
        }
    }

    // =========================================================
    // ACTUALIZAR LISTA
    // =========================================================

    private fun actualizarLista() {

        pedidoAdapter.actualizarPedidos(
            listaPedidos
        )

        actualizarEstadoPantalla()
    }

    // =========================================================
    // CARGANDO
    // =========================================================

    private fun mostrarCargando() {

        progressBarPedidos.visibility =
            View.VISIBLE

        rvPedidos.visibility =
            View.GONE

        layoutSinPedidos.visibility =
            View.GONE
    }

    // =========================================================
    // ESTADO PANTALLA
    // =========================================================

    private fun actualizarEstadoPantalla() {

        progressBarPedidos.visibility =
            View.GONE

        if (
            listaPedidos.isEmpty()
        ) {

            mostrarSinPedidos()

        } else {

            rvPedidos.visibility =
                View.VISIBLE

            layoutSinPedidos.visibility =
                View.GONE
        }
    }

    // =========================================================
    // SIN PEDIDOS
    // =========================================================

    private fun mostrarSinPedidos() {

        progressBarPedidos.visibility =
            View.GONE

        rvPedidos.visibility =
            View.GONE

        layoutSinPedidos.visibility =
            View.VISIBLE
    }

    // =========================================================
    // LIMPIAR
    // =========================================================

    private fun limpiarPedidos() {

        listaPedidos.clear()

        pedidoAdapter.actualizarPedidos(
            emptyList()
        )
    }

    // =========================================================
    // ON START
    // =========================================================

    override fun onStart() {
        super.onStart()

        escucharPedidos()

        handler.removeCallbacks(
            revisarPedidosRunnable
        )

        handler.post(
            revisarPedidosRunnable
        )
    }

    // =========================================================
    // ON STOP
    // =========================================================

    override fun onStop() {

        pedidosListener?.remove()

        pedidosListener =
            null

        handler.removeCallbacks(
            revisarPedidosRunnable
        )

        super.onStop()
    }

    // =========================================================
    // CONSTANTES
    // =========================================================

    companion object {

        private const val ESCALA_NORMAL =
            1f

        private const val ESCALA_PRESIONADA =
            1.12f

        private const val ESCALA_ACTIVA =
            1.08f

        private const val TRASLACION_NORMAL =
            0f

        private const val TRASLACION_PRESIONADA =
            -9f

        private const val TRASLACION_ACTIVA =
            -5f

        private const val DURACION_PRESION =
            150L

        private const val DURACION_RETORNO =
            180L

        private const val DURACION_ACTIVA =
            280L
    }
}