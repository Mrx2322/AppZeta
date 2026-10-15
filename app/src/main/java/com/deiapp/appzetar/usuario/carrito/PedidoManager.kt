package com.deiapp.appzetar.usuario.carrito

object PedidoManager {

    val pedido = mutableListOf<PedidoItem>()

    // =========================================================
    // AGREGAR PRODUCTOS
    // =========================================================

    fun agregarProducto(item: PedidoItem) {
        val existente = buscarProducto(item)

        if (existente != null) {
            existente.cantidad += item.cantidad
        } else {
            pedido.add(item)
        }
    }

    // =========================================================
    // AUMENTAR CANTIDAD
    // =========================================================

    fun aumentarCantidad(item: PedidoItem) {
        buscarProducto(item)?.let { producto ->
            producto.cantidad++
        }
    }

    // =========================================================
    // DISMINUIR CANTIDAD
    // =========================================================

    fun disminuirCantidad(item: PedidoItem) {
        val producto = buscarProducto(item) ?: return

        if (producto.cantidad > 1) {
            producto.cantidad--
        } else {
            pedido.remove(producto)
        }
    }

    // =========================================================
    // ELIMINAR PRODUCTOS
    // =========================================================

    fun eliminarProducto(item: PedidoItem) {
        buscarProducto(item)?.let { producto ->
            pedido.remove(producto)
        }
    }

    // =========================================================
    // CONTAR MENÚS EN EL PEDIDO
    // =========================================================

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

    // =========================================================
    // CONTAR CENAS EN EL PEDIDO
    // =========================================================

    fun cantidadCenaEnPedido(cenaId: Int): Int {
        return pedido
            .filter { item ->
                item.tipo == TipoPedido.CENA &&
                        item.id == cenaId
            }
            .sumOf { item ->
                item.cantidad
            }
    }

    // =========================================================
    // CONTAR ENTRADAS EN EL PEDIDO
    // =========================================================

    fun cantidadEntradaEnPedido(entradaId: Int): Int {

        // Entradas asociadas a los menús.
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

        // Entradas agregadas individualmente.
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

    // =========================================================
    // CALCULAR TOTAL DEL PEDIDO
    // =========================================================

    fun totalPedido(): Double {
        return pedido.sumOf { item ->
            item.subtotal()
        }
    }

    // =========================================================
    // CALCULAR CANTIDAD TOTAL DE PRODUCTOS
    // =========================================================

    fun cantidadTotal(): Int {
        return pedido.sumOf { item ->
            item.cantidad
        }
    }

    // =========================================================
    // LIMPIAR CARRITO
    // =========================================================

    fun limpiar() {
        pedido.clear()
    }

    // =========================================================
    // BUSCAR PRODUCTO CON LA MISMA CONFIGURACIÓN
    // =========================================================

    private fun buscarProducto(item: PedidoItem): PedidoItem? {
        return pedido.find { producto ->
            producto.tieneMismaConfiguracionQue(item)
        }
    }
}