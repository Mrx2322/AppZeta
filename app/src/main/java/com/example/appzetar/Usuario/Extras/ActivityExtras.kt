package com.example.appzetar.Usuario

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appzetar.R
import com.google.firebase.firestore.FirebaseFirestore

class ActivityExtras : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    private lateinit var rvCategorias: RecyclerView
    private lateinit var rvExtras: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvSinExtras: TextView
    private lateinit var tvCantidadExtras: TextView
    private lateinit var estadoSinExtras: LinearLayout

    private lateinit var categoriaAdapter: CategoriaAdapter
    private lateinit var extraAdapter: ExtraAdapter

    private val listaCategorias = mutableListOf<CategoriaItem>()
    private val listaExtras = mutableListOf<ExtraItem>()

    private var categoriaSeleccionadaId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_extras)

        initComponent()
        initUI()
        cargarCategorias()
        cargarExtras()
    }

    private fun initComponent() {
        rvCategorias = findViewById(R.id.rvCategorias)
        rvExtras = findViewById(R.id.rvExtras)
        progressBar = findViewById(R.id.progressBar)
        tvSinExtras = findViewById(R.id.tvSinExtras)
        tvCantidadExtras = findViewById(R.id.tvCantidadExtras)
        estadoSinExtras = findViewById(R.id.estadoSinExtras)
    }

    private fun initUI() {
        categoriaAdapter = CategoriaAdapter(listaCategorias) { categoria ->
            categoriaSeleccionadaId = categoria.id
            filtrarExtras(categoria.id)
        }

        rvCategorias.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        rvCategorias.adapter = categoriaAdapter

        extraAdapter = ExtraAdapter(listaExtras) { extra ->
            agregarExtraAlPedido(extra)
        }

        rvExtras.layoutManager = LinearLayoutManager(this)
        rvExtras.adapter = extraAdapter
    }

    private fun cargarCategorias() {
        listaCategorias.clear()
        listaCategorias.add(CategoriaItem(0, "Todos", R.drawable.ic_extra))
        listaCategorias.add(CategoriaItem(1, "Gaseosas", R.drawable.ic_gaseosa))
        listaCategorias.add(CategoriaItem(2, "Tortas", R.drawable.ic_torta))
        listaCategorias.add(CategoriaItem(3, "Platos", R.drawable.ic_plato))
        categoriaAdapter.notifyDataSetChanged()
    }

    private fun cargarExtras() {
        progressBar.visibility = View.VISIBLE

        db.collection("extras")
            .addSnapshotListener { resultado, error ->
                progressBar.visibility = View.GONE

                if (error != null) {
                    Log.e("EXTRAS", "Error cargando extras", error)
                    mostrarEstadoLista(0)
                    Toast.makeText(
                        this,
                        "Error al cargar extras",
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
                    val id = documento.getLong("id")?.toInt() ?: continue
                    val nombre = documento.getString("nombre") ?: "Extra"
                    val precio = documento.getDouble("precio") ?: 0.0
                    val categoriaId = documento.getLong("categoriaId")?.toInt() ?: 0

                    val icono = when (categoriaId) {
                        1 -> R.drawable.ic_gaseosa
                        2 -> R.drawable.ic_torta
                        3 -> R.drawable.ic_plato
                        else -> R.drawable.ic_plato
                    }

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

                // Actualiza la copia interna que realmente muestra el adaptador.
                extraAdapter.actualizarExtras(
                    listaExtras,
                    categoriaSeleccionadaId
                )

                mostrarEstadoLista(extraAdapter.itemCount)
            }
    }

    private fun filtrarExtras(categoriaId: Int) {
        extraAdapter.filtrarPorCategoria(categoriaId)
        mostrarEstadoLista(extraAdapter.itemCount)
    }

    private fun mostrarEstadoLista(cantidad: Int) {
        val estaVacia = cantidad == 0

        rvExtras.visibility = if (estaVacia) View.GONE else View.VISIBLE
        estadoSinExtras.visibility = if (estaVacia) View.VISIBLE else View.GONE

        tvCantidadExtras.text = if (cantidad == 1) {
            "1 opción"
        } else {
            "$cantidad opciones"
        }
    }

    private fun agregarExtraAlPedido(extra: ExtraItem) {
        PedidoManager.agregarProducto(
            PedidoItem(
                id = extra.id,
                nombre = extra.nombre,
                precio = extra.precio,
                cantidad = 1,
                tipo = TipoPedido.EXTRA
            )
        )

        Toast.makeText(
            this,
            "${extra.nombre} agregado al carrito",
            Toast.LENGTH_SHORT
        ).show()
    }
}
