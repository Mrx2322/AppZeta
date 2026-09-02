package com.example.appzetar.Usuario

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
import com.example.appzetar.R
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class ActivityConfirmarPedido : AppCompatActivity() {

    // =========================================================
    // FIREBASE
    // =========================================================

    private val auth =
        FirebaseAuth.getInstance()

    private val db =
        FirebaseFirestore.getInstance()


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
    // DATOS RECIBIDOS
    // =========================================================

    private var tipoEntrega =
        "Delivery"

    private var metodoPago =
        "Contra entrega"

    private var direccion =
        ""

    private var referencia =
        ""

    private var telefono =
        ""


    // =========================================================
    // DATOS DEL USUARIO
    // =========================================================

    private var nombreUsuario =
        "Cliente"


    // =========================================================
    // CONTROL
    // =========================================================

    private var confirmandoPedido =
        false


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
            R.layout.activity_confirmar_pedido
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

        recibirDatos()

        cargarDatosUsuario()

        cargarResumenPedido()

        mostrarDatosEntrega()

        configurarEntrega()

        configurarBoton()
    }


    // =========================================================
    // COMPONENTES
    // =========================================================

    private fun initComponent() {

        tvNombreUsuario =
            findViewById(
                R.id.tvNombreUsuario
            )

        tvCorreoUsuario =
            findViewById(
                R.id.tvCorreoUsuario
            )

        tvResumenProductos =
            findViewById(
                R.id.tvResumenProductos
            )

        tvTotalPedido =
            findViewById(
                R.id.tvTotalPedido
            )

        radioGroupEntrega =
            findViewById(
                R.id.radioGroupEntrega
            )

        radioDelivery =
            findViewById(
                R.id.radioDelivery
            )

        radioRecojo =
            findViewById(
                R.id.radioRecojo
            )

        tvDireccion =
            findViewById(
                R.id.tvDireccion
            )

        tvReferencia =
            findViewById(
                R.id.tvReferencia
            )

        tvTelefono =
            findViewById(
                R.id.tvTelefono
            )

        tvMetodoPago =
            findViewById(
                R.id.tvMetodoPago
            )

        btnContinuarPago =
            findViewById(
                R.id.btnContinuarPago
            )
    }


    // =========================================================
    // RECIBIR DATOS
    // =========================================================

    private fun recibirDatos() {

        tipoEntrega =
            intent.getStringExtra(
                "tipoEntrega"
            )
                ?: "Delivery"


        metodoPago =
            intent.getStringExtra(
                "metodoPago"
            )
                ?: "Contra entrega"


        direccion =
            intent.getStringExtra(
                "direccion"
            )
                ?: ""


        referencia =
            intent.getStringExtra(
                "referencia"
            )
                ?: ""


        telefono =
            intent.getStringExtra(
                "telefono"
            )
                ?: ""
    }


    // =========================================================
    // CARGAR USUARIO
    // =========================================================

    private fun cargarDatosUsuario() {

        val usuario =
            auth.currentUser


        if (usuario == null) {

            Toast.makeText(
                this,
                "No hay una sesión activa",
                Toast.LENGTH_SHORT
            ).show()

            finish()

            return
        }


        tvCorreoUsuario.text =
            usuario.email
                ?: "Sin correo"


        db.collection("usuarios")
            .document(usuario.uid)
            .get()
            .addOnSuccessListener { documento ->

                if (documento.exists()) {

                    nombreUsuario =
                        documento.getString(
                            "nombre"
                        )
                            ?: "Cliente"

                } else {

                    nombreUsuario =
                        "Cliente"
                }


                if (
                    nombreUsuario.isBlank()
                ) {

                    nombreUsuario =
                        "Cliente"
                }


                tvNombreUsuario.text =
                    nombreUsuario
            }
            .addOnFailureListener {

                nombreUsuario =
                    "Cliente"

                tvNombreUsuario.text =
                    "Cliente"
            }
    }


    // =========================================================
    // RESUMEN DEL PEDIDO
    // =========================================================

    private fun cargarResumenPedido() {

        val pedido =
            PedidoManager.pedido


        if (pedido.isEmpty()) {

            Toast.makeText(
                this,
                "El carrito está vacío",
                Toast.LENGTH_SHORT
            ).show()

            finish()

            return
        }


        val cantidad =
            PedidoManager.cantidadTotal()


        tvResumenProductos.text =
            if (cantidad == 1) {

                "1 producto"

            } else {

                "$cantidad productos"
            }


        val total =
            pedido.sumOf {
                it.precio * it.cantidad
            }


        tvTotalPedido.text =
            "S/ %.2f".format(total)
    }


    // =========================================================
    // MOSTRAR DATOS DE ENTREGA
    // =========================================================

    private fun mostrarDatosEntrega() {

        if (
            tipoEntrega.equals(
                "Delivery",
                ignoreCase = true
            )
        ) {

            radioDelivery.isChecked =
                true


            tvDireccion.text =
                if (
                    direccion.isNotEmpty()
                ) {

                    direccion

                } else {

                    "No especificada"
                }


            tvReferencia.text =
                if (
                    referencia.isNotEmpty()
                ) {

                    referencia

                } else {

                    "Sin referencia"
                }


            tvTelefono.text =
                if (
                    telefono.isNotEmpty()
                ) {

                    telefono

                } else {

                    "No especificado"
                }

        } else {

            radioRecojo.isChecked =
                true


            tvDireccion.text =
                "Recojo en tienda"

            tvReferencia.text =
                "No aplica"

            tvTelefono.text =
                if (
                    telefono.isNotEmpty()
                ) {

                    telefono

                } else {

                    "No especificado"
                }
        }


        tvMetodoPago.text =
            metodoPago
    }


    // =========================================================
    // ENTREGA
    // =========================================================

    private fun configurarEntrega() {

        radioGroupEntrega.setOnCheckedChangeListener {
                _,
                checkedId ->

            when (checkedId) {

                R.id.radioDelivery -> {

                    tipoEntrega =
                        "Delivery"


                    tvDireccion.text =
                        if (
                            direccion.isNotEmpty()
                        ) {

                            direccion

                        } else {

                            "No especificada"
                        }


                    tvReferencia.text =
                        if (
                            referencia.isNotEmpty()
                        ) {

                            referencia

                        } else {

                            "Sin referencia"
                        }


                    tvTelefono.text =
                        if (
                            telefono.isNotEmpty()
                        ) {

                            telefono

                        } else {

                            "No especificado"
                        }
                }


                R.id.radioRecojo -> {

                    tipoEntrega =
                        "Recojo en tienda"


                    tvDireccion.text =
                        "Recojo en tienda"

                    tvReferencia.text =
                        "No aplica"

                    tvTelefono.text =
                        if (
                            telefono.isNotEmpty()
                        ) {

                            telefono

                        } else {

                            "No especificado"
                        }
                }
            }
        }
    }


    // =========================================================
    // BOTÓN
    // =========================================================

    private fun configurarBoton() {

        btnContinuarPago.setOnClickListener {

            if (!confirmandoPedido) {

                confirmarPedido()
            }
        }
    }


    // =========================================================
    // CONFIRMAR PEDIDO
    // =========================================================

    private fun confirmarPedido() {

        if (confirmandoPedido) {
            return
        }


        // -----------------------------------------------------
        // USUARIO
        // -----------------------------------------------------

        val usuario =
            auth.currentUser


        if (usuario == null) {

            Toast.makeText(
                this,
                "No hay una sesión activa",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        // -----------------------------------------------------
        // CARRITO
        // -----------------------------------------------------

        val pedido =
            PedidoManager.pedido


        if (pedido.isEmpty()) {

            Toast.makeText(
                this,
                "El carrito está vacío",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        // -----------------------------------------------------
        // ENTREGA
        // -----------------------------------------------------

        val opcionSeleccionada =
            radioGroupEntrega.checkedRadioButtonId


        if (
            opcionSeleccionada == -1
        ) {

            Toast.makeText(
                this,
                "Selecciona cómo deseas recibir tu pedido",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        tipoEntrega =
            when (opcionSeleccionada) {

                R.id.radioDelivery ->
                    "Delivery"

                R.id.radioRecojo ->
                    "Recojo en tienda"

                else ->
                    ""
            }


        metodoPago =
            "Contra entrega"


        // -----------------------------------------------------
        // VALIDAR DELIVERY
        // -----------------------------------------------------

        if (
            tipoEntrega.equals(
                "Delivery",
                ignoreCase = true
            )
        ) {

            if (
                direccion.isBlank()
            ) {

                Toast.makeText(
                    this,
                    "No se encontró la dirección de entrega",
                    Toast.LENGTH_SHORT
                ).show()

                return
            }


            if (
                telefono.isBlank()
            ) {

                Toast.makeText(
                    this,
                    "No se encontró el teléfono",
                    Toast.LENGTH_SHORT
                ).show()

                return
            }
        }


        // -----------------------------------------------------
        // BLOQUEAR BOTÓN
        // -----------------------------------------------------

        confirmandoPedido =
            true

        btnContinuarPago.isEnabled =
            false

        btnContinuarPago.alpha =
            0.5f

        btnContinuarPago.text =
            "CONFIRMANDO..."


        // -----------------------------------------------------
        // PRODUCTOS
        // -----------------------------------------------------

        val productos =
            pedido.map { item ->

                hashMapOf<String, Any>(

                    "id" to item.id,

                    "nombre" to item.nombre,

                    "precio" to item.precio,

                    "cantidad" to item.cantidad,

                    "tipo" to item.tipo.name
                )
            }


        // -----------------------------------------------------
        // TOTAL
        // -----------------------------------------------------

        val total =
            pedido.sumOf {
                it.precio * it.cantidad
            }


        // -----------------------------------------------------
        // BUSCAR REFERENCIAS
        // -----------------------------------------------------

        buscarReferenciasStock(
            pedido = pedido,

            onSuccess = { referenciasStock ->

                guardarPedidoYDescontarStock(
                    usuarioId = usuario.uid,
                    correo = usuario.email ?: "",
                    productos = productos,
                    total = total,
                    referenciasStock = referenciasStock
                )
            },

            onError = { exception ->

                finalizarConError(
                    exception
                )
            }
        )
    }


    // =========================================================
    // BUSCAR DOCUMENTOS DE STOCK
    // =========================================================

    private fun buscarReferenciasStock(
        pedido: List<PedidoItem>,
        onSuccess: (
            Map<PedidoItem, DocumentReference>
        ) -> Unit,
        onError: (Exception) -> Unit
    ) {

        val referencias =
            mutableMapOf<
                    PedidoItem,
                    DocumentReference
                    >()


        val productosConStock =
            pedido.filter {

                it.tipo == TipoPedido.MENU ||
                        it.tipo == TipoPedido.ENTRADA
            }


        if (
            productosConStock.isEmpty()
        ) {

            onSuccess(
                referencias
            )

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
    // BUSCAR REFERENCIA
    // =========================================================

    private fun buscarReferenciaRecursiva(
        productos: List<PedidoItem>,
        posicion: Int,
        referencias: MutableMap<
                PedidoItem,
                DocumentReference
                >,
        onSuccess: (
            Map<PedidoItem, DocumentReference>
        ) -> Unit,
        onError: (Exception) -> Unit
    ) {

        if (
            posicion >= productos.size
        ) {

            onSuccess(
                referencias
            )

            return
        }


        val item =
            productos[posicion]


        val coleccion =
            when (item.tipo) {

                TipoPedido.MENU ->
                    "menu"

                TipoPedido.ENTRADA ->
                    "entradas"

                TipoPedido.EXTRA ->
                    ""
            }


        if (
            coleccion.isEmpty()
        ) {

            buscarReferenciaRecursiva(
                productos = productos,
                posicion = posicion + 1,
                referencias = referencias,
                onSuccess = onSuccess,
                onError = onError
            )

            return
        }


        db.collection(coleccion)
            .whereEqualTo(
                "id",
                item.id
            )
            .limit(1)
            .get()
            .addOnSuccessListener { resultado ->

                if (
                    resultado.isEmpty
                ) {

                    onError(
                        IllegalStateException(
                            "No se encontró '${item.nombre}' en la colección '$coleccion' con id ${item.id}."
                        )
                    )

                    return@addOnSuccessListener
                }


                val documento =
                    resultado.documents.first()


                referencias[item] =
                    documento.reference


                buscarReferenciaRecursiva(
                    productos = productos,
                    posicion = posicion + 1,
                    referencias = referencias,
                    onSuccess = onSuccess,
                    onError = onError
                )
            }
            .addOnFailureListener { exception ->

                onError(
                    exception
                )
            }
    }


    // =========================================================
    // GUARDAR PEDIDO + DESCONTAR STOCK
    // =========================================================

    private fun guardarPedidoYDescontarStock(
        usuarioId: String,
        correo: String,
        productos: List<HashMap<String, Any>>,
        total: Double,
        referenciasStock: Map<
                PedidoItem,
                DocumentReference
                >
    ) {

        val referenciaPedido =
            db.collection("pedidos")
                .document()


        db.runTransaction { transaction ->

            // -------------------------------------------------
            // STOCK ACTUAL
            // -------------------------------------------------

            val stocksActuales =
                mutableMapOf<
                        PedidoItem,
                        Long
                        >()


            // -------------------------------------------------
            // LEER STOCK DE TODOS LOS PRODUCTOS
            // -------------------------------------------------

            for (
            entrada
            in referenciasStock.entries
            ) {

                val item =
                    entrada.key

                val referencia =
                    entrada.value


                val snapshot =
                    transaction.get(
                        referencia
                    )


                if (
                    !snapshot.exists()
                ) {

                    throw IllegalStateException(
                        "El producto '${item.nombre}' ya no existe."
                    )
                }


                val stockValue =
                    snapshot.getLong(
                        "stock"
                    )


                if (
                    stockValue == null
                ) {

                    throw IllegalStateException(
                        "El producto '${item.nombre}' no tiene un stock válido en Firebase."
                    )
                }


                val stock =
                    stockValue


                if (
                    stock < 0
                ) {

                    throw IllegalStateException(
                        "El stock de '${item.nombre}' no es válido."
                    )
                }


                if (
                    stock < item.cantidad
                ) {

                    throw IllegalStateException(
                        "SIN_STOCK:${item.nombre}:$stock:${item.cantidad}"
                    )
                }


                stocksActuales[item] =
                    stock
            }


            // -------------------------------------------------
            // DESCONTAR STOCK
            // -------------------------------------------------

            for (
            entrada
            in referenciasStock.entries
            ) {

                val item =
                    entrada.key

                val referencia =
                    entrada.value


                val stockActual =
                    stocksActuales[item]
                        ?: 0L


                val nuevoStock =
                    stockActual -
                            item.cantidad


                transaction.update(
                    referencia,
                    "stock",
                    nuevoStock
                )
            }


            // -------------------------------------------------
            // DATOS DEL PEDIDO
            // -------------------------------------------------

            val datosPedido =
                hashMapOf<String, Any>(

                    "usuarioId" to usuarioId,

                    "nombreUsuario" to nombreUsuario,

                    "correo" to correo,

                    "productos" to productos,

                    "total" to total,

                    "tipoEntrega" to tipoEntrega,

                    "direccion" to direccion,

                    "referencia" to referencia,

                    "telefono" to telefono,

                    "metodoPago" to "Contra entrega",

                    "estadoPago" to "Pendiente",

                    "estadoPedido" to "Pendiente",

                    "fecha" to
                            FieldValue.serverTimestamp()
                )


            // -------------------------------------------------
            // GUARDAR PEDIDO
            // -------------------------------------------------

            transaction.set(
                referenciaPedido,
                datosPedido
            )

        }
            .addOnSuccessListener {

                pedidoConfirmado()
            }
            .addOnFailureListener { exception ->

                finalizarConError(
                    exception
                )
            }
    }


    // =========================================================
    // PEDIDO CONFIRMADO
    // =========================================================

    private fun pedidoConfirmado() {

        // -----------------------------------------------------
        // LIMPIAR CARRITO
        // -----------------------------------------------------

        PedidoManager.limpiar()


        Toast.makeText(
            this,
            "¡Pedido confirmado correctamente! 🚀",
            Toast.LENGTH_LONG
        ).show()


        // -----------------------------------------------------
        // VOLVER AL MENÚ
        // -----------------------------------------------------

        val intent =
            Intent(
                this,
                ActivityMenuUsuario::class.java
            )


        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK


        startActivity(
            intent
        )


        finish()
    }


    // =========================================================
    // ERROR
    // =========================================================

    private fun finalizarConError(
        exception: Exception
    ) {

        confirmandoPedido =
            false

        btnContinuarPago.isEnabled =
            true

        btnContinuarPago.alpha =
            1f

        btnContinuarPago.text =
            "CONFIRMAR PEDIDO"


        val mensaje =
            when {

                exception.message
                    ?.startsWith(
                        "SIN_STOCK:"
                    ) == true -> {

                    val partes =
                        exception.message
                            ?.split(":")


                    val nombre =
                        partes
                            ?.getOrNull(1)
                            ?: "este producto"


                    val stockActual =
                        partes
                            ?.getOrNull(2)
                            ?: "0"


                    val cantidadSolicitada =
                        partes
                            ?.getOrNull(3)
                            ?: "0"


                    "No hay stock suficiente de $nombre. Disponible: $stockActual. Solicitado: $cantidadSolicitada."
                }


                exception is IllegalStateException -> {

                    exception.message
                        ?: "No se pudo confirmar el pedido."
                }


                else -> {

                    "No se pudo confirmar el pedido. Inténtalo nuevamente."
                }
            }


        Toast.makeText(
            this,
            mensaje,
            Toast.LENGTH_LONG
        ).show()
    }
}