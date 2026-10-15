package com.deiapp.appzetar.usuario.carrito

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deiapp.appzeta.R

class PedidoAdapter(
    private val listaPedido: MutableList<PedidoItem>,
    private val onAumentar: (PedidoItem) -> Unit,
    private val onDisminuir: (PedidoItem) -> Unit,
    private val onEliminar: (PedidoItem) -> Unit
) : RecyclerView.Adapter<PedidoViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PedidoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_pedido,
                parent,
                false
            )

        return PedidoViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PedidoViewHolder,
        position: Int
    ) {
        holder.render(
            listaPedido[position],
            onAumentar,
            onDisminuir,
            onEliminar
        )
    }

    override fun getItemCount(): Int {
        return listaPedido.size
    }
}