package com.example.appzetar.Usuario

import android.os.Bundle
import com.example.appzetar.Usuario.Pagos.ActivityPagoYape
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
import android.content.Intent
import kotlin.jvm.java

class ActivityPedido : AppCompatActivity() {

    private lateinit var rvPedido: RecyclerView
    private lateinit var tvTotalProductos: TextView
    private lateinit var tvMensajeVacio: TextView

    private lateinit var btnContinuar: View

    private lateinit var pedidoAdapter: PedidoAdapter

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

        initComponent()
        initUI()
        actualizarPedido()
    }

    private fun initComponent() {

        rvPedido =
            findViewById(R.id.rvPedido)

        tvTotalProductos =
            findViewById(R.id.tvTotalProductos)

        tvMensajeVacio =
            findViewById(R.id.tvMensajeVacio)

        btnContinuar =
            findViewById(R.id.btnContinuar)
    }

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

        btnContinuar.setOnClickListener {

            if (PedidoManager.pedido.isEmpty()) {
                return@setOnClickListener
            }

            val intent = Intent(
                this,
                ActivityPagoYape::class.java
            )

            startActivity(intent)
        }
    }

    private fun actualizarPedido() {

        pedidoAdapter.notifyDataSetChanged()

        val cantidad =
            PedidoManager.cantidadTotal()

        tvTotalProductos.text =
            "$cantidad productos"

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
                true

            btnContinuar.alpha =
                1f
        }

        }
        override fun onResume() {
            super.onResume()
            actualizarPedido()
        }
   }