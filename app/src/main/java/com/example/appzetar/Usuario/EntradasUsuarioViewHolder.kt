package com.example.appzetar.Usuario

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.appzetar.Menu.TaskEntradas
import com.example.appzetar.R

class EntradasUsuarioViewHolder(
    view: View
) : RecyclerView.ViewHolder(view) {

    private val tvEntradasName: TextView =
        view.findViewById(R.id.tvNombrePlato)

    private val divider: View =
        view.findViewById(R.id.divider)

    fun render(taskEntradas: TaskEntradas) {

        tvEntradasName.text = taskEntradas.nombre

        when (taskEntradas) {

            is TaskEntradas.Ceviche -> {
                divider.setBackgroundColor(
                    ContextCompat.getColor(
                        divider.context,
                        R.color.black
                    )
                )
            }

            is TaskEntradas.Huancaina -> {
                divider.setBackgroundColor(
                    ContextCompat.getColor(
                        divider.context,
                        R.color.todo_business_category
                    )
                )
            }

            is TaskEntradas.Otros -> {
                divider.setBackgroundColor(
                    ContextCompat.getColor(
                        divider.context,
                        R.color.todo_background_todo_app
                    )
                )
            }
        }

        itemView.setOnClickListener(null)
    }
}