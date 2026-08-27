package com.example.appzetar.Usuario

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appzetar.R

class PedidoViewHolder(
    view: View
) : RecyclerView.ViewHolder(view) {

    private val tvNombre =
        view.findViewById<TextView>(
            R.id.tvNombreProducto
        )

    private val tvCantidad =
        view.findViewById<TextView>(
            R.id.tvCantidad
        )

    private val btnMenos =
        view.findViewById<ImageButton>(
            R.id.btnMenos
        )

    private val btnMas =
        view.findViewById<ImageButton>(
            R.id.btnMas
        )

    private val btnEliminar =
        view.findViewById<ImageButton>(
            R.id.btnEliminar
        )

    fun render(
        item: PedidoItem,
        onPedidoActualizado: () -> Unit
    ) {

        tvNombre.text =
            item.nombre

        tvCantidad.text =
            item.cantidad.toString()

        btnMas.setOnClickListener {

            PedidoManager.aumentarCantidad(
                item.id
            )

            onPedidoActualizado()
        }

        btnMenos.setOnClickListener {

            PedidoManager.disminuirCantidad(
                item.id
            )

            onPedidoActualizado()
        }

        btnEliminar.setOnClickListener {

            PedidoManager.eliminarProducto(
                item.id
            )

            onPedidoActualizado()
        }
    }
}