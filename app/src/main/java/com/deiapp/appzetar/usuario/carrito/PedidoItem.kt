package com.deiapp.appzetar.usuario.carrito

/**
 * Entrada asociada a una unidad del menú.
 */
data class EntradaPedido(
    val id: Int,
    val nombre: String,
    val cantidad: Int = 1,
    val precioUnitario: Double = ReglasPrecioPedido.PRECIO_ENTRADA
)

data class PedidoItem(
    val id: Int,
    val nombre: String,

    // Precio final por unidad que verá y pagará el cliente.
    val precio: Double,

    var cantidad: Int = 1,

    val tipo: TipoPedido = TipoPedido.MENU,

    // Solo se utiliza para productos de tipo MENU.
    val precioBaseMenu: Double? = null,

    val entradas: List<EntradaPedido> = emptyList()
) {

    fun subtotal(): Double =
        precio * cantidad


    fun cantidadEntradas(): Int =
        entradas.sumOf { entrada ->
            entrada.cantidad
        }


    fun descripcionEntradas(): String {

        if (tipo != TipoPedido.MENU) {
            return ""
        }

        if (entradas.isEmpty()) {
            return "Sin entrada"
        }

        return entradas.joinToString(
            separator = ", "
        ) { entrada ->

            if (entrada.cantidad > 1) {

                "${entrada.cantidad} x ${entrada.nombre}"

            } else {

                entrada.nombre
            }
        }
    }


    /**
     * Permite agrupar cantidades únicamente cuando el producto
     * y su combinación de entradas son exactamente iguales.
     */
    fun tieneMismaConfiguracionQue(
        otro: PedidoItem
    ): Boolean {

        if (
            id != otro.id ||
            tipo != otro.tipo ||
            precio != otro.precio
        ) {
            return false
        }

        if (tipo != TipoPedido.MENU) {
            return true
        }

        return firmaEntradas() ==
                otro.firmaEntradas()
    }


    private fun firmaEntradas():
            List<Triple<Int, Int, Double>> {

        return entradas
            .groupBy { entrada ->
                entrada.id
            }
            .map { (entradaId, coincidencias) ->

                Triple(
                    entradaId,

                    coincidencias.sumOf { entrada ->
                        entrada.cantidad
                    },

                    coincidencias
                        .first()
                        .precioUnitario
                )
            }
            .sortedBy { firma ->
                firma.first
            }
    }
}


enum class TipoPedido {
    MENU,
    ENTRADA,
    EXTRA
}


/**
 * Reglas comerciales del menú.
 *
 * Sin entrada:
 * precio del menú con entrada - S/1.
 *
 * Con una entrada:
 * se mantiene el precio normal del menú.
 *
 * Cada entrada adicional:
 * + S/5.
 */
object ReglasPrecioPedido {

    /**
     * Valor interno de la entrada incluida.
     * Se mantiene en S/2 para no alterar la estructura
     * existente de los pedidos.
     */
    const val PRECIO_ENTRADA =
        2.0

    /**
     * Precio cobrado al cliente por cada entrada adicional.
     */
    const val PRECIO_ENTRADA_ADICIONAL =
        5.0

    /**
     * Descuento cuando el cliente pide el menú sin entrada.
     */
    const val DESCUENTO_SIN_ENTRADA =
        1.0


    fun calcularPrecioMenu(
        precioMenuConEntrada: Double,
        cantidadEntradas: Int
    ): Double {

        return if (cantidadEntradas <= 0) {

            (
                    precioMenuConEntrada -
                            DESCUENTO_SIN_ENTRADA
                    )
                .coerceAtLeast(
                    0.0
                )

        } else {

            precioMenuConEntrada +
                    (
                            PRECIO_ENTRADA_ADICIONAL *
                                    (cantidadEntradas - 1)
                            )
        }
    }
}