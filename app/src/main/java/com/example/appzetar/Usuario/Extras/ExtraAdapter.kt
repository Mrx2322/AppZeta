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

        fun bind(extra: ExtraItem) {

            // Imagen
            imgExtra.setImageResource(
                extra.icono
            )

            // Nombre
            tvNombreExtra.text =
                extra.nombre

            // Precio
            tvPrecioExtra.text =
                String.format(
                    Locale.US,
                    "S/ %.2f",
                    extra.precio
                )

            // Botón +
            btnAgregarExtra.setOnClickListener {

                onAgregarClick(
                    extra
                )
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ExtraViewHolder {

        val view =
            LayoutInflater.from(parent.context)
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
            extras[position]
        )
    }

    override fun getItemCount(): Int =
        extras.size
}