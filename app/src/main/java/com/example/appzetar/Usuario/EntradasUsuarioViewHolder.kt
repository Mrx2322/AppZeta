package com.example.appzetar.Usuario

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appzetar.Menu.TaskEntradas
import com.example.appzetar.R
import com.google.android.material.button.MaterialButton

class EntradasUsuarioViewHolder(
    view: View
) : RecyclerView.ViewHolder(view) {

    private val tvEntradasName: TextView =
        view.findViewById(R.id.tvNombrePlato)

    private val tvStockEntrada: TextView =
        view.findViewById(R.id.tvStockEntrada)

    private val btnAgregarEntrada: MaterialButton =
        view.findViewById(R.id.btnAgregarEntrada)

    fun render(
        taskEntradas: TaskEntradas,
        onAgregarClick: (TaskEntradas) -> Unit
    ) {

        // Nombre
        tvEntradasName.text = taskEntradas.nombre

        // ==============================
        // ESTADO DEL STOCK
        // ==============================

        when {
            !taskEntradas.disponible || taskEntradas.stock <= 0 -> {

                tvStockEntrada.text = "Agotado"
                tvStockEntrada.setTextColor(
                    Color.parseColor("#D32F2F")
                )

                btnAgregarEntrada.isEnabled = false
                btnAgregarEntrada.alpha = 0.45f
                btnAgregarEntrada.setOnClickListener(null)
            }

            taskEntradas.stock <= 3 -> {

                tvStockEntrada.text = when (taskEntradas.stock) {
                    1 -> "¡Última unidad!"
                    else -> "¡Últimas ${taskEntradas.stock} unidades!"
                }

                tvStockEntrada.setTextColor(
                    Color.parseColor("#E65100")
                )

                btnAgregarEntrada.isEnabled = true
                btnAgregarEntrada.alpha = 1f

                btnAgregarEntrada.setOnClickListener {
                    onAgregarClick(taskEntradas)
                }
            }

            else -> {

                tvStockEntrada.text = "Disponible"

                tvStockEntrada.setTextColor(
                    Color.parseColor("#757575")
                )

                btnAgregarEntrada.isEnabled = true
                btnAgregarEntrada.alpha = 1f

                btnAgregarEntrada.setOnClickListener {
                    onAgregarClick(taskEntradas)
                }
            }
        }

        // La tarjeta no realiza ninguna acción
        itemView.setOnClickListener(null)
    }
}