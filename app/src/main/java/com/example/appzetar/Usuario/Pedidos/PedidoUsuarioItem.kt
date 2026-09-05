package com.example.appzetar.Usuario

data class PedidoUsuarioItem(
    val id: String = "",
    val numeroPedido: Long = 0L,
    val estado: String = "Pendiente",
    val total: Double = 0.0,
    val fecha: Long = 0L
) {

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
}