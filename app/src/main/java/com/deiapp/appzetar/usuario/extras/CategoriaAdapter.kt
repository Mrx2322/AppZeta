package com.deiapp.appzetar.usuario.extras

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.deiapp.appzeta.R

class CategoriaAdapter(
    private val categorias: List<CategoriaItem>,
    private val onCategoriaClick: (CategoriaItem) -> Unit
) : RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder>() {

    private var categoriaSeleccionada = CATEGORIA_TODOS

    inner class CategoriaViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val imgCategoria: ImageView =
            itemView.findViewById(R.id.imgCategoria)

        private val tvCategoria: TextView =
            itemView.findViewById(R.id.tvCategoria)

        fun bind(
            categoria: CategoriaItem
        ) {
            imgCategoria.setImageResource(
                categoria.icono
            )

            tvCategoria.text =
                categoria.nombre

            mostrarEstadoSeleccion(
                categoria.id == categoriaSeleccionada
            )

            itemView.setOnClickListener {
                seleccionarCategoria(categoria)
            }
        }

        private fun mostrarEstadoSeleccion(
            seleccionada: Boolean
        ) {
            if (seleccionada) {
                tvCategoria.setTextColor(
                    obtenerColor(
                        R.color.categoria_texto_seleccionado
                    )
                )

                tvCategoria.alpha =
                    ALPHA_SELECCIONADA

            } else {
                tvCategoria.setTextColor(
                    obtenerColor(
                        R.color.categoria_texto_normal
                    )
                )

                tvCategoria.alpha =
                    ALPHA_NORMAL
            }
        }

        private fun obtenerColor(
            colorRes: Int
        ): Int {
            return ContextCompat.getColor(
                itemView.context,
                colorRes
            )
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoriaViewHolder {

        val view =
            LayoutInflater
                .from(parent.context)
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

    private fun seleccionarCategoria(
        categoria: CategoriaItem
    ) {
        if (categoria.id == categoriaSeleccionada) {
            onCategoriaClick(categoria)
            return
        }

        val categoriaAnterior =
            categoriaSeleccionada

        categoriaSeleccionada =
            categoria.id

        notificarCambioCategoria(
            categoriaAnterior
        )

        notificarCambioCategoria(
            categoriaSeleccionada
        )

        onCategoriaClick(categoria)
    }

    private fun notificarCambioCategoria(
        categoriaId: Int
    ) {
        val posicion =
            categorias.indexOfFirst { categoria ->
                categoria.id == categoriaId
            }

        if (posicion != RecyclerView.NO_POSITION) {
            notifyItemChanged(posicion)
        }
    }

    companion object {

        private const val CATEGORIA_TODOS = 0

        private const val ALPHA_SELECCIONADA =
            1f

        private const val ALPHA_NORMAL =
            0.75f
    }
}