package com.example.appzetar.Menu

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appzetar.R
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PedidoAdminViewHolder(
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    private val tvNumeroPedido =
        itemView.findViewById<TextView>(
            R.id.tvNumeroPedido
        )

    private val tvFechaPedido =
        itemView.findViewById<TextView>(
            R.id.tvFechaPedido
        )

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

    private val layoutCambiarEstado =
        itemView.findViewById<View>(
            R.id.layoutCambiarEstado
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
        onCambiarEstado: (PedidoAdmin, String) -> Unit,
        modoHistorial: Boolean = false
    ) {

        // =====================================================
        // NÚMERO DE PEDIDO
        // =====================================================

        if (pedido.numeroPedido > 0L) {

            tvNumeroPedido.text =
                "📋 Pedido #${
                    String.format(
                        "%04d",
                        pedido.numeroPedido
                    )
                }"

        } else {

            tvNumeroPedido.text =
                "📋 Pedido sin número"
        }


        // =====================================================
        // FECHA Y HORA
        // =====================================================

        if (pedido.fecha != null) {

            val formato =
                SimpleDateFormat(
                    "dd/MM/yyyy • hh:mm a",
                    Locale("es", "PE")
                )

            tvFechaPedido.text =
                "📅 ${formato.format(
                    pedido.fecha.toDate()
                )}"

            tvFechaPedido.visibility =
                View.VISIBLE

        } else {

            tvFechaPedido.text =
                "📅 Fecha no disponible"

            tvFechaPedido.visibility =
                View.VISIBLE
        }


        // =====================================================
        // CLIENTE
        // =====================================================

        tvNombreUsuario.text =
            "👤 ${pedido.nombreUsuario}"


        // =====================================================
        // TELÉFONO
        // =====================================================

        tvTelefono.text =
            "📞 ${pedido.telefono}"


        // =====================================================
        // TIPO DE ENTREGA
        // =====================================================

        tvTipoEntrega.text =
            "🚚 ${pedido.tipoEntrega}"


        // =====================================================
        // DIRECCIÓN
        // =====================================================

        tvDireccion.text =
            if (pedido.direccion.isNotEmpty()) {
                "📍 ${pedido.direccion}"
            } else {
                "📍 Sin dirección"
            }


        // =====================================================
        // REFERENCIA
        // =====================================================

        tvReferencia.text =
            if (pedido.referencia.isNotEmpty()) {
                "Referencia: ${pedido.referencia}"
            } else {
                "Sin referencia"
            }


        // =====================================================
        // MÉTODO DE PAGO
        // =====================================================

        tvMetodoPago.text =
            "💳 ${pedido.metodoPago}"


        // =====================================================
        // TOTAL
        // =====================================================

        tvTotal.text =
            "💰 S/ %.2f".format(
                pedido.total
            )


        // =====================================================
        // ESTADO
        // =====================================================

        tvEstadoPedido.text =
            pedido.estadoPedido.uppercase()

        if (
            pedido.estadoPedido.equals(
                "Entregado",
                ignoreCase = true
            )
        ) {

            tvEstadoPedido.setBackgroundColor(
                Color.parseColor(
                    "#E8F5E9"
                )
            )

            tvEstadoPedido.setTextColor(
                Color.parseColor(
                    "#2E7D32"
                )
            )

        } else {

            tvEstadoPedido.setBackgroundColor(
                Color.parseColor(
                    "#FFF3E0"
                )
            )

            tvEstadoPedido.setTextColor(
                Color.parseColor(
                    "#F57C00"
                )
            )
        }


        // =====================================================
        // PRODUCTOS
        // =====================================================

        mostrarProductos(
            pedido.productos
        )


        // =====================================================
        // CONTROLES DE ESTADO
        // =====================================================

        if (modoHistorial) {

            layoutCambiarEstado.visibility =
                View.GONE

        } else {

            layoutCambiarEstado.visibility =
                View.VISIBLE

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

        texto.append(
            "🍽️ PRODUCTOS\n"
        )

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