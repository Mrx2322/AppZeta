package com.example.appzetar.Menu

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
import com.google.firebase.firestore.FirebaseFirestore

class ActivityPedidosAdmin : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    private lateinit var rvPedidos: RecyclerView
    private lateinit var tvSinPedidos: TextView

    private val listaPedidos = mutableListOf<PedidoAdmin>()

    private lateinit var adapter: PedidoAdminAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(
            window,
            false
        )

        setContentView(R.layout.activity_pedidos_admin)

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

        initComponent()
        initUI()
        escucharPedidos()
    }

    private fun initComponent() {

        rvPedidos = findViewById(R.id.rvPedidos)

        tvSinPedidos = findViewById(R.id.tvSinPedidos)
    }

    private fun initUI() {

        adapter = PedidoAdminAdapter(
            listaPedidos
        ) { pedido, nuevoEstado ->

            cambiarEstadoPedido(
                pedido,
                nuevoEstado
            )
        }

        rvPedidos.layoutManager =
            LinearLayoutManager(this)

        rvPedidos.adapter = adapter
    }

    private fun escucharPedidos() {

        db.collection("pedidos")
            .addSnapshotListener { resultado, error ->

                if (error != null) {

                    Log.e(
                        "PEDIDOS_ADMIN",
                        "Error escuchando pedidos",
                        error
                    )

                    Toast.makeText(
                        this,
                        "Error al cargar pedidos",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@addSnapshotListener
                }

                if (resultado == null) {
                    return@addSnapshotListener
                }

                listaPedidos.clear()

                for (documento in resultado.documents) {

                    val pedido = PedidoAdmin(

                        id = documento.id,

                        nombreUsuario =
                            documento.getString(
                                "nombreUsuario"
                            ) ?: "Cliente",

                        correo =
                            documento.getString(
                                "correo"
                            ) ?: "",

                        total =
                            documento.getDouble(
                                "total"
                            ) ?: 0.0,

                        tipoEntrega =
                            documento.getString(
                                "tipoEntrega"
                            ) ?: "Delivery",

                        direccion =
                            documento.getString(
                                "direccion"
                            ) ?: "",

                        referencia =
                            documento.getString(
                                "referencia"
                            ) ?: "",

                        telefono =
                            documento.getString(
                                "telefono"
                            ) ?: "",

                        metodoPago =
                            documento.getString(
                                "metodoPago"
                            ) ?: "Contra entrega",

                        estadoPago =
                            documento.getString(
                                "estadoPago"
                            ) ?: "Pendiente",

                        estadoPedido =
                            documento.getString(
                                "estadoPedido"
                            ) ?: "Pendiente",

                        productos =
                            documento.get(
                                "productos"
                            ) as? List<Map<String, Any>>
                                ?: emptyList()
                    )

                    listaPedidos.add(pedido)
                }

                // Los pedidos más recientes aparecen primero.
                listaPedidos.reverse()

                adapter.notifyDataSetChanged()

                actualizarEstadoVacio()

                Log.d(
                    "PEDIDOS_ADMIN",
                    "Pedidos recibidos: ${listaPedidos.size}"
                )
            }
    }

    private fun cambiarEstadoPedido(
        pedido: PedidoAdmin,
        nuevoEstado: String
    ) {

        db.collection("pedidos")
            .document(pedido.id)
            .update(
                "estadoPedido",
                nuevoEstado
            )
            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "Pedido actualizado: $nuevoEstado",
                    Toast.LENGTH_SHORT
                ).show()

                Log.d(
                    "PEDIDOS_ADMIN",
                    "Pedido ${pedido.id} actualizado a $nuevoEstado"
                )
            }
            .addOnFailureListener { error ->

                Log.e(
                    "PEDIDOS_ADMIN",
                    "Error actualizando estado",
                    error
                )

                Toast.makeText(
                    this,
                    "No se pudo actualizar el pedido",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun actualizarEstadoVacio() {

        if (listaPedidos.isEmpty()) {

            rvPedidos.visibility = View.GONE

            tvSinPedidos.visibility = View.VISIBLE

        } else {

            rvPedidos.visibility = View.VISIBLE

            tvSinPedidos.visibility = View.GONE
        }
    }
}