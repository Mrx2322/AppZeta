package com.example.appzetar.Usuario

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appzetar.Menu.TaskEntradas
import com.example.appzetar.R
import com.google.android.material.button.MaterialButton

class EntradasUsuarioViewHolder(
    view: View
) : RecyclerView.ViewHolder(view) {

    private val tvEntradasName: TextView =
        view.findViewById(R.id.tvNombrePlato)

    private val btnAgregarEntrada: MaterialButton =
        view.findViewById(R.id.btnAgregarEntrada)

    fun render(
        taskEntradas: TaskEntradas,
        onAgregarClick: (TaskEntradas) -> Unit
    ) {

        // Nombre
        tvEntradasName.text = taskEntradas.nombre

        // Botón agregar
        btnAgregarEntrada.setOnClickListener {
            onAgregarClick(taskEntradas)
        }

        // La tarjeta no realiza ninguna acción
        itemView.setOnClickListener(null)
    }
}