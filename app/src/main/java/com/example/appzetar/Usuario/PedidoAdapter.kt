package com.example.appzetar.Usuario

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.appzetar.R

class PedidoAdapter(
    private val listaPedido: MutableList<PedidoItem>,
    private val onPedidoActualizado: () -> Unit
) : RecyclerView.Adapter<PedidoViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PedidoViewHolder {

        val view =
            LayoutInflater.from(parent.context)
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
            onPedidoActualizado
        )
    }

    override fun getItemCount(): Int {

        return listaPedido.size
    }
}