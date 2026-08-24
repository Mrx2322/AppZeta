package com.example.appzetar.Menu

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appzetar.R

class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val tvNombrePlato: TextView = view.findViewById(R.id.tvMenuPlato)
    val btnEditar: ImageButton = view.findViewById(R.id.btnEditar)
    val btnEliminar: ImageButton = view.findViewById(R.id.btnEliminar)

    fun render(
        taskMenu: TaskMenu,
        onEditClick: (Int) -> Unit,
        onDeleteClick: (Int) -> Unit
    ) {
        tvNombrePlato.text = taskMenu.name

        btnEditar.setOnClickListener {
            onEditClick(adapterPosition)
        }

        btnEliminar.setOnClickListener {
            onDeleteClick(adapterPosition)
        }
    }
}