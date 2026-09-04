package com.example.appzetar.Menu

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appzetar.R
import com.google.android.material.button.MaterialButton

class PedidoAdminViewHolder(
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    private val tvNombreUsuario =
        itemView.findViewById<TextView>(
            R.id.tvNombreUsuario
        )

    private val tvTelefono =
        itemView.findViewById<TextView>(
            R.id.tvTelefono
        )

    private val tvProductos =
        itemView.findViewById<TextView>(
            R.id.tvProductos
        )

    private val tvTipoEntrega =
        itemView.findViewById<TextView>(
            R.id.tvTipoEntrega
        )

    private val tvDireccion =
        itemView.findViewById<TextView>(
            R.id.tvDireccion
        )

    private val tvReferencia =
        itemView.findViewById<TextView>(
            R.id.tvReferencia
        )

    private val tvMetodoPago =
        itemView.findViewById<TextView>(
            R.id.tvMetodoPago
        )

    private val tvTotal =
        itemView.findViewById<TextView>(
            R.id.tvTotal
        )

    private val tvEstadoPedido =
        itemView.findViewById<TextView>(
            R.id.tvEstadoPedido
        )

    private val btnPendiente =
        itemView.findViewById<MaterialButton>(
            R.id.btnPendiente
        )

    private val btnPreparando =
        itemView.findViewById<MaterialButton>(
            R.id.btnPreparando
        )

    private val btnEnCamino =
        itemView.findViewById<MaterialButton>(
            R.id.btnEnCamino
        )

    private val btnEntregado =
        itemView.findViewById<MaterialButton>(
            R.id.btnEntregado
        )

    fun render(
        pedido: PedidoAdmin,
        onCambiarEstado: (PedidoAdmin, String) -> Unit
    ) {

        tvNombreUsuario.text =
            "👤 ${pedido.nombreUsuario}"

        tvTelefono.text =
            "📞 ${pedido.telefono}"

        tvTipoEntrega.text =
            "🚚 ${pedido.tipoEntrega}"

        tvDireccion.text =
            if (pedido.direccion.isNotEmpty()) {
                "📍 ${pedido.direccion}"
            } else {
                "📍 Sin dirección"
            }

        tvReferencia.text =
            if (pedido.referencia.isNotEmpty()) {
                "Referencia: ${pedido.referencia}"
            } else {
                "Sin referencia"
            }

        tvMetodoPago.text =
            "💳 ${pedido.metodoPago}"

        tvTotal.text =
            "💰 S/ %.2f".format(
                pedido.total
            )

        tvEstadoPedido.text =
            pedido.estadoPedido.uppercase()

        mostrarProductos(
            pedido.productos
        )

        btnPendiente.setOnClickListener {
            onCambiarEstado(
                pedido,
                "Pendiente"
            )
        }

        btnPreparando.setOnClickListener {
            onCambiarEstado(
                pedido,
                "Preparando"
            )
        }

        btnEnCamino.setOnClickListener {
            onCambiarEstado(
                pedido,
                "En camino"
            )
        }

        btnEntregado.setOnClickListener {
            onCambiarEstado(
                pedido,
                "Entregado"
            )
        }
    }

    private fun mostrarProductos(
        productos: List<Map<String, Any>>
    ) {

        if (productos.isEmpty()) {

            tvProductos.text =
                "🍽️ Sin productos"

            return
        }

        val texto =
            StringBuilder()

        texto.append("🍽️ PRODUCTOS\n")

        for (producto in productos) {

            val nombre =
                producto["nombre"]
                    ?.toString()
                    ?: "Producto"

            val cantidad =
                (producto["cantidad"] as? Number)
                    ?.toInt()
                    ?: 1

            val precio =
                (producto["precio"] as? Number)
                    ?.toDouble()
                    ?: 0.0

            texto.append(
                "• $cantidad × $nombre — S/ %.2f\n"
                    .format(
                        precio * cantidad
                    )
            )
        }

        tvProductos.text =
            texto.toString().trim()
    }
}