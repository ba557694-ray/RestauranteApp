# Fase 1: Base de Datos y Modelado

## Entregables

- `script.sql`: creación de la base de datos SQLite, dos tablas, llaves, restricciones y datos iniciales.
- `documentacion/casos_uso.puml`: diagrama de casos de uso.
- `documentacion/clases.puml`: diagrama de clases.
- `documentacion/objetos.puml`: diagrama de objetos.
- `documentacion/componentes.puml`: diagrama de componentes.
- `documentacion/imagenes`: versión PNG de los cuatro diagramas.

## Modelo relacional

### Tabla `mesas`

| Campo | Tipo | Restricción |
|---|---|---|
| `id_mesa` | INTEGER | PK, AUTOINCREMENT |
| `numero` | INTEGER | NOT NULL, UNIQUE |
| `capacidad` | INTEGER | NOT NULL, CHECK mayor que cero |
| `estado` | TEXT | NOT NULL, CHECK `Disponible` u `Ocupada` |
| `ubicacion` | TEXT | NOT NULL |

### Tabla `pedidos`

| Campo | Tipo | Restricción |
|---|---|---|
| `id_pedido` | INTEGER | PK, AUTOINCREMENT |
| `id_mesa` | INTEGER | FK hacia `mesas.id_mesa` |
| `platillo` | TEXT | NOT NULL |
| `cantidad` | INTEGER | NOT NULL, CHECK mayor que cero |
| `precio_unitario` | REAL | NOT NULL, CHECK mayor o igual que cero |
| `subtotal` | REAL | NOT NULL, CHECK mayor o igual que cero |
| `estado` | TEXT | NOT NULL, CHECK `Pendiente` o `Entregado` |
| `pagado` | INTEGER | NOT NULL, CHECK cero o uno |
| `metodo_pago` | TEXT | `Efectivo`, `Terminal` o nulo |
| `fecha_hora` | TEXT | NOT NULL, valor por defecto `CURRENT_TIMESTAMP` |
| `fecha_pago` | TEXT | Nulo hasta registrar el pago |

## Decisiones de diseño

El requerimiento establece exactamente dos tablas. Por esa razón, cada fila de `pedidos` representa una línea de la orden de una mesa. La tabla visual de la orden se obtiene con `SELECT` filtrando por `id_mesa` y `pagado = 0`.

El cierre del pedido se conserva en la misma tabla: al registrar el pago se actualizan todas las líneas pendientes de la mesa con `pagado = 1`, `metodo_pago` y `fecha_pago`. Después se cambia el estado de la mesa a `Disponible`. Así se mantiene el historial sin crear una tercera tabla.

El menú de platillos se maneja en memoria mediante la clase `Platillo`. Los datos necesarios para la orden se copian a `pedidos` como nombre, precio unitario y subtotal.

## Datos iniciales

La tabla `mesas` contiene nueve registros: las mesas 1, 2 y 3 están ocupadas; las mesas 4 a 9 están disponibles. La ampliación a nueve mesas permite cubrir el grid de mesas 1 a 9 de la interfaz.

La tabla `pedidos` contiene cinco líneas activas distribuidas en las mesas 1, 2 y 3. Los totales iniciales son:

| Mesa | Total pendiente |
|---|---|
| 1 | 240.00 |
| 2 | 120.00 |
| 3 | 265.00 |
| 4 a 9 | 0.00 |

## Validación del script

El script fue ejecutado en una base SQLite en memoria y produjo este resultado:

- Tablas de usuario: `mesas` y `pedidos`.
- Registros en `mesas`: 9.
- Registros en `pedidos`: 5.
- Verificación de llaves foráneas: sin errores.
- Total pendiente de la mesa 1: 240.00.

## Visualización de los diagramas

Los archivos `.puml` se pueden abrir con el plugin PlantUML de IntelliJ IDEA o con cualquier visor compatible con PlantUML. También se pueden renderizar con el comando:

```text
java -jar plantuml.jar documentacion\casos_uso.puml documentacion\clases.puml documentacion\objetos.puml documentacion\componentes.puml
```

## Trazabilidad con las siguientes fases

- `Mesa` y `Pedido` se implementarán en el paquete `modelo`.
- `ConexionBD`, `MesaDAO` y `PedidoDAO` se implementarán en el paquete `datos`.
- `ServicioRestaurante` se implementará en el paquete `negocio`.
- `VentanaPrincipal` se implementará en el paquete `vista` con IntelliJ GUI Designer y archivo `.form`.
- La interfaz consumirá exclusivamente `ServicioRestaurante`, y este delegará en los DAO.
