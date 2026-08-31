package com.example.appzetar.Usuario

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appzetar.Menu.TaskMenu
import com.example.appzetar.R
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

    val btnAgregar = itemView.findViewById<MaterialButton>(R.id.btnAgregar)

    @SuppressLint("SetTextI18n")
    fun render(
        taskMenu: TaskMenu,
        onAgregarClick: (TaskMenu) -> Unit
    ) {

        tvMenuPlato.text = taskMenu.name

        // Imagen provisional
        imgPlato.setImageResource(
            R.drawable.fondo_menu
        )

        tvPrecio.text = "S/ %.2f".format(taskMenu.precio)

        // BOTÓN AGREGAR
        btnAgregar.setOnClickListener {
            onAgregarClick(taskMenu)
        }

        // El usuario no tiene funciones administrativas
        itemView.setOnClickListener(null)
    }
}

