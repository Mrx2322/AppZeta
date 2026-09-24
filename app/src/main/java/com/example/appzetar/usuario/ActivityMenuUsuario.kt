package com.example.appzetar.usuario

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.edit
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.appzetar.R
import com.example.appzetar.usuario.extras.ActivityExtras
import com.example.appzetar.usuario.extras.CategoriaAdapter
import com.example.appzetar.usuario.extras.CategoriaItem
import com.example.appzetar.usuario.extras.ExtraAdapter
import com.example.appzetar.usuario.extras.ExtraItem
import com.example.appzetar.usuario.menus.ActivityTodosLosMenus
import com.example.appzetar.usuario.carrito.ActivityPedido
import com.example.appzetar.usuario.carrito.EntradaPedido
import com.example.appzetar.usuario.carrito.PedidoItem
import com.example.appzetar.usuario.carrito.PedidoManager
import com.example.appzetar.usuario.carrito.ReglasPrecioPedido
import com.example.appzetar.usuario.carrito.TipoPedido
import com.example.appzetar.usuario.entradas.EntradasUsuarioAdapter
import com.example.appzetar.usuario.modelos.TaskEntradas
import com.example.appzetar.usuario.modelos.TaskMenu
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.example.appzetar.usuario.navegacionUsuario.NavegacionUsuario
import com.example.appzetar.usuario.pedidos.ActivityPedidosUsuario
import com.example.appzetar.usuario.perfilusuario.ActivityPerfilUsuario
import kotlin.math.abs

