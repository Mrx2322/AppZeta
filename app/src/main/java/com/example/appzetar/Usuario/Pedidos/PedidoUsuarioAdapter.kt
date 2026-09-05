package com.example.appzetar.Usuario

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appzetar.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PedidoUsuarioAdapter :
    RecyclerView.Adapter<PedidoUsuarioAdapter.PedidoViewHolder>() {

    private val pedidos =
        mutableListOf<PedidoUsuarioItem>()

    class PedidoViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val tvNumeroPedido: TextView =
            itemView.findViewById(R.id.tvNumeroPedido)

        val tvEstadoPedido: TextView =
            itemView.findViewById(R.id.tvEstadoPedido)

        val tvTotalPedido: TextView =
            itemView.findViewById(R.id.tvTotalPedido)

        val tvFechaPedido: TextView =
            itemView.findViewById(R.id.tvFechaPedido)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PedidoViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_pedido_usuario,
                    parent,
                    false
                )

        return PedidoViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PedidoViewHolder,
        position: Int
    ) {

        val pedido =
            pedidos[position]

        // -----------------------------------------------------
        // NÚMERO DEL PEDIDO
        // -----------------------------------------------------

        holder.tvNumeroPedido.text =
            "Pedido #${pedido.numeroPedidoFormateado()}"


        // -----------------------------------------------------
        // ESTADO
        // -----------------------------------------------------

        holder.tvEstadoPedido.text =
            pedido.estado

        configurarEstado(
            holder.tvEstadoPedido,
            pedido.estado
        )


        // -----------------------------------------------------
        // TOTAL
        // -----------------------------------------------------

        holder.tvTotalPedido.text =
            "Total: S/ %.2f".format(
                Locale.US,
                pedido.total
            )


        // -----------------------------------------------------
        // FECHA
        // -----------------------------------------------------

        holder.tvFechaPedido.text =
            formatearFecha(
                pedido.fecha
            )


        Log.d(
            "PEDIDOS_ADAPTER",
            "Mostrando pedido: " +
                    "${pedido.numeroPedidoFormateado()} | " +
                    "Estado: ${pedido.estado} | " +
                    "Total: ${pedido.total}"
        )
    }

    override fun getItemCount(): Int {
        return pedidos.size
    }


    // =========================================================
    // FORMATEAR FECHA
    // =========================================================

    private fun formatearFecha(
        fecha: Long
    ): String {

        if (fecha <= 0L) {
            return "Fecha no disponible"
        }

        val date =
            Date(fecha)

        val formato =
            SimpleDateFormat(
                "dd/MM/yyyy HH:mm",
                Locale.getDefault()
            )

        return formato.format(date)
    }


    // =========================================================
    // ESTADO
    // =========================================================

    private fun configurarEstado(
        textView: TextView,
        estado: String
    ) {

        when (
            estado.lowercase(
                Locale.getDefault()
            )
        ) {

            "pendiente" -> {
                textView.text = "Pendiente"
            }

            "confirmado" -> {
                textView.text = "Confirmado"
            }

            "preparando" -> {
                textView.text = "Preparando"
            }

            "en camino" -> {
                textView.text = "En camino"
            }

            "entregado" -> {
                textView.text = "Entregado"
            }

            "cancelado" -> {
                textView.text = "Cancelado"
            }

            else -> {
                textView.text = estado
            }
        }
    }


    // =========================================================
    // ACTUALIZAR PEDIDOS
    // =========================================================

    fun actualizarPedidos(
        nuevosPedidos: List<PedidoUsuarioItem>
    ) {

        pedidos.clear()

        pedidos.addAll(
            nuevosPedidos
        )

        Log.d(
            "PEDIDOS_ADAPTER",
            "Pedidos recibidos: ${nuevosPedidos.size}"
        )

        Log.d(
            "PEDIDOS_ADAPTER",
            "Pedidos en adapter: ${pedidos.size}"
        )

        notifyDataSetChanged()
    }
}