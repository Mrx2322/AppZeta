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

            // =================================================
            // IMAGEN SEGÚN CATEGORÍA
            // =================================================

            val imagen = when (extra.categoriaId) {

                // Gaseosas
                1 ->
                    R.drawable.ic_gaseosa

                // Tortas
                2 ->
                    R.drawable.ic_torta

                // Cualquier categoría no válida
                else ->
                    R.drawable.ic_plato
            }


            imgExtra.setImageResource(
                imagen
            )


            // =================================================
            // NOMBRE
            // =================================================

            tvNombreExtra.text =
                extra.nombre


            // =================================================
            // PRECIO
            // =================================================

            tvPrecioExtra.text =
                String.format(
                    Locale.US,
                    "S/ %.2f",
                    extra.precio
                )


            // =================================================
            // BOTÓN AGREGAR
            // =================================================

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
            extras[position]
        )
    }


    // =========================================================
    // CANTIDAD
    // =========================================================

    override fun getItemCount(): Int =
        extras.size
}