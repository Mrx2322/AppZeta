
package com.deiapp.appzetar.usuario.cenas

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.deiapp.appzeta.R
import com.deiapp.appzetar.usuario.modelos.TaskCenas
import com.google.android.material.button.MaterialButton
import java.util.Locale

class CenasUsuarioViewHolder(
    view: View
) : RecyclerView.ViewHolder(view) {

    private val tvNombreCena: TextView =
        view.findViewById(R.id.tvMenuPlato)

    private val imgCena: ImageView =
        view.findViewById(R.id.imgPlato)

    private val tvPrecio: TextView =
        view.findViewById(R.id.tvPrecio)

    private val tvStockCena: TextView =
        view.findViewById(R.id.tvStockPlato)

    private val btnAgregar: MaterialButton =
        view.findViewById(R.id.btnAgregar)

    fun render(
        cena: TaskCenas,
        onAgregarClick: (TaskCenas) -> Unit
    ) {
        tvNombreCena.text = cena.name

        val nombreCena = cena.name
            .lowercase(Locale.ROOT)
            .replace("á", "a")
            .replace("é", "e")
            .replace("í", "i")
            .replace("ó", "o")
            .replace("ú", "u")

        val imagenCena = when {
            "chaufa" in nombreCena -> {
                R.drawable.cena_chaufa_pollo
            }

            "broaster" in nombreCena ||
                    "broasther" in nombreCena -> {
                R.drawable.cena_pollo_broasther
            }

            "caldo" in nombreCena &&
                    "gallina" in nombreCena -> {
                R.drawable.cena_caldo_gallina
            }

            else -> {
                R.drawable.fondo_menu
            }
        }

        imgCena.setImageResource(imagenCena)

        tvPrecio.text = String.format(
            Locale.forLanguageTag("es-PE"),
            itemView.context.getString(
                R.string.cena_precio_formato
            ),
            cena.precio
        )

        mostrarStock(
            cena = cena,
            onAgregarClick = onAgregarClick
        )

        itemView.setOnClickListener(null)
    }

    private fun mostrarStock(
        cena: TaskCenas,
        onAgregarClick: (TaskCenas) -> Unit
    ) {
        when {
            cena.stock <= 0 -> {
                tvStockCena.text =
                    itemView.context.getString(
                        R.string.cena_estado_agotado
                    )

                tvStockCena.setTextColor(
                    obtenerColor(R.color.menu_stock_agotado)
                )

                configurarBoton(habilitado = false)
            }

            cena.stock == 1 -> {
                tvStockCena.text =
                    itemView.context.getString(
                        R.string.cena_ultima_unidad
                    )

                tvStockCena.setTextColor(
                    obtenerColor(R.color.menu_stock_bajo)
                )

                configurarBoton(
                    habilitado = true,
                    cena = cena,
                    onAgregarClick = onAgregarClick
                )
            }

            cena.stock in 2..3 -> {
                tvStockCena.text =
                    itemView.context.getString(
                        R.string.cena_ultimas_unidades,
                        cena.stock
                    )

                tvStockCena.setTextColor(
                    obtenerColor(R.color.menu_stock_bajo)
                )

                configurarBoton(
                    habilitado = true,
                    cena = cena,
                    onAgregarClick = onAgregarClick
                )
            }

            else -> {
                tvStockCena.text =
                    itemView.context.getString(
                        R.string.cena_estado_disponible
                    )

                tvStockCena.setTextColor(
                    obtenerColor(R.color.menu_stock_disponible)
                )

                configurarBoton(
                    habilitado = true,
                    cena = cena,
                    onAgregarClick = onAgregarClick
                )
            }
        }
    }

    private fun configurarBoton(
        habilitado: Boolean,
        cena: TaskCenas? = null,
        onAgregarClick: ((TaskCenas) -> Unit)? = null
    ) {
        btnAgregar.isEnabled = habilitado

        btnAgregar.alpha = if (habilitado) {
            ALPHA_HABILITADO
        } else {
            ALPHA_DESHABILITADO
        }

        if (
            habilitado &&
            cena != null &&
            onAgregarClick != null
        ) {
            btnAgregar.setOnClickListener {
                onAgregarClick(cena)
            }
        } else {
            btnAgregar.setOnClickListener(null)
        }
    }

    private fun obtenerColor(colorRes: Int): Int {
        return ContextCompat.getColor(
            itemView.context,
            colorRes
        )
    }

    companion object {
        private const val ALPHA_HABILITADO = 1f
        private const val ALPHA_DESHABILITADO = 0.45f
    }
}
