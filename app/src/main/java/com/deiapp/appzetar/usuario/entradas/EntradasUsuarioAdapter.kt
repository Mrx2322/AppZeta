package com.deiapp.appzetar.usuario.entradas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deiapp.appzeta.R
import com.deiapp.appzetar.usuario.carrito.EntradaPedido
import com.deiapp.appzetar.usuario.carrito.PedidoManager
import com.deiapp.appzetar.usuario.modelos.TaskEntradas

class EntradasUsuarioAdapter(
    private val entradas: MutableList<TaskEntradas>
) : RecyclerView.Adapter<EntradasUsuarioViewHolder>() {

    private val cantidadesSeleccionadas =
        mutableMapOf<Int, Int>()


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EntradasUsuarioViewHolder {

        val view =
            LayoutInflater
                .from(parent.context)
                .inflate(
                    R.layout.item_task_entradas,
                    parent,
                    false
                )

        return EntradasUsuarioViewHolder(
            view
        )
    }


    override fun onBindViewHolder(
        holder: EntradasUsuarioViewHolder,
        position: Int
    ) {

        val entrada =
            entradas[position]


        val maximoSeleccionable =
            calcularMaximoSeleccionable(
                entrada
            )


        val cantidadActual =
            (
                    cantidadesSeleccionadas[
                        entrada.id
                    ] ?: 0
                    )
                .coerceAtMost(
                    maximoSeleccionable
                )


        if (cantidadActual > 0) {

            cantidadesSeleccionadas[
                entrada.id
            ] = cantidadActual

        } else {

            cantidadesSeleccionadas.remove(
                entrada.id
            )
        }


        holder.render(
            taskEntradas = entrada,
            cantidadSeleccionada = cantidadActual,
            maximoSeleccionable = maximoSeleccionable,

            onSumarClick = {

                sumarEntrada(
                    entrada
                )
            },

            onRestarClick = {

                restarEntrada(
                    entrada
                )
            }
        )
    }


    override fun getItemCount(): Int =
        entradas.size


    // =============================================================
    // ENTRADAS SELECCIONADAS
    // =============================================================

    fun obtenerEntradasSeleccionadas():
            List<EntradaPedido> {

        return entradas.mapNotNull { entrada ->

            val cantidad =
                cantidadesSeleccionadas[
                    entrada.id
                ] ?: 0


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


    // =============================================================
    // LIMPIAR
    // =============================================================

    fun limpiarSeleccion() {

        if (
            cantidadesSeleccionadas.isEmpty()
        ) {
            return
        }

        cantidadesSeleccionadas.clear()

        notifyDataSetChanged()
    }


    // =============================================================
    // SUMAR
    // =============================================================

    private fun sumarEntrada(
        entrada: TaskEntradas
    ) {

        val maximoSeleccionable =
            calcularMaximoSeleccionable(
                entrada
            )

        val cantidadActual =
            cantidadesSeleccionadas[
                entrada.id
            ] ?: 0


        if (
            !entrada.disponible ||
            cantidadActual >= maximoSeleccionable
        ) {
            return
        }


        cantidadesSeleccionadas[
            entrada.id
        ] =
            cantidadActual + 1


        notificarEntradaCambiada(
            entrada.id
        )
    }


    // =============================================================
    // RESTAR
    // =============================================================

    private fun restarEntrada(
        entrada: TaskEntradas
    ) {

        val cantidadActual =
            cantidadesSeleccionadas[
                entrada.id
            ] ?: 0


        if (cantidadActual <= 0) {
            return
        }


        if (cantidadActual == 1) {

            cantidadesSeleccionadas.remove(
                entrada.id
            )

        } else {

            cantidadesSeleccionadas[
                entrada.id
            ] =
                cantidadActual - 1
        }


        notificarEntradaCambiada(
            entrada.id
        )
    }


    // =============================================================
    // STOCK DISPONIBLE
    // =============================================================

    private fun calcularMaximoSeleccionable(
        entrada: TaskEntradas
    ): Int {

        val cantidadEnCarrito =
            PedidoManager
                .cantidadEntradaEnPedido(
                    entrada.id
                )


        return (
                entrada.stock -
                        cantidadEnCarrito
                )
            .coerceAtLeast(0)
    }


    // =============================================================
    // ACTUALIZAR TARJETA
    // =============================================================

    private fun notificarEntradaCambiada(
        entradaId: Int
    ) {

        val posicion =
            entradas.indexOfFirst { entrada ->
                entrada.id == entradaId
            }


        if (
            posicion !=
            RecyclerView.NO_POSITION
        ) {

            notifyItemChanged(
                posicion
            )
        }
    }
}