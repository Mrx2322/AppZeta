package com.example.appzetar.Usuario

data class PedidoUsuarioItem(

    val id: String = "",

    val numeroPedido: Long = 0L,

    val estado: String = "Pendiente",

    val tipoEntrega: String = "Delivery",

    val total: Double = 0.0,

    val fecha: Long = 0L

) {

    // =========================================================
    // NÚMERO FORMATEADO
    // =========================================================

    fun numeroPedidoFormateado(): String {

        return if (numeroPedido > 0L) {

            String.format(
                "%04d",
                numeroPedido
            )

        } else {

            "----"
        }
    }

    // =========================================================
    // VALIDAR TIPO DE ENTREGA
    // =========================================================

    fun esDelivery(): Boolean {

        return tipoEntrega.equals(
            "Delivery",
            ignoreCase = true
        )
    }

    // =========================================================
    // ESTADO NORMALIZADO
    // =========================================================

    fun estadoNormalizado(): String {

        return when (
            estado.trim().lowercase()
        ) {

            "pendiente",
            "pedido recibido" ->
                "Pendiente"

            "confirmado" ->
                "Confirmado"

            "preparando",
            "en preparación",
            "en preparacion" ->
                "En preparación"

            "en camino" ->
                "En camino"

            "listo para recoger",
            "listo para recojo" ->
                "Listo para recoger"

            "entregado" ->
                "Entregado"

            "cancelado" ->
                "Cancelado"

            else ->
                estado.trim().ifBlank {
                    "Pendiente"
                }
        }
    }

    // =========================================================
    // POSICIÓN DEL PROGRESO
    // =========================================================

    fun posicionEstado(): Int {

        return when (estadoNormalizado()) {

            "Pendiente" ->
                0

            "Confirmado" ->
                1

            "En preparación" ->
                2

            "En camino",
            "Listo para recoger" ->
                3

            "Entregado" ->
                4

            "Cancelado" ->
                -1

            else ->
                0
        }
    }
}