
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

    private class StockInsuficienteException(
        val nombreProducto: String,
        val disponible: Long,
        val solicitado: Int
    ) : Exception()

    // =========================================================
    // FIREBASE
    // =========================================================

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    // =========================================================
    // COMPONENTES
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
    // DATOS DEL PEDIDO
    // =========================================================

    private var nombreUsuario = ""

    private var tipoEntrega = TIPO_ENTREGA_DELIVERY

    private var direccion = ""

    private var referencia = ""

    private var telefono = ""

    // =========================================================
    // UBICACIÓN GPS OPCIONAL
    // =========================================================

    private var latitud: Double? = null

    private var longitud: Double? = null

    private var ubicacionMaps = ""

    private var observacion = ""

    private var confirmandoPedido = false

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
    // INICIALIZAR COMPONENTES
    // =========================================================

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

    // =========================================================
    // RECIBIR DATOS DE ENTREGA
    // =========================================================

    private fun recibirDatos() {
        nombreUsuario = intent
            .getStringExtra(EXTRA_NOMBRE)
            ?.trim()
            .orEmpty()

        telefono = intent
            .getStringExtra(EXTRA_TELEFONO)
            ?.trim()
            .orEmpty()

        direccion = intent
            .getStringExtra(EXTRA_DIRECCION)
            ?.trim()
            .orEmpty()

        observacion = intent
            .getStringExtra(EXTRA_OBSERVACION)
            ?.trim()
            .orEmpty()

        referencia = intent
            .getStringExtra(EXTRA_REFERENCIA)
            ?.trim()
            .orEmpty()

        tipoEntrega = intent
            .getStringExtra(EXTRA_TIPO_ENTREGA)
            ?.trim()
            .orEmpty()
            .ifBlank {
                TIPO_ENTREGA_DELIVERY
            }

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

        ubicacionMaps = intent
            .getStringExtra(EXTRA_UBICACION_MAPS)
            ?.trim()
            .orEmpty()
    }

    // =========================================================
    // CARGAR DATOS DEL USUARIO
    // =========================================================

    private fun cargarDatosUsuario() {
        val usuario = auth.currentUser

        if (usuario == null) {
            Toast.makeText(
                this,
                getString(R.string.confirmar_pedido_sesion_no_activa),
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
                nombreUsuario = documento
                    .getString(CAMPO_NOMBRE)
                    ?.trim()
                    .orEmpty()
                    .ifBlank {
                        getString(R.string.confirmar_pedido_cliente)
                    }

                tvNombreUsuario.text = nombreUsuario
            }
            .addOnFailureListener {
                nombreUsuario =
                    getString(R.string.confirmar_pedido_cliente)

                tvNombreUsuario.text = nombreUsuario
            }
    }

    // =========================================================
    // RESUMEN DEL PEDIDO
    // =========================================================

    private fun cargarResumenPedido() {
        val pedido = PedidoManager.pedido

        if (pedido.isEmpty()) {
            Toast.makeText(
                this,
                getString(R.string.confirmar_pedido_carrito_vacio),
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

    // =========================================================
    // MOSTRAR DATOS DE ENTREGA
    // =========================================================

    private fun mostrarDatosEntrega() {
        if (tipoEntrega.equals(
                TIPO_ENTREGA_DELIVERY,
                ignoreCase = true
            )
        ) {
            radioDelivery.isChecked = true
            mostrarDatosDelivery()
        } else {
            radioRecojo.isChecked = true
            mostrarDatosRecojo()
        }

        tvMetodoPago.text =
            getString(R.string.confirmar_pedido_contra_entrega)
    }

    private fun mostrarDatosDelivery() {
        tvDireccion.text = direccion.ifBlank {
            getString(
                R.string.confirmar_pedido_direccion_no_especificada
            )
        }

        tvReferencia.text = when {
            referencia.isNotBlank() -> referencia
            observacion.isNotBlank() -> observacion
            else -> getString(
                R.string.confirmar_pedido_sin_observaciones
            )
        }

        tvTelefono.text = telefono.ifBlank {
            getString(
                R.string.confirmar_pedido_telefono_no_especificado
            )
        }
    }

    private fun mostrarDatosRecojo() {
        tvDireccion.text =
            getString(R.string.confirmar_pedido_recojo_tienda)

        tvReferencia.text = observacion.ifBlank {
            getString(
                R.string.confirmar_pedido_sin_observaciones
            )
        }

        tvTelefono.text = telefono.ifBlank {
            getString(
                R.string.confirmar_pedido_telefono_no_especificado
            )
        }
    }

    // =========================================================
    // TIPO DE ENTREGA
    // =========================================================

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

    // =========================================================
    // BOTÓN DE CONFIRMACIÓN
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
        if (confirmandoPedido) return

        val usuario = auth.currentUser

        if (usuario == null) {
            Toast.makeText(
                this,
                getString(R.string.confirmar_pedido_sesion_no_activa),
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // Tomar una copia del carrito para que esta operación
        // utilice cantidades y productos consistentes.
        val pedido = PedidoManager.pedido.map { item ->
            item.copy(
                entradas = item.entradas.map { entrada ->
                    entrada.copy()
                }
            )
        }

        if (pedido.isEmpty()) {
            Toast.makeText(
                this,
                getString(R.string.confirmar_pedido_carrito_vacio),
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (pedido.any { it.cantidad <= 0 }) {
            Toast.makeText(
                this,
                getString(R.string.confirmar_pedido_error_generico),
                Toast.LENGTH_LONG
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

        tipoEntrega = when (opcionSeleccionada) {
            R.id.radioDelivery -> TIPO_ENTREGA_DELIVERY
            R.id.radioRecojo -> TIPO_ENTREGA_RECOJO
            else -> return
        }

        if (tipoEntrega == TIPO_ENTREGA_DELIVERY) {
            if (direccion.isBlank()) {
                Toast.makeText(
                    this,
                    getString(
                        R.string.confirmar_pedido_direccion_no_encontrada
                    ),
                    Toast.LENGTH_SHORT
                ).show()

                return
            }

            if (telefono.isBlank()) {
                Toast.makeText(
                    this,
                    getString(
                        R.string.confirmar_pedido_telefono_no_encontrado
                    ),
                    Toast.LENGTH_SHORT
                ).show()

                return
            }
        }

        bloquearBoton()

        val productos: List<HashMap<String, Any>> =
            pedido.map { item ->

                val entradas: List<HashMap<String, Any>> =
                    item.entradas.map { entrada ->
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
                    "precioBaseMenu" to
                            (item.precioBaseMenu ?: 0.0),
                    "entradas" to entradas
                )
            }

        val total = pedido.sumOf { it.subtotal() }

        buscarReferenciasStock(
            pedido = pedido,

            onSuccess = { referencias ->
                guardarPedidoYValidarStock(
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
        confirmandoPedido = true

        btnContinuarPago.isEnabled = false
        btnContinuarPago.alpha = 0.5f

        btnContinuarPago.text =
            getString(R.string.confirmar_pedido_confirmando)
    }

    // =========================================================
    // PREPARAR VALIDACIÓN DE STOCK
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
                    agregarSolicitud(
                        coleccion = COLECCION_MENU,
                        id = item.id,
                        nombre = item.nombre,
                        cantidad = item.cantidad
                    )

                    // Válida también las entradas asociadas al menú.
                    item.entradas.forEach { entrada ->
                        agregarSolicitud(
                            coleccion = COLECCION_ENTRADAS,
                            id = entrada.id,
                            nombre = entrada.nombre,
                            cantidad = entrada.cantidad * item.cantidad
                        )
                    }
                }

                TipoPedido.CENA -> {
                    agregarSolicitud(
                        coleccion = COLECCION_CENAS,
                        id = item.id,
                        nombre = item.nombre,
                        cantidad = item.cantidad
                    )
                }

                TipoPedido.ENTRADA -> {
                    agregarSolicitud(
                        coleccion = COLECCION_ENTRADAS,
                        id = item.id,
                        nombre = item.nombre,
                        cantidad = item.cantidad
                    )
                }

                // Los extras todavía no cuentan con control de stock.
                TipoPedido.EXTRA -> Unit
            }
        }

        val productosConStock =
            solicitudesAgrupadas.values.toList()

        val referencias =
            mutableMapOf<SolicitudStock, DocumentReference>()

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
    // BUSCAR REFERENCIAS DE PRODUCTOS EN FIRESTORE
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

        val item = productos[posicion]

        db.collection(item.coleccion)
            .whereEqualTo(CAMPO_ID, item.idProducto)
            .limit(1)
            .get()
            .addOnSuccessListener { resultado ->

                val documento = resultado.documents.firstOrNull()

                if (documento == null) {
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

                referencias[item] = documento.reference

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
    // VALIDAR STOCK Y GUARDAR PEDIDO
    // =========================================================

    /**
     * Comprueba el stock actual mediante una transacción y crea
     * el pedido pendiente.
     *
     * IMPORTANTE:
     * Esta función NO descuenta ni modifica el stock.
     * La actualización de inventario queda a cargo del flujo
     * administrativo autorizado.
     */
    private fun guardarPedidoYValidarStock(
        usuarioId: String,
        correo: String,
        productos: List<HashMap<String, Any>>,
        total: Double,
        referenciasStock: Map<SolicitudStock, DocumentReference>
    ) {
        val referenciaPedido =
            db.collection(COLECCION_PEDIDOS).document()

        // Referencia numérica temporal, sin escribir un contador
        // global desde la aplicación del usuario.
        val numeroPedido = System.currentTimeMillis()

        db.runTransaction { transaction ->

            // Primero se leen y validan todos los productos.
            // No se modifica ningún documento de inventario.
            for ((item, referenciaStock) in referenciasStock) {

                val snapshot = transaction.get(referenciaStock)

                if (!snapshot.exists()) {
                    throw IllegalStateException(
                        getString(
                            R.string.confirmar_pedido_producto_ya_no_existe,
                            item.nombre
                        )
                    )
                }

                val stock = snapshot.getLong(CAMPO_STOCK)
                    ?: throw IllegalStateException(
                        getString(
                            R.string.confirmar_pedido_stock_invalido,
                            item.nombre
                        )
                    )

                if (stock < 0L) {
                    throw IllegalStateException(
                        getString(
                            R.string.confirmar_pedido_stock_invalido,
                            item.nombre
                        )
                    )
                }

                if (stock < item.cantidad.toLong()) {
                    throw StockInsuficienteException(
                        nombreProducto = item.nombre,
                        disponible = stock,
                        solicitado = item.cantidad
                    )
                }
            }

            val datosPedido: HashMap<String, Any> =
                hashMapOf(
                    "numeroPedido" to numeroPedido,
                    "usuarioId" to usuarioId,

                    "nombreUsuario" to nombreUsuario.ifBlank {
                        getString(R.string.confirmar_pedido_cliente)
                    },

                    "correo" to correo,
                    "productos" to productos,
                    "total" to total,

                    "tipoEntrega" to tipoEntrega,

                    "dirección" to if (
                        tipoEntrega == TIPO_ENTREGA_DELIVERY
                    ) {
                        direccion
                    } else {
                        TIPO_ENTREGA_RECOJO
                    },

                    "referencia" to referencia,
                    "teléfono" to telefono,
                    "observación" to observacion,

                    "métodoPago" to METODO_PAGO_CONTRA_ENTREGA,
                    "estadoPago" to ESTADO_PENDIENTE,
                    "estadoPedido" to ESTADO_PENDIENTE,

                    "fecha" to FieldValue.serverTimestamp()
                )

            // Guarda ubicación únicamente si es Delivery
            // y el cliente proporcionó coordenadas y enlace.
            val latitudPedido = latitud
            val longitudPedido = longitud

            if (
                tipoEntrega == TIPO_ENTREGA_DELIVERY &&
                latitudPedido != null &&
                longitudPedido != null &&
                ubicacionMaps.isNotBlank()
            ) {
                datosPedido["latitud"] = latitudPedido
                datosPedido["longitud"] = longitudPedido
                datosPedido["ubicaciónMaps"] = ubicacionMaps
            }

            // Única escritura de negocio de esta transacción:
            // crear el pedido. No se escribe stock ni contador.
            transaction.set(
                referenciaPedido,
                datosPedido
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
    // MANEJO DE ERRORES
    // =========================================================

    private fun finalizarConError(exception: Exception) {
        confirmandoPedido = false

        btnContinuarPago.isEnabled = true
        btnContinuarPago.alpha = 1f

        btnContinuarPago.text =
            getString(R.string.confirmar_pedido_boton_confirmar)

        val errorStock = buscarErrorStock(exception)

        val mensaje = if (errorStock != null) {
            getString(
                R.string.confirmar_pedido_stock_insuficiente,
                errorStock.nombreProducto,
                errorStock.disponible.toString(),
                errorStock.solicitado.toString()
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

    private fun buscarErrorStock(
        error: Throwable
    ): StockInsuficienteException? {
        var actual: Throwable? = error

        while (actual != null) {
            if (actual is StockInsuficienteException) {
                return actual
            }

            actual = actual.cause
        }

        return null
    }

    // =========================================================
    // CONSTANTES
    // =========================================================

    private companion object {

        const val EXTRA_NOMBRE = "nombre"
        const val EXTRA_TELEFONO = "teléfono"
        const val EXTRA_DIRECCION = "dirección"
        const val EXTRA_OBSERVACION = "observación"
        const val EXTRA_REFERENCIA = "referencia"
        const val EXTRA_TIPO_ENTREGA = "tipoEntrega"

        const val EXTRA_LATITUD = "latitud"
        const val EXTRA_LONGITUD = "longitud"
        const val EXTRA_UBICACION_MAPS = "ubicaciónMaps"

        const val TIPO_ENTREGA_DELIVERY = "Delivery"
        const val TIPO_ENTREGA_RECOJO = "Recojo en tienda"

        const val METODO_PAGO_CONTRA_ENTREGA = "Contra entrega"
        const val ESTADO_PENDIENTE = "Pendiente"

        const val COLECCION_USUARIOS = "usuarios"
        const val COLECCION_MENU = "menu"
        const val COLECCION_CENAS = "cenas"
        const val COLECCION_ENTRADAS = "entradas"
        const val COLECCION_PEDIDOS = "pedidos"

        const val CAMPO_ID = "id"
        const val CAMPO_NOMBRE = "nombre"
        const val CAMPO_STOCK = "stock"
    }
}