package com.deiapp.appzetar.usuario

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deiapp.appzeta.R
import com.deiapp.appzetar.usuario.modelos.TaskMenu

class MenuUsuarioAdapter(
    private val listaMenu: MutableList<TaskMenu>,
    private val onAgregarClick: (TaskMenu) -> Unit
) : RecyclerView.Adapter<MenuUsuarioViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MenuUsuarioViewHolder {

        val view =
            LayoutInflater
                .from(parent.context)
                .inflate(
                    R.layout.item_task_menu_usuario,
                    parent,
                    false
                )

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

    override fun getItemCount(): Int =
        listaMenu.size
}