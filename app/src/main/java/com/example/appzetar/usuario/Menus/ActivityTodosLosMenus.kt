package com.example.appzetar.usuario

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
import com.example.appzetar.usuario.Modelos.TaskMenu
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class ActivityTodosLosMenus : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    private lateinit var btnVolver: MaterialButton
    private lateinit var rvTodosLosMenus: RecyclerView
    private lateinit var progressTodosLosMenus: View
    private lateinit var tvSinMenus: TextView

    private val listaMenu =
        mutableListOf<TaskMenu>()

    private lateinit var menuAdapter:
            MenuUsuarioAdapter

    private var menuListener:
            ListenerRegistration? = null

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

        iniciarComponentes()
        configurarPantalla()
        escucharMenus()
    }

    private fun iniciarComponentes() {

        btnVolver =
            findViewById(R.id.btnVolver)

        rvTodosLosMenus =
            findViewById(R.id.rvTodosLosMenus)

        progressTodosLosMenus =
            findViewById(R.id.progressTodosLosMenus)

        tvSinMenus =
            findViewById(R.id.tvSinMenus)
    }

    private fun configurarPantalla() {

        menuAdapter =
            MenuUsuarioAdapter(
                listaMenu
            ) { plato ->

                devolverMenuSeleccionado(
                    plato
                )
            }

        rvTodosLosMenus.layoutManager =
            LinearLayoutManager(this)

        rvTodosLosMenus.adapter =
            menuAdapter

        rvTodosLosMenus.setHasFixedSize(
            false
        )

        rvTodosLosMenus.itemAnimator =
            null

        btnVolver.setOnClickListener {
            cerrarPantalla()
        }
    }

    private fun escucharMenus() {

        progressTodosLosMenus.visibility =
            View.VISIBLE

        tvSinMenus.visibility =
            View.GONE

        menuListener =
            db.collection("menu")
                .addSnapshotListener { resultado, error ->

                    progressTodosLosMenus.visibility =
                        View.GONE

                    if (error != null) {

                        Log.e(
                            "TODOS_LOS_MENUS",
                            "Error cargando los menús",
                            error
                        )

                        Toast.makeText(
                            this,
                            "No se pudieron cargar los menús",
                            Toast.LENGTH_SHORT
                        ).show()

                        mostrarEstadoVacio()

                        return@addSnapshotListener
                    }

                    if (resultado == null) {
                        mostrarEstadoVacio()
                        return@addSnapshotListener
                    }

                    listaMenu.clear()

                    for (documento in resultado.documents) {

                        val id =
                            documento
                                .getLong("id")
                                ?.toInt()
                                ?: documento.id
                                    .toIntOrNull()
                                ?: 0

                        val nombre =
                            documento
                                .getString("nombre")
                                .orEmpty()

                        val precio =
                            documento
                                .getDouble("precio")
                                ?: 0.0

                        val stock =
                            documento
                                .getLong("stock")
                                ?.toInt()
                                ?: 0

                        if (
                            id > 0 &&
                            nombre.isNotBlank()
                        ) {
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

                    listaMenu.sortBy { menu ->
                        menu.name.lowercase()
                    }

                    menuAdapter.notifyDataSetChanged()

                    mostrarEstadoVacio()
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
                "Este plato está agotado",
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

        finish()

        overridePendingTransition(
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
    }

    private fun cerrarPantalla() {

        finish()

        overridePendingTransition(
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
    }

    override fun onDestroy() {

        menuListener?.remove()
        menuListener = null

        super.onDestroy()
    }

    companion object {

        const val EXTRA_MENU_ID =
            "extra_menu_id"
    }
}