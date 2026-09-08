package com.example.appzetar.Usuario

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appzetar.R
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

    // =========================================================
    // COMPONENTES
    // =========================================================

    private lateinit var rvPedidos: RecyclerView
    private lateinit var progressBarPedidos: ProgressBar
    private lateinit var layoutSinPedidos: LinearLayout
    private lateinit var btnVolver: ImageButton

    // =========================================================
    // ADAPTER Y LISTA
    // =========================================================

    private lateinit var pedidoAdapter: PedidoUsuarioAdapter

    private val listaPedidos =
        mutableListOf<PedidoUsuarioItem>()

    // =========================================================
    // LISTENER
    // =========================================================

    private var pedidosListener:
            ListenerRegistration? = null

    // =========================================================
    // ON CREATE
    // =========================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_pedidos_usuario
        )

        inicializarComponentes()
        configurarRecyclerView()
        configurarBotonVolver()
    }

    // =========================================================
    // COMPONENTES
    // =========================================================

    private fun inicializarComponentes() {

        rvPedidos =
            findViewById(R.id.rvPedidos)

        progressBarPedidos =
            findViewById(R.id.progressBarPedidos)

        layoutSinPedidos =
            findViewById(R.id.layoutSinPedidos)

        btnVolver =
            findViewById(R.id.btnVolver)
    }

    // =========================================================
    // RECYCLERVIEW
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

            setHasFixedSize(false)

            itemAnimator =
                null
        }
    }

    // =========================================================
    // BOTÓN VOLVER
    // =========================================================

    private fun configurarBotonVolver() {

        btnVolver.setOnClickListener {

            finish()
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
            db.collection("pedidos")
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

                    for (documento in resultado.documents) {

                        val pedido =
                            convertirDocumentoEnPedido(
                                documento
                            )

                        listaPedidos.add(
                            pedido
                        )
                    }

                    listaPedidos.sortByDescending { pedido ->
                        pedido.fecha
                    }

                    pedidoAdapter.actualizarPedidos(
                        listaPedidos
                    )

                    actualizarEstadoPantalla()

                    Log.d(
                        "PEDIDOS_USUARIO",
                        "Pedidos cargados: ${listaPedidos.size}"
                    )
                }
    }

    // =========================================================
    // CONVERTIR DOCUMENTO
    // =========================================================

    private fun convertirDocumentoEnPedido(
        documento: DocumentSnapshot
    ): PedidoUsuarioItem {

        val numeroPedido =
            documento.getLong("numeroPedido")
                ?: 0L

        val estado =
            documento.getString("estadoPedido")
                ?.trim()
                .orEmpty()
                .ifBlank {
                    "Pendiente"
                }

        val tipoEntrega =
            documento.getString("tipoEntrega")
                ?.trim()
                .orEmpty()
                .ifBlank {
                    "Delivery"
                }

        val total =
            documento.getDouble("total")
                ?: documento.getLong("total")
                    ?.toDouble()
                ?: 0.0

        val fecha =
            obtenerFecha(documento)

        Log.d(
            "PEDIDOS_USUARIO",
            "Pedido=${documento.id} | " +
                    "Estado=$estado | " +
                    "Entrega=$tipoEntrega"
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
                fecha
        )
    }

    // =========================================================
    // OBTENER FECHA
    // =========================================================

    private fun obtenerFecha(
        documento: DocumentSnapshot
    ): Long {

        return documento
            .getTimestamp("fecha")
            ?.toDate()
            ?.time
            ?: 0L
    }

    // =========================================================
    // PANTALLA
    // =========================================================

    private fun mostrarCargando() {

        progressBarPedidos.visibility =
            View.VISIBLE

        rvPedidos.visibility =
            View.GONE

        layoutSinPedidos.visibility =
            View.GONE
    }

    private fun actualizarEstadoPantalla() {

        if (listaPedidos.isEmpty()) {

            mostrarSinPedidos()

        } else {

            rvPedidos.visibility =
                View.VISIBLE

            layoutSinPedidos.visibility =
                View.GONE
        }
    }

    private fun mostrarSinPedidos() {

        progressBarPedidos.visibility =
            View.GONE

        rvPedidos.visibility =
            View.GONE

        layoutSinPedidos.visibility =
            View.VISIBLE
    }

    private fun limpiarPedidos() {

        listaPedidos.clear()

        pedidoAdapter.actualizarPedidos(
            emptyList()
        )
    }

    // =========================================================
    // CICLO DE VIDA
    // =========================================================

    override fun onStart() {
        super.onStart()

        escucharPedidos()
    }

    override fun onStop() {

        pedidosListener?.remove()

        pedidosListener =
            null

        super.onStop()
    }
}