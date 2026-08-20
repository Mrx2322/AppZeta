package com.example.appzetar.Menu

sealed class TaskEntradas(var isSelected:Boolean = true) {
    object ceviche : TaskEntradas()
    object otros : TaskEntradas()
    object huancaina : TaskEntradas()
}