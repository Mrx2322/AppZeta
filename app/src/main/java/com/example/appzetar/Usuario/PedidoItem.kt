package com.example.appzetar.Usuario

data class PedidoItem(
    val id: Int,
    val nombre: String,
    val precio: Double,
    var cantidad: Int = 1,
    val tipo: TipoPedido = TipoPedido.MENU
)

enum class TipoPedido {
    MENU,
    ENTRADA,
    EXTRA
}