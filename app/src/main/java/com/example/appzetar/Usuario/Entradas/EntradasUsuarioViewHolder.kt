package com.example.appzetar.Usuario.Entradas

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appzetar.AdminMenu.EntradasAdmin.TaskEntradas
import com.example.appzetar.R
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
        onSumarClick: () -> Unit,
        onRestarClick: () -> Unit
    ) {

        tvEntradasName.text = taskEntradas.nombre

        mostrarEstadoSeleccion(
            cantidadSeleccionada
        )

        val agotada =
            !taskEntradas.disponible ||
                    taskEntradas.stock <= 0

        // ==============================
        // ESTADO DEL STOCK
        // ==============================

        when {
            agotada -> {

                tvStockEntrada.text = "Agotado"
                tvStockEntrada.setTextColor(
                    Color.parseColor("#D32F2F")
                )

            }

            taskEntradas.stock <= 3 -> {

                tvStockEntrada.text = when (taskEntradas.stock) {
                    1 -> "¡Última unidad!"
                    else -> "¡Últimas ${taskEntradas.stock} unidades!"
                }

                tvStockEntrada.setTextColor(
                    Color.parseColor("#E65100")
                )

            }

            else -> {

                tvStockEntrada.text = "Disponible"

                tvStockEntrada.setTextColor(
                    Color.parseColor("#757575")
                )

            }
        }

        val puedeSumar =
            !agotada &&
                    cantidadSeleccionada < maximoSeleccionable

        btnAgregarEntrada.isEnabled =
            puedeSumar

        btnAgregarEntrada.alpha =
            if (puedeSumar) 1f else 0.45f

        btnAgregarEntrada.setOnClickListener {
            onSumarClick()
        }

        itemView.setOnClickListener(null)
    }

    private fun mostrarEstadoSeleccion(
        cantidadSeleccionada: Int
    ) {

        val seleccionada =
            cantidadSeleccionada > 0

        // El botón siempre se mantiene visible. Cada toque suma una
        // entrada internamente, sin mostrar controles de cantidad.
        btnAgregarEntrada.visibility = View.VISIBLE

        cardEntrada.setCardBackgroundColor(
            Color.parseColor(
                if (seleccionada) {
                    "#FFF3E8"
                } else {
                    "#FFFFFF"
                }
            )
        )

        cardEntrada.strokeColor =
            Color.parseColor(
                if (seleccionada) {
                    "#E87520"
                } else {
                    "#EEEEEE"
                }
            )

        cardEntrada.strokeWidth =
            if (seleccionada) {
                dpToPx(2)
            } else {
                dpToPx(1)
            }
    }

    private fun dpToPx(dp: Int): Int {
        return (
                dp * itemView.resources
                    .displayMetrics.density
                ).toInt()
    }
}