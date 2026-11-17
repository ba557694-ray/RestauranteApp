# Fase 2: Configuración y Conexión JDBC

## Componentes implementados

```text
src/
├── modelo/
│   ├── Mesa.java
│   ├── Pedido.java
│   └── Platillo.java
└── datos/
    ├── ConexionBD.java
    ├── ExcepcionBaseDatos.java
    ├── MesaDAO.java
    └── PedidoDAO.java

lib/
└── sqlite-jdbc-3.54.1.0.jar
```

## Conexión a SQLite

`ConexionBD` trabaja con el archivo local `restaurante.db`. La clase busca el archivo primero en el directorio de trabajo y, si no lo encuentra, junto al JAR o a la carpeta de clases en ejecución. La dirección JDBC se arma con la ruta absoluta encontrada:

```text
jdbc:sqlite:<ruta absoluta>\restaurante.db
```

Cada conexión habilita las llaves foráneas y un tiempo de espera para bloqueos:

```sql
PRAGMA foreign_keys = ON;
PRAGMA busy_timeout = 5000;
```

El controlador `org.sqlite.JDBC` se carga al construir la clase. Si el archivo de base de datos no existe, SQLite lo crea, pero las tablas deben existir previamente mediante `script.sql`.

## DAO implementados

### `MesaDAO`

| Método | Operación |
|---|---|
| `insertar(Mesa)` | Inserta una mesa y recupera su llave generada |
| `actualizar(Mesa)` | Actualiza número, capacidad, estado y ubicación |
| `eliminar(int)` | Elimina una mesa por llave primaria |
| `obtenerTodos()` | Devuelve todas las mesas ordenadas por número |
| `obtenerPorId(int)` | Devuelve una mesa o `null` |
| `cambiarEstado(int, String)` | Cambia el estado de una mesa |

### `PedidoDAO`

| Método | Operación |
|---|---|
| `insertar(Pedido)` | Inserta una línea de pedido y recupera su llave generada |
| `actualizar(Pedido)` | Actualiza todos los datos editables de la línea |
| `eliminar(int)` | Elimina una línea por llave primaria |
| `obtenerTodos()` | Devuelve todas las líneas de pedido |
| `obtenerPorId(int)` | Devuelve una línea o `null` |
| `obtenerPorMesa(int)` | Devuelve todo el historial de una mesa |
| `obtenerPendientesPorMesa(int)` | Devuelve las líneas con `pagado = 0` |
| `marcarEntregado(int)` | Cambia el estado de la línea a `Entregado` |
| `registrarPagoMesa(int, String)` | Marca como pagadas todas las líneas pendientes de la mesa |
| `calcularTotalMesa(int)` | Suma los subtotales pendientes de la mesa |

## Seguridad y manejo de errores

- Todas las consultas utilizan `PreparedStatement` con parámetros.
- Los valores se enlazan mediante `setInt`, `setDouble`, `setString` y `setNull`.
- Los recursos se cierran con `try-with-resources`.
- Cada operación captura `SQLException` y lanza `ExcepcionBaseDatos` con un mensaje en español.
- Las llaves foráneas están activas en cada conexión.
- La base conserva el historial de pagos sin crear tablas adicionales.

## Validación ejecutada

Se copió `restaurante.db` a un directorio temporal y se ejecutó una prueba completa de integración:

- Lectura inicial de nueve mesas y cinco pedidos.
- Inserción de una mesa con recuperación de llave generada.
- Actualización y consulta de la mesa.
- Cambio de estado de la mesa.
- Inserción de un pedido con subtotal calculado.
- Actualización de cantidad y precio con recálculo del subtotal.
- Consulta de pedidos pendientes por mesa.
- Cálculo del total de la mesa.
- Marcado de la línea como entregada.
- Registro del pago con método `Efectivo` y fecha automática.
- Verificación del total pendiente en cero.
- Intento de eliminación de una mesa con pedidos, bloqueado por la llave foránea.
- Eliminación controlada del pedido y de la mesa de prueba.
- Restauración de los conteos iniciales de nueve mesas y cinco pedidos.

Resultado:

```text
Prueba de la Fase 2 completada correctamente
```

## Compilación

```text
javac -encoding UTF-8 --release 17 -cp lib\sqlite-jdbc-3.54.1.0.jar -d out\clases src\modelo\*.java src\datos\*.java
```

El proyecto requiere JDK 17 o superior. El archivo `sqlite-jdbc-3.54.1.0.jar` se encuentra en la carpeta `lib` y posteriormente se integrará al JAR final.
