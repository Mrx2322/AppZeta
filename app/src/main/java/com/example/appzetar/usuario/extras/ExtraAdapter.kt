package com.example.appzetar.usuario.extras

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.appzetar.R

class ExtraAdapter(
    extrasIniciales: List<ExtraItem>,
    private val onAgregarClick: (ExtraItem) -> Unit
) : RecyclerView.Adapter<ExtraAdapter.ExtraViewHolder>() {

    private var todosLosExtras: List<ExtraItem> =
        extrasIniciales.toList()

    private var extrasVisibles: List<ExtraItem> =
        todosLosExtras.toList()

    private var categoriaActual =
        CATEGORIA_TODOS

    inner class ExtraViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val imgExtra: ImageView =
            itemView.findViewById(R.id.imgExtra)

        private val tvNombreExtra: TextView =
            itemView.findViewById(R.id.tvNombreExtra)

        private val tvPrecioExtra: TextView =
            itemView.findViewById(R.id.tvPrecioExtra)

        private val btnAgregarExtra: View =
            itemView.findViewById(R.id.btnAgregarExtra)

        fun bind(
            extra: ExtraItem
        ) {
            imgExtra.setImageResource(
                extra.icono
            )

            tvNombreExtra.text =
                extra.nombre

            tvPrecioExtra.text =
                itemView.context.getString(
                    R.string.precio_soles_formato,
                    extra.precio
                )

            btnAgregarExtra.setOnClickListener {
                onAgregarClick(extra)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ExtraViewHolder {

        val view =
            LayoutInflater
                .from(parent.context)
                .inflate(
                    R.layout.item_extra,
                    parent,
                    false
                )

        return ExtraViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ExtraViewHolder,
        position: Int
    ) {
        holder.bind(
            extrasVisibles[position]
        )
    }

    override fun getItemCount(): Int =
        extrasVisibles.size

    fun actualizarExtras(
        nuevosExtras: List<ExtraItem>,
        categoriaId: Int = categoriaActual
    ) {
        todosLosExtras =
            nuevosExtras.toList()

        filtrarPorCategoria(
            categoriaId
        )
    }

    fun filtrarPorCategoria(
        categoriaId: Int
    ) {
        categoriaActual =
            categoriaId

        val nuevaLista =
            if (categoriaId == CATEGORIA_TODOS) {
                todosLosExtras.toList()
            } else {
                todosLosExtras.filter { extra ->
                    extra.categoriaId == categoriaId
                }
            }

        actualizarListaVisible(
            nuevaLista
        )
    }

    private fun actualizarListaVisible(
        nuevaLista: List<ExtraItem>
    ) {
        val listaAnterior =
            extrasVisibles

        val resultadoDiff =
            DiffUtil.calculateDiff(
                ExtraDiffCallback(
                    listaAnterior = listaAnterior,
                    listaNueva = nuevaLista
                )
            )

        extrasVisibles =
            nuevaLista

        resultadoDiff.dispatchUpdatesTo(this)
    }

    private class ExtraDiffCallback(
        private val listaAnterior: List<ExtraItem>,
        private val listaNueva: List<ExtraItem>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int =
            listaAnterior.size

        override fun getNewListSize(): Int =
            listaNueva.size

        override fun areItemsTheSame(
            oldItemPosition: Int,
            newItemPosition: Int
        ): Boolean {

            return listaAnterior[oldItemPosition].id ==
                    listaNueva[newItemPosition].id
        }

        override fun areContentsTheSame(
            oldItemPosition: Int,
            newItemPosition: Int
        ): Boolean {

            return listaAnterior[oldItemPosition] ==
                    listaNueva[newItemPosition]
        }
    }

    companion object {

        private const val CATEGORIA_TODOS =
            0
    }
}