package com.deiapp.appzetar.usuario.pedidos

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.deiapp.appzeta.R
import com.deiapp.appzetar.usuario.carrito.PedidoManager
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class ActivityConfirmarPedido : AppCompatActivity() {

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private lateinit var tvNombreUsuario: TextView
    private lateinit var tvCorreoUsuario: TextView
    private lateinit var tvResumenProductos: TextView
    private lateinit var tvTotalPedido: TextView

    private lateinit var radioGroupEntrega: RadioGroup
    private lateinit var radioDelivery: RadioButton
    private lateinit var radioRecojo: RadioButton

    private lateinit var tvDireccion: TextView
    private lateinit var tvReferencia: TextView
    private lateinit var tvTelefono: TextView
    private lateinit var tvMetodoPago: TextView

    private lateinit var btnContinuarPago: MaterialButton

    private var nombreUsuario = ""
    private var tipoEntrega = TIPO_ENTREGA_DELIVERY

    private var direccion = ""
    private var referencia = ""
    private var telefono = ""

    private var latitud: Double? = null
    private var longitud: Double? = null
    private var ubicacionMaps = ""
    private var observacion = ""

    private var confirmandoPedido = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_confirmar_pedido)

        aplicarInsets()
        initComponent()
        recibirDatos()
        cargarDatosUsuario()
        cargarResumenPedido()
        mostrarDatosEntrega()
        configurarEntrega()
        configurarBoton()
    }

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

    private fun initComponent() {
        tvNombreUsuario = findViewById(R.id.tvNombreUsuario)
        tvCorreoUsuario = findViewById(R.id.tvCorreoUsuario)
        tvResumenProductos = findViewById(R.id.tvResumenProductos)
        tvTotalPedido = findViewById(R.id.tvTotalPedido)

        radioGroupEntrega = findViewById(R.id.radioGroupEntrega)
        radioDelivery = findViewById(R.id.radioDelivery)
        radioRecojo = findViewById(R.id.radioRecojo)

        tvDireccion = findViewById(R.id.tvDireccion)
        tvReferencia = findViewById(R.id.tvReferencia)
        tvTelefono = findViewById(R.id.tvTelefono)
        tvMetodoPago = findViewById(R.id.tvMetodoPago)

        btnContinuarPago = findViewById(R.id.btnContinuarPago)
    }

    private fun leerExtraTexto(vararg claves: String): String {
        for (clave in claves) {
            val valor = intent.getStringExtra(clave)?.trim()
            if (!valor.isNullOrBlank()) {
                return valor
            }
        }

        return ""
    }

    private fun recibirDatos() {
        nombreUsuario = leerExtraTexto(
            EXTRA_NOMBRE,
            "nombreUsuario"
        )

        telefono = leerExtraTexto(
            "telefono",
            EXTRA_TELEFONO,
            "telefonoUsuario",
            "telefonoCliente",
            "numeroTelefono",
            "númeroTelefono"
        )

        direccion = leerExtraTexto(
            "direccion",
            EXTRA_DIRECCION
        )

        observacion = leerExtraTexto(
            "observacion",
            EXTRA_OBSERVACION
        )

        referencia = leerExtraTexto(EXTRA_REFERENCIA)

        tipoEntrega = leerExtraTexto(EXTRA_TIPO_ENTREGA)
            .ifBlank { TIPO_ENTREGA_DELIVERY }

        latitud = if (intent.hasExtra(EXTRA_LATITUD)) {
            intent.getDoubleExtra(EXTRA_LATITUD, 0.0)
        } else {
            null
        }

        longitud = if (intent.hasExtra(EXTRA_LONGITUD)) {
            intent.getDoubleExtra(EXTRA_LONGITUD, 0.0)
        } else {
            null
        }

        ubicacionMaps = leerExtraTexto(
            EXTRA_UBICACION_MAPS,
            "ubicacionMaps"
        )
    }

    private fun cargarDatosUsuario() {
        val usuario = auth.currentUser

        if (usuario == null) {
            Toast.makeText(
                this,
                R.string.confirmar_pedido_sesion_no_activa,
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        tvCorreoUsuario.text = usuario.email
            ?: getString(R.string.confirmar_pedido_sin_correo)

        if (nombreUsuario.isNotBlank()) {
            tvNombreUsuario.text = nombreUsuario
            return
        }

        db.collection(COLECCION_USUARIOS)
            .document(usuario.uid)
            .get()
            .addOnSuccessListener { documento ->
                nombreUsuario = documento.getString(CAMPO_NOMBRE)
                    ?.trim()
                    .orEmpty()
                    .ifBlank {
                        getString(R.string.confirmar_pedido_cliente)
                    }

                tvNombreUsuario.text = nombreUsuario
            }
            .addOnFailureListener { exception ->
                Log.e(
                    TAG,
                    "No se pudo cargar el nombre del usuario",
                    exception
                )

                nombreUsuario = getString(R.string.confirmar_pedido_cliente)
                tvNombreUsuario.text = nombreUsuario
            }
    }

    private fun cargarResumenPedido() {
        val pedido = PedidoManager.pedido

        if (pedido.isEmpty()) {
            Toast.makeText(
                this,
                R.string.confirmar_pedido_carrito_vacio,
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        val cantidad = PedidoManager.cantidadTotal()

        tvResumenProductos.text = resources.getQuantityString(
            R.plurals.confirmar_pedido_cantidad_productos,
            cantidad,
            cantidad
        )

        tvTotalPedido.text = getString(
            R.string.confirmar_pedido_total_formato,
            PedidoManager.totalPedido()
        )
    }

    private fun mostrarDatosEntrega() {
        if (tipoEntrega.equals(TIPO_ENTREGA_DELIVERY, ignoreCase = true)) {
            radioDelivery.isChecked = true
            mostrarDatosDelivery()
        } else {
            radioRecojo.isChecked = true
            mostrarDatosRecojo()
        }

        tvMetodoPago.text = getString(
            R.string.confirmar_pedido_contra_entrega
        )
    }

    private fun mostrarDatosDelivery() {
        tvDireccion.text = direccion.ifBlank {
            getString(R.string.confirmar_pedido_direccion_no_especificada)
        }

        tvReferencia.text = when {
            referencia.isNotBlank() -> referencia
            observacion.isNotBlank() -> observacion
            else -> getString(R.string.confirmar_pedido_sin_observaciones)
        }

        tvTelefono.text = telefono.ifBlank {
            getString(R.string.confirmar_pedido_telefono_no_especificado)
        }
    }

    private fun mostrarDatosRecojo() {
        tvDireccion.text = getString(
            R.string.confirmar_pedido_recojo_tienda
        )

        tvReferencia.text = observacion.ifBlank {
            getString(R.string.confirmar_pedido_sin_observaciones)
        }

        tvTelefono.text = telefono.ifBlank {
            getString(R.string.confirmar_pedido_telefono_no_especificado)
        }
    }

    private fun configurarEntrega() {
        radioGroupEntrega.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioDelivery -> {
                    tipoEntrega = TIPO_ENTREGA_DELIVERY
                    mostrarDatosDelivery()
                }

                R.id.radioRecojo -> {
                    tipoEntrega = TIPO_ENTREGA_RECOJO
                    mostrarDatosRecojo()
                }
            }
        }
    }

    private fun configurarBoton() {
        btnContinuarPago.setOnClickListener {
            confirmarPedido()
        }
    }

    private fun confirmarPedido() {
        if (confirmandoPedido) return

        val usuario = auth.currentUser ?: run {
            Toast.makeText(
                this,
                R.string.confirmar_pedido_sesion_no_activa,
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val pedido = PedidoManager.pedido.map { item ->
            item.copy(entradas = item.entradas.map { it.copy() })
        }

        if (pedido.isEmpty()) {
            Toast.makeText(
                this,
                R.string.confirmar_pedido_carrito_vacio,
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (pedido.size > MAX_PRODUCTOS_POR_SOLICITUD) {
            Toast.makeText(
                this,
                R.string.solicitud_error_demasiados_productos,
                Toast.LENGTH_LONG
            ).show()
            return
        }

        if (
            pedido.any { item ->
                item.cantidad !in 1..MAX_CANTIDAD_POR_PRODUCTO ||
                        item.entradas.any {
                            it.cantidad !in 1..MAX_CANTIDAD_POR_PRODUCTO
                        }
            }
        ) {
            Toast.makeText(
                this,
                R.string.solicitud_error_datos,
                Toast.LENGTH_LONG
            ).show()
            return
        }

        tipoEntrega = when (radioGroupEntrega.checkedRadioButtonId) {
            R.id.radioDelivery -> TIPO_ENTREGA_DELIVERY
            R.id.radioRecojo -> TIPO_ENTREGA_RECOJO
            else -> {
                Toast.makeText(
                    this,
                    R.string.confirmar_pedido_seleccionar_entrega,
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
        }

        if (tipoEntrega == TIPO_ENTREGA_DELIVERY) {
            if (direccion.isBlank()) {
                Toast.makeText(
                    this,
                    R.string.confirmar_pedido_direccion_no_encontrada,
                    Toast.LENGTH_LONG
                ).show()
                return
            }

            if (telefono.isBlank()) {
                Toast.makeText(
                    this,
                    R.string.confirmar_pedido_telefono_no_encontrado,
                    Toast.LENGTH_LONG
                ).show()
                return
            }
        }

        if (
            direccion.length > 300 ||
            referencia.length > 300 ||
            telefono.length > 25 ||
            observacion.length > 500
        ) {
            Toast.makeText(
                this,
                R.string.solicitud_error_datos,
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val totalEstimado = pedido.sumOf { it.subtotal() }
        if (!totalEstimado.isFinite() || totalEstimado <= 0.0) {
            Toast.makeText(
                this,
                R.string.solicitud_error_datos,
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val productos = pedido.map { item ->
            hashMapOf(
                "id" to item.id,
                "nombre" to item.nombre.take(MAX_LONGITUD_NOMBRE),
                "tipo" to item.tipo.name,
                "cantidad" to item.cantidad,
                "precioEstimado" to item.precio,
                "entradas" to item.entradas.map { entrada ->
                    hashMapOf(
                        "id" to entrada.id,
                        "nombre" to entrada.nombre.take(MAX_LONGITUD_NOMBRE),
                        "cantidad" to entrada.cantidad,
                        "precioEstimado" to entrada.precioUnitario
                    )
                }
            )
        }

        val datos = hashMapOf(
            "usuarioId" to usuario.uid,
            "nombreUsuario" to nombreUsuario
                .ifBlank { getString(R.string.confirmar_pedido_cliente) }
                .take(MAX_LONGITUD_NOMBRE),
            "correo" to usuario.email.orEmpty().take(MAX_LONGITUD_CORREO),
            "productos" to productos,
            "totalEstimado" to totalEstimado,
            "tipoEntrega" to tipoEntrega,
            "direccion" to if (tipoEntrega == TIPO_ENTREGA_DELIVERY) {
                direccion
            } else {
                TIPO_ENTREGA_RECOJO
            },
            "referencia" to referencia,
            "telefono" to telefono,
            "observacion" to observacion,
            "metodoPago" to METODO_PAGO_CONTRA_ENTREGA,
            "estado" to ESTADO_SOLICITUD_PENDIENTE,
            "fecha" to FieldValue.serverTimestamp()
        )

        val latitudSolicitud = latitud
        val longitudSolicitud = longitud

        if (
            tipoEntrega == TIPO_ENTREGA_DELIVERY &&
            latitudSolicitud != null &&
            longitudSolicitud != null &&
            ubicacionMaps.isNotBlank()
        ) {
            datos["latitud"] = latitudSolicitud
            datos["longitud"] = longitudSolicitud
            datos["ubicacionMaps"] = ubicacionMaps.take(MAX_LONGITUD_MAPS)
        }

        bloquearBoton()

        db.collection(COLECCION_SOLICITUDES)
            .add(datos)
            .addOnSuccessListener { documento ->
                solicitudEnviada(documento.id, totalEstimado)
            }
            .addOnFailureListener(::finalizarConError)
    }

    private fun bloquearBoton() {
        confirmandoPedido = true
        btnContinuarPago.isEnabled = false
        btnContinuarPago.alpha = 0.5f
        btnContinuarPago.text = getString(R.string.solicitud_enviando)
    }

    private fun solicitudEnviada(
        idSolicitud: String,
        totalEstimado: Double
    ) {
        PedidoManager.limpiar()

        startActivity(
            Intent(this, ActivityPedidoConfirmado::class.java).apply {
                putExtra(EXTRA_ID_SOLICITUD, idSolicitud)
                putExtra(EXTRA_TOTAL_ESTIMADO, totalEstimado)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )

        finish()
    }

    private fun finalizarConError(error: Exception) {
        confirmandoPedido = false
        btnContinuarPago.isEnabled = true
        btnContinuarPago.alpha = 1f
        btnContinuarPago.text = getString(R.string.solicitud_boton_enviar)

        Log.e(TAG, "No se pudo enviar la solicitud de pedido", error)

        Toast.makeText(
            this,
            R.string.solicitud_error_envio,
            Toast.LENGTH_LONG
        ).show()
    }

    private companion object {
        const val TAG = "CONFIRMAR_PEDIDO"

        const val EXTRA_NOMBRE = "nombre"
        const val EXTRA_TELEFONO = "teléfono"
        const val EXTRA_DIRECCION = "dirección"
        const val EXTRA_OBSERVACION = "observación"
        const val EXTRA_REFERENCIA = "referencia"
        const val EXTRA_TIPO_ENTREGA = "tipoEntrega"

        const val EXTRA_LATITUD = "latitud"
        const val EXTRA_LONGITUD = "longitud"
        const val EXTRA_UBICACION_MAPS = "ubicaciónMaps"

        const val EXTRA_ID_SOLICITUD = "extra_id_solicitud"
        const val EXTRA_TOTAL_ESTIMADO = "extra_total_estimado"

        const val TIPO_ENTREGA_DELIVERY = "Delivery"
        const val TIPO_ENTREGA_RECOJO = "Recojo en tienda"

        const val METODO_PAGO_CONTRA_ENTREGA = "Contra entrega"
        const val ESTADO_SOLICITUD_PENDIENTE = "Pendiente de aprobacion"

        const val COLECCION_USUARIOS = "usuarios"
        const val COLECCION_SOLICITUDES = "solicitudesPedidos"
        const val CAMPO_NOMBRE = "nombre"

        const val MAX_PRODUCTOS_POR_SOLICITUD = 100
        const val MAX_CANTIDAD_POR_PRODUCTO = 99
        const val MAX_LONGITUD_NOMBRE = 100
        const val MAX_LONGITUD_CORREO = 254
        const val MAX_LONGITUD_MAPS = 500
    }
}