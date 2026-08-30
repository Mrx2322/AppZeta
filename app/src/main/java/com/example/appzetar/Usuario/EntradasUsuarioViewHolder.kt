package com.example.appzetar.Usuario

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.appzetar.Menu.TaskEntradas
import com.example.appzetar.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class EntradasUsuarioViewHolder(
    view: View
) : RecyclerView.ViewHolder(view) {

    private val tvEntradasName: TextView =
        view.findViewById(R.id.tvNombrePlato)

    private val divider: View =
        view.findViewById(R.id.divider)

    private val btnAgregarEntrada: FloatingActionButton =
        view.findViewById(R.id.btnAgregarEntrada)


    fun render(
        taskEntradas: TaskEntradas,
        onAgregarClick: (TaskEntradas) -> Unit
    ) {

        // -----------------------------------------------------
        // NOMBRE
        // -----------------------------------------------------

        tvEntradasName.text =
            taskEntradas.nombre


        // -----------------------------------------------------
        // COLOR DE LA LÍNEA
        // -----------------------------------------------------

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


        // -----------------------------------------------------
        // BOTÓN AGREGAR
        // -----------------------------------------------------

        btnAgregarEntrada.setOnClickListener {

            onAgregarClick(
                taskEntradas
            )
        }


        // -----------------------------------------------------
        // EVITAR CLICK EN LA TARJETA
        // -----------------------------------------------------

        itemView.setOnClickListener(null)
    }
}