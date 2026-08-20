package com.example.appzetar.Menu

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appzetar.R

class EntradasViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val tvEntradasName: TextView = view.findViewById(R.id.tvNombrePlato)

    fun render(taskEntradas: TaskEntradas){
        tvEntradasName.text = "Eejmplo"
    }
}