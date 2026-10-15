package com.deiapp.appzetar.usuario.modelos

sealed class TaskEntradas(
    var id: Int,
    var nombre: String,
    var disponible: Boolean,
    var stock: Int
) {

    class Ceviche(
        id: Int = 1,
        nombre: String = "Ceviche",
        disponible: Boolean = true,
        stock: Int = 0
    ) : TaskEntradas(
        id = id,
        nombre = nombre,
        disponible = disponible,
        stock = stock
    )

    class Huancaina(
        id: Int = 2,
        nombre: String = "Huancaína",
        disponible: Boolean = true,
        stock: Int = 0
    ) : TaskEntradas(
        id = id,
        nombre = nombre,
        disponible = disponible,
        stock = stock
    )

    class Otros(
        id: Int = 3,
        nombre: String = "Otros",
        disponible: Boolean = true,
        stock: Int = 0
    ) : TaskEntradas(
        id = id,
        nombre = nombre,
        disponible = disponible,
        stock = stock
    )
}