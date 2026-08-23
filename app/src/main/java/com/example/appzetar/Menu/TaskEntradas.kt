package com.example.appzetar.Menu

sealed class TaskEntradas(var nombre: String) {
    class Ceviche(nombre: String = "Ceviche") : TaskEntradas(nombre)
    class Huancaina(nombre: String = "Huancaína") : TaskEntradas(nombre)
    class Otros(nombre: String = "Otros") : TaskEntradas(nombre)
}