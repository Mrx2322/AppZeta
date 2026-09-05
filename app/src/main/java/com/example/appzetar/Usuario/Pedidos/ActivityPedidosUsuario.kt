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
    // ADAPTER
    // =========================================================

    private lateinit var pedidoAdapter: PedidoUsuarioAdapter


    // =========================================================
    // LISTA
    // =========================================================

    private val listaPedidos =
        mutableListOf<PedidoUsuarioItem>()


    // =========================================================
    // LISTENER FIRESTORE
    // =========================================================

    private var pedidosListener:
            ListenerRegistration? = null


    // =========================================================
    // ON CREATE
    // =========================================================

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )

        setContentView(
            R.layout.activity_pedidos_usuario
        )

        inicializarComponentes()

        configurarRecyclerView()

        configurarBotonVolver()

        escucharPedidos()
    }


    // =========================================================
    // INICIALIZAR COMPONENTES
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

        btnVolver =
            findViewById(
                R.id.btnVolver
            )
    }


    // =========================================================
    // RECYCLERVIEW
    // =========================================================

    private fun configurarRecyclerView() {

        pedidoAdapter =
            PedidoUsuarioAdapter()

        rvPedidos.layoutManager =
            LinearLayoutManager(this)

        rvPedidos.adapter =
            pedidoAdapter

        rvPedidos.setHasFixedSize(true)

        rvPedidos.itemAnimator =
            null
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


        // -----------------------------------------------------
        // VALIDAR SESIÓN
        // -----------------------------------------------------

        if (usuarioActual == null) {

            Toast.makeText(
                this,
                "Debes iniciar sesión para ver tus pedidos",
                Toast.LENGTH_SHORT
            ).show()

            finish()

            return
        }


        val usuarioId =
            usuarioActual.uid


        // -----------------------------------------------------
        // MOSTRAR CARGANDO
        // -----------------------------------------------------

        progressBarPedidos.visibility =
            View.VISIBLE

        rvPedidos.visibility =
            View.GONE

        layoutSinPedidos.visibility =
            View.GONE


        // -----------------------------------------------------
        // ELIMINAR LISTENER ANTERIOR
        // -----------------------------------------------------

        pedidosListener?.remove()


        // -----------------------------------------------------
        // LISTENER EN TIEMPO REAL
        // -----------------------------------------------------

        pedidosListener =
            db.collection("pedidos")
                .whereEqualTo(
                    "usuarioId",
                    usuarioId
                )
                .addSnapshotListener {
                        resultado,
                        error ->

                    progressBarPedidos.visibility =
                        View.GONE


                    // =========================================
                    // ERROR
                    // =========================================

                    if (error != null) {

                        Log.e(
                            "PEDIDOS_USUARIO",
                            "Error escuchando pedidos",
                            error
                        )

                        listaPedidos.clear()

                        pedidoAdapter.actualizarPedidos(
                            emptyList()
                        )

                        mostrarSinPedidos()

                        Toast.makeText(
                            this,
                            "No se pudieron cargar los pedidos",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@addSnapshotListener
                    }


                    // =========================================
                    // RESULTADO NULO
                    // =========================================

                    if (resultado == null) {

                        listaPedidos.clear()

                        pedidoAdapter.actualizarPedidos(
                            emptyList()
                        )

                        mostrarSinPedidos()

                        return@addSnapshotListener
                    }


                    // =========================================
                    // LIMPIAR LISTA
                    // =========================================

                    listaPedidos.clear()


                    // =========================================
                    // RECORRER PEDIDOS
                    // =========================================

                    for (
                    documento
                    in resultado.documents
                    ) {

                        val id =
                            documento.id


                        // -------------------------------------
                        // NÚMERO DEL PEDIDO
                        // -------------------------------------

                        val numeroPedido =
                            documento.getLong(
                                "numeroPedido"
                            ) ?: 0L


                        // -------------------------------------
                        // ESTADO
                        // -------------------------------------

                        val estado =
                            documento.getString(
                                "estadoPedido"
                            )
                                ?: "Pendiente"


                        // -------------------------------------
                        // TOTAL
                        // -------------------------------------

                        val total =
                            documento.getDouble(
                                "total"
                            )
                                ?: documento.getLong(
                                    "total"
                                )?.toDouble()
                                ?: 0.0


                        // -------------------------------------
                        // FECHA
                        // -------------------------------------

                        val fecha =
                            obtenerFecha(
                                documento
                            )


                        // -------------------------------------
                        // AGREGAR PEDIDO
                        // -------------------------------------

                        listaPedidos.add(

                            PedidoUsuarioItem(

                                id = id,

                                numeroPedido =
                                    numeroPedido,

                                estado = estado,

                                total = total,

                                fecha = fecha
                            )
                        )


                        Log.d(
                            "PEDIDOS_USUARIO",
                            "Pedido encontrado: " +
                                    "ID=$id | " +
                                    "Numero=$numeroPedido | " +
                                    "Estado=$estado | " +
                                    "Total=$total"
                        )
                    }


                    // =========================================
                    // ORDENAR MÁS RECIENTE PRIMERO
                    // =========================================

                    listaPedidos.sortByDescending {
                        it.fecha
                    }


                    // =========================================
                    // ACTUALIZAR ADAPTER
                    // =========================================

                    pedidoAdapter.actualizarPedidos(
                        listaPedidos
                    )


                    // =========================================
                    // ACTUALIZAR PANTALLA
                    // =========================================

                    actualizarEstadoPantalla()


                    Log.d(
                        "PEDIDOS_USUARIO",
                        "Pedidos cargados: " +
                                listaPedidos.size
                    )
                }
    }


    // =========================================================
    // OBTENER FECHA
    // =========================================================

    private fun obtenerFecha(
        documento: DocumentSnapshot
    ): Long {

        val timestamp =
            documento.getTimestamp(
                "fecha"
            )

        return timestamp
            ?.toDate()
            ?.time
            ?: 0L
    }


    // =========================================================
    // ESTADO DE LA PANTALLA
    // =========================================================

    private fun actualizarEstadoPantalla() {

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

        rvPedidos.visibility =
            View.GONE

        layoutSinPedidos.visibility =
            View.VISIBLE
    }


    // =========================================================
    // DESTRUIR
    // =========================================================

    override fun onDestroy() {

        pedidosListener?.remove()

        pedidosListener =
            null

        super.onDestroy()
    }
}