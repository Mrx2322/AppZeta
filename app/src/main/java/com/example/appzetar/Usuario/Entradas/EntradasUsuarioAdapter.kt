package com.example.appzetar.Usuario.Entradas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.appzetar.AdminMenu.EntradasAdmin.TaskEntradas
import com.example.appzetar.R
import com.example.appzetar.Usuario.Carrito.EntradaPedido
import com.example.appzetar.Usuario.Carrito.PedidoManager

class EntradasUsuarioAdapter(
    private val entradas: MutableList<TaskEntradas>
) : RecyclerView.Adapter<EntradasUsuarioViewHolder>() {

    private val cantidadesSeleccionadas =
        mutableMapOf<Int, Int>()

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

        val entrada = entradas[position]

        val cantidadEnCarrito =
            PedidoManager.cantidadEntradaEnPedido(
                entrada.id
            )

        val maximoSeleccionable =
            (entrada.stock - cantidadEnCarrito)
                .coerceAtLeast(0)

        val cantidadActual =
            cantidadesSeleccionadas[entrada.id]
                ?.coerceAtMost(maximoSeleccionable)
                ?: 0

        if (cantidadActual > 0) {
            cantidadesSeleccionadas[entrada.id] =
                cantidadActual
        } else {
            cantidadesSeleccionadas.remove(entrada.id)
        }

        holder.render(
            taskEntradas = entrada,
            cantidadSeleccionada = cantidadActual,
            maximoSeleccionable = maximoSeleccionable,

            onSumarClick = {
                sumarEntrada(entrada)
            },

            onRestarClick = {
                restarEntrada(entrada)
            }
        )
    }

    override fun getItemCount(): Int = entradas.size

    fun obtenerEntradasSeleccionadas(): List<EntradaPedido> {

        return entradas.mapNotNull { entrada ->

            val cantidad =
                cantidadesSeleccionadas[entrada.id]
                    ?: 0

            if (cantidad <= 0) {
                null
            } else {
                EntradaPedido(
                    id = entrada.id,
                    nombre = entrada.nombre,
                    cantidad = cantidad
                )
            }
        }
    }

    fun limpiarSeleccion() {

        if (cantidadesSeleccionadas.isEmpty()) {
            return
        }

        cantidadesSeleccionadas.clear()
        notifyDataSetChanged()
    }

    private fun sumarEntrada(
        entrada: TaskEntradas
    ) {

        val cantidadEnCarrito =
            PedidoManager.cantidadEntradaEnPedido(
                entrada.id
            )

        val maximoSeleccionable =
            (entrada.stock - cantidadEnCarrito)
                .coerceAtLeast(0)

        val cantidadActual =
            cantidadesSeleccionadas[entrada.id]
                ?: 0

        if (
            !entrada.disponible ||
            cantidadActual >= maximoSeleccionable
        ) {
            return
        }

        cantidadesSeleccionadas[entrada.id] =
            cantidadActual + 1

        notificarEntradaCambiada(
            entrada.id
        )
    }

    private fun restarEntrada(
        entrada: TaskEntradas
    ) {

        val cantidadActual =
            cantidadesSeleccionadas[entrada.id]
                ?: 0

        if (cantidadActual <= 1) {
            cantidadesSeleccionadas.remove(
                entrada.id
            )
        } else {
            cantidadesSeleccionadas[entrada.id] =
                cantidadActual - 1
        }

        notificarEntradaCambiada(
            entrada.id
        )
    }

    private fun notificarEntradaCambiada(
        entradaId: Int
    ) {

        val posicion = entradas.indexOfFirst { entrada ->
            entrada.id == entradaId
        }

        if (posicion >= 0) {
            notifyItemChanged(posicion)
        }
    }
}