class ActivityMenuUsuario : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var rvCategorias: RecyclerView
    private lateinit var categoriaAdapter: CategoriaAdapter
    private val categorias = mutableListOf<CategoriaItem>()
    private var categoriaSeleccionadaId = 0

    private lateinit var rvExtras: RecyclerView
    private lateinit var extraAdapter: ExtraAdapter
    private val todosLosExtras = mutableListOf<ExtraItem>()
    private val listaExtras = mutableListOf<ExtraItem>()

    private val entradas = mutableListOf<TaskEntradas>()
    private lateinit var rvEntradas: RecyclerView
    private lateinit var entradasAdapter: EntradasUsuarioAdapter

    // Conserva todos los menús recibidos desde Firebase.
    // Se usa para recuperar el plato seleccionado en "Ver todo".
    private val listaMenu = mutableListOf<TaskMenu>()

    // El carrusel de Inicio muestra como máximo el límite configurado.
    private val listaMenuCarrusel = mutableListOf<TaskMenu>()
    private lateinit var rvMenu: RecyclerView
    private lateinit var menuAdapter: MenuUsuarioAdapter

    private lateinit var progressBarMenu: ProgressBar
    private lateinit var tvCantidadCarrito: TextView
    private lateinit var btnCarrito: FloatingActionButton
    private lateinit var tvSaludo: TextView
    private lateinit var tvVerTodo: TextView

    private lateinit var tvSubtituloMenu: TextView
    private lateinit var tvTituloEntradas: TextView
    private lateinit var layoutTituloMenu: LinearLayout
    private lateinit var bottomNavigation: View
    private lateinit var contenedorCarrito: View

    private var animacionInicialEjecutada = false

    private lateinit var navInicio: LinearLayout
    private lateinit var navExtras: LinearLayout
    private lateinit var navPedidos: LinearLayout
    private lateinit var navCarrito: LinearLayout
    private lateinit var navPerfil: LinearLayout

    private val launcherTodosLosMenus =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { resultado ->

            if (resultado.resultCode != RESULT_OK) {
                return@registerForActivityResult
            }

            val menuId =
                resultado.data?.getIntExtra(
                    ActivityTodosLosMenus.EXTRA_MENU_ID,
                    -1
                ) ?: -1

            val platoSeleccionado =
                listaMenu.firstOrNull { plato ->
                    plato.id == menuId
                }

            if (platoSeleccionado != null) {
                mostrarAlertaAgregar(platoSeleccionado)
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.menu_seleccionado_no_disponible),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        WindowInsetsControllerCompat(
            window,
            window.decorView
        ).apply {
            hide(WindowInsetsCompat.Type.statusBars())

            systemBarsBehavior =
                WindowInsetsControllerCompat
                    .BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        setContentView(R.layout.activity_menu_usuario)

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

        initComponent()
        initUI()
        prepararAnimaciones()

        cargarNombreUsuario()
        cargarCategorias()
        cargarDatosDesdeFirebase()
    }

    private fun initComponent() {
        rvCategorias = findViewById(R.id.rvCategorias)
        rvEntradas = findViewById(R.id.rvEntradas)
        rvExtras = findViewById(R.id.rvExtras)
        rvMenu = findViewById(R.id.rvMenu)

        progressBarMenu = findViewById(R.id.progressBarMenu)
        tvCantidadCarrito = findViewById(R.id.tvCantidadCarrito)
        btnCarrito = findViewById(R.id.btnCarrito)
        tvSaludo = findViewById(R.id.tvSaludo)
        tvVerTodo = findViewById(R.id.tvVerTodo)

        tvSubtituloMenu = findViewById(R.id.tvSubtituloMenu)
        tvTituloEntradas = findViewById(R.id.tvTituloEntradas)
        layoutTituloMenu = findViewById(R.id.layoutTituloMenu)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        contenedorCarrito = findViewById(R.id.contenedorCarrito)

        navInicio = findViewById(R.id.navInicio)
        navExtras = findViewById(R.id.navExtras)
        navPedidos = findViewById(R.id.navPedidos)
        navCarrito = findViewById(R.id.navCarrito)
        navPerfil = findViewById(R.id.navPerfil)
    }

    private fun prepararAnimaciones() {
        tvSaludo.alpha = 0f
        tvSaludo.translationY = 25f

        tvSubtituloMenu.alpha = 0f
        tvSubtituloMenu.translationY = 20f

        tvTituloEntradas.alpha = 0f
        tvTituloEntradas.translationY = 20f

        rvEntradas.alpha = 0f
        rvEntradas.translationY = 25f

        layoutTituloMenu.alpha = 0f
        layoutTituloMenu.translationY = 20f

        rvMenu.alpha = 0f
        rvMenu.translationY = 30f

        bottomNavigation.alpha = 0f
        bottomNavigation.translationY = 90f
    }

    private fun ejecutarAnimacionInicial() {
        if (animacionInicialEjecutada) return

        animacionInicialEjecutada = true

        val saludoAlpha =
            ObjectAnimator.ofFloat(tvSaludo, View.ALPHA, 0f, 1f)

        val saludoMovimiento =
            ObjectAnimator.ofFloat(
                tvSaludo,
                View.TRANSLATION_Y,
                25f,
                0f
            )

        val subtituloAlpha =
            ObjectAnimator.ofFloat(
                tvSubtituloMenu,
                View.ALPHA,
                0f,
                1f
            )

        val subtituloMovimiento =
            ObjectAnimator.ofFloat(
                tvSubtituloMenu,
                View.TRANSLATION_Y,
                20f,
                0f
            )

        val tituloEntradasAlpha =
            ObjectAnimator.ofFloat(
                tvTituloEntradas,
                View.ALPHA,
                0f,
                1f
            )

        val tituloEntradasMovimiento =
            ObjectAnimator.ofFloat(
                tvTituloEntradas,
                View.TRANSLATION_Y,
                20f,
                0f
            )

        val entradasAlpha =
            ObjectAnimator.ofFloat(
                rvEntradas,
                View.ALPHA,
                0f,
                1f
            )

        val entradasMovimiento =
            ObjectAnimator.ofFloat(
                rvEntradas,
                View.TRANSLATION_Y,
                25f,
                0f
            )

        val tituloMenuAlpha =
            ObjectAnimator.ofFloat(
                layoutTituloMenu,
                View.ALPHA,
                0f,
                1f
            )

        val tituloMenuMovimiento =
            ObjectAnimator.ofFloat(
                layoutTituloMenu,
                View.TRANSLATION_Y,
                20f,
                0f
            )

        val menuAlpha =
            ObjectAnimator.ofFloat(
                rvMenu,
                View.ALPHA,
                0f,
                1f
            )

        val menuMovimiento =
            ObjectAnimator.ofFloat(
                rvMenu,
                View.TRANSLATION_Y,
                30f,
                0f
            )

        val barraAlpha =
            ObjectAnimator.ofFloat(
                bottomNavigation,
                View.ALPHA,
                0f,
                1f
            )

        val barraMovimiento =
            ObjectAnimator.ofFloat(
                bottomNavigation,
                View.TRANSLATION_Y,
                90f,
                0f
            )

        val animadores =
            listOf(
                saludoAlpha,
                saludoMovimiento,
                subtituloAlpha,
                subtituloMovimiento,
                tituloEntradasAlpha,
                tituloEntradasMovimiento,
                entradasAlpha,
                entradasMovimiento,
                tituloMenuAlpha,
                tituloMenuMovimiento,
                menuAlpha,
                menuMovimiento,
                barraAlpha,
                barraMovimiento
            )

        animadores.forEach { animador ->
            animador.duration = 420
            animador.interpolator = DecelerateInterpolator()
        }

        crearAnimacion(saludoAlpha, saludoMovimiento, 80)
        crearAnimacion(subtituloAlpha, subtituloMovimiento, 160)
        crearAnimacion(
            tituloEntradasAlpha,
            tituloEntradasMovimiento,
            240
        )
        crearAnimacion(entradasAlpha, entradasMovimiento, 320)
        crearAnimacion(tituloMenuAlpha, tituloMenuMovimiento, 400)
        crearAnimacion(menuAlpha, menuMovimiento, 480)
        crearAnimacion(barraAlpha, barraMovimiento, 600)
    }

    private fun crearAnimacion(
        alpha: ObjectAnimator,
        movimiento: ObjectAnimator,
        retraso: Long
    ) {
        AnimatorSet().apply {
            playTogether(alpha, movimiento)
            startDelay = retraso
            start()
        }
    }

    private fun animarBadgeCarrito() {
        if (!tvCantidadCarrito.isVisible) return

        tvCantidadCarrito.animate()
            .scaleX(1.25f)
            .scaleY(1.25f)
            .setDuration(120)
            .withEndAction {
                tvCantidadCarrito.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(150)
                    .start()
            }
            .start()
    }

    private fun cargarNombreUsuario() {
        val usuarioActual = auth.currentUser

        if (usuarioActual == null || usuarioActual.isAnonymous) {
            cargarNombreInvitado(usuarioActual)
            return
        }

        // Usuario registrado
        db.collection("usuarios")
            .document(usuarioActual.uid)
            .get()
            .addOnSuccessListener { documento ->

                val nombre =
                    documento.getString("nombre")

                tvSaludo.text =
                    if (!nombre.isNullOrEmpty()) {
                        getString(
                            R.string.saludo_usuario,
                            nombre
                        )
                    } else {
                        getString(R.string.saludo_generico)
                    }
            }
            .addOnFailureListener { error ->

                Log.e(
                    "USUARIO_FIREBASE",
                    "Error obteniendo nombre",
                    error
                )

                tvSaludo.text =
                    getString(R.string.saludo_generico)
            }
    }

    private fun cargarNombreInvitado(usuario: FirebaseUser?) {
        val preferencias = getSharedPreferences(
            PREFERENCIAS_INVITADO,
            MODE_PRIVATE
        )

        val claveNombre = obtenerClaveNombreInvitado(usuario)
        val nombreGuardado = preferencias
            .getString(claveNombre, null)
            ?.trim()
            .orEmpty()

        if (nombreGuardado.isNotEmpty()) {
            guardarNombreInvitadoEnFirebase(nombreGuardado)
            mostrarSaludo(nombreGuardado)
        } else {
            solicitarNombreInvitado(claveNombre)
        }
    }

    private fun solicitarNombreInvitado(claveNombre: String) {
        val vistaDialogo = layoutInflater.inflate(
            R.layout.dialog_nombre_invitado,
            null
        )
        val campoNombre = vistaDialogo.findViewById<AppCompatEditText>(
            R.id.etNombreInvitado
        )

        val dialogo = AlertDialog.Builder(this)
            .setTitle(R.string.titulo_nombre_invitado)
            .setMessage(R.string.mensaje_nombre_invitado)
            .setView(vistaDialogo)
            .setCancelable(false)
            .setPositiveButton(R.string.continuar_nombre_invitado, null)
            .create()

        dialogo.setOnShowListener {
            dialogo.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener {
                    val nombre = campoNombre.text
                        ?.toString()
                        ?.trim()
                        .orEmpty()

                    if (nombre.isBlank()) {
                        campoNombre.error = getString(
                            R.string.error_nombre_invitado
                        )
                        return@setOnClickListener
                    }

                    getSharedPreferences(
                        PREFERENCIAS_INVITADO,
                        MODE_PRIVATE
                    ).edit {
                        putString(claveNombre, nombre)
                    }

                    guardarNombreInvitadoEnFirebase(nombre)
                    mostrarSaludo(nombre)
                    dialogo.dismiss()
                }
        }

        dialogo.show()
        campoNombre.requestFocus()
    }

    private fun mostrarSaludo(nombre: String) {
        tvSaludo.text = getString(
            R.string.saludo_usuario,
            nombre
        )
    }

    private fun guardarNombreInvitadoEnFirebase(nombre: String) {
        val usuarioActual = auth.currentUser ?: return

        db.collection("usuarios")
            .document(usuarioActual.uid)
            .set(
                mapOf(
                    "nombre" to nombre,
                    "esInvitado" to true
                ),
                SetOptions.merge()
            )
            .addOnFailureListener { error ->
                Log.e(
                    "USUARIO_FIREBASE",
                    "Error guardando nombre del invitado",
                    error
                )
            }
    }

    private fun obtenerClaveNombreInvitado(usuario: FirebaseUser?): String {
        val identificador = usuario?.uid ?: INVITADO_SIN_UID
        return "$CLAVE_NOMBRE_INVITADO$identificador"
    }

    private fun initUI() {
        entradasAdapter =
            EntradasUsuarioAdapter(entradas)

        rvEntradas.layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )

        rvEntradas.adapter = entradasAdapter

        menuAdapter =
            MenuUsuarioAdapter(listaMenuCarrusel) { plato ->
                mostrarAlertaAgregar(plato)
            }

        configurarCarruselMenu()
        rvMenu.adapter = menuAdapter

        extraAdapter =
            ExtraAdapter(listaExtras) { extra ->
                agregarExtraAlPedido(extra)
            }

        rvExtras.layoutManager =
            LinearLayoutManager(this)

        rvExtras.adapter = extraAdapter

        tvVerTodo.setOnClickListener {
            val intent =
                Intent(
                    this,
                    ActivityTodosLosMenus::class.java
                )

            val animacion =
                ActivityOptionsCompat.makeCustomAnimation(
                    this,
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                )

            launcherTodosLosMenus.launch(
                intent,
                animacion
            )
        }

        btnCarrito.setOnClickListener {
            abrirCarrito()
        }

        configurarClickNavegacion(navInicio) {
            // Ya estamos en Inicio.
        }

        configurarClickNavegacion(navExtras) {
            NavegacionUsuario.abrir(
                this,
                ActivityExtras::class.java
            )
        }

        configurarClickNavegacion(navPedidos) {
            NavegacionUsuario.abrir(
                this,
                ActivityPedidosUsuario::class.java
            )
        }

        configurarClickNavegacion(navCarrito) {
            abrirCarrito()
        }

        configurarClickNavegacion(navPerfil) {
            NavegacionUsuario.abrir(
                this,
                ActivityPerfilUsuario::class.java
            )
        }

        marcarInicioActivo()
        actualizarContadorCarrito()
    }

    private fun configurarClickNavegacion(
        item: View,
        accion: () -> Unit
    ) {
        item.setOnClickListener { vista ->
            vista.animate().cancel()
            vista.animate()
                .scaleX(1.12f)
                .scaleY(1.12f)
                .translationY(-9f)
                .setDuration(120)
                .setInterpolator(DecelerateInterpolator())
                .withEndAction {
                    vista.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .translationY(0f)
                        .setDuration(160)
                        .setInterpolator(DecelerateInterpolator())
                        .withEndAction {
                            accion()
                        }
                        .start()
                }
                .start()
        }
    }

    private fun marcarInicioActivo() {
        navInicio.post {
            navInicio.animate()
                .scaleX(1.08f)
                .scaleY(1.08f)
                .translationY(-5f)
                .setDuration(280)
                .setInterpolator(
                    DecelerateInterpolator()
                )
                .start()
        }
    }

    private fun abrirCarrito() {
        NavegacionUsuario.abrir(
            this,
            ActivityPedido::class.java
        )
    }

    private fun configurarCarruselMenu() {
        val layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )

        rvMenu.layoutManager = layoutManager
        rvMenu.clipToPadding = false
        rvMenu.clipChildren = false
        rvMenu.setPadding(90, 0, 90, 0)
        rvMenu.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        rvMenu.itemAnimator = null

        LinearSnapHelper()
            .attachToRecyclerView(rvMenu)

        rvMenu.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {

                override fun onScrolled(
                    recyclerView: RecyclerView,
                    dx: Int,
                    dy: Int
                ) {
                    super.onScrolled(recyclerView, dx, dy)
                    animarTarjetasCarrusel(recyclerView)
                }

                override fun onScrollStateChanged(
                    recyclerView: RecyclerView,
                    newState: Int
                ) {
                    super.onScrollStateChanged(
                        recyclerView,
                        newState
                    )

                    if (
                        newState ==
                        RecyclerView.SCROLL_STATE_IDLE
                    ) {
                        animarTarjetaCentral(recyclerView)
                    }
                }
            }
        )

        rvMenu.post {
            animarTarjetasCarrusel(rvMenu)
        }
    }

    private fun animarTarjetasCarrusel(
        recyclerView: RecyclerView
    ) {
        if (recyclerView.width <= 0) return

        val centroX = recyclerView.width / 2f
        val distanciaMaxima = recyclerView.width / 2f

        if (distanciaMaxima <= 0f) return

        for (i in 0 until recyclerView.childCount) {
            val tarjeta = recyclerView.getChildAt(i)

            val centroTarjeta =
                (tarjeta.left + tarjeta.right) / 2f

            val distancia =
                abs(centroX - centroTarjeta)

            val posicion =
                (distancia / distanciaMaxima)
                    .coerceIn(0f, 1f)

            val escala =
                1f - (posicion * 0.14f)

            tarjeta.scaleX = escala
            tarjeta.scaleY = escala
            tarjeta.alpha = 1f - (posicion * 0.25f)
            tarjeta.translationY = posicion * 14f
            tarjeta.translationZ = (1f - posicion) * 25f
        }
    }

    private fun animarTarjetaCentral(
        recyclerView: RecyclerView
    ) {
        if (recyclerView.width <= 0) return

        val centroX = recyclerView.width / 2f
        var tarjetaCentral: View? = null
        var distanciaMenor = Float.MAX_VALUE

        for (i in 0 until recyclerView.childCount) {
            val tarjeta = recyclerView.getChildAt(i)

            val centroTarjeta =
                (tarjeta.left + tarjeta.right) / 2f

            val distancia =
                abs(centroX - centroTarjeta)

            if (distancia < distanciaMenor) {
                distanciaMenor = distancia
                tarjetaCentral = tarjeta
            }
        }

        tarjetaCentral?.let { tarjeta ->
            tarjeta.animate().cancel()

            tarjeta.animate()
                .scaleX(1.06f)
                .scaleY(1.06f)
                .translationY(-2f)
                .translationZ(30f)
                .setDuration(180)
                .setInterpolator(
                    DecelerateInterpolator()
                )
                .withEndAction {
                    tarjeta.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .translationY(0f)
                        .translationZ(25f)
                        .setDuration(220)
                        .setInterpolator(
                            DecelerateInterpolator()
                        )
                        .start()
                }
                .start()
        }
    }

    private fun cargarCategorias() {
        categorias.clear()

        categorias.add(
            CategoriaItem(
                1,
                getString(R.string.categoria_gaseosas),
                R.drawable.ic_gaseosa
            )
        )

        categorias.add(
            CategoriaItem(
                2,
                getString(R.string.categoria_tortas),
                R.drawable.ic_torta
            )
        )

        categorias.add(
            CategoriaItem(
                3,
                getString(R.string.categoria_platos),
                R.drawable.ic_plato
            )
        )

        categoriaAdapter =
            CategoriaAdapter(categorias) { categoria ->

                if (
                    categoriaSeleccionadaId == categoria.id &&
                    rvExtras.isVisible
                ) {
                    rvExtras.isVisible = false
                } else {
                    categoriaSeleccionadaId = categoria.id
                    mostrarExtrasPorCategoria(categoria.id)
                }
            }

        rvCategorias.layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )

        rvCategorias.adapter = categoriaAdapter
    }

    private fun mostrarExtrasPorCategoria(
        categoriaId: Int
    ) {
        val cantidadAnterior =
            listaExtras.size

        if (cantidadAnterior > 0) {
            listaExtras.clear()

            extraAdapter.notifyItemRangeRemoved(
                0,
                cantidadAnterior
            )
        }

        val nuevosExtras =
            todosLosExtras.filter { extra ->
                extra.categoriaId == categoriaId
            }

        listaExtras.addAll(nuevosExtras)

        if (nuevosExtras.isNotEmpty()) {
            extraAdapter.notifyItemRangeInserted(
                0,
                nuevosExtras.size
            )
        }

        rvExtras.isVisible =
            listaExtras.isNotEmpty()
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
                R.string.extra_agregado_pedido,
                extra.nombre
            ),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun mostrarAlertaAgregar(
        plato: TaskMenu
    ) {
        val cantidadMenuEnCarrito =
            PedidoManager.cantidadMenuEnPedido(
                plato.id
            )

        if (
            plato.stock <= 0 ||
            cantidadMenuEnCarrito >= plato.stock
        ) {
            Toast.makeText(
                this,
                getString(R.string.limite_unidades_plato),
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val seleccionActual =
            entradasAdapter
                .obtenerEntradasSeleccionadas()

        val cantidadEntradas =
            seleccionActual.sumOf { entrada ->
                entrada.cantidad
            }

        val precioSinEntrada =
            ReglasPrecioPedido.calcularPrecioMenu(
                precioMenuConEntrada = plato.precio,
                cantidadEntradas = 0
            )

        val precioConEntrada =
            ReglasPrecioPedido.calcularPrecioMenu(
                precioMenuConEntrada = plato.precio,
                cantidadEntradas = 1
            )

        val precioFinal =
            ReglasPrecioPedido.calcularPrecioMenu(
                precioMenuConEntrada = plato.precio,
                cantidadEntradas = cantidadEntradas
            )

        val dialogView =
            layoutInflater.inflate(
                R.layout.dialog_agregar_pedido,
                null
            )

        val tvNombrePlato =
            dialogView.findViewById<TextView>(
                R.id.tvNombrePlato
            )

        val tvPrecioSinEntrada =
            dialogView.findViewById<TextView>(
                R.id.tvPrecioSinEntrada
            )

        val tvPrecioConEntrada =
            dialogView.findViewById<TextView>(
                R.id.tvPrecioConEntrada
            )

        val tvPrecioEntradaAdicional =
            dialogView.findViewById<TextView>(
                R.id.tvPrecioEntradaAdicional
            )

        val tvEntradasSeleccionadas =
            dialogView.findViewById<TextView>(
                R.id.tvEntradasSeleccionadas
            )

        val tvTotalSeleccion =
            dialogView.findViewById<TextView>(
                R.id.tvTotalSeleccion
            )

        val tvAyudaSeleccion =
            dialogView.findViewById<TextView>(
                R.id.tvAyudaSeleccion
            )

        val btnCancelar =
            dialogView.findViewById<Button>(
                R.id.btnCancelarPedido
            )

        val btnAgregar =
            dialogView.findViewById<Button>(
                R.id.btnAgregarPedido
            )

        tvNombrePlato.text = plato.name

        tvPrecioSinEntrada.text =
            getString(
                R.string.formato_precio_soles,
                precioSinEntrada
            )

        tvPrecioConEntrada.text =
            getString(
                R.string.formato_precio_soles,
                precioConEntrada
            )

        tvPrecioEntradaAdicional.text =
            getString(
                R.string.precio_entrada_adicional,
                ReglasPrecioPedido.PRECIO_ENTRADA
            )

        tvEntradasSeleccionadas.text =
            descripcionSeleccionEntradas(
                seleccionActual
            )

        tvTotalSeleccion.text =
            getString(
                R.string.formato_precio_soles,
                precioFinal
            )

        tvAyudaSeleccion.text =
            when (cantidadEntradas) {
                0 ->
                    getString(
                        R.string.ayuda_menu_sin_entrada,
                        precioFinal
                    )

                1 ->
                    getString(
                        R.string.ayuda_menu_con_entrada,
                        precioFinal
                    )

                else ->
                    getString(
                        R.string.ayuda_cantidad_entradas,
                        cantidadEntradas
                    )
            }

        val dialog =
            AlertDialog.Builder(this)
                .setView(dialogView)
                .create()

        dialog.show()

        dialog.window?.setBackgroundDrawable(
            Color.TRANSPARENT.toDrawable()
        )

        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }

        btnAgregar.setOnClickListener {
            val cantidadActualEnCarrito =
                PedidoManager.cantidadMenuEnPedido(
                    plato.id
                )

            if (
                plato.stock <= 0 ||
                cantidadActualEnCarrito >= plato.stock
            ) {
                Toast.makeText(
                    this,
                    getString(R.string.limite_unidades_plato),
                    Toast.LENGTH_SHORT
                ).show()

                dialog.dismiss()
                return@setOnClickListener
            }

            btnAgregar.isEnabled = false

            agregarAlPedido(
                plato = plato,
                seleccionEntradas = seleccionActual,
                precioFinal = precioFinal,
                onCompletado = {
                    dialog.dismiss()
                },
                onError = {
                    if (!isFinishing && !isDestroyed) {
                        btnAgregar.isEnabled = true
                    }
                }
            )
        }
    }

    private fun agregarAlPedido(
        plato: TaskMenu,
        seleccionEntradas: List<EntradaPedido>,
        precioFinal: Double,
        onCompletado: () -> Unit,
        onError: () -> Unit
    ) {
        val cantidadMenuEnCarrito =
            PedidoManager.cantidadMenuEnPedido(
                plato.id
            )

        if (
            plato.stock <= 0 ||
            cantidadMenuEnCarrito >= plato.stock
        ) {
            Toast.makeText(
                this,
                getString(R.string.limite_unidades_plato),
                Toast.LENGTH_SHORT
            ).show()

            onError()
            return
        }

        for (entradaSeleccionada in seleccionEntradas) {
            val entradaActual =
                entradas.find { entrada ->
                    entrada.id == entradaSeleccionada.id
                }

            val cantidadEnCarrito =
                PedidoManager.cantidadEntradaEnPedido(
                    entradaSeleccionada.id
                )

            val cantidadNecesaria =
                cantidadEnCarrito +
                        entradaSeleccionada.cantidad

            if (
                entradaActual == null ||
                !entradaActual.disponible ||
                cantidadNecesaria > entradaActual.stock
            ) {
                Toast.makeText(
                    this,
                    getString(
                        R.string.stock_entrada_insuficiente,
                        entradaSeleccionada.nombre
                    ),
                    Toast.LENGTH_SHORT
                ).show()

                if (entradas.isNotEmpty()) {
                    entradasAdapter.notifyItemRangeChanged(
                        0,
                        entradas.size
                    )
                }
                onError()
                return
            }
        }

        PedidoManager.agregarProducto(
            PedidoItem(
                id = plato.id,
                nombre = plato.name,
                precio = precioFinal,
                cantidad = 1,
                tipo = TipoPedido.MENU,
                precioBaseMenu = plato.precio,
                entradas = seleccionEntradas
            )
        )

        entradasAdapter.limpiarSeleccion()
        actualizarContadorCarrito()

        Toast.makeText(
            this,
            getString(
                R.string.plato_agregado_precio,
                plato.name,
                precioFinal
            ),
            Toast.LENGTH_SHORT
        ).show()

        onCompletado()
    }

    private fun descripcionSeleccionEntradas(
        seleccionEntradas: List<EntradaPedido>
    ): String {
        if (seleccionEntradas.isEmpty()) {
            return getString(R.string.sin_entrada)
        }

        val detalle =
            seleccionEntradas.joinToString(
                separator = ", "
            ) { entrada ->

                if (entrada.cantidad > 1) {
                    getString(
                        R.string.detalle_cantidad_entrada,
                        entrada.cantidad,
                        entrada.nombre
                    )
                } else {
                    entrada.nombre
                }
            }

        val cantidadTotal =
            seleccionEntradas.sumOf { entrada ->
                entrada.cantidad
            }

        return if (cantidadTotal == 1) {
            getString(
                R.string.con_entrada_detalle,
                detalle
            )
        } else {
            getString(
                R.string.con_varias_entradas_detalle,
                cantidadTotal,
                detalle
            )
        }
    }

    private fun actualizarContadorCarrito() {
        val cantidadTotal =
            PedidoManager.cantidadTotal()

        tvCantidadCarrito.text =
            cantidadTotal.toString()

        tvCantidadCarrito.isVisible =
            cantidadTotal > 0

        if (cantidadTotal > 0) {
            animarBadgeCarrito()
        }
    }

    private fun cargarDatosDesdeFirebase() {
        progressBarMenu.isVisible = true
        rvEntradas.isVisible = false
        rvMenu.isVisible = false
        rvExtras.isVisible = false

        cargarEntradas()
        cargarMenu()
        cargarExtrasDesdeFirebase()
    }

    private fun cargarExtrasDesdeFirebase() {
        db.collection("extras")
            .addSnapshotListener { resultado, error ->

                if (error != null) {
                    Log.e(
                        "USUARIO_FIREBASE",
                        "Error escuchando extras",
                        error
                    )

                    return@addSnapshotListener
                }

                if (resultado == null) {
                    return@addSnapshotListener
                }

                todosLosExtras.clear()

                for (documento in resultado) {
                    val id =
                        documento.getLong("id")?.toInt()
                            ?: documento.id.toIntOrNull()
                            ?: 0

                    val nombre =
                        documento.getString("nombre").orEmpty()

                    val precio =
                        documento.getDouble("precio") ?: 0.0

                    val categoriaId =
                        documento
                            .getLong("categoriaId")
                            ?.toInt()
                            ?: 0

                    val icono =
                        when (categoriaId) {
                            1 -> R.drawable.ic_gaseosa
                            2 -> R.drawable.ic_torta
                            3 -> R.drawable.ic_plato
                            else -> R.drawable.ic_gaseosa
                        }

                    if (
                        id > 0 &&
                        nombre.isNotEmpty() &&
                        categoriaId > 0
                    ) {
                        todosLosExtras.add(
                            ExtraItem(
                                id = id,
                                nombre = nombre,
                                precio = precio,
                                categoriaId = categoriaId,
                                icono = icono
                            )
                        )
                    }
                }

                if (categoriaSeleccionadaId != 0) {
                    mostrarExtrasPorCategoria(
                        categoriaSeleccionadaId
                    )
                }
            }
    }

    private fun cargarEntradas() {
        db.collection("entradas")
            .addSnapshotListener { resultado, error ->

                if (error != null) {
                    Log.e(
                        "USUARIO_FIREBASE",
                        "Error escuchando entradas",
                        error
                    )

                    return@addSnapshotListener
                }

                if (resultado == null) {
                    return@addSnapshotListener
                }

                val cantidadAnterior =
                    entradas.size

                entradas.clear()

                if (cantidadAnterior > 0) {
                    entradasAdapter.notifyItemRangeRemoved(
                        0,
                        cantidadAnterior
                    )
                }

                for (documento in resultado) {
                    val id =
                        documento.getLong("id")?.toInt()
                            ?: documento.id.toIntOrNull()
                            ?: 0

                    val nombre =
                        documento.getString("nombre").orEmpty()

                    val disponible =
                        documento.getBoolean("disponible")
                            ?: true

                    val stock =
                        documento.getLong("stock")?.toInt()
                            ?: 0

                    if (id > 0 && nombre.isNotEmpty()) {
                        val entrada =
                            when (id) {
                                1 ->
                                    TaskEntradas.Ceviche(
                                        id,
                                        nombre,
                                        disponible,
                                        stock
                                    )

                                2 ->
                                    TaskEntradas.Huancaina(
                                        id,
                                        nombre,
                                        disponible,
                                        stock
                                    )

                                else ->
                                    TaskEntradas.Otros(
                                        id,
                                        nombre,
                                        disponible,
                                        stock
                                    )
                            }

                        entradas.add(entrada)
                    }
                }

                if (entradas.isNotEmpty()) {
                    entradasAdapter.notifyItemRangeInserted(
                        0,
                        entradas.size
                    )
                }
            }
    }

    private fun cargarMenu() {
        db.collection("menu")
            .addSnapshotListener { resultado, error ->

                if (error != null) {
                    Log.e(
                        "USUARIO_FIREBASE",
                        "Error escuchando menú",
                        error
                    )

                    mostrarContenido()
                    return@addSnapshotListener
                }

                if (resultado == null) {
                    return@addSnapshotListener
                }

                listaMenu.clear()

                for (documento in resultado) {
                    val id =
                        documento.getLong("id")?.toInt()
                            ?: documento.id.toIntOrNull()
                            ?: 0

                    val nombre =
                        documento.getString("nombre").orEmpty()

                    val precio =
                        documento.getDouble("precio") ?: 0.0

                    val stock =
                        documento.getLong("stock")?.toInt()
                            ?: 0

                    if (id > 0 && nombre.isNotEmpty()) {
                        listaMenu.add(
                            TaskMenu(
                                id = id,
                                name = nombre,
                                precio = precio,
                                stock = stock
                            )
                        )
                    }
                }

                // Orden estable para mostrar primero los menús con menor id.
                listaMenu.sortBy { plato ->
                    plato.id
                }

                val cantidadAnteriorCarrusel =
                    listaMenuCarrusel.size

                listaMenuCarrusel.clear()

                if (cantidadAnteriorCarrusel > 0) {
                    menuAdapter.notifyItemRangeRemoved(
                        0,
                        cantidadAnteriorCarrusel
                    )
                }

                val nuevosMenusCarrusel =
                    listaMenu.take(LIMITE_MENU_CARRUSEL)

                listaMenuCarrusel.addAll(
                    nuevosMenusCarrusel
                )

                if (nuevosMenusCarrusel.isNotEmpty()) {
                    menuAdapter.notifyItemRangeInserted(
                        0,
                        nuevosMenusCarrusel.size
                    )
                }

                mostrarContenido()
            }
    }

    private fun mostrarContenido() {
        progressBarMenu.isVisible = false
        rvEntradas.isVisible = true
        rvMenu.isVisible = true

        ejecutarAnimacionInicial()

        rvMenu.post {
            animarTarjetasCarrusel(rvMenu)
        }
    }

    override fun onResume() {
        super.onResume()
        actualizarContadorCarrito()
    }

    companion object {
        private const val LIMITE_MENU_CARRUSEL = 3
        private const val PREFERENCIAS_INVITADO = "preferencias_invitado"
        private const val CLAVE_NOMBRE_INVITADO = "nombre_invitado_"
        private const val INVITADO_SIN_UID = "sin_uid"
    }
}