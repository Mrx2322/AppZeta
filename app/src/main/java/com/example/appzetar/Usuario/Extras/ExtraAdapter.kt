package com.example.appzetar.Usuario

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appzetar.R
import java.util.Locale

class ExtraAdapter(
    private val extras: List<ExtraItem>,
    private val onAgregarClick: (ExtraItem) -> Unit
) : RecyclerView.Adapter<ExtraAdapter.ExtraViewHolder>() {

    // Lista que realmente se muestra
    private var extrasVisibles =
        extras.toList()


// =========================================================
// VIEW HOLDER
// =========================================================

    inner class ExtraViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val imgExtra =
            itemView.findViewById<ImageView>(
                R.id.imgExtra
            )

        private val tvNombreExtra =
            itemView.findViewById<TextView>(
                R.id.tvNombreExtra
            )

        private val tvPrecioExtra =
            itemView.findViewById<TextView>(
                R.id.tvPrecioExtra
            )

        private val btnAgregarExtra =
            itemView.findViewById<View>(
                R.id.btnAgregarExtra
            )


        // =====================================================
        // BIND
        // =====================================================

        fun bind(extra: ExtraItem) {

            val imagen =
                when (extra.categoriaId) {

                    1 ->
                        R.drawable.ic_gaseosa

                    2 ->
                        R.drawable.ic_torta

                    else ->
                        R.drawable.ic_plato
                }

            imgExtra.setImageResource(
                imagen
            )

            tvNombreExtra.text =
                extra.nombre

            tvPrecioExtra.text =
                String.format(
                    Locale.US,
                    "S/ %.2f",
                    extra.precio
                )

            btnAgregarExtra.setOnClickListener {

                onAgregarClick(
                    extra
                )
            }
        }
    }


// =========================================================
// CREAR VIEW HOLDER
// =========================================================

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ExtraViewHolder {

        val view =
            LayoutInflater.from(
                parent.context
            ).inflate(
                R.layout.item_extra,
                parent,
                false
            )

        return ExtraViewHolder(
            view
        )
    }


// =========================================================
// VINCULAR DATOS
// =========================================================

    override fun onBindViewHolder(
        holder: ExtraViewHolder,
        position: Int
    ) {

        holder.bind(
            extrasVisibles[position]
        )
    }


// =========================================================
// CANTIDAD
// =========================================================

    override fun getItemCount(): Int =
        extrasVisibles.size


// =========================================================
// FILTRAR POR CATEGORÍA
// =========================================================

    fun filtrarPorCategoria(
        categoriaId: Int
    ) {

        extrasVisibles =
            if (categoriaId == 0) {

                // 0 = TODOS
                extras.toList()

            } else {

                extras.filter {
                    it.categoriaId == categoriaId
                }
            }

        notifyDataSetChanged()
    }

}