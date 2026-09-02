package com.example.appzetar.Usuario

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.appzetar.Menu.TaskEntradas
import com.example.appzetar.R

class EntradasUsuarioAdapter(
    private val entradas: MutableList<TaskEntradas>,
    private val onAgregarClick: (TaskEntradas) -> Unit
) : RecyclerView.Adapter<EntradasUsuarioViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EntradasUsuarioViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_task_entradas,
            parent,
            false
        )

        return EntradasUsuarioViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: EntradasUsuarioViewHolder,
        position: Int
    ) {
        holder.render(
            entradas[position],
            onAgregarClick
        )
    }

    override fun getItemCount(): Int = entradas.size
}