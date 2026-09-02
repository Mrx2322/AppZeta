package com.example.appzetar.Usuario

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
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
            findViewById(
                R.id.rvPedido
            )

        tvTotalProductos =
            findViewById(
                R.id.tvTotalProductos
            )

        tvTotalPedido =
            findViewById(
                R.id.tvTotalPedido
            )

        tvMensajeVacio =
            findViewById(
                R.id.tvMensajeVacio
            )

        btnContinuar =
            findViewById(
                R.id.btnContinuar
            )
    }


    // =========================================================
    // UI
    // =========================================================

    private fun initUI() {

        pedidoAdapter =
            PedidoAdapter(
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


        rvPedido.layoutManager =
            LinearLayoutManager(this)

        rvPedido.adapter =
            pedidoAdapter


        // =====================================================
        // BOTÓN CONTINUAR
        // =====================================================

        btnContinuar.setOnClickListener {

            continuarCompra()
        }
    }


    // =========================================================
    // AUMENTAR PRODUCTO
    // =========================================================
    //
    // IMPORTANTE:
    //
    // Aquí NO se modifica Firestore.
    // Solo se modifica el carrito.
    //
    // El stock se descontará al confirmar el pedido.
    // =========================================================

    private fun aumentarProducto(
        item: PedidoItem
    ) {

        PedidoManager.aumentarCantidad(
            item.id,
            item.tipo
        )

        actualizarPedido()
    }


    // =========================================================
    // DISMINUIR PRODUCTO
    // =========================================================
    //
    // Aquí tampoco se devuelve stock.
    // Solo se modifica el carrito.
    // =========================================================

    private fun disminuirProducto(
        item: PedidoItem
    ) {

        PedidoManager.disminuirCantidad(
            item.id,
            item.tipo
        )

        actualizarPedido()
    }


    // =========================================================
    // ELIMINAR PRODUCTO
    // =========================================================
    //
    // Solo elimina del carrito.
    // No modifica Firestore.
    // =========================================================

    private fun eliminarProducto(
        item: PedidoItem
    ) {

        PedidoManager.eliminarProducto(
            item.id,
            item.tipo
        )

        actualizarPedido()
    }


    // =========================================================
    // CONTINUAR COMPRA
    // =========================================================

    private fun continuarCompra() {

        if (
            PedidoManager.pedido.isEmpty()
        ) {
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

        if (
            PedidoManager.pedido.isEmpty()
        ) {

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
                true

            btnContinuar.alpha =
                1f
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