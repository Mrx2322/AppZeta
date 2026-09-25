package com.deiapp.appzetar.usuario

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.deiapp.appzeta.R
import com.deiapp.appzetar.usuario.carrito.ReglasPrecioPedido
import com.deiapp.appzetar.usuario.modelos.TaskMenu
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

    fun render(
        taskMenu: TaskMenu,
        onAgregarClick: (TaskMenu) -> Unit
    ) {
        mostrarNombre(taskMenu)
        mostrarImagen()
        mostrarPrecio(taskMenu)
        mostrarStock(
            taskMenu = taskMenu,
            onAgregarClick = onAgregarClick
        )

        itemView.setOnClickListener(null)
    }

    private fun mostrarNombre(
        taskMenu: TaskMenu
    ) {
        tvMenuPlato.text =
            taskMenu.name
    }

    private fun mostrarImagen() {
        imgPlato.setImageResource(
            R.drawable.fondo_menu
        )
    }

    private fun mostrarPrecio(
        taskMenu: TaskMenu
    ) {
        val precioSinEntrada =
            ReglasPrecioPedido.calcularPrecioMenu(
                precioMenuConEntrada = taskMenu.precio,
                cantidadEntradas = 0
            )

        tvPrecio.text =
            itemView.context.getString(
                R.string.menu_precio_sin_entrada,
                precioSinEntrada
            )
    }

    private fun mostrarStock(
        taskMenu: TaskMenu,
        onAgregarClick: (TaskMenu) -> Unit
    ) {
        when {

            taskMenu.stock <= 0 -> {
                mostrarAgotado()
            }

            taskMenu.stock == 1 -> {
                mostrarUltimaUnidad(
                    taskMenu,
                    onAgregarClick
                )
            }

            taskMenu.stock in 2..3 -> {
                mostrarUltimasUnidades(
                    taskMenu,
                    onAgregarClick
                )
            }

            else -> {
                mostrarDisponible(
                    taskMenu,
                    onAgregarClick
                )
            }
        }
    }

    private fun mostrarAgotado() {
        tvStockPlato.text =
            itemView.context.getString(
                R.string.menu_estado_agotado
            )

        tvStockPlato.setTextColor(
            obtenerColor(
                R.color.menu_stock_agotado
            )
        )

        configurarBotonAgregar(
            habilitado = false
        )
    }

    private fun mostrarUltimaUnidad(
        taskMenu: TaskMenu,
        onAgregarClick: (TaskMenu) -> Unit
    ) {
        tvStockPlato.text =
            itemView.context.getString(
                R.string.menu_ultima_unidad
            )

        tvStockPlato.setTextColor(
            obtenerColor(
                R.color.menu_stock_bajo
            )
        )

        configurarBotonAgregar(
            habilitado = true,
            taskMenu = taskMenu,
            onAgregarClick = onAgregarClick
        )
    }

    private fun mostrarUltimasUnidades(
        taskMenu: TaskMenu,
        onAgregarClick: (TaskMenu) -> Unit
    ) {
        tvStockPlato.text =
            itemView.context.getString(
                R.string.menu_ultimas_unidades,
                taskMenu.stock
            )

        tvStockPlato.setTextColor(
            obtenerColor(
                R.color.menu_stock_bajo
            )
        )

        configurarBotonAgregar(
            habilitado = true,
            taskMenu = taskMenu,
            onAgregarClick = onAgregarClick
        )
    }

    private fun mostrarDisponible(
        taskMenu: TaskMenu,
        onAgregarClick: (TaskMenu) -> Unit
    ) {
        tvStockPlato.text =
            itemView.context.getString(
                R.string.menu_estado_disponible
            )

        tvStockPlato.setTextColor(
            obtenerColor(
                R.color.menu_stock_disponible
            )
        )

        configurarBotonAgregar(
            habilitado = true,
            taskMenu = taskMenu,
            onAgregarClick = onAgregarClick
        )
    }

    private fun configurarBotonAgregar(
        habilitado: Boolean,
        taskMenu: TaskMenu? = null,
        onAgregarClick: ((TaskMenu) -> Unit)? = null
    ) {
        btnAgregar.isEnabled =
            habilitado

        btnAgregar.alpha =
            if (habilitado) {
                ALPHA_HABILITADO
            } else {
                ALPHA_DESHABILITADO
            }

        if (
            habilitado &&
            taskMenu != null &&
            onAgregarClick != null
        ) {
            btnAgregar.setOnClickListener {
                onAgregarClick(taskMenu)
            }
        } else {
            btnAgregar.setOnClickListener(null)
        }
    }

    private fun obtenerColor(
        colorRes: Int
    ): Int {
        return ContextCompat.getColor(
            itemView.context,
            colorRes
        )
    }

    companion object {

        private const val ALPHA_HABILITADO =
            1f

        private const val ALPHA_DESHABILITADO =
            0.45f
    }
}