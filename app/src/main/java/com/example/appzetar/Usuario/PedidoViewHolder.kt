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

    private val tvPrecio =
        view.findViewById<TextView>(
            R.id.tvPrecioProducto
        )

    private val tvCantidad =
        view.findViewById<TextView>(
            R.id.tvCantidad
        )

    private val tvSubtotal =
        view.findViewById<TextView>(
            R.id.tvSubtotalProducto
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


    // =========================================================
    // RENDER
    // =========================================================

    fun render(
        item: PedidoItem,
        onPedidoActualizado: () -> Unit
    ) {

        // =====================================================
        // DATOS
        // =====================================================

        tvNombre.text =
            item.nombre

        tvPrecio.text =
            "S/ %.2f c/u".format(
                item.precio
            )

        tvCantidad.text =
            item.cantidad.toString()


        // =====================================================
        // SUBTOTAL DEL PRODUCTO
        // =====================================================

        val subtotal =
            item.precio * item.cantidad

        tvSubtotal.text =
            "S/ %.2f".format(
                subtotal
            )


        // =====================================================
        // BOTÓN +
        // =====================================================

        btnMas.setOnClickListener {

            PedidoManager.aumentarCantidad(
                item.id,
                item.tipo
            )

            onPedidoActualizado()
        }


        // =====================================================
        // BOTÓN -
        // =====================================================

        btnMenos.setOnClickListener {

            PedidoManager.disminuirCantidad(
                item.id,
                item.tipo
            )

            onPedidoActualizado()
        }


        // =====================================================
        // ELIMINAR
        // =====================================================

        btnEliminar.setOnClickListener {

            PedidoManager.eliminarProducto(
                item.id,
                item.tipo
            )

            onPedidoActualizado()
        }
    }
}