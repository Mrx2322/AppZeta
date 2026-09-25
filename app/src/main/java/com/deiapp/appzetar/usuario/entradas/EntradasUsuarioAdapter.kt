package com.deiapp.appzetar.usuario.entradas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deiapp.appzeta.R
import com.deiapp.appzetar.usuario.modelos.TaskEntradas
import com.deiapp.appzetar.usuario.carrito.EntradaPedido
import com.deiapp.appzetar.usuario.carrito.PedidoManager

class EntradasUsuarioAdapter(
    private val entradas: MutableList<TaskEntradas>
) : RecyclerView.Adapter<EntradasUsuarioViewHolder>() {

    private val cantidadesSeleccionadas = mutableMapOf<Int, Int>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EntradasUsuarioViewHolder {

        val view = LayoutInflater
            .from(parent.context)
            .inflate(
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

        val cantidadSeleccionada =
            cantidadesSeleccionadas[entrada.id] ?: 0

        holder.render(
            taskEntradas = entrada,
            cantidadSeleccionada = cantidadSeleccionada,
            maximoSeleccionable = calcularMaximoSeleccionable(entrada),
            onAlternarClick = {
                alternarEntrada(entrada)
            }
        )
    }

    override fun getItemCount(): Int = entradas.size

    fun obtenerEntradasSeleccionadas(): List<EntradaPedido> {
        return entradas.mapNotNull { entrada ->

            val cantidad =
                cantidadesSeleccionadas[entrada.id] ?: 0

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

        val entradasSeleccionadas =
            cantidadesSeleccionadas.keys.toSet()

        cantidadesSeleccionadas.clear()

        entradas.forEachIndexed { index, entrada ->

            if (entrada.id in entradasSeleccionadas) {
                notifyItemChanged(index)
            }
        }
    }

    private fun alternarEntrada(
        entrada: TaskEntradas
    ) {

        val cantidadActual =
            cantidadesSeleccionadas[entrada.id] ?: 0

        // Si ya está seleccionada, se desmarca.
        if (cantidadActual > 0) {

            cantidadesSeleccionadas.remove(entrada.id)

            notificarEntradaCambiada(entrada.id)

            return
        }

        val maximoSeleccionable =
            calcularMaximoSeleccionable(entrada)

        // No permite seleccionar entradas agotadas
        // o no disponibles.
        if (
            !entrada.disponible ||
            maximoSeleccionable <= 0
        ) {
            return
        }

        // Solo una unidad por selección.
        cantidadesSeleccionadas[entrada.id] = 1

        notificarEntradaCambiada(entrada.id)
    }

    private fun calcularMaximoSeleccionable(
        entrada: TaskEntradas
    ): Int {

        val cantidadEnCarrito =
            PedidoManager.cantidadEntradaEnPedido(
                entrada.id
            )

        return (entrada.stock - cantidadEnCarrito)
            .coerceAtLeast(0)
    }

    private fun notificarEntradaCambiada(
        entradaId: Int
    ) {

        val posicion =
            entradas.indexOfFirst { entrada ->
                entrada.id == entradaId
            }

        if (posicion != RecyclerView.NO_POSITION) {
            notifyItemChanged(posicion)
        }
    }
}