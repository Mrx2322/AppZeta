package com.deiapp.appzetar.usuario.modelos

data class TaskCenas(
    val id: Int,
    var name: String,
    var precio: Double,
    var stock: Int = 0
)