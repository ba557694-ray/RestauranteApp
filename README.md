# Sistema de Gestión de Restaurante

Aplicación de escritorio en Java Swing para el rol de Mesero. Permite controlar las mesas del salón, registrar los platillos de cada orden, marcar entregas, calcular totales y cobrar en efectivo o con terminal. La información se guarda en una base SQLite local.

## Requisitos

- Java 17 o superior.
- IntelliJ IDEA con el plugin de GUI Designer para editar la ventana.
- SQLite no requiere instalación: el controlador viaja dentro del JAR.

## Ejecución rápida

```text
java -jar RestauranteApp.jar
```

El archivo `restaurante.db` debe estar junto al JAR o en el directorio de trabajo. La clase `ConexionBD` busca la base en ambos lugares.

## Estructura

```text
Proyecto Restaurante/
├── src/
│   ├── modelo/          Mesa, Pedido y Platillo
│   ├── datos/           ConexionBD, MesaDAO, PedidoDAO y ExcepcionBaseDatos
│   ├── negocio/         ServicioRestaurante
│   └── vista/           VentanaPrincipal, Principal e IconoUsuario
├── lib/                 sqlite-jdbc-3.54.1.0.jar
├── documentacion/
│   ├── FASE1.md ... FASE5.md
│   ├── Manual_RestauranteApp.pdf
│   ├── *.puml y imagenes/
│   └── generar_manual.py
├── script.sql
├── restaurante.db
├── RestauranteApp.jar
├── empaquetar.ps1
└── RestauranteApp.iml
```

## Funcionalidades

- Grid de mesas 1 a 9 en verde (disponible) y rojo (ocupada).
- Al hacer clic en una mesa disponible se ocupa y se abre su orden.
- Alta de platillos con cantidad acumulada y subtotal automático.
- Marcado de platillos como entregados.
- Eliminación de líneas con menú contextual o la tecla Supr.
- Generación de la cuenta y registro de pago en efectivo o terminal.
- Al cobrar, la mesa se libera y el historial queda en la tabla `pedidos`.
- Consulta de pedidos activos, ocupación y total pendiente.
- Mensajes de error y validaciones con `JOptionPane`.

## Base de datos

El script `script.sql` crea exactamente dos tablas:

- `mesas`: número, capacidad, estado y ubicación.
- `pedidos`: una fila por línea de la orden, con precio, subtotal, estado, pago y fechas.

Datos iniciales: 9 mesas (1 a 3 ocupadas) y 5 líneas de pedido por un total pendiente de $625.00.

## Compilación y empaquetado

```text
javac -encoding UTF-8 --release 17 -cp lib\sqlite-jdbc-3.54.1.0.jar -d out\clases src\modelo\*.java src\datos\*.java src\negocio\*.java src\vista\*.java
powershell -ExecutionPolicy Bypass -File .\empaquetar.ps1
```

El script `empaquetar.ps1` genera `RestauranteApp.jar` con las clases de SQLite extraídas dentro, equivalente a la opción `extract to the target JAR` de IntelliJ IDEA.

## Publicación en GitHub Releases

El JAR funciona por sí solo cuando `restaurante.db` está en la misma carpeta. Para una entrega completa, adjuntar como asset de la release el paquete `entrega/RestauranteApp_v1.0.zip`, que contiene:

- `RestauranteApp.jar`
- `restaurante.db` con los datos iniciales
- `iniciar.bat` para Windows
- `LEEME.txt` con los pasos de ejecución

El usuario final necesita Java 17 o superior. Si descarga solo el JAR y no tiene la base junto a él, la aplicación abre pero muestra el error de tablas faltantes.

Para regenerar el paquete:

```text
powershell -ExecutionPolicy Bypass -File .\empaquetar.ps1
Copy-Item restaurante.db entrega\restaurante.db -Force
Copy-Item RestauranteApp.jar entrega\RestauranteApp.jar -Force
Compress-Archive -Path entrega\RestauranteApp.jar, entrega\restaurante.db, entrega\iniciar.bat, entrega\LEEME.txt -DestinationPath entrega\RestauranteApp_v1.0.zip -Force
```

## Documentación

- `documentacion/Manual_RestauranteApp.pdf`: manual técnico y de usuario con portada, capturas, diagramas, declaración de roles y guía de ejecución.
- `documentacion/FASE1.md` a `FASE5.md`: detalle de cada fase del desarrollo.
- `documentacion/casos_uso.puml`, `clases.puml`, `objetos.puml` y `componentes.puml`: diagramas UML con su versión PNG en `documentacion/imagenes`.
