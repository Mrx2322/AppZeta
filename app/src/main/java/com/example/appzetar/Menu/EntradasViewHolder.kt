package com.example.appzetar.Menu

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appzetar.R

class EntradasViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val tvEntradasName: TextView = view.findViewById(R.id.tvNombrePlato)
    private val divider: View = view.findViewById(R.id.divider)

    fun render(taskEntradas: TaskEntradas){

        when(taskEntradas){
            TaskEntradas.ceviche -> {
                tvEntradasName.text = "Ceviche"
            }
            TaskEntradas.huancaina -> {
                tvEntradasName.text = "Huancaina"
            }
            TaskEntradas.otros -> {
                tvEntradasName.text = "Otros"
            }
        }
    }
}