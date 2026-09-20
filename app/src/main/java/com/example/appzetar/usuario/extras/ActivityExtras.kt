package com.example.appzetar.usuario.extras

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appzetar.R
import com.example.appzetar.usuario.ActivityMenuUsuario
import com.example.appzetar.usuario.ActivityPedidosUsuario
import com.example.appzetar.usuario.ActivityPerfilUsuario
import com.example.appzetar.usuario.NavegacionUsuario
import com.example.appzetar.usuario.carrito.ActivityPedido
import com.example.appzetar.usuario.carrito.PedidoItem
import com.example.appzetar.usuario.carrito.PedidoManager
import com.example.appzetar.usuario.carrito.TipoPedido
import com.google.firebase.firestore.FirebaseFirestore

class ActivityExtras : AppCompatActivity() {

    private val db =
        FirebaseFirestore.getInstance()

    private lateinit var rvCategorias: RecyclerView
    private lateinit var rvExtras: RecyclerView

    private lateinit var progressBar: ProgressBar

    private lateinit var tvCantidadExtras: TextView
    private lateinit var tvCantidadCarrito: TextView

    private lateinit var estadoSinExtras: LinearLayout

    private lateinit var navInicio: LinearLayout
    private lateinit var navExtras: LinearLayout
    private lateinit var navPedidos: LinearLayout
    private lateinit var navCarrito: LinearLayout
    private lateinit var navPerfil: LinearLayout

    private lateinit var categoriaAdapter: CategoriaAdapter
    private lateinit var extraAdapter: ExtraAdapter

    private val listaCategorias =
        mutableListOf<CategoriaItem>()

    private val listaExtras =
        mutableListOf<ExtraItem>()

