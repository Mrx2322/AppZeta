package com.example.appzetar.Menu


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.customview.R
import androidx.recyclerview.widget.RecyclerView

class EntradasAdapter(private val entradas: List<TaskEntradas>) :
    RecyclerView.Adapter<EntradasViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EntradasViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task_entradas, parent, false)
        return EntradasViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: EntradasViewHolder,
        position: Int
    ) {
        TODO("Not yet implemented")
    }

    override fun getItemCount() = entradas.size
}