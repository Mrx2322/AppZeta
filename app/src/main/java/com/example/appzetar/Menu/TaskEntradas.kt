package com.example.appzetar.Menu

sealed class TaskEntradas(
    var id: Int,
    var nombre: String,
    var disponible: Boolean
) {

    class Ceviche(
        id: Int = 1,
        nombre: String = "Ceviche",
        disponible: Boolean = true
    ) : TaskEntradas(id, nombre, disponible)

    class Huancaina(
        id: Int = 2,
        nombre: String = "Huancaína",
        disponible: Boolean = true
    ) : TaskEntradas(id, nombre, disponible)

    class Otros(
        id: Int = 3,
        nombre: String = "Otros",
        disponible: Boolean = true
    ) : TaskEntradas(id, nombre, disponible)
}