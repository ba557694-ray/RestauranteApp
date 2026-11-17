# Fase 5: Empaquetado y entrega

## Contenido de la entrega

```text
Proyecto Restaurante/
├── src/
│   ├── modelo/
│   ├── datos/
│   ├── negocio/
│   └── vista/
├── lib/
│   └── sqlite-jdbc-3.54.1.0.jar
├── documentacion/
│   ├── FASE1.md ... FASE5.md
│   ├── Manual_RestauranteApp.pdf
│   ├── casos_uso.puml
│   ├── clases.puml
│   ├── objetos.puml
│   ├── componentes.puml
│   └── imagenes/
├── script.sql
├── restaurante.db
├── RestauranteApp.jar
├── empaquetar.ps1
├── README.md
└── RestauranteApp.iml
```

## Construcción del JAR desde la terminal

El script `empaquetar.ps1` compila todas las fuentes, copia las clases a una carpeta temporal, extrae el contenido de `sqlite-jdbc-3.54.1.0.jar` dentro de esa carpeta y genera el JAR final:

```powershell
powershell -ExecutionPolicy Bypass -File .\empaquetar.ps1
```

Resultado:

```text
JAR generado en ...\RestauranteApp.jar
Contenido del controlador SQLite incluido en el JAR
Ejecute: java -jar RestauranteApp.jar
```

El JAR incluye:

- Clases de `modelo`, `datos`, `negocio` y `vista`.
- Clases de `org.sqlite` y las bibliotecas nativas de SQLite para Windows, Linux y macOS.
- El archivo `META-INF/services/java.sql.Driver` con el controlador JDBC.
- El manifiesto con `Main-Class: vista.Principal`.

La base `restaurante.db` no se empaqueta: se entrega como archivo externo para que el usuario conserve sus datos.

## Configuración del artefacto en IntelliJ IDEA

1. Abrir `File > Project Structure > Artifacts`.
2. Pulsar `+ > JAR > From modules with dependencies`.
3. En `Main Class` elegir `vista.Principal`.
4. En `JAR files from libraries` seleccionar `extract to the target JAR`.
5. Confirmar con `OK`.
6. En `Output directory` conservar la carpeta del proyecto.
7. En `Output layout`, verificar que el nombre sea `RestauranteApp.jar`.
8. Aplicar los cambios con `Apply` y `OK`.
9. Ejecutar `Build > Build Artifacts > RestauranteApp > Build`.

La opción `extract to the target JAR` es obligatoria: inserta las clases del controlador SQLite y sus bibliotecas nativas dentro del JAR, sin necesidad de llevar `sqlite-jdbc-3.54.1.0.jar` por separado.

Si el IDE muestra el aviso de que el proyecto usa el compilador de GUI Designer, comprobar en `Settings > Editor > GUI Designer` que la opción sea `Generate GUI into: Java source code`.

## Distribución

Entregar en una misma carpeta:

```text
RestauranteApp.jar
restaurante.db
```

La clase `ConexionBD` busca la base en el directorio de trabajo y, si no la encuentra, junto al JAR. Por eso el sistema funciona tanto al abrir el JAR desde IntelliJ como al hacer doble clic sobre él.

## Ejecución

Con Java 17 o superior instalado:

```text
java -jar RestauranteApp.jar
```

También se puede ejecutar desde IntelliJ con la clase `vista.Principal` y el directorio de trabajo en la raíz del proyecto.

## Verificación del empaquetado

| Comprobación | Resultado |
|---|---|
| El JAR contiene `vista/Principal.class` | Correcto |
| El JAR contiene `negocio/ServicioRestaurante.class` | Correcto |
| El JAR contiene `org/sqlite/JDBC.class` | Correcto |
| El JAR contiene bibliotecas nativas en `org/sqlite/native` | Correcto |
| El manifiesto declara `Main-Class: vista.Principal` | Correcto |
| El JAR abre la ventana y lee la base externa | Correcto |
| La base original conserva 9 mesas, 5 pedidos y 0 pagos | Correcto |

También se probó el JAR en una carpeta distinta a la base, con `restaurante.db` junto al archivo ejecutable y un directorio de trabajo diferente. La aplicación encontró la base, mostró las mesas 1 a 3 ocupadas y la orden de la mesa 1 con un total de $240.00.

## Lista de entrega

- [x] Script `script.sql` con dos tablas, llaves y datos iniciales.
- [x] Base `restaurante.db` con 9 mesas y 5 pedidos.
- [x] Modelo, DAO, servicio y vista en Java.
- [x] Interfaz Swing con formulario de GUI Designer.
- [x] CRUD y eventos de la interfaz, incluida la eliminación con menú contextual y tecla Supr.
- [x] Cuatro diagramas UML en PlantUML y PNG.
- [x] JAR con el controlador SQLite extraído.
- [x] Manual en PDF con portada, capturas, declaración de roles y ejecución.
- [x] README con requisitos, estructura y pasos de ejecución.
- [x] Documentación de las cinco fases.
