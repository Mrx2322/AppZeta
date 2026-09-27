package com.deiapp.appzetar.usuario.entradas

import android.content.res.ColorStateList
import android.view.View
import android.widget.LinearLayout
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

    private val layoutCantidadEntrada: LinearLayout =
        view.findViewById(R.id.layoutCantidadEntrada)

    private val btnRestarEntrada: MaterialButton =
        view.findViewById(R.id.btnRestarEntrada)

    private val tvCantidadEntrada: TextView =
        view.findViewById(R.id.tvCantidadEntrada)

    private val btnSumarEntrada: MaterialButton =
        view.findViewById(R.id.btnSumarEntrada)


    fun render(
        taskEntradas: TaskEntradas,
        cantidadSeleccionada: Int,
        maximoSeleccionable: Int,
        onSumarClick: () -> Unit,
        onRestarClick: () -> Unit
    ) {

        tvEntradasName.text =
            taskEntradas.nombre


        // =========================================================
        // ESTADO DE SELECCIÓN
        // =========================================================

        mostrarEstadoSeleccion(
            cantidadSeleccionada
        )


        // =========================================================
        // STOCK
        // =========================================================

        val agotada =
            !taskEntradas.disponible ||
                    taskEntradas.stock <= 0

        mostrarEstadoStock(
            taskEntradas = taskEntradas,
            agotada = agotada
        )


        // =========================================================
        // CONTROL PARA SUMAR
        // =========================================================

        val puedeSumar =
            !agotada &&
                    cantidadSeleccionada < maximoSeleccionable

        btnAgregarEntrada.isEnabled =
            puedeSumar

        btnAgregarEntrada.alpha =
            if (puedeSumar) {
                1f
            } else {
                0.45f
            }

        btnSumarEntrada.isEnabled =
            puedeSumar

        btnSumarEntrada.alpha =
            if (puedeSumar) {
                1f
            } else {
                0.45f
            }


        // =========================================================
        // CONTROL PARA RESTAR
        // =========================================================

        val puedeRestar =
            cantidadSeleccionada > 0

        btnRestarEntrada.isEnabled =
            puedeRestar

        btnRestarEntrada.alpha =
            if (puedeRestar) {
                1f
            } else {
                0.45f
            }


        // =========================================================
        // CLICS
        // =========================================================

        btnAgregarEntrada.setOnClickListener {

            if (puedeSumar) {
                onSumarClick()
            }
        }

        btnSumarEntrada.setOnClickListener {

            if (puedeSumar) {
                onSumarClick()
            }
        }

        btnRestarEntrada.setOnClickListener {

            if (puedeRestar) {
                onRestarClick()
            }
        }

        itemView.setOnClickListener(null)
    }


    // =============================================================
    // ESTADO DEL STOCK
    // =============================================================

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
                    color(
                        R.color.entrada_stock_agotado
                    )
                )
            }


            taskEntradas.stock == 1 -> {

                tvStockEntrada.text =
                    itemView.context.getString(
                        R.string.entrada_ultima_unidad
                    )

                tvStockEntrada.setTextColor(
                    color(
                        R.color.entrada_stock_bajo
                    )
                )
            }


            taskEntradas.stock in 2..3 -> {

                tvStockEntrada.text =
                    itemView.context.getString(
                        R.string.entrada_ultimas_unidades,
                        taskEntradas.stock
                    )

                tvStockEntrada.setTextColor(
                    color(
                        R.color.entrada_stock_bajo
                    )
                )
            }


            else -> {

                tvStockEntrada.text =
                    itemView.context.getString(
                        R.string.entrada_estado_disponible
                    )

                tvStockEntrada.setTextColor(
                    color(
                        R.color.entrada_stock_disponible
                    )
                )
            }
        }
    }


    // =============================================================
    // ESTADO VISUAL DE LA SELECCIÓN
    // =============================================================

    private fun mostrarEstadoSeleccion(
        cantidadSeleccionada: Int
    ) {

        val seleccionada =
            cantidadSeleccionada > 0

        if (seleccionada) {

            // Oculta completamente "Agregar"
            btnAgregarEntrada.visibility =
                View.GONE

            // Muestra solamente - 1 +
            layoutCantidadEntrada.visibility =
                View.VISIBLE

            tvCantidadEntrada.text =
                cantidadSeleccionada.toString()

            cardEntrada.setCardBackgroundColor(
                color(R.color.entrada_card_seleccionada)
            )

            cardEntrada.strokeColor =
                color(R.color.entrada_borde_seleccionado)

            cardEntrada.strokeWidth =
                dpToPx(2)

        } else {

            // Muestra solamente "Agregar"
            btnAgregarEntrada.visibility =
                View.VISIBLE

            // Oculta completamente - 1 +
            layoutCantidadEntrada.visibility =
                View.GONE

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


    // =============================================================
    // COLOR
    // =============================================================

    private fun color(
        colorRes: Int
    ): Int {

        return ContextCompat.getColor(
            itemView.context,
            colorRes
        )
    }


    // =============================================================
    // DP → PX
    // =============================================================

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