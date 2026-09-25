package com.deiapp.appzetar.usuario.pedidos

import android.content.Intent
import android.os.Bundle
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
import com.deiapp.appzetar.usuario.ActivityMenuUsuario
import com.deiapp.appzetar.usuario.carrito.PedidoItem
import com.deiapp.appzetar.usuario.carrito.PedidoManager
import com.deiapp.appzetar.usuario.carrito.TipoPedido
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class ActivityConfirmarPedido : AppCompatActivity() {

    private data class SolicitudStock(
        val coleccion: String,
        val idProducto: Int,
        val nombre: String,
        val cantidad: Int
    )

    // =========================================================
    // FIREBASE
    // =========================================================

    private val auth =
        FirebaseAuth.getInstance()

    private val db =
        FirebaseFirestore.getInstance()

    // =========================================================
    // COMPONENTE
    // =========================================================

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

    // =========================================================
    // DATO DEL PEDIDO
    // =========================================================

    private var nombreUsuario =
        ""

    private var tipoEntrega =
        TIPO_ENTREGA_DELIVERY

    private var direccion =
        ""

    private var referencia =
        ""

    private var telefono =
        ""

    private var observacion =
        ""

    private var confirmandoPedido =
        false

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
            R.layout.activity_confirmar_pedido
        )

        aplicarInsets()
        initComponent()
        recibirDatos()
        cargarDatosUsuario()
        cargarResumenPedido()
        mostrarDatosEntrega()
        configurarEntrega()
        configurarBoton()
    }

    // =========================================================
    // INSETS
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
    // COMPONENTE
    // =========================================================

    private fun initComponent() {

        tvNombreUsuario =
            findViewById(R.id.tvNombreUsuario)

        tvCorreoUsuario =
            findViewById(R.id.tvCorreoUsuario)

        tvResumenProductos =
            findViewById(R.id.tvResumenProductos)

        tvTotalPedido =
            findViewById(R.id.tvTotalPedido)

        radioGroupEntrega =
            findViewById(R.id.radioGroupEntrega)

        radioDelivery =
            findViewById(R.id.radioDelivery)

        radioRecojo =
            findViewById(R.id.radioRecojo)

        tvDireccion =
            findViewById(R.id.tvDireccion)

        tvReferencia =
            findViewById(R.id.tvReferencia)

        tvTelefono =
            findViewById(R.id.tvTelefono)

        tvMetodoPago =
            findViewById(R.id.tvMetodoPago)

        btnContinuarPago =
            findViewById(R.id.btnContinuarPago)
    }

    // =========================================================
    // RECIBIR DATO
    // =========================================================

    private fun recibirDatos() {

        nombreUsuario =
            intent.getStringExtra(EXTRA_NOMBRE)
                ?.trim()
                .orEmpty()

        telefono =
            intent.getStringExtra(EXTRA_TELEFONO)
                ?.trim()
                .orEmpty()

        direccion =
            intent.getStringExtra(EXTRA_DIRECCION)
                ?.trim()
                .orEmpty()

        observacion =
            intent.getStringExtra(EXTRA_OBSERVACION)
                ?.trim()
                .orEmpty()

        referencia =
            intent.getStringExtra(EXTRA_REFERENCIA)
                ?.trim()
                .orEmpty()

        tipoEntrega =
            intent.getStringExtra(EXTRA_TIPO_ENTREGA)
                ?.trim()
                .orEmpty()
                .ifBlank {
                    TIPO_ENTREGA_DELIVERY
                }

    }

    // =========================================================
    // DATO DEL USER
    // =========================================================

    private fun cargarDatosUsuario() {

        val usuario =
            auth.currentUser

        if (usuario == null) {

            Toast.makeText(
                this,
                getString(R.string.confirmar_pedido_sesion_no_activa),
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        tvCorreoUsuario.text =
            usuario.email ?: getString(R.string.confirmar_pedido_sin_correo)

        if (nombreUsuario.isNotBlank()) {

            tvNombreUsuario.text =
                nombreUsuario

            return
        }

        db.collection(COLECCION_USUARIOS)
            .document(usuario.uid)
            .get()
            .addOnSuccessListener { documento ->

                nombreUsuario =
                    documento.getString(CAMPO_NOMBRE)
                        ?.trim()
                        .orEmpty()
                        .ifBlank {
                            getString(R.string.confirmar_pedido_cliente)
                        }

                tvNombreUsuario.text =
                    nombreUsuario
            }
            .addOnFailureListener {

                nombreUsuario =
                    getString(R.string.confirmar_pedido_cliente)

                tvNombreUsuario.text =
                    nombreUsuario
            }
    }

    // =========================================================
    // RESUME
    // =========================================================

    private fun cargarResumenPedido() {

        val pedido =
            PedidoManager.pedido

        if (pedido.isEmpty()) {

            Toast.makeText(
                this,
                getString(R.string.confirmar_pedido_carrito_vacio),
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        val cantidad =
            PedidoManager.cantidadTotal()

        tvResumenProductos.text = resources.getQuantityString(
            R.plurals.confirmar_pedido_cantidad_productos,
            cantidad,
            cantidad
        )

        val total = PedidoManager.totalPedido()

        tvTotalPedido.text = getString(
            R.string.confirmar_pedido_total_formato,
            total
        )
    }

    // =========================================================
    // MOSTRA ENTREGA
    // =========================================================

    private fun mostrarDatosEntrega() {

        if (tipoEntrega.equals(TIPO_ENTREGA_DELIVERY, ignoreCase = true)) {

            radioDelivery.isChecked =
                true

            mostrarDatosDelivery()

        } else {

            radioRecojo.isChecked =
                true

            mostrarDatosRecojo()
        }

        tvMetodoPago.text =
            getString(R.string.confirmar_pedido_contra_entrega)
    }

    private fun mostrarDatosDelivery() {

        tvDireccion.text =
            direccion.ifBlank {
                getString(R.string.confirmar_pedido_direccion_no_especificada)
            }

        /*
         * Si todavía no tienes un campo separado para referencia,
         * aquí se muestra la observación.
         */
        tvReferencia.text =
            when {
                referencia.isNotBlank() -> referencia
                observacion.isNotBlank() -> observacion
                else -> getString(R.string.confirmar_pedido_sin_observaciones)
            }

        tvTelefono.text =
            telefono.ifBlank {
                getString(R.string.confirmar_pedido_telefono_no_especificado)
            }
    }

    private fun mostrarDatosRecojo() {

        tvDireccion.text =
            getString(R.string.confirmar_pedido_recojo_tienda)

        tvReferencia.text =
            observacion.ifBlank {
                getString(R.string.confirmar_pedido_sin_observaciones)
            }

        tvTelefono.text =
            telefono.ifBlank {
                getString(R.string.confirmar_pedido_telefono_no_especificado)
            }
    }

    // =========================================================
    // TIPO DE ENTREGA
    // =========================================================

    private fun configurarEntrega() {

        radioGroupEntrega.setOnCheckedChangeListener {
                _,
                checkedId ->

            when (checkedId) {

                R.id.radioDelivery -> {

                    tipoEntrega =
                        TIPO_ENTREGA_DELIVERY

                    mostrarDatosDelivery()
                }

                R.id.radioRecojo -> {

                    tipoEntrega =
                        TIPO_ENTREGA_RECOJO

                    mostrarDatosRecojo()
                }
            }
        }
    }

    // =========================================================
    // BOTÓN
    // =========================================================

    private fun configurarBoton() {

        btnContinuarPago.setOnClickListener {

            confirmarPedido()
        }
    }

    // =========================================================
    // CONFIRMAR PEDIDO
    // =========================================================

    private fun confirmarPedido() {

        if (confirmandoPedido) {
            return
        }

        val usuario =
            auth.currentUser

        if (usuario == null) {

            Toast.makeText(
                this,
                getString(R.string.confirmar_pedido_sesion_no_activa),
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val pedido =
            PedidoManager.pedido

        if (pedido.isEmpty()) {

            Toast.makeText(
                this,
                getString(R.string.confirmar_pedido_carrito_vacio),
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val opcionSeleccionada =
            radioGroupEntrega.checkedRadioButtonId

        if (opcionSeleccionada == -1) {

            Toast.makeText(
                this,
                getString(R.string.confirmar_pedido_seleccionar_entrega),
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        tipoEntrega =
            when (opcionSeleccionada) {

                R.id.radioDelivery ->
                    TIPO_ENTREGA_DELIVERY

                R.id.radioRecojo ->
                    TIPO_ENTREGA_RECOJO

                else ->
                    return
            }

        if (tipoEntrega == TIPO_ENTREGA_DELIVERY) {

            if (direccion.isBlank()) {

                Toast.makeText(
                    this,
                    getString(R.string.confirmar_pedido_direccion_no_encontrada),
                    Toast.LENGTH_SHORT
                ).show()

                return
            }

            if (telefono.isBlank()) {

                Toast.makeText(
                    this,
                    getString(R.string.confirmar_pedido_telefono_no_encontrado),
                    Toast.LENGTH_SHORT
                ).show()

                return
            }
        }

        bloquearBoton()

        val productos: List<HashMap<String, Any>> = pedido.map { item ->
            val entradas: List<HashMap<String, Any>> = item.entradas.map { entrada ->
                hashMapOf(
                    "id" to entrada.id,
                    "nombre" to entrada.nombre,
                    "cantidad" to entrada.cantidad,
                    "precioUnitario" to entrada.precioUnitario
                )
            }

            hashMapOf(
                "id" to item.id,
                "nombre" to item.nombre,
                "precio" to item.precio,
                "cantidad" to item.cantidad,
                "tipo" to item.tipo.name,
                "precioBaseMenu" to (item.precioBaseMenu ?: 0.0),
                "entradas" to entradas
            )
        }

        val total = PedidoManager.totalPedido()

        buscarReferenciasStock(
            pedido = pedido,

            onSuccess = { referencias ->

                guardarPedidoYDescontarStock(
                    usuarioId = usuario.uid,
                    correo = usuario.email.orEmpty(),
                    productos = productos,
                    total = total,
                    referenciasStock = referencias
                )
            },

            onError = { exception ->

                finalizarConError(exception)
            }
        )
    }

    // =========================================================
    // BLOQUEAR BOTÓN
    // =========================================================

    private fun bloquearBoton() {

        confirmandoPedido =
            true

        btnContinuarPago.isEnabled =
            false

        btnContinuarPago.alpha =
            0.5f

        btnContinuarPago.text =
            getString(R.string.confirmar_pedido_confirmando)
    }

    // =========================================================
    // BUSCAR REFERENCIAS DE STOCK
    // =========================================================

    private fun buscarReferenciasStock(
        pedido: List<PedidoItem>,
        onSuccess: (Map<SolicitudStock, DocumentReference>) -> Unit,
        onError: (Exception) -> Unit
    ) {

        val solicitudesAgrupadas =
            mutableMapOf<Pair<String, Int>, SolicitudStock>()

        fun agregarSolicitud(
            coleccion: String,
            id: Int,
            nombre: String,
            cantidad: Int
        ) {
            if (cantidad <= 0) return

            val clave = coleccion to id
            val existente = solicitudesAgrupadas[clave]

            solicitudesAgrupadas[clave] = SolicitudStock(
                coleccion = coleccion,
                idProducto = id,
                nombre = nombre,
                cantidad = (existente?.cantidad ?: 0) + cantidad
            )
        }

        pedido.forEach { item ->
            when (item.tipo) {
                TipoPedido.MENU -> {
                    agregarSolicitud(COLECCION_MENU, item.id, item.nombre, item.cantidad)

                    item.entradas.forEach { entrada ->
                        agregarSolicitud(
                            coleccion = COLECCION_ENTRADAS,
                            id = entrada.id,
                            nombre = entrada.nombre,
                            cantidad = entrada.cantidad * item.cantidad
                        )
                    }
                }

                // Compatibilidad temporal con entradas antiguas independientes.
                TipoPedido.ENTRADA ->
                    agregarSolicitud(COLECCION_ENTRADAS, item.id, item.nombre, item.cantidad)

                TipoPedido.EXTRA -> Unit
            }
        }

        val productosConStock = solicitudesAgrupadas.values.toList()
        val referencias = mutableMapOf<SolicitudStock, DocumentReference>()

        if (productosConStock.isEmpty()) {

            onSuccess(referencias)
            return
        }

        buscarReferenciaRecursiva(
            productos = productosConStock,
            posicion = 0,
            referencias = referencias,
            onSuccess = onSuccess,
            onError = onError
        )
    }

    // =========================================================
    // BÚSQUEDA RECURS
    // =========================================================

    private fun buscarReferenciaRecursiva(
        productos: List<SolicitudStock>,
        posicion: Int,
        referencias: MutableMap<SolicitudStock, DocumentReference>,
        onSuccess: (Map<SolicitudStock, DocumentReference>) -> Unit,
        onError: (Exception) -> Unit
    ) {

        if (posicion >= productos.size) {

            onSuccess(referencias)
            return
        }

        val item =
            productos[posicion]

        db.collection(item.coleccion)
            .whereEqualTo(CAMPO_ID, item.idProducto)
            .limit(1)
            .get()
            .addOnSuccessListener { resultado ->

                if (resultado.isEmpty) {

                    onError(
                        IllegalStateException(
                            getString(
                                R.string.confirmar_pedido_producto_no_encontrado,
                                item.nombre
                            )
                        )
                    )

                    return@addOnSuccessListener
                }

                referencias[item] =
                    resultado.documents
                        .first()
                        .reference

                buscarReferenciaRecursiva(
                    productos = productos,
                    posicion = posicion + 1,
                    referencias = referencias,
                    onSuccess = onSuccess,
                    onError = onError
                )
            }
            .addOnFailureListener { exception ->

                onError(exception)
            }
    }

    // =========================================================
    // GUARDAR PEDIDO Y DES STOCK
    // =========================================================

    private fun guardarPedidoYDescontarStock(
        usuarioId: String,
        correo: String,
        productos: List<HashMap<String, Any>>,
        total: Double,
        referenciasStock: Map<SolicitudStock, DocumentReference>
    ) {

        val referenciaPedido =
            db.collection(COLECCION_PEDIDOS)
                .document()

        val contadorReferencia =
            db.collection(COLECCION_CONFIGURACION)
                .document(DOCUMENTO_CONTADOR_PEDIDOS)

        db.runTransaction { transaction ->

            val contadorSnapshot =
                transaction.get(contadorReferencia)

            val ultimoNumero =
                contadorSnapshot.getLong(CAMPO_ULTIMO_NUMERO)
                    ?: 0L

            val numeroPedido =
                ultimoNumero + 1

            val stocksActuales = mutableMapOf<SolicitudStock, Long>()

            // Primero se realizan todas las lecturas.
            for ((item, referenciaStock) in referenciasStock) {

                val snapshot =
                    transaction.get(referenciaStock)

                if (!snapshot.exists()) {

                    throw IllegalStateException(
                        getString(
                            R.string.confirmar_pedido_producto_ya_no_existe,
                            item.nombre
                        )
                    )
                }

                val stock =
                    snapshot.getLong(CAMPO_STOCK)
                        ?: throw IllegalStateException(
                            getString(
                                R.string.confirmar_pedido_stock_invalido,
                                item.nombre
                            )
                        )

                if (stock < item.cantidad) {

                    throw IllegalStateException(
                        "$ERROR_SIN_STOCK${SEPARADOR_ERROR}${item.nombre}" +
                                "$SEPARADOR_ERROR$stock$SEPARADOR_ERROR${item.cantidad}"
                    )
                }

                stocksActuales[item] =
                    stock
            }

            // Después se realizan las escrituras.
            for ((item, referenciaStock) in referenciasStock) {

                val stockActual =
                    stocksActuales[item]
                        ?: 0L

                val nuevoStock =
                    stockActual - item.cantidad

                transaction.update(
                    referenciaStock,
                    CAMPO_STOCK,
                    nuevoStock
                )
            }

            val datosPedido: HashMap<String, Any> =
                hashMapOf(

                    "numeroPedido" to numeroPedido,
                    "usuarioId" to usuarioId,

                    "nombreUsuario" to
                            nombreUsuario.ifBlank {
                                getString(R.string.confirmar_pedido_cliente)
                            },

                    "correo" to correo,
                    "productos" to productos,
                    "total" to total,

                    "tipoEntrega" to tipoEntrega,

                    "direccion" to
                            if (tipoEntrega == TIPO_ENTREGA_DELIVERY) {
                                direccion
                            } else {
                                TIPO_ENTREGA_RECOJO
                            },

                    "referencia" to referencia,
                    "telefono" to telefono,
                    "observacion" to observacion,

                    // Único método disponible actualmente
                    "metodoPago" to METODO_PAGO_CONTRA_ENTREGA,
                    "estadoPago" to ESTADO_PENDIENTE,
                    "estadoPedido" to ESTADO_PENDIENTE,

                    "fecha" to
                            FieldValue.serverTimestamp()
                )

            transaction.set(
                referenciaPedido,
                datosPedido
            )

            transaction.set(
                contadorReferencia,
                hashMapOf(
                    CAMPO_ULTIMO_NUMERO to numeroPedido
                )
            )
        }
            .addOnSuccessListener {

                pedidoConfirmado()
            }
            .addOnFailureListener { exception ->

                finalizarConError(exception)
            }
    }

    // =========================================================
    // PEDIDO CONFIRMADO
    // =========================================================

    private fun pedidoConfirmado() {

        PedidoManager.limpiar()

        Toast.makeText(
            this,
            getString(R.string.confirmar_pedido_exito),
            Toast.LENGTH_LONG
        ).show()

        val intent = Intent(
            this,
            ActivityMenuUsuario::class.java
        ).apply {

            flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        startActivity(intent)
        finish()
    }

    // =========================================================
    // MANEJO DE ERROR
    // =========================================================

    private fun finalizarConError(exception: Exception) {

        confirmandoPedido =
            false

        btnContinuarPago.isEnabled =
            true

        btnContinuarPago.alpha =
            1f

        btnContinuarPago.text =
            getString(R.string.confirmar_pedido_boton_confirmar)

        val mensaje =
            if (
                exception.message
                    ?.startsWith("$ERROR_SIN_STOCK$SEPARADOR_ERROR") == true
            ) {

                val partes =
                    exception.message
                        ?.split(SEPARADOR_ERROR)

                val producto =
                    partes?.getOrNull(1)
                        ?: getString(R.string.confirmar_pedido_producto_generico)

                val disponible =
                    partes?.getOrNull(2)
                        ?: "0"

                val solicitado =
                    partes?.getOrNull(3)
                        ?: "0"

                getString(
                    R.string.confirmar_pedido_stock_insuficiente,
                    producto,
                    disponible,
                    solicitado
                )

            } else {

                getString(R.string.confirmar_pedido_error_generico)
            }

        Toast.makeText(
            this,
            mensaje,
            Toast.LENGTH_LONG
        ).show()
    }

    private companion object {
        const val EXTRA_NOMBRE = "nombre"
        const val EXTRA_TELEFONO = "telefono"
        const val EXTRA_DIRECCION = "direccion"
        const val EXTRA_OBSERVACION = "observacion"
        const val EXTRA_REFERENCIA = "referencia"
        const val EXTRA_TIPO_ENTREGA = "tipoEntrega"

        const val TIPO_ENTREGA_DELIVERY = "Delivery"
        const val TIPO_ENTREGA_RECOJO = "Recojo en tienda"
        const val METODO_PAGO_CONTRA_ENTREGA = "Contra entrega"
        const val ESTADO_PENDIENTE = "Pendiente"

        const val COLECCION_USUARIOS = "usuarios"
        const val COLECCION_MENU = "menu"
        const val COLECCION_ENTRADAS = "entradas"
        const val COLECCION_PEDIDOS = "pedidos"
        const val COLECCION_CONFIGURACION = "configuracion"
        const val DOCUMENTO_CONTADOR_PEDIDOS = "contadorPedidos"

        const val CAMPO_ID = "id"
        const val CAMPO_NOMBRE = "nombre"
        const val CAMPO_STOCK = "stock"
        const val CAMPO_ULTIMO_NUMERO = "ultimoNumero"

        const val ERROR_SIN_STOCK = "SIN_STOCK"
        const val SEPARADOR_ERROR = ":"
    }
}