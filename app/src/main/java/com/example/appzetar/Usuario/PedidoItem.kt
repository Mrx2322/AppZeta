package com.example.appzetar.Usuario

data class PedidoItem(
    val id: Int,
    val nombre: String,
    var cantidad: Int = 1
)
