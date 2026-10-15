package com.deiapp.appzetar.usuario

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deiapp.appzeta.R
import com.deiapp.appzetar.usuario.modelos.TaskMenu

class MenuUsuarioAdapter(
    private val listaMenu: MutableList<TaskMenu>,
    private val centrarTarjetas: Boolean = false,
    private val onAgregarClick: (TaskMenu) -> Unit
) : RecyclerView.Adapter<MenuUsuarioViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MenuUsuarioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_task_menu_usuario,
            parent,
            false
        )

        val viewportWidth = if (parent.width > 0) {
            parent.width
        } else {
            parent.resources.displayMetrics.widthPixels
        }
        val cardWidth = (viewportWidth * FRACTION_ANCHO_TARJETA).toInt()
        val layoutParams = view.layoutParams as RecyclerView.LayoutParams
        layoutParams.width = cardWidth
        layoutParams.height = RecyclerView.LayoutParams.WRAP_CONTENT

        if (centrarTarjetas) {
            val margenLateral = ((viewportWidth - cardWidth) / 2).coerceAtLeast(0)
            layoutParams.marginStart = margenLateral
            layoutParams.marginEnd = margenLateral
        }

        view.layoutParams = layoutParams

        return MenuUsuarioViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MenuUsuarioViewHolder,
        position: Int
    ) {
        holder.render(
            taskMenu = listaMenu[position],
            onAgregarClick = onAgregarClick
        )
    }

    override fun getItemCount(): Int = listaMenu.size

    private companion object {
        const val FRACTION_ANCHO_TARJETA = 0.80f
    }
}
