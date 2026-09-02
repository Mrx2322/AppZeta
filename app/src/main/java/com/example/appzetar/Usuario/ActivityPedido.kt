package com.example.appzetar.Usuario

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appzetar.R
import com.google.android.material.button.MaterialButton

class ActivityPedido : AppCompatActivity() {

    // =========================================================
    // COMPONENTES
    // =========================================================

    private lateinit var rvPedido: RecyclerView
    private lateinit var tvTotalProductos: TextView
    private lateinit var tvTotalPedido: TextView
    private lateinit var tvMensajeVacio: TextView
    private lateinit var btnContinuar: MaterialButton

    private lateinit var pedidoAdapter: PedidoAdapter

    private var procesandoOperacion = false


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
            R.layout.activity_pedido
        )


        // =====================================================
        // INSETS
        // =====================================================

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


        // =====================================================
        // INICIALIZAR
        // =====================================================

        initComponent()
        initUI()
        actualizarPedido()
    }


    // =========================================================
    // COMPONENTES
    // =========================================================

    private fun initComponent() {

        rvPedido =
            findViewById(R.id.rvPedido)

        tvTotalProductos =
            findViewById(R.id.tvTotalProductos)

        tvTotalPedido =
            findViewById(R.id.tvTotalPedido)

        tvMensajeVacio =
            findViewById(R.id.tvMensajeVacio)

        btnContinuar =
            findViewById(R.id.btnContinuar)
    }


    // =========================================================
    // UI
    // =========================================================

    private fun initUI() {

        pedidoAdapter =
            PedidoAdapter(
                PedidoManager.pedido,

                onAumentar = { item ->

                    if (!procesandoOperacion) {
                        aumentarProducto(item)
                    }
                },

                onDisminuir = { item ->

                    if (!procesandoOperacion) {
                        disminuirProducto(item)
                    }
                },

                onEliminar = { item ->

                    if (!procesandoOperacion) {
                        eliminarProducto(item)
                    }
                }
            )


        rvPedido.layoutManager =
            LinearLayoutManager(this)

        rvPedido.adapter =
            pedidoAdapter


        // =====================================================
        // BOTÓN CONTINUAR
        // =====================================================

        btnContinuar.setOnClickListener {

            if (!procesandoOperacion) {
                continuarCompra()
            }
        }
    }


    // =========================================================
    // AUMENTAR PRODUCTO
    // =========================================================

    private fun aumentarProducto(
        item: PedidoItem
    ) {

        // =====================================================
        // EXTRA
        // =====================================================

        if (item.tipo == TipoPedido.EXTRA) {

            PedidoManager.aumentarCantidad(
                item.id,
                item.tipo
            )

            actualizarPedido()

            return
        }


        // =====================================================
        // EVITAR DOBLE CLIC
        // =====================================================

        if (procesandoOperacion) {
            return
        }

        procesandoOperacion = true

        actualizarEstadoBotones()


        // =====================================================
        // DESCONTAR STOCK
        // =====================================================

        StockManager.descontarStock(
            item = item,
            cantidad = 1,

            onSuccess = {

                PedidoManager.aumentarCantidad(
                    item.id,
                    item.tipo
                )

                procesandoOperacion = false

                actualizarEstadoBotones()
                actualizarPedido()
            },

            onError = { exception ->

                procesandoOperacion = false

                actualizarEstadoBotones()

                mostrarErrorStock(
                    exception
                )
            }
        )
    }


    // =========================================================
    // DISMINUIR PRODUCTO
    // =========================================================

    private fun disminuirProducto(
        item: PedidoItem
    ) {

        // =====================================================
        // EXTRA
        // =====================================================

        if (item.tipo == TipoPedido.EXTRA) {

            PedidoManager.disminuirCantidad(
                item.id,
                item.tipo
            )

            actualizarPedido()

            return
        }


        // =====================================================
        // EVITAR DOBLE CLIC
        // =====================================================

        if (procesandoOperacion) {
            return
        }

        procesandoOperacion = true

        actualizarEstadoBotones()


        // =====================================================
        // DEVOLVER STOCK
        // =====================================================

        StockManager.devolverStock(
            item = item,
            cantidad = 1,

            onSuccess = {

                PedidoManager.disminuirCantidad(
                    item.id,
                    item.tipo
                )

                procesandoOperacion = false

                actualizarEstadoBotones()
                actualizarPedido()
            },

            onError = { exception ->

                procesandoOperacion = false

                actualizarEstadoBotones()

                mostrarErrorStock(
                    exception
                )
            }
        )
    }


    // =========================================================
    // ELIMINAR PRODUCTO
    // =========================================================

    private fun eliminarProducto(
        item: PedidoItem
    ) {

        // =====================================================
        // EXTRA
        // =====================================================

        if (item.tipo == TipoPedido.EXTRA) {

            PedidoManager.eliminarProducto(
                item.id,
                item.tipo
            )

            actualizarPedido()

            return
        }


        // =====================================================
        // EVITAR DOBLE CLIC
        // =====================================================

        if (procesandoOperacion) {
            return
        }

        procesandoOperacion = true

        actualizarEstadoBotones()


        // =====================================================
        // CANTIDAD A DEVOLVER
        // =====================================================

        val cantidadADevolver =
            item.cantidad


        // =====================================================
        // DEVOLVER TODO EL STOCK
        // =====================================================

        StockManager.devolverStock(
            item = item,
            cantidad = cantidadADevolver,

            onSuccess = {

                PedidoManager.eliminarProducto(
                    item.id,
                    item.tipo
                )

                procesandoOperacion = false

                actualizarEstadoBotones()
                actualizarPedido()
            },

            onError = { exception ->

                procesandoOperacion = false

                actualizarEstadoBotones()

                mostrarErrorStock(
                    exception
                )
            }
        )
    }


    // =========================================================
    // ESTADO DE BOTONES
    // =========================================================

    private fun actualizarEstadoBotones() {

        btnContinuar.isEnabled =
            !procesandoOperacion &&
                    PedidoManager.pedido.isNotEmpty()

        btnContinuar.alpha =
            if (btnContinuar.isEnabled) {
                1f
            } else {
                0.5f
            }
    }


    // =========================================================
    // ERROR DE STOCK
    // =========================================================

    private fun mostrarErrorStock(
        exception: Exception
    ) {

        val mensaje =
            if (
                exception is IllegalStateException &&
                exception.message == "SIN_STOCK"
            ) {

                "Ya no hay stock disponible."

            } else {

                "No se pudo actualizar el stock."
            }


        Toast.makeText(
            this,
            mensaje,
            Toast.LENGTH_SHORT
        ).show()
    }


    // =========================================================
    // CONTINUAR COMPRA
    // =========================================================

    private fun continuarCompra() {

        if (PedidoManager.pedido.isEmpty()) {
            return
        }

        val intent =
            Intent(
                this,
                ActivityEntrega::class.java
            )

        startActivity(intent)
    }


    // =========================================================
    // ACTUALIZAR PEDIDO
    // =========================================================

    private fun actualizarPedido() {

        pedidoAdapter.notifyDataSetChanged()


        // =====================================================
        // CANTIDAD TOTAL
        // =====================================================

        val cantidad =
            PedidoManager.cantidadTotal()

        tvTotalProductos.text =
            if (cantidad == 1) {
                "1 producto"
            } else {
                "$cantidad productos"
            }


        // =====================================================
        // TOTAL
        // =====================================================

        val total =
            PedidoManager.pedido.sumOf {
                it.precio * it.cantidad
            }

        tvTotalPedido.text =
            "S/ %.2f".format(total)


        // =====================================================
        // PEDIDO VACÍO
        // =====================================================

        if (PedidoManager.pedido.isEmpty()) {

            rvPedido.visibility =
                View.GONE

            tvMensajeVacio.visibility =
                View.VISIBLE

            btnContinuar.isEnabled =
                false

            btnContinuar.alpha =
                0.5f

        } else {

            rvPedido.visibility =
                View.VISIBLE

            tvMensajeVacio.visibility =
                View.GONE

            btnContinuar.isEnabled =
                !procesandoOperacion

            btnContinuar.alpha =
                if (procesandoOperacion) {
                    0.5f
                } else {
                    1f
                }
        }
    }


    // =========================================================
    // AL REGRESAR
    // =========================================================

    override fun onResume() {
        super.onResume()

        actualizarPedido()
    }
}