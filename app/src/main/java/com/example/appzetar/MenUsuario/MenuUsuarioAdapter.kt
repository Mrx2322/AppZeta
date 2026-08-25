package com.example.appzetar.Menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appzetar.R

class MenuUsuarioAdapter(
    private val menuList: MutableList<TaskMenu>
) : RecyclerView.Adapter<MenuUsuarioAdapter.MenuUsuarioViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MenuUsuarioViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu_usuario, parent, false)

        return MenuUsuarioViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MenuUsuarioViewHolder,
        position: Int
    ) {
        holder.render(menuList[position])
    }

    override fun getItemCount(): Int = menuList.size

    class MenuUsuarioViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        private val tvNombre: TextView =
            view.findViewById(R.id.tvNombreMenuUsuario)

        fun render(taskMenu: TaskMenu) {
            tvNombre.text = taskMenu.name
        }
    }
}