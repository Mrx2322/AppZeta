package com.example.appzetar.usuario.confirmacion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
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
import com.example.appzetar.R
import com.example.appzetar.usuario.pedidos.ActivityConfirmarPedido
import com.example.appzetar.usuario.carrito.PedidoManager
import com.google.firebase.auth.FirebaseAuth

class ActivityResumenPedido : AppCompatActivity() {

    // =========================================================
    // COMPONENTES
    // =========================================================

    private lateinit var rvResumenPedido: RecyclerView
    private lateinit var tvNombreCliente: TextView
    private lateinit var tvTelefonoCliente: TextView
    private lateinit var tvDireccionCliente: TextView
    private lateinit var tvObservacionCliente: TextView
    private lateinit var tvTotalProductos: TextView
    private lateinit var btnConfirmarPedido: Button

    // =========================================================
    // DATOS RECIBIDOS
    // =========================================================

    private var nombre = ""
    private var telefono = ""
    private var direccion = ""
    private var observacion = ""

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

        setContentView(R.layout.activity_resumen_pedido)

        aplicarInsets()
        obtenerDatos()
        initComponent()
        initUI()
    }

    // =========================================================
    // BARRAS DEL SISTEMA
    // =========================================================

    private fun aplicarInsets() {

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
    }

    // =========================================================
    // OBTENER DATOS
    // =========================================================

    private fun obtenerDatos() {

        val nombreRecibido =
            intent.getStringExtra("nombre")
                ?.trim()
                .orEmpty()

        nombre = obtenerNombreCliente(nombreRecibido)

        telefono =
            intent.getStringExtra("telefono")
                ?.trim()
                .orEmpty()

        direccion =
            intent.getStringExtra("direccion")
                ?.trim()
                .orEmpty()

        observacion =
            intent.getStringExtra("observacion")
                ?.trim()
                .orEmpty()
    }

    private fun obtenerNombreCliente(nombreRecibido: String): String {
        val nombreGenerico = getString(R.string.login_nombre_invitado)

        if (
            nombreRecibido.isNotBlank() &&
            !nombreRecibido.equals(nombreGenerico, ignoreCase = true)
        ) {
            return nombreRecibido
        }

        val usuarioActual = FirebaseAuth.getInstance().currentUser
        val identificador = usuarioActual?.uid ?: INVITADO_SIN_UID
        val claveNombre = "$CLAVE_NOMBRE_INVITADO$identificador"

        return getSharedPreferences(
            PREFERENCIAS_INVITADO,
            MODE_PRIVATE
        ).getString(claveNombre, null)
            ?.trim()
            ?.takeIf { nombre -> nombre.isNotBlank() }
            ?: nombreGenerico
    }

    // =========================================================
    // COMPONENTES
    // =========================================================

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

    // =========================================================
    // INTERFAZ
    // =========================================================

    private fun initUI() {

        mostrarDatosCliente()
        configurarListaPedido()
        mostrarCantidadProductos()
        configurarBotonConfirmar()
    }

    // =========================================================
    // MOSTRAR DATOS DEL CLIENTE
    // =========================================================

    private fun mostrarDatosCliente() {

        tvNombreCliente.text =
            nombre

        tvTelefonoCliente.text =
            telefono

        tvDireccionCliente.text =
            direccion

        tvObservacionCliente.isVisible = observacion.isNotBlank()

        if (observacion.isNotBlank()) {
            tvObservacionCliente.text =
                observacion
        }
    }

    // =========================================================
    // LISTA DEL PEDIDO
    // =========================================================

    private fun configurarListaPedido() {

        val resumenAdapter =
            PedidoResumenAdapter(
                PedidoManager.pedido
            )

        rvResumenPedido.apply {

            layoutManager =
                LinearLayoutManager(
                    this@ActivityResumenPedido
                )

            adapter =
                resumenAdapter
        }
    }

    // =========================================================
    // CANTIDAD TOTAL
    // =========================================================

    private fun mostrarCantidadProductos() {

        val cantidad =
            PedidoManager.cantidadTotal()

        tvTotalProductos.text = resources.getQuantityString(
            R.plurals.resumen_cantidad_productos,
            cantidad,
            cantidad
        )
    }

    // =========================================================
    // BOTÓN CONFIRMAR
    // =========================================================

    private fun configurarBotonConfirmar() {

        btnConfirmarPedido.setOnClickListener {
            confirmarPedido()
        }
    }

    // =========================================================
    // CONTINUAR A LA CONFIRMACIÓN FINAL
    // =========================================================

    private fun confirmarPedido() {

        if (PedidoManager.pedido.isEmpty()) {

            Toast.makeText(
                this,
                R.string.resumen_carrito_vacio,
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val intent = Intent(
            this,
            ActivityConfirmarPedido::class.java
        ).apply {

            putExtra("nombre", nombre)
            putExtra("telefono", telefono)
            putExtra("direccion", direccion)
            putExtra("observacion", observacion)

            // Opciones activas actualmente
            putExtra("tipoEntrega", "Delivery")
            putExtra("metodoPago", "Contra entrega")
        }

        startActivity(intent)
    }

    companion object {
        private const val PREFERENCIAS_INVITADO = "preferencias_invitado"
        private const val CLAVE_NOMBRE_INVITADO = "nombre_invitado_"
        private const val INVITADO_SIN_UID = "sin_uid"
    }
}