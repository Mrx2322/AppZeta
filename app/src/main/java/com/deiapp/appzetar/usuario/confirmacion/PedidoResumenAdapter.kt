package com.deiapp.appzetar.usuario.confirmacion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.deiapp.appzeta.R
import com.deiapp.appzetar.usuario.carrito.PedidoItem

class PedidoResumenAdapter(
    private val listaPedido: List<PedidoItem>
) : RecyclerView.Adapter<PedidoResumenAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_pedido_resumen,
            parent,
            false
        )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.render(listaPedido[position])
    }

    override fun getItemCount(): Int = listaPedido.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val resources = view.resources

        private val tvNombre: TextView =
            view.findViewById(R.id.tvNombreProducto)

        private val tvCantidad: TextView =
            view.findViewById(R.id.tvCantidadProducto)

        fun render(item: PedidoItem) {
            tvNombre.text = item.nombre

            tvCantidad.text = resources.getString(
                R.string.resumen_cantidad_item,
                item.cantidad
            )
        }
    }
}