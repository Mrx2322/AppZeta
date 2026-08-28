package com.example.appzetar.Usuario

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appzetar.R

class CategoriaAdapter(
    private val categorias: List<CategoriaItem>,
    private val onCategoriaClick: (CategoriaItem) -> Unit
) : RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder>() {

    private var categoriaSeleccionada = 0

    inner class CategoriaViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val cardCategoria =
            itemView.findViewById<View>(R.id.cardCategoria)

        private val imgCategoria =
            itemView.findViewById<ImageView>(R.id.imgCategoria)

        private val tvCategoria =
            itemView.findViewById<TextView>(R.id.tvCategoria)

        fun bind(categoria: CategoriaItem) {

            imgCategoria.setImageResource(
                categoria.icono
            )

            tvCategoria.text =
                categoria.nombre

            if (categoria.id == categoriaSeleccionada) {

                cardCategoria.setBackgroundColor(
                    android.graphics.Color.parseColor(
                        "#FFF3E0"
                    )
                )

                tvCategoria.setTextColor(
                    android.graphics.Color.parseColor(
                        "#FF9500"
                    )
                )

            } else {

                cardCategoria.setBackgroundColor(
                    android.graphics.Color.WHITE
                )

                tvCategoria.setTextColor(
                    android.graphics.Color.parseColor(
                        "#333333"
                    )
                )
            }

            itemView.setOnClickListener {

                categoriaSeleccionada =
                    categoria.id

                notifyDataSetChanged()

                onCategoriaClick(categoria)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoriaViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_categoria,
                    parent,
                    false
                )

        return CategoriaViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: CategoriaViewHolder,
        position: Int
    ) {

        holder.bind(
            categorias[position]
        )
    }

    override fun getItemCount(): Int =
        categorias.size
}