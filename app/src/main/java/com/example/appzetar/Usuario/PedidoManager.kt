package com.example.appzetar.Usuario

object PedidoManager {

    val pedido = mutableListOf<PedidoItem>()

    fun agregarProducto(item: PedidoItem) {

        val existente = pedido.find {
            it.id == item.id
        }

        if (existente != null) {
            existente.cantidad += item.cantidad
        } else {
            pedido.add(item)
        }
    }

    fun aumentarCantidad(id: Int) {

        pedido.find {
            it.id == id
        }?.cantidad++
    }

    fun disminuirCantidad(id: Int) {

        val producto = pedido.find {
            it.id == id
        }

        if (producto != null) {

            if (producto.cantidad > 1) {
                producto.cantidad--
            } else {
                pedido.remove(producto)
            }
        }
    }

    fun eliminarProducto(id: Int) {

        pedido.removeAll {
            it.id == id
        }
    }

    fun cantidadTotal(): Int {

        return pedido.sumOf {
            it.cantidad
        }
    }

    fun limpiar() {
        pedido.clear()
    }
}