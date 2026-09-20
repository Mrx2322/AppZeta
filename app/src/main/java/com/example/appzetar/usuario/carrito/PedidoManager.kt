package com.example.appzetar.usuario.carrito

object PedidoManager {

    val pedido = mutableListOf<PedidoItem>()

    fun agregarProducto(item: PedidoItem) {
        val existente = buscarProducto(item)

        if (existente != null) {
            existente.cantidad += item.cantidad
        } else {
            pedido.add(item)
        }
    }

    fun aumentarCantidad(item: PedidoItem) {
        buscarProducto(item)?.let { producto ->
            producto.cantidad++
        }
    }

    fun disminuirCantidad(item: PedidoItem) {
        val producto = buscarProducto(item) ?: return

        if (producto.cantidad > 1) {
            producto.cantidad--
        } else {
            pedido.remove(producto)
        }
    }

    fun eliminarProducto(item: PedidoItem) {
        buscarProducto(item)?.let { producto ->
            pedido.remove(producto)
        }
    }

    fun cantidadMenuEnPedido(menuId: Int): Int {
        return pedido
            .filter { item ->
                item.tipo == TipoPedido.MENU &&
                        item.id == menuId
            }
            .sumOf { item ->
                item.cantidad
            }
    }

    fun cantidadEntradaEnPedido(entradaId: Int): Int {
        val entradasAsociadas = pedido
            .filter { item ->
                item.tipo == TipoPedido.MENU
            }
            .sumOf { item ->
                val cantidadPorMenu = item.entradas
                    .filter { entrada ->
                        entrada.id == entradaId
                    }
                    .sumOf { entrada ->
                        entrada.cantidad
                    }

                cantidadPorMenu * item.cantidad
            }

        val entradasAntiguas = pedido
            .filter { item ->
                item.tipo == TipoPedido.ENTRADA &&
                        item.id == entradaId
            }
            .sumOf { item ->
                item.cantidad
            }

        return entradasAsociadas + entradasAntiguas
    }

    fun totalPedido(): Double {
        return pedido.sumOf { item ->
            item.subtotal()
        }
    }

    fun cantidadTotal(): Int {
        return pedido.sumOf { item ->
            item.cantidad
        }
    }

    fun limpiar() {
        pedido.clear()
    }

    private fun buscarProducto(item: PedidoItem): PedidoItem? {
        return pedido.find { producto ->
            producto.tieneMismaConfiguracionQue(item)
        }
    }
}