    private var categoriaSeleccionadaId =
        CATEGORIA_TODOS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_extras)

        inicializarVistas()
        configurarInterfaz()
        cargarCategorias()
        cargarExtras()
    }

    override fun onResume() {
        super.onResume()

        actualizarContadorCarrito()
    }

    private fun inicializarVistas() {
        rvCategorias =
            findViewById(R.id.rvCategorias)

        rvExtras =
            findViewById(R.id.rvExtras)

        progressBar =
            findViewById(R.id.progressBar)

        tvCantidadExtras =
            findViewById(R.id.tvCantidadExtras)

        tvCantidadCarrito =
            findViewById(R.id.tvCantidadCarrito)

        estadoSinExtras =
            findViewById(R.id.estadoSinExtras)

        navInicio =
            findViewById(R.id.navInicio)

        navExtras =
            findViewById(R.id.navExtras)

        navPedidos =
            findViewById(R.id.navPedidos)

        navCarrito =
            findViewById(R.id.navCarrito)

        navPerfil =
            findViewById(R.id.navPerfil)
    }

    private fun configurarInterfaz() {
        categoriaAdapter =
            CategoriaAdapter(
                listaCategorias
            ) { categoria ->

                categoriaSeleccionadaId =
                    categoria.id

                filtrarExtras(
                    categoria.id
                )
            }

        rvCategorias.layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )

        rvCategorias.adapter =
            categoriaAdapter

        extraAdapter =
            ExtraAdapter(
                listaExtras
            ) { extra ->

                agregarExtraAlPedido(extra)
            }

        rvExtras.layoutManager =
            LinearLayoutManager(this)

        rvExtras.adapter =
            extraAdapter

        configurarNavegacion()
        configurarAnimacionesBarra()

        marcarExtrasActivo()

        actualizarContadorCarrito()
    }

    private fun configurarNavegacion() {
        navInicio.setOnClickListener {
            NavegacionUsuario.abrir(
                this,
                ActivityMenuUsuario::class.java
            )
        }

        navExtras.setOnClickListener {
            // Ya estamos en esta pantalla.
        }

        navPedidos.setOnClickListener {
            NavegacionUsuario.abrir(
                this,
                ActivityPedidosUsuario::class.java
            )
        }

        navCarrito.setOnClickListener {
            NavegacionUsuario.abrir(
                this,
                ActivityPedido::class.java
            )
        }

        navPerfil.setOnClickListener {
            NavegacionUsuario.abrir(
                this,
                ActivityPerfilUsuario::class.java
            )
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun configurarAnimacionesBarra() {
        val opciones =
            listOf(
                navInicio,
                navExtras,
                navPedidos,
                navCarrito,
                navPerfil
            )

        opciones.forEach { opcion ->

            opcion.setOnTouchListener { vista, evento ->

                when (evento.actionMasked) {

                    MotionEvent.ACTION_DOWN -> {
                        vista.animate()
                            .scaleX(ESCALA_PRESIONADA)
                            .scaleY(ESCALA_PRESIONADA)
                            .translationY(
                                TRASLACION_PRESIONADA
                            )
                            .setDuration(
                                DURACION_PRESION
                            )
                            .setInterpolator(
                                DecelerateInterpolator()
                            )
                            .start()
                    }

                    MotionEvent.ACTION_UP,
                    MotionEvent.ACTION_CANCEL -> {
                        vista.animate()
                            .scaleX(ESCALA_NORMAL)
                            .scaleY(ESCALA_NORMAL)
                            .translationY(
                                TRASLACION_NORMAL
                            )
                            .setDuration(
                                DURACION_RETORNO
                            )
                            .setInterpolator(
                                DecelerateInterpolator()
                            )
                            .start()
                    }
                }

                false
            }
        }
    }

    private fun marcarExtrasActivo() {
        navExtras.post {
            navExtras.animate()
                .scaleX(ESCALA_ACTIVA)
                .scaleY(ESCALA_ACTIVA)
                .translationY(
                    TRASLACION_ACTIVA
                )
                .setDuration(
                    DURACION_ACTIVA
                )
                .setInterpolator(
                    DecelerateInterpolator()
                )
                .start()
        }
    }

    private fun actualizarContadorCarrito() {
        val cantidad =
            PedidoManager.cantidadTotal()

        tvCantidadCarrito.text =
            cantidad.toString()

        tvCantidadCarrito.visibility =
            if (cantidad > 0) {
                View.VISIBLE
            } else {
                View.GONE
            }
    }

    private fun cargarCategorias() {
        listaCategorias.clear()

        listaCategorias.addAll(
            listOf(
                CategoriaItem(
                    CATEGORIA_TODOS,
                    getString(R.string.categoria_todos),
                    R.drawable.ic_extra
                ),
                CategoriaItem(
                    CATEGORIA_GASEOSAS,
                    getString(R.string.categoria_gaseosas),
                    R.drawable.ic_gaseosa
                ),
                CategoriaItem(
                    CATEGORIA_TORTAS,
                    getString(R.string.categoria_tortas),
                    R.drawable.ic_torta
                ),
                CategoriaItem(
                    CATEGORIA_PLATOS,
                    getString(R.string.categoria_platos),
                    R.drawable.ic_plato
                )
            )
        )

        categoriaAdapter.notifyItemRangeInserted(
            0,
            listaCategorias.size
        )
    }

    private fun cargarExtras() {
        progressBar.visibility =
            View.VISIBLE

        db.collection(COLECCION_EXTRAS)
            .addSnapshotListener { resultado, error ->

                progressBar.visibility =
                    View.GONE

                if (error != null) {
                    Log.e(
                        TAG_EXTRAS,
                        LOG_ERROR_CARGAR,
                        error
                    )

                    mostrarEstadoLista(0)

                    Toast.makeText(
                        this,
                        getString(
                            R.string.error_cargar_extras
                        ),
                        Toast.LENGTH_SHORT
                    ).show()

                    return@addSnapshotListener
                }

                if (resultado == null) {
                    mostrarEstadoLista(0)

                    return@addSnapshotListener
                }

                listaExtras.clear()

                for (documento in resultado.documents) {

                    val id =
                        documento
                            .getLong(CAMPO_ID)
                            ?.toInt()
                            ?: continue

                    val nombre =
                        documento
                            .getString(CAMPO_NOMBRE)
                            ?.takeIf {
                                it.isNotBlank()
                            }
                            ?: getString(
                                R.string.extra_nombre_generico
                            )

                    val precio =
                        documento
                            .getDouble(CAMPO_PRECIO)
                            ?: 0.0

                    val categoriaId =
                        documento
                            .getLong(CAMPO_CATEGORIA_ID)
                            ?.toInt()
                            ?: CATEGORIA_TODOS

                    val icono =
                        obtenerIconoCategoria(
                            categoriaId
                        )

                    listaExtras.add(
                        ExtraItem(
                            id = id,
                            nombre = nombre,
                            precio = precio,
                            categoriaId = categoriaId,
                            icono = icono
                        )
                    )
                }

                extraAdapter.actualizarExtras(
                    listaExtras,
                    categoriaSeleccionadaId
                )

                mostrarEstadoLista(
                    extraAdapter.itemCount
                )
            }
    }

    private fun obtenerIconoCategoria(
        categoriaId: Int
    ): Int {
        return when (categoriaId) {

            CATEGORIA_GASEOSAS ->
                R.drawable.ic_gaseosa

            CATEGORIA_TORTAS ->
                R.drawable.ic_torta

            CATEGORIA_PLATOS ->
                R.drawable.ic_plato

            else ->
                R.drawable.ic_extra
        }
    }

    private fun filtrarExtras(
        categoriaId: Int
    ) {
        extraAdapter.filtrarPorCategoria(
            categoriaId
        )

        mostrarEstadoLista(
            extraAdapter.itemCount
        )
    }

    private fun mostrarEstadoLista(
        cantidad: Int
    ) {
        val estaVacia =
            cantidad == 0

        rvExtras.visibility =
            if (estaVacia) {
                View.GONE
            } else {
                View.VISIBLE
            }

        estadoSinExtras.visibility =
            if (estaVacia) {
                View.VISIBLE
            } else {
                View.GONE
            }

        tvCantidadExtras.text =
            resources.getQuantityString(
                R.plurals.cantidad_opciones_extras,
                cantidad,
                cantidad
            )
    }

    private fun agregarExtraAlPedido(
        extra: ExtraItem
    ) {
        PedidoManager.agregarProducto(
            PedidoItem(
                id = extra.id,
                nombre = extra.nombre,
                precio = extra.precio,
                cantidad = 1,
                tipo = TipoPedido.EXTRA
            )
        )

        actualizarContadorCarrito()

        Toast.makeText(
            this,
            getString(
                R.string.extra_agregado_carrito,
                extra.nombre
            ),
            Toast.LENGTH_SHORT
        ).show()
    }

    companion object {

        private const val CATEGORIA_TODOS = 0
        private const val CATEGORIA_GASEOSAS = 1
        private const val CATEGORIA_TORTAS = 2
        private const val CATEGORIA_PLATOS = 3

        private const val ESCALA_NORMAL = 1f
        private const val ESCALA_PRESIONADA = 1.12f
        private const val ESCALA_ACTIVA = 1.08f

        private const val TRASLACION_NORMAL = 0f
        private const val TRASLACION_PRESIONADA = -9f
        private const val TRASLACION_ACTIVA = -5f

        private const val DURACION_PRESION = 150L
        private const val DURACION_RETORNO = 180L
        private const val DURACION_ACTIVA = 280L

        private const val COLECCION_EXTRAS =
            "extras"

        private const val CAMPO_ID =
            "id"

        private const val CAMPO_NOMBRE =
            "nombre"

        private const val CAMPO_PRECIO =
            "precio"

        private const val CAMPO_CATEGORIA_ID =
            "categoriaId"

        private const val TAG_EXTRAS =
            "EXTRAS"

        private const val LOG_ERROR_CARGAR =
            "Error cargando extras"
    }
}