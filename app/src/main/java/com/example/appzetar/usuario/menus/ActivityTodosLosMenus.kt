package com.example.appzetar.usuario.menus

import android.os.Build
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appzetar.R
import com.example.appzetar.usuario.MenuUsuarioAdapter
import com.example.appzetar.usuario.modelos.TaskMenu
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class ActivityTodosLosMenus : AppCompatActivity() {

    private val db =
        FirebaseFirestore.getInstance()

    private lateinit var btnVolver: MaterialButton
    private lateinit var rvTodosLosMenus: RecyclerView
    private lateinit var progressTodosLosMenus: View
    private lateinit var tvSinMenus: TextView

    private lateinit var menuAdapter: MenuUsuarioAdapter

    private val listaMenu =
        mutableListOf<TaskMenu>()

    private var menuListener: ListenerRegistration? =
        null

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
            R.layout.activity_todos_los_menus
        )

        aplicarInsets()
        inicializarVistas()
        configurarRecycler()
        configurarEventos()
        escucharMenus()
    }

    private fun aplicarInsets() {
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
    }

    private fun inicializarVistas() {
        btnVolver =
            findViewById(R.id.btnVolver)

        rvTodosLosMenus =
            findViewById(R.id.rvTodosLosMenus)

        progressTodosLosMenus =
            findViewById(R.id.progressTodosLosMenus)

        tvSinMenus =
            findViewById(R.id.tvSinMenus)
    }

    private fun configurarRecycler() {
        menuAdapter =
            MenuUsuarioAdapter(
                listaMenu
            ) { plato ->
                devolverMenuSeleccionado(
                    plato
                )
            }

        rvTodosLosMenus.apply {
            layoutManager =
                LinearLayoutManager(
                    this@ActivityTodosLosMenus
                )

            adapter =
                menuAdapter

            setHasFixedSize(true)

            itemAnimator =
                null
        }
    }

    private fun configurarEventos() {
        btnVolver.setOnClickListener {
            cerrarPantalla()
        }
    }

    private fun escucharMenus() {
        mostrarCargando(true)

        menuListener =
            db.collection(COLECCION_MENU)
                .addSnapshotListener { resultado, error ->

                    mostrarCargando(false)

                    if (error != null) {
                        manejarErrorCarga(error)
                        return@addSnapshotListener
                    }

                    if (resultado == null) {
                        actualizarMenus(
                            emptyList()
                        )
                        return@addSnapshotListener
                    }

                    val nuevosMenus =
                        resultado.documents
                            .mapNotNull { documento ->
                                documento.toTaskMenuOrNull()
                            }
                            .sortedBy { menu ->
                                menu.name.lowercase()
                            }

                    actualizarMenus(
                        nuevosMenus
                    )
                }
    }

    private fun manejarErrorCarga(
        error: Exception
    ) {
        Log.e(
            TAG,
            LOG_ERROR_CARGA,
            error
        )

        Toast.makeText(
            this,
            getString(
                R.string.error_cargar_menus
            ),
            Toast.LENGTH_SHORT
        ).show()

        actualizarMenus(
            emptyList()
        )
    }

    private fun actualizarMenus(
        nuevosMenus: List<TaskMenu>
    ) {
        val listaAnterior =
            listaMenu.toList()

        listaMenu.clear()
        listaMenu.addAll(
            nuevosMenus
        )

        if (
            listaAnterior.isEmpty() &&
            listaMenu.isNotEmpty()
        ) {
            menuAdapter.notifyItemRangeInserted(
                0,
                listaMenu.size
            )

        } else if (
            listaAnterior.isNotEmpty() &&
            listaMenu.isEmpty()
        ) {
            menuAdapter.notifyItemRangeRemoved(
                0,
                listaAnterior.size
            )

        } else {
            menuAdapter.notifyItemRangeChanged(
                0,
                minOf(
                    listaAnterior.size,
                    listaMenu.size
                )
            )

            if (listaMenu.size > listaAnterior.size) {
                menuAdapter.notifyItemRangeInserted(
                    listaAnterior.size,
                    listaMenu.size - listaAnterior.size
                )
            }

            if (listaAnterior.size > listaMenu.size) {
                menuAdapter.notifyItemRangeRemoved(
                    listaMenu.size,
                    listaAnterior.size - listaMenu.size
                )
            }
        }

        mostrarEstadoVacio()
    }

    private fun DocumentSnapshot.toTaskMenuOrNull(): TaskMenu? {
        val id =
            getLong(CAMPO_ID)
                ?.toInt()
                ?: id.toIntOrNull()
                ?: return null

        val nombre =
            getString(CAMPO_NOMBRE)
                ?.trim()
                .orEmpty()

        if (nombre.isBlank()) {
            return null
        }

        val precio =
            getDouble(CAMPO_PRECIO)
                ?: 0.0

        val stock =
            getLong(CAMPO_STOCK)
                ?.toInt()
                ?: 0

        return TaskMenu(
            id = id,
            name = nombre,
            precio = precio,
            stock = stock
        )
    }

    private fun mostrarCargando(
        mostrar: Boolean
    ) {
        progressTodosLosMenus.visibility =
            if (mostrar) {
                View.VISIBLE
            } else {
                View.GONE
            }

        if (mostrar) {
            tvSinMenus.visibility =
                View.GONE
        }
    }

    private fun mostrarEstadoVacio() {
        val listaVacia =
            listaMenu.isEmpty()

        tvSinMenus.visibility =
            if (listaVacia) {
                View.VISIBLE
            } else {
                View.GONE
            }

        rvTodosLosMenus.visibility =
            if (listaVacia) {
                View.GONE
            } else {
                View.VISIBLE
            }
    }

    private fun devolverMenuSeleccionado(
        plato: TaskMenu
    ) {
        if (plato.stock <= 0) {
            Toast.makeText(
                this,
                getString(
                    R.string.menu_agotado
                ),
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val resultado =
            Intent().apply {
                putExtra(
                    EXTRA_MENU_ID,
                    plato.id
                )
            }

        setResult(
            RESULT_OK,
            resultado
        )

        cerrarPantalla()
    }

    private fun cerrarPantalla() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {

            overrideActivityTransition(
                OVERRIDE_TRANSITION_CLOSE,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )

            finish()

        } else {

            finish()

            @Suppress("DEPRECATION")
            overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
        }
    }

    override fun onDestroy() {
        menuListener?.remove()
        menuListener =
            null

        super.onDestroy()
    }

    companion object {

        const val EXTRA_MENU_ID =
            "extra_menu_id"

        private const val COLECCION_MENU =
            "menu"

        private const val CAMPO_ID =
            "id"

        private const val CAMPO_NOMBRE =
            "nombre"

        private const val CAMPO_PRECIO =
            "precio"

        private const val CAMPO_STOCK =
            "stock"

        private const val TAG =
            "TODOS_LOS_MENUS"

        private const val LOG_ERROR_CARGA =
            "Error cargando los menús"
    }
}