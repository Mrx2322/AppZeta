package com.example.appzetar.Usuario

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appzetar.Menu.TaskMenu
import com.example.appzetar.R
import com.google.android.material.button.MaterialButton

class MenuUsuarioViewHolder(
    view: View
) : RecyclerView.ViewHolder(view) {

    private val tvMenuPlato: TextView =
        view.findViewById(R.id.tvMenuPlato)

    private val imgPlato: ImageView =
        view.findViewById(R.id.imgPlato)

    private val tvPrecio: TextView =
        view.findViewById(R.id.tvPrecio)

    private val tvStockPlato: TextView =
        view.findViewById(R.id.tvStockPlato)

    private val btnAgregar: MaterialButton =
        view.findViewById(R.id.btnAgregar)

    @SuppressLint("SetTextI18n")
    fun render(
        taskMenu: TaskMenu,
        onAgregarClick: (TaskMenu) -> Unit
    ) {

        // Nombre
        tvMenuPlato.text = taskMenu.name

        // Imagen provisional
        imgPlato.setImageResource(
            R.drawable.fondo_menu
        )

        // Precio
        tvPrecio.text = "S/ %.2f".format(taskMenu.precio)

        // ==============================
        // ESTADO DEL STOCK
        // ==============================

        when {
            taskMenu.stock <= 0 -> {

                tvStockPlato.text = "Agotado"
                tvStockPlato.setTextColor(
                    Color.parseColor("#D32F2F")
                )

                btnAgregar.isEnabled = false
                btnAgregar.alpha = 0.45f
                btnAgregar.setOnClickListener(null)
            }

            taskMenu.stock <= 3 -> {

                tvStockPlato.text = when (taskMenu.stock) {
                    1 -> "¡Última unidad!"
                    else -> "¡Últimas ${taskMenu.stock} unidades!"
                }

                tvStockPlato.setTextColor(
                    Color.parseColor("#E65100")
                )

                btnAgregar.isEnabled = true
                btnAgregar.alpha = 1f

                btnAgregar.setOnClickListener {
                    onAgregarClick(taskMenu)
                }
            }

            else -> {

                tvStockPlato.text = "Disponible"

                tvStockPlato.setTextColor(
                    Color.parseColor("#757575")
                )

                btnAgregar.isEnabled = true
                btnAgregar.alpha = 1f

                btnAgregar.setOnClickListener {
                    onAgregarClick(taskMenu)
                }
            }
        }

        // El usuario no tiene funciones administrativas
        itemView.setOnClickListener(null)
    }
}