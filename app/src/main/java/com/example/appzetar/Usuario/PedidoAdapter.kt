package com.example.appzetar.Usuario

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.appzetar.R

class PedidoAdapter(
    private val listaPedido: MutableList<PedidoItem>,
    private val onAumentar: (PedidoItem) -> Unit,
    private val onDisminuir: (PedidoItem) -> Unit,
    private val onEliminar: (PedidoItem) -> Unit
) : RecyclerView.Adapter<PedidoViewHolder>() {

    // =========================================================
    // ON CREATE VIEW HOLDER
    // =========================================================

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


    // =========================================================
    // ON BIND VIEW HOLDER
    // =========================================================

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


    // =========================================================
    // CANTIDAD DE ELEMENTOS
    // =========================================================

    override fun getItemCount(): Int {
        return listaPedido.size
    }


    // =========================================================
    // BLOQUEAR BOTONES DE UN PRODUCTO
    // =========================================================

    fun setBotonesHabilitados(
        id: Int,
        tipo: TipoPedido,
        habilitado: Boolean
    ) {

        for (i in 0 until itemCount) {

            val item =
                listaPedido[i]

            if (
                item.id == id &&
                item.tipo == tipo
            ) {

                val holder =
                    rvHolder(i)

                holder?.setBotonesHabilitados(
                    habilitado
                )

                break
            }
        }
    }


    // =========================================================
    // BLOQUEAR / DESBLOQUEAR TODOS
    // =========================================================

    fun setTodosLosBotonesHabilitados(
        habilitado: Boolean
    ) {

        for (i in 0 until itemCount) {

            val holder =
                rvHolder(i)

            holder?.setBotonesHabilitados(
                habilitado
            )
        }
    }


    // =========================================================
    // OBTENER VIEW HOLDER
    // =========================================================

    private fun rvHolder(
        position: Int
    ): PedidoViewHolder? {

        return null
    }
}