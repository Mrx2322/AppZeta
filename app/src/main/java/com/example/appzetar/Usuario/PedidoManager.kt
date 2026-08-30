package com.example.appzetar.Usuario

object PedidoManager {

    val pedido = mutableListOf<PedidoItem>()


    // =========================================================
    // AGREGAR PRODUCTO
    // =========================================================

    fun agregarProducto(item: PedidoItem) {

        val existente = pedido.find {

            it.id == item.id &&
                    it.tipo == item.tipo
        }

        if (existente != null) {

            existente.cantidad += item.cantidad

        } else {

            pedido.add(item)
        }
    }


    // =========================================================
    // AUMENTAR CANTIDAD
    // =========================================================

    fun aumentarCantidad(
        id: Int,
        tipo: TipoPedido
    ) {

        pedido.find {

            it.id == id &&
                    it.tipo == tipo

        }?.cantidad++
    }


    // =========================================================
    // DISMINUIR CANTIDAD
    // =========================================================

    fun disminuirCantidad(
        id: Int,
        tipo: TipoPedido
    ) {

        val producto =
            pedido.find {

                it.id == id &&
                        it.tipo == tipo
            }

        if (producto != null) {

            if (producto.cantidad > 1) {

                producto.cantidad--

            } else {

                pedido.remove(producto)
            }
        }
    }


    // =========================================================
    // ELIMINAR PRODUCTO
    // =========================================================

    fun eliminarProducto(
        id: Int,
        tipo: TipoPedido
    ) {

        pedido.removeAll {

            it.id == id &&
                    it.tipo == tipo
        }
    }


    // =========================================================
    // CANTIDAD TOTAL
    // =========================================================

    fun cantidadTotal(): Int {

        return pedido.sumOf {

            it.cantidad
        }
    }


    // =========================================================
    // LIMPIAR
    // =========================================================

    fun limpiar() {

        pedido.clear()
    }
}