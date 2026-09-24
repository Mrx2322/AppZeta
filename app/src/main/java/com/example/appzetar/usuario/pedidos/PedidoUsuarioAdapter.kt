package com.example.appzetar.usuario.pedidos

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.appzetar.R
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PedidoUsuarioAdapter :
    RecyclerView.Adapter<PedidoUsuarioAdapter.PedidoViewHolder>() {

    // =========================================================
    // COLORES
    // =========================================================

    @ColorRes
    private val colorNaranja = R.color.pedido_naranja

    @ColorRes
    private val colorNaranjaClaro = R.color.pedido_naranja_claro

    @ColorRes
    private val colorGris = R.color.pedido_gris

    @ColorRes
    private val colorTextoGris = R.color.pedido_texto_gris

    @ColorRes
    private val colorVerde = R.color.pedido_verde

    @ColorRes
    private val colorVerdeClaro = R.color.pedido_verde_claro

    @ColorRes
    private val colorRojo = R.color.pedido_rojo

    @ColorRes
    private val colorRojoClaro = R.color.pedido_rojo_claro

    @ColorRes
    private val colorBlanco = R.color.pedido_blanco

    // =========================================================
    // LISTA
    // =========================================================

    private val pedidos =
        mutableListOf<PedidoUsuarioItem>()

    // =========================================================
    // VIEW HOLDER
    // =========================================================

    class PedidoViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val tvNumeroPedido: TextView =
            itemView.findViewById(R.id.tvNumeroPedido)

        val tvFechaPedido: TextView =
            itemView.findViewById(R.id.tvFechaPedido)

        val tvEstadoPedido: TextView =
            itemView.findViewById(R.id.tvEstadoPedido)

        val tvMensajeEstado: TextView =
            itemView.findViewById(R.id.tvMensajeEstado)

        val tvTotalPedido: TextView =
            itemView.findViewById(R.id.tvTotalPedido)

        val cardEstadoPedido: MaterialCardView =
            itemView.findViewById(R.id.cardEstadoPedido)

        val layoutSeguimiento: LinearLayout =
            itemView.findViewById(R.id.layoutSeguimiento)

        val layoutEtiquetasSeguimiento: LinearLayout =
            itemView.findViewById(
                R.id.layoutEtiquetasSeguimiento
            )

        val indicadores =
            listOf<MaterialCardView>(
                itemView.findViewById(R.id.indicadorRecibido),
                itemView.findViewById(R.id.indicadorConfirmado),
                itemView.findViewById(R.id.indicadorPreparacion),
                itemView.findViewById(R.id.indicadorDespacho),
                itemView.findViewById(R.id.indicadorEntregado)
            )

        val textosIndicadores =
            listOf<TextView>(
                itemView.findViewById(R.id.tvIndicadorRecibido),
                itemView.findViewById(R.id.tvIndicadorConfirmado),
                itemView.findViewById(R.id.tvIndicadorPreparacion),
                itemView.findViewById(R.id.tvIndicadorDespacho),
                itemView.findViewById(R.id.tvIndicadorEntregado)
            )

        val lineas =
            listOf<View>(
                itemView.findViewById(R.id.lineaConfirmado),
                itemView.findViewById(R.id.lineaPreparacion),
                itemView.findViewById(R.id.lineaDespacho),
                itemView.findViewById(R.id.lineaEntregado)
            )

        val etiquetas =
            listOf<TextView>(
                itemView.findViewById(R.id.tvEtiquetaRecibido),
                itemView.findViewById(R.id.tvEtiquetaConfirmado),
                itemView.findViewById(R.id.tvEtiquetaPreparacion),
                itemView.findViewById(R.id.tvEtiquetaDespacho),
                itemView.findViewById(R.id.tvEtiquetaEntregado)
            )

        val tvEtiquetaDespacho: TextView =
            itemView.findViewById(R.id.tvEtiquetaDespacho)

        var animacionPulso: AnimatorSet? =
            null

        var animacionLineas: AnimatorSet? =
            null
    }

    // =========================================================
    // CREAR VIEW HOLDER
    // =========================================================

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

    // =========================================================
    // MOSTRAR PEDIDO
    // =========================================================

    override fun onBindViewHolder(
        holder: PedidoViewHolder,
        position: Int
    ) {

        val pedido =
            pedidos[position]

        detenerAnimaciones(holder)

        val contexto = holder.itemView.context

        holder.tvNumeroPedido.text = contexto.getString(
            R.string.pedido_numero_formato,
            pedido.numeroPedidoFormateado()
        )

        holder.tvFechaPedido.text =
            formatearFecha(
                fecha = pedido.fecha,
                context = contexto
            )

        holder.tvTotalPedido.text = contexto.getString(
            R.string.pedido_total_formato,
            pedido.total
        )

        val estado =
            pedido.estadoNormalizado()

        holder.tvEstadoPedido.text =
            estado

        holder.tvEtiquetaDespacho.text =
            if (pedido.esDelivery()) {
                contexto.getString(R.string.pedido_estado_en_camino)
            } else {
                contexto.getString(R.string.pedido_estado_listo_recoger)
            }

        configurarEstadoGeneral(
            holder = holder,
            pedido = pedido,
            estado = estado
        )

        configurarSeguimiento(
            holder = holder,
            pedido = pedido
        )
    }

    override fun getItemCount(): Int {
        return pedidos.size
    }

    // =========================================================
    // ESTADO GENERAL
    // =========================================================

    private fun configurarEstadoGeneral(
        holder: PedidoViewHolder,
        pedido: PedidoUsuarioItem,
        estado: String
    ) {

        when (estado) {

            "Pendiente" -> {

                configurarBadge(
                    holder,
                    colorNaranjaClaro,
                    colorNaranja
                )

                holder.tvMensajeEstado.text =
                    holder.itemView.context.getString(R.string.pedido_mensaje_recibido)
            }

            "Confirmado" -> {

                configurarBadge(
                    holder,
                    colorNaranjaClaro,
                    colorNaranja
                )

                holder.tvMensajeEstado.text =
                    holder.itemView.context.getString(R.string.pedido_mensaje_confirmado)
            }

            "En preparación" -> {

                configurarBadge(
                    holder,
                    colorNaranjaClaro,
                    colorNaranja
                )

                holder.tvMensajeEstado.text =
                    holder.itemView.context.getString(R.string.pedido_mensaje_preparacion)
            }

            "En camino" -> {

                configurarBadge(
                    holder,
                    colorNaranjaClaro,
                    colorNaranja
                )

                holder.tvMensajeEstado.text =
                    holder.itemView.context.getString(R.string.pedido_mensaje_en_camino)
            }

            "Listo para recoger" -> {

                configurarBadge(
                    holder,
                    colorNaranjaClaro,
                    colorNaranja
                )

                holder.tvMensajeEstado.text =
                    holder.itemView.context.getString(R.string.pedido_mensaje_listo_recoger)
            }

            "Entregado" -> {

                configurarBadge(
                    holder,
                    colorVerdeClaro,
                    colorVerde
                )

                holder.tvMensajeEstado.text =
                    if (pedido.esDelivery()) {
                        holder.itemView.context.getString(R.string.pedido_mensaje_entregado)
                    } else {
                        holder.itemView.context.getString(R.string.pedido_mensaje_recogido)
                    }
            }

            "Cancelado" -> {

                configurarBadge(
                    holder,
                    colorRojoClaro,
                    colorRojo
                )

                holder.tvMensajeEstado.text =
                    holder.itemView.context.getString(R.string.pedido_mensaje_cancelado)
            }

            else -> {

                configurarBadge(
                    holder,
                    colorNaranjaClaro,
                    colorNaranja
                )

                holder.tvMensajeEstado.text =
                    holder.itemView.context.getString(R.string.pedido_mensaje_revision)
            }
        }
    }

    private fun configurarBadge(
        holder: PedidoViewHolder,
        colorFondo: Int,
        colorTexto: Int
    ) {

        holder.cardEstadoPedido.setCardBackgroundColor(
            obtenerColor(holder, colorFondo)
        )

        holder.tvEstadoPedido.setTextColor(
            obtenerColor(holder, colorTexto)
        )
    }

    // =========================================================
    // SEGUIMIENTO
    // =========================================================

    private fun configurarSeguimiento(
        holder: PedidoViewHolder,
        pedido: PedidoUsuarioItem
    ) {

        val estadoActual =
            pedido.posicionEstado()

        if (estadoActual == -1) {

            configurarPedidoCancelado(holder)
            return
        }

        holder.layoutSeguimiento.visibility =
            View.VISIBLE

        holder.layoutEtiquetasSeguimiento.visibility =
            View.VISIBLE

        for (indice in holder.indicadores.indices) {

            val indicador =
                holder.indicadores[indice]

            val textoIndicador =
                holder.textosIndicadores[indice]

            val etiqueta =
                holder.etiquetas[indice]

            indicador.scaleX =
                1f

            indicador.scaleY =
                1f

            when {

                // Paso completado
                indice < estadoActual -> {

                    indicador.setCardBackgroundColor(
                        obtenerColor(holder, colorNaranja)
                    )

                    textoIndicador.text =
                        holder.itemView.context.getString(R.string.simbolo_check)

                    textoIndicador.setTextColor(
                        obtenerColor(holder, colorBlanco)
                    )

                    etiqueta.setTextColor(
                        obtenerColor(holder, colorNaranja)
                    )

                    etiqueta.setTypeface(
                        null,
                        Typeface.NORMAL
                    )
                }

                // Paso actual
                indice == estadoActual -> {

                    val colorActual =
                        if (
                            pedido.estadoNormalizado() ==
                            "Entregado"
                        ) {
                            obtenerColor(holder, colorVerde)
                        } else {
                            obtenerColor(holder, colorNaranja)
                        }

                    indicador.setCardBackgroundColor(
                        colorActual
                    )

                    textoIndicador.text =
                        if (
                            pedido.estadoNormalizado() ==
                            "Entregado"
                        ) {
                            holder.itemView.context.getString(R.string.simbolo_check)
                        } else {
                            holder.itemView.context.getString(
                                R.string.pedido_paso_numero,
                                indice + 1
                            )
                        }

                    textoIndicador.setTextColor(
                        obtenerColor(holder, colorBlanco)
                    )

                    etiqueta.setTextColor(
                        colorActual
                    )

                    etiqueta.setTypeface(
                        null,
                        Typeface.BOLD
                    )
                }

                // Paso pendiente
                else -> {

                    indicador.setCardBackgroundColor(
                        obtenerColor(holder, colorGris)
                    )

                    textoIndicador.text =
                        holder.itemView.context.getString(
                            R.string.pedido_paso_numero,
                            indice + 1
                        )

                    textoIndicador.setTextColor(
                        obtenerColor(holder, colorTextoGris)
                    )

                    etiqueta.setTextColor(
                        obtenerColor(holder, colorTextoGris)
                    )

                    etiqueta.setTypeface(
                        null,
                        Typeface.NORMAL
                    )
                }
            }
        }

        configurarLineas(
            holder = holder,
            estadoActual = estadoActual,
            entregado =
                pedido.estadoNormalizado() == "Entregado"
        )

        /*
         * El estado Entregado queda fijo con check.
         * Los estados activos muestran una pulsación.
         */
        if (
            pedido.estadoNormalizado() != "Entregado"
        ) {

            iniciarPulso(
                holder = holder,
                posicion = estadoActual
            )
        }
    }

    // =========================================================
    // LÍNEAS DE PROGRESO
    // =========================================================

    private fun configurarLineas(
        holder: PedidoViewHolder,
        estadoActual: Int,
        entregado: Boolean
    ) {

        val animaciones =
            mutableListOf<Animator>()

        holder.lineas.forEachIndexed {
                indice,
                linea ->

            val completada =
                estadoActual > indice

            if (completada) {

                linea.setBackgroundColor(
                    if (entregado) {
                        obtenerColor(holder, colorVerde)
                    } else {
                        obtenerColor(holder, colorNaranja)
                    }
                )

                linea.scaleX =
                    0f

                linea.pivotX =
                    0f

                val animacion =
                    ObjectAnimator.ofFloat(
                        linea,
                        View.SCALE_X,
                        0f,
                        1f
                    ).apply {

                        duration =
                            260L

                        startDelay =
                            indice * 100L

                        interpolator =
                            AccelerateDecelerateInterpolator()
                    }

                animaciones.add(
                    animacion
                )

            } else {

                linea.animate()
                    .cancel()

                linea.scaleX =
                    1f

                linea.setBackgroundColor(
                    obtenerColor(holder, colorGris)
                )
            }
        }

        if (animaciones.isNotEmpty()) {

            holder.animacionLineas =
                AnimatorSet().apply {

                    playTogether(
                        animaciones
                    )

                    start()
                }
        }
    }

    // =========================================================
    // ANIMACIÓN DE PULSO
    // =========================================================

    private fun iniciarPulso(
        holder: PedidoViewHolder,
        posicion: Int
    ) {

        if (
            posicion !in holder.indicadores.indices
        ) {
            return
        }

        val indicador =
            holder.indicadores[posicion]

        val escalaX =
            ObjectAnimator.ofFloat(
                indicador,
                View.SCALE_X,
                1f,
                1.14f
            )

        val escalaY =
            ObjectAnimator.ofFloat(
                indicador,
                View.SCALE_Y,
                1f,
                1.14f
            )

        holder.animacionPulso =
            AnimatorSet().apply {

                playTogether(
                    escalaX,
                    escalaY
                )

                duration =
                    650L

                interpolator =
                    AccelerateDecelerateInterpolator()

                addListener(
                    object : AnimatorListenerAdapter() {

                        override fun onAnimationEnd(
                            animation: Animator
                        ) {

                            if (
                                holder.bindingAdapterPosition !=
                                RecyclerView.NO_POSITION
                            ) {

                                indicador.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(650L)
                                    .withEndAction {

                                        if (
                                            holder.bindingAdapterPosition !=
                                            RecyclerView.NO_POSITION
                                        ) {
                                            start()
                                        }
                                    }
                                    .start()
                            }
                        }
                    }
                )

                start()
            }
    }

    // =========================================================
    // PEDIDO CANCELADO
    // =========================================================

    private fun configurarPedidoCancelado(
        holder: PedidoViewHolder
    ) {

        detenerAnimaciones(holder)

        holder.layoutSeguimiento.visibility =
            View.VISIBLE

        holder.layoutEtiquetasSeguimiento.visibility =
            View.VISIBLE

        holder.indicadores.forEach { indicador ->

            indicador.setCardBackgroundColor(
                obtenerColor(holder, colorGris)
            )

            indicador.scaleX =
                1f

            indicador.scaleY =
                1f
        }

        holder.textosIndicadores.forEachIndexed {
                indice,
                texto ->

            texto.text =
                holder.itemView.context.getString(
                    R.string.pedido_paso_numero,
                    indice + 1
                )

            texto.setTextColor(
                obtenerColor(holder, colorTextoGris)
            )
        }

        holder.lineas.forEach { linea ->

            linea.scaleX =
                1f

            linea.setBackgroundColor(
                obtenerColor(holder, colorGris)
            )
        }

        holder.etiquetas.forEach { etiqueta ->

            etiqueta.setTextColor(
                obtenerColor(holder, colorTextoGris)
            )

            etiqueta.setTypeface(
                null,
                Typeface.NORMAL
            )
        }
    }

    // =========================================================
    // FECHA
    // =========================================================

    private fun formatearFecha(
        fecha: Long,
        context: Context
    ): String {

        if (fecha <= 0L) {
            return context.getString(R.string.pedido_fecha_no_disponible)
        }

        val formato =
            SimpleDateFormat(
                context.getString(R.string.pedido_formato_fecha),
                Locale.getDefault()
            )

        return formato.format(
            Date(fecha)
        )
    }

    // =========================================================
    // ACTUALIZAR LISTA
    // =========================================================

    fun actualizarPedidos(
        nuevosPedidos: List<PedidoUsuarioItem>
    ) {

        val pedidosAnteriores = pedidos.toList()
        val resultado = DiffUtil.calculateDiff(
            object : DiffUtil.Callback() {
                override fun getOldListSize() = pedidosAnteriores.size

                override fun getNewListSize() = nuevosPedidos.size

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return pedidosAnteriores[oldItemPosition].numeroPedido ==
                            nuevosPedidos[newItemPosition].numeroPedido
                }

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return pedidosAnteriores[oldItemPosition] == nuevosPedidos[newItemPosition]
                }
            }
        )

        pedidos.clear()
        pedidos.addAll(nuevosPedidos)
        resultado.dispatchUpdatesTo(this)
    }

    // =========================================================
    // DETENER ANIMACION
    // =========================================================

    private fun detenerAnimaciones(
        holder: PedidoViewHolder
    ) {

        holder.animacionPulso
            ?.removeAllListeners()

        holder.animacionPulso
            ?.cancel()

        holder.animacionPulso =
            null

        holder.animacionLineas
            ?.cancel()

        holder.animacionLineas =
            null

        holder.indicadores.forEach { indicador ->

            indicador.animate()
                .cancel()

            indicador.scaleX =
                1f

            indicador.scaleY =
                1f
        }
    }

    // =========================================================
    // RECICLAR VIEW HOLDER
    // =========================================================

    override fun onViewRecycled(
        holder: PedidoViewHolder
    ) {

        detenerAnimaciones(holder)

        super.onViewRecycled(holder)
    }

    private fun obtenerColor(
        holder: PedidoViewHolder,
        @ColorRes color: Int
    ): Int = ContextCompat.getColor(holder.itemView.context, color)

}