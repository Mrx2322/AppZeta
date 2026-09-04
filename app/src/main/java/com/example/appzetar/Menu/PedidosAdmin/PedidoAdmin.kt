package com.example.appzetar.Menu

data class PedidoAdmin(

    val id: String,

    val nombreUsuario: String,

    val correo: String,

    val total: Double,

    val tipoEntrega: String,

    val direccion: String,

    val referencia: String,

    val telefono: String,

    val metodoPago: String,

    val estadoPago: String,

    val estadoPedido: String,

    val productos: List<Map<String, Any>>
)