# Flujo de solicitudes de pedido

La app del cliente crea documentos en `solicitudesPedidos`. El `totalEstimado` y los precios dentro de `productos` son datos informativos enviados por el teléfono; la app de administración debe validarlos contra el catálogo antes de aceptar.

## Estados

- `Pendiente de aprobacion`: enviada por el cliente; todavía no reserva ni descuenta stock.
- `Aprobada`: la app de administración ya creó el pedido definitivo.
- `Rechazada`: la app de administración rechazó la solicitud.

## Aprobación desde la app de administración

1. Consultar las solicitudes pendientes.
2. Volver a leer del catálogo cada producto y verificar su precio y stock actual.
3. Calcular el total final en la app de administración.
4. En una transacción de Firestore, descontar el stock, avanzar `configuracion/contadorPedidos`, crear el documento definitivo en `pedidos` y actualizar la solicitud a `Aprobada` con `numeroPedido`, `totalConfirmado` y `fechaRevision`.
5. Si no se puede completar, no modificar stock ni contador. Para rechazar, actualizar la solicitud a `Rechazada`, con `motivoRevision` y `fechaRevision`.

Las reglas de `firestore.rules` dejan las escrituras de catálogo, stock, pedidos y contador solo para administradores. El cliente puede crear una solicitud y leer las propias, pero no puede cambiarla después de enviarla.

Antes de probar la app del cliente, publica estas reglas en el mismo proyecto Firebase que usa la app. La app de administración debe usar los estados y campos anteriores para que el seguimiento del cliente se actualice correctamente.
