package com.example.appzetar.Usuario

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appzetar.Menu.TaskMenu
import com.example.appzetar.R

class MenuUsuarioViewHolder(
    view: View
) : RecyclerView.ViewHolder(view) {

    private val tvMenuPlato: TextView =
        view.findViewById(R.id.tvMenuPlato)

    private val btnEditar: ImageButton =
        view.findViewById(R.id.btnEditar)

    private val btnEliminar: ImageButton =
        view.findViewById(R.id.btnEliminar)

    fun render(taskMenu: TaskMenu) {

        tvMenuPlato.text = taskMenu.name

        // El usuario NO tiene permisos de administración
        btnEditar.visibility = View.GONE
        btnEliminar.visibility = View.GONE

        itemView.setOnClickListener(null)
    }
}