package com.example.appzetar.Menu

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appzetar.R

class EntradasViewHolder(
    view: View
) : RecyclerView.ViewHolder(view) {

    private val tvEntradasName: TextView =
        view.findViewById(R.id.tvNombrePlato)

    fun render(
        taskEntradas: TaskEntradas,
        onItemClick: (Int) -> Unit
    ) {

        // Nombre de la entrada
        tvEntradasName.text = taskEntradas.nombre

        // Click en la tarjeta
        itemView.setOnClickListener {

            val position = bindingAdapterPosition

            if (position != RecyclerView.NO_POSITION) {
                onItemClick(position)
            }
        }
    }
}