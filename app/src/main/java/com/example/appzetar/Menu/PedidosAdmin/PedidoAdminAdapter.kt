package com.example.appzetar.Menu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.appzetar.R

class PedidoAdminAdapter(
    private val listaPedidos: MutableList<PedidoAdmin>,
    private val onCambiarEstado: (PedidoAdmin, String) -> Unit
) : RecyclerView.Adapter<PedidoAdminViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PedidoAdminViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_pedido_admin,
                    parent,
                    false
                )

        return PedidoAdminViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PedidoAdminViewHolder,
        position: Int
    ) {

        holder.render(
            listaPedidos[position],
            onCambiarEstado
        )
    }

    override fun getItemCount(): Int {
        return listaPedidos.size
    }
}