package com.deiapp.appzetar.usuario.pedidos

data class PedidoUsuarioItem(
    val id: String = "",
    val numeroPedido: Long = 0L,
    val estado: String = "Pendiente",
    val tipoEntrega: String = "Delivery",
    val total: Double = 0.0,
    val fecha: Long = 0L,
    val fechaEntrega: Long = 0L,
    val fechaActualizacion: Long = 0L
) {

    companion object {
        // Tiempo durante el que los pedidos entregados permanecen visibles: 2 horas.
        private const val TIEMPO_VISIBLE_ENTREGADO = 2L * 60L * 60L * 1000L

        // Tiempo durante el que los pedidos cancelados permanecen visibles: 1 hora.
        private const val TIEMPO_VISIBLE_CANCELADO = 1L * 60L * 60L * 1000L
    }

    /** Devuelve el número de pedido con cuatro dígitos como mínimo. */
    fun numeroPedidoFormateado(): String =
        if (numeroPedido > 0L) {
            numeroPedido.toString().padStart(4, '0')
        } else {
            "----"
        }

    /** Indica si el pedido corresponde a delivery. */
    fun esDelivery(): Boolean = tipoEntrega.equals("Delivery", ignoreCase = true)

    /** Normaliza los estados conocidos y conserva los desconocidos no vacíos. */
    fun estadoNormalizado(): String =
        when (estado.trim().lowercase()) {
            "pendiente",
            "pedido recibido" -> "Pendiente"

            "confirmado" -> "Confirmado"

            "preparando",
            "en preparación",
            "en preparacion" -> "En preparación"

            "en camino" -> "En camino"

            "listo para recoger",
            "listo para recojo" -> "Listo para recoger"

            "entregado" -> "Entregado"
            "cancelado" -> "Cancelado"

            else -> estado.trim().ifBlank { "Pendiente" }
        }

    /** Devuelve la posición del estado en el seguimiento del pedido. */
    fun posicionEstado(): Int =
        when (estadoNormalizado()) {
            "Pendiente" -> 0
            "Confirmado" -> 1
            "En preparación" -> 2
            "En camino",
            "Listo para recoger" -> 3

            "Entregado" -> 4
            "Cancelado" -> -1
            else -> 0
        }

    /** Determina si el pedido debe seguir visible en la pantalla del cliente. */
    fun debeMostrarse(ahora: Long = System.currentTimeMillis()): Boolean =
        when (estadoNormalizado()) {
            "Entregado" -> {
                // Los pedidos antiguos sin fechaEntrega no se muestran.
                if (fechaEntrega <= 0L) {
                    false
                } else {
                    val tiempoTranscurrido = ahora - fechaEntrega
                    tiempoTranscurrido < TIEMPO_VISIBLE_ENTREGADO
                }
            }

            "Cancelado" -> {
                if (fechaActualizacion <= 0L) {
                    false
                } else {
                    val tiempoTranscurrido = ahora - fechaActualizacion
                    tiempoTranscurrido < TIEMPO_VISIBLE_CANCELADO
                }
            }

            // Los pedidos activos siempre aparecen.
            else -> true
        }
}