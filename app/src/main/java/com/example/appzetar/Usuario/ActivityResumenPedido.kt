package com.example.appzetar.Usuario

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appzetar.R

class ActivityResumenPedido : AppCompatActivity() {

    private lateinit var rvResumenPedido: RecyclerView
    private lateinit var tvNombreCliente: TextView
    private lateinit var tvTelefonoCliente: TextView
    private lateinit var tvDireccionCliente: TextView
    private lateinit var tvObservacionCliente: TextView
    private lateinit var tvTotalProductos: TextView
    private lateinit var btnConfirmarPedido: Button

    private lateinit var resumenAdapter: PedidoResumenAdapter

    private var nombre = ""
    private var telefono = ""
    private var direccion = ""
    private var observacion = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(
            window,
            false
        )

        setContentView(R.layout.activity_resumen_pedido)

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

        obtenerDatos()
        initComponent()
        initUI()
    }

    // ---------------------------------------------------------
    // DATOS RECIBIDOS
    // ---------------------------------------------------------

    private fun obtenerDatos() {

        nombre =
            intent.getStringExtra("nombre") ?: ""

        telefono =
            intent.getStringExtra("telefono") ?: ""

        direccion =
            intent.getStringExtra("direccion") ?: ""

        observacion =
            intent.getStringExtra("observacion") ?: ""
    }

    // ---------------------------------------------------------
    // COMPONENTES
    // ---------------------------------------------------------

    private fun initComponent() {

        rvResumenPedido =
            findViewById(R.id.rvResumenPedido)

        tvNombreCliente =
            findViewById(R.id.tvNombreCliente)

        tvTelefonoCliente =
            findViewById(R.id.tvTelefonoCliente)

        tvDireccionCliente =
            findViewById(R.id.tvDireccionCliente)

        tvObservacionCliente =
            findViewById(R.id.tvObservacionCliente)

        tvTotalProductos =
            findViewById(R.id.tvTotalProductos)

        btnConfirmarPedido =
            findViewById(R.id.btnConfirmarPedido)
    }

    // ---------------------------------------------------------
    // UI
    // ---------------------------------------------------------

    private fun initUI() {

        tvNombreCliente.text =
            nombre

        tvTelefonoCliente.text =
            telefono

        tvDireccionCliente.text =
            direccion

        if (observacion.isEmpty()) {

            tvObservacionCliente.visibility =
                View.GONE

        } else {

            tvObservacionCliente.visibility =
                View.VISIBLE

            tvObservacionCliente.text =
                observacion
        }

        // -----------------------------------------------------
        // LISTA DEL PEDIDO
        // -----------------------------------------------------

        resumenAdapter =
            PedidoResumenAdapter(
                PedidoManager.pedido
            )

        rvResumenPedido.layoutManager =
            LinearLayoutManager(this)

        rvResumenPedido.adapter =
            resumenAdapter

        // -----------------------------------------------------
        // TOTAL
        // -----------------------------------------------------

        val cantidad =
            PedidoManager.cantidadTotal()

        tvTotalProductos.text =
            "$cantidad productos"

        // -----------------------------------------------------
        // CONFIRMAR
        // -----------------------------------------------------

        btnConfirmarPedido.setOnClickListener {

            confirmarPedido()
        }
    }

    // ---------------------------------------------------------
    // CONFIRMAR PEDIDO
    // ---------------------------------------------------------

    private fun confirmarPedido() {

        if (PedidoManager.pedido.isEmpty()) {
            return
        }

        android.widget.Toast.makeText(
            this,
            "Pedido listo para enviar",
            android.widget.Toast.LENGTH_SHORT
        ).show()
    }
}