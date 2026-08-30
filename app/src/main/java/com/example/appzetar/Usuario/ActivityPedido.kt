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
import com.example.appzetar.Usuario.Pagos.ActivityPagoYape

class ActivityPedido : AppCompatActivity() {

    private lateinit var rvPedido: RecyclerView
    private lateinit var tvTotalProductos: TextView
    private lateinit var tvTotalPedido: TextView
    private lateinit var tvMensajeVacio: TextView

    private lateinit var btnContinuar: View

    private lateinit var pedidoAdapter: PedidoAdapter


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
                PedidoManager.pedido
            ) {

                actualizarPedido()
            }


        rvPedido.layoutManager =
            LinearLayoutManager(this)

        rvPedido.adapter =
            pedidoAdapter


        // =====================================================
        // CONTINUAR AL PAGO
        // =====================================================

        btnContinuar.setOnClickListener {

            if (
                PedidoManager.pedido.isEmpty()
            ) {
                return@setOnClickListener
            }


            val intent =
                Intent(
                    this,
                    ActivityPagoYape::class.java
                )

            startActivity(intent)
        }
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
            "$cantidad productos"


        // =====================================================
        // CALCULAR TOTAL
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

            // =================================================
            // PEDIDO CON PRODUCTOS
            // =================================================

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