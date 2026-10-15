package com.deiapp.appzetar.usuario.cenas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deiapp.appzeta.R
import com.deiapp.appzetar.usuario.modelos.TaskCenas

class CenasUsuarioAdapter(
    private val listaCenas: MutableList<TaskCenas>,
    private val onAgregarClick: (TaskCenas) -> Unit
) : RecyclerView.Adapter<CenasUsuarioViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CenasUsuarioViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(
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

        view.layoutParams = RecyclerView.LayoutParams(
            cardWidth,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )

        return CenasUsuarioViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: CenasUsuarioViewHolder,
        position: Int
    ) {
        holder.render(
            cena = listaCenas[position],
            onAgregarClick = onAgregarClick
        )
    }

    override fun getItemCount(): Int = listaCenas.size

    private companion object {
        const val FRACTION_ANCHO_TARJETA = 0.80f
    }
}
