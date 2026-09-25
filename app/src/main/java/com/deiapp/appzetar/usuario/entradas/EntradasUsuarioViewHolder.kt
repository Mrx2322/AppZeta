package com.deiapp.appzetar.usuario.entradas

import android.content.res.ColorStateList
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.deiapp.appzeta.R
import com.deiapp.appzetar.usuario.modelos.TaskEntradas
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class EntradasUsuarioViewHolder(
    view: View
) : RecyclerView.ViewHolder(view) {

    private val cardEntrada: MaterialCardView =
        view.findViewById(R.id.cardEntrada)

    private val tvEntradasName: TextView =
        view.findViewById(R.id.tvNombrePlato)

    private val tvStockEntrada: TextView =
        view.findViewById(R.id.tvStockEntrada)

    private val btnAgregarEntrada: MaterialButton =
        view.findViewById(R.id.btnAgregarEntrada)

    fun render(
        taskEntradas: TaskEntradas,
        cantidadSeleccionada: Int,
        maximoSeleccionable: Int,
        onAlternarClick: () -> Unit
    ) {
        tvEntradasName.text = taskEntradas.nombre

        val seleccionada = cantidadSeleccionada > 0

        mostrarEstadoSeleccion(seleccionada)

        val agotada =
            !taskEntradas.disponible ||
                    taskEntradas.stock <= 0

        mostrarEstadoStock(
            taskEntradas = taskEntradas,
            agotada = agotada
        )

        val puedeInteractuar =
            seleccionada ||
                    (!agotada && maximoSeleccionable > 0)

        btnAgregarEntrada.isEnabled = puedeInteractuar

        btnAgregarEntrada.alpha =
            if (puedeInteractuar) 1f else 0.45f

        btnAgregarEntrada.setOnClickListener {
            onAlternarClick()
        }

        itemView.setOnClickListener(null)
    }

    private fun mostrarEstadoStock(
        taskEntradas: TaskEntradas,
        agotada: Boolean
    ) {
        when {

            agotada -> {
                tvStockEntrada.text =
                    itemView.context.getString(
                        R.string.entrada_estado_agotado
                    )

                tvStockEntrada.setTextColor(
                    color(R.color.entrada_stock_agotado)
                )
            }

            taskEntradas.stock == 1 -> {
                tvStockEntrada.text =
                    itemView.context.getString(
                        R.string.entrada_ultima_unidad
                    )

                tvStockEntrada.setTextColor(
                    color(R.color.entrada_stock_bajo)
                )
            }

            taskEntradas.stock in 2..3 -> {
                tvStockEntrada.text =
                    itemView.context.getString(
                        R.string.entrada_ultimas_unidades,
                        taskEntradas.stock
                    )

                tvStockEntrada.setTextColor(
                    color(R.color.entrada_stock_bajo)
                )
            }

            else -> {
                tvStockEntrada.text =
                    itemView.context.getString(
                        R.string.entrada_estado_disponible
                    )

                tvStockEntrada.setTextColor(
                    color(R.color.entrada_stock_disponible)
                )
            }
        }
    }

    private fun mostrarEstadoSeleccion(
        seleccionada: Boolean
    ) {
        if (seleccionada) {

            cardEntrada.setCardBackgroundColor(
                color(R.color.entrada_card_seleccionada)
            )

            cardEntrada.strokeColor =
                color(R.color.entrada_borde_seleccionado)

            cardEntrada.strokeWidth =
                dpToPx(2)

            btnAgregarEntrada.text =
                itemView.context.getString(
                    R.string.entrada_quitar
                )

            btnAgregarEntrada.setTextColor(
                color(R.color.entrada_boton_texto_seleccionado)
            )

            btnAgregarEntrada.backgroundTintList =
                ColorStateList.valueOf(
                    color(R.color.entrada_boton_fondo_seleccionado)
                )

        } else {

            cardEntrada.setCardBackgroundColor(
                color(R.color.entrada_card_normal)
            )

            cardEntrada.strokeColor =
                color(R.color.entrada_borde_normal)

            cardEntrada.strokeWidth =
                dpToPx(1)

            btnAgregarEntrada.text =
                itemView.context.getString(
                    R.string.entrada_agregar
                )

            btnAgregarEntrada.setTextColor(
                color(R.color.entrada_boton_texto_normal)
            )

            btnAgregarEntrada.backgroundTintList =
                ColorStateList.valueOf(
                    color(R.color.entrada_boton_fondo_normal)
                )
        }
    }

    private fun color(
        colorRes: Int
    ): Int {
        return ContextCompat.getColor(
            itemView.context,
            colorRes
        )
    }

    private fun dpToPx(
        dp: Int
    ): Int {
        return (
                dp *
                        itemView.resources
                            .displayMetrics
                            .density
                ).toInt()
    }
}