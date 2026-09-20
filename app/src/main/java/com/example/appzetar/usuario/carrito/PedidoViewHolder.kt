package com.example.appzetar.usuario.carrito

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.appzetar.R

class PedidoViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val resources = view.resources

    private val tvNombre: TextView = view.findViewById(R.id.tvNombreProducto)

    private val tvPrecio: TextView = view.findViewById(R.id.tvPrecioProducto)

    private val tvDetalleProducto: TextView =
        view.findViewById(R.id.tvDetalleProducto)

    private val tvCantidad: TextView = view.findViewById(R.id.tvCantidad)

    private val tvSubtotal: TextView =
        view.findViewById(R.id.tvSubtotalProducto)

    private val btnMenos: ImageButton = view.findViewById(R.id.btnMenos)

    private val btnMas: ImageButton = view.findViewById(R.id.btnMas)

    private val btnEliminar: ImageButton = view.findViewById(R.id.btnEliminar)

    fun render(
        item: PedidoItem,
        onAumentar: (PedidoItem) -> Unit,
        onDisminuir: (PedidoItem) -> Unit,
        onEliminar: (PedidoItem) -> Unit
    ) {
        tvNombre.text = item.nombre

        val esMenu = item.tipo == TipoPedido.MENU
        tvDetalleProducto.isVisible = esMenu

        if (esMenu) {
            tvDetalleProducto.text = obtenerDetalleMenu(item)
        } else {
            tvDetalleProducto.text = null
        }

        tvPrecio.text = resources.getString(
            R.string.pedido_precio_unitario,
            item.precio
        )
        tvCantidad.text = item.cantidad.toString()
        tvSubtotal.text = resources.getString(
            R.string.pedido_subtotal,
            item.subtotal()
        )

        btnMas.setOnClickListener { onAumentar(item) }
        btnMenos.setOnClickListener { onDisminuir(item) }
        btnEliminar.setOnClickListener { onEliminar(item) }
    }

    private fun obtenerDetalleMenu(item: PedidoItem): String {
        if (item.entradas.isEmpty()) {
            return resources.getString(R.string.pedido_sin_entrada)
        }

        val recursoDetalle = if (item.cantidadEntradas() == 1) {
            R.string.pedido_entrada_detalle
        } else {
            R.string.pedido_entradas_detalle
        }

        return resources.getString(
            recursoDetalle,
            item.descripcionEntradas()
        )
    }
}
