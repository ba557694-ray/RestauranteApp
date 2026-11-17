from pathlib import Path

from reportlab.lib import colors
from reportlab.lib.enums import TA_CENTER, TA_JUSTIFY, TA_LEFT
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle
from reportlab.lib.units import cm
from reportlab.lib.utils import ImageReader
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.platypus import (
    BaseDocTemplate,
    Frame,
    Image,
    KeepTogether,
    NextPageTemplate,
    PageBreak,
    PageTemplate,
    Paragraph,
    Spacer,
    Table,
    TableStyle,
)
from reportlab.platypus.tableofcontents import TableOfContents

RAIZ = Path(__file__).resolve().parent
IMAGENES = RAIZ / "imagenes"
SALIDA = RAIZ / "Manual_RestauranteApp.pdf"

OSCURO = colors.HexColor("#1E1E2D")
LATERAL = colors.HexColor("#2A2A3D")
NARANJA = colors.HexColor("#E67E22")
VERDE = colors.HexColor("#27AE60")
GRIS = colors.HexColor("#6C6C80")
BORDE = colors.HexColor("#E4E6EF")
FONDO = colors.HexColor("#F5F6FA")

pdfmetrics.registerFont(TTFont("SegoeUI", r"C:\Windows\Fonts\segoeui.ttf"))
pdfmetrics.registerFont(TTFont("SegoeUIBold", r"C:\Windows\Fonts\segoeuib.ttf"))
pdfmetrics.registerFontFamily("SegoeUI", normal="SegoeUI", bold="SegoeUIBold")

ESTILOS = {
    "PortadaTitulo": ParagraphStyle(
        "PortadaTitulo", fontName="SegoeUIBold", fontSize=32, leading=38,
        textColor=colors.white, alignment=TA_LEFT, spaceAfter=12,
    ),
    "PortadaSubtitulo": ParagraphStyle(
        "PortadaSubtitulo", fontName="SegoeUI", fontSize=13, leading=20,
        textColor=colors.HexColor("#D8D8E2"), alignment=TA_LEFT, spaceAfter=6,
    ),
    "PortadaCredito": ParagraphStyle(
        "PortadaCredito", fontName="SegoeUIBold", fontSize=10, leading=16,
        textColor=NARANJA, alignment=TA_LEFT,
    ),
    "PortadaPie": ParagraphStyle(
        "PortadaPie", fontName="SegoeUI", fontSize=9, leading=14,
        textColor=colors.HexColor("#B8B8C7"), alignment=TA_LEFT,
    ),
    "H1": ParagraphStyle(
        "H1", fontName="SegoeUIBold", fontSize=19, leading=24,
        textColor=OSCURO, spaceBefore=4, spaceAfter=12,
    ),
    "H2": ParagraphStyle(
        "H2", fontName="SegoeUIBold", fontSize=13, leading=18,
        textColor=NARANJA, spaceBefore=12, spaceAfter=6,
    ),
    "Body": ParagraphStyle(
        "Body", fontName="SegoeUI", fontSize=10, leading=15,
        textColor=colors.HexColor("#2B2B38"), alignment=TA_JUSTIFY, spaceAfter=7,
    ),
    "Bullet": ParagraphStyle(
        "Bullet", fontName="SegoeUI", fontSize=10, leading=14.5,
        textColor=colors.HexColor("#2B2B38"), alignment=TA_JUSTIFY,
        leftIndent=14, bulletIndent=3, spaceAfter=4,
    ),
    "Caption": ParagraphStyle(
        "Caption", fontName="SegoeUI", fontSize=8.8, leading=12,
        textColor=GRIS, alignment=TA_CENTER, spaceBefore=4, spaceAfter=12,
    ),
    "Cell": ParagraphStyle(
        "Cell", fontName="SegoeUI", fontSize=8.7, leading=12,
        textColor=colors.HexColor("#2B2B38"),
    ),
    "CellBold": ParagraphStyle(
        "CellBold", fontName="SegoeUIBold", fontSize=8.7, leading=12,
        textColor=OSCURO,
    ),
    "CellHeader": ParagraphStyle(
        "CellHeader", fontName="SegoeUIBold", fontSize=8.7, leading=12,
        textColor=colors.white,
    ),
    "Nota": ParagraphStyle(
        "Nota", fontName="SegoeUI", fontSize=9.3, leading=13.5,
        textColor=GRIS, alignment=TA_JUSTIFY,
    ),
    "TOC": ParagraphStyle(
        "TOC", fontName="SegoeUI", fontSize=10.5, leading=20,
        textColor=OSCURO,
    ),
}


def parrafo(texto, estilo="Body"):
    return Paragraph(texto, ESTILOS[estilo])


def vineta(texto):
    return Paragraph(texto, ESTILOS["Bullet"], bulletText="•")


def subtitulo(texto):
    return Paragraph(texto, ESTILOS["H2"])


def figura(nombre, ancho_max=15.4 * cm, alto_max=21.5 * cm):
    ruta = IMAGENES / nombre
    ancho_px, alto_px = ImageReader(str(ruta)).getSize()
    escala = min(ancho_max / ancho_px, alto_max / alto_px)
    imagen = Image(str(ruta), width=ancho_px * escala, height=alto_px * escala)
    contenedor = Table([[imagen]], colWidths=[ancho_px * escala + 0.4 * cm])
    contenedor.setStyle(TableStyle([
        ("ALIGN", (0, 0), (-1, -1), "CENTER"),
        ("VALIGN", (0, 0), (-1, -1), "MIDDLE"),
        ("BOX", (0, 0), (-1, -1), 0.6, BORDE),
        ("LEFTPADDING", (0, 0), (-1, -1), 0.2 * cm),
        ("RIGHTPADDING", (0, 0), (-1, -1), 0.2 * cm),
        ("TOPPADDING", (0, 0), (-1, -1), 0.2 * cm),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 0.2 * cm),
    ]))
    contenedor.hAlign = "CENTER"
    return contenedor


def tabla(filas, anchos, cabecera=True, alineacion="LEFT"):
    contenido = []
    for indice, fila in enumerate(filas):
        if cabecera and indice == 0:
            estilo = "CellHeader"
        elif cabecera:
            estilo = "Cell"
        else:
            estilo = "CellBold" if indice == 0 else "Cell"
        contenido.append([Paragraph(str(celda), ESTILOS[estilo]) for celda in fila])
    tabla_resultado = Table(contenido, colWidths=anchos, repeatRows=1 if cabecera else 0)
    comandos = [
        ("VALIGN", (0, 0), (-1, -1), "TOP"),
        ("GRID", (0, 0), (-1, -1), 0.5, BORDE),
        ("LEFTPADDING", (0, 0), (-1, -1), 6),
        ("RIGHTPADDING", (0, 0), (-1, -1), 6),
        ("TOPPADDING", (0, 0), (-1, -1), 5),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 5),
        ("ALIGN", (0, 0), (-1, -1), alineacion),
    ]
    if cabecera:
        comandos.extend([
            ("BACKGROUND", (0, 0), (-1, 0), OSCURO),
            ("TEXTCOLOR", (0, 0), (-1, 0), colors.white),
        ])
    for indice in range(1, len(filas)):
        if indice % 2 == 0:
            comandos.append(("BACKGROUND", (0, indice), (-1, indice), FONDO))
    tabla_resultado.setStyle(TableStyle(comandos))
    tabla_resultado.hAlign = "LEFT"
    return tabla_resultado


class Manual(BaseDocTemplate):
    def __init__(self, archivo, **argumentos):
        BaseDocTemplate.__init__(self, archivo, **argumentos)
        frame_portada = Frame(2.1 * cm, 1.8 * cm, A4[0] - 4.2 * cm, A4[1] - 3.4 * cm, id="portada")
        frame_contenido = Frame(2.1 * cm, 2.2 * cm, A4[0] - 4.2 * cm, A4[1] - 4.3 * cm, id="contenido")
        self.addPageTemplates([
            PageTemplate(id="Portada", frames=[frame_portada], onPage=self.fondo_portada),
            PageTemplate(id="Contenido", frames=[frame_contenido], onPage=self.decorar),
        ])
        self.titulo_actual = ""

    def fondo_portada(self, lienzo, documento):
        lienzo.saveState()
        lienzo.setFillColor(OSCURO)
        lienzo.rect(0, 0, A4[0], A4[1], stroke=0, fill=1)
        lienzo.setFillColor(LATERAL)
        lienzo.rect(0, A4[1] - 1.5 * cm, A4[0], 1.5 * cm, stroke=0, fill=1)
        lienzo.setFillColor(NARANJA)
        lienzo.rect(0, A4[1] - 1.62 * cm, A4[0], 0.12 * cm, stroke=0, fill=1)
        lienzo.setFillColor(NARANJA)
        lienzo.rect(0, 0, 0.32 * cm, A4[1], stroke=0, fill=1)
        lienzo.restoreState()

    def decorar(self, lienzo, documento):
        lienzo.saveState()
        lienzo.setStrokeColor(BORDE)
        lienzo.setLineWidth(0.6)
        lienzo.line(2.1 * cm, A4[1] - 1.55 * cm, A4[0] - 2.1 * cm, A4[1] - 1.55 * cm)
        lienzo.line(2.1 * cm, 1.65 * cm, A4[0] - 2.1 * cm, 1.65 * cm)
        lienzo.setFont("SegoeUI", 8)
        lienzo.setFillColor(GRIS)
        lienzo.drawString(2.1 * cm, A4[1] - 1.25 * cm, "Sistema de Gestión de Restaurante")
        lienzo.drawRightString(A4[0] - 2.1 * cm, A4[1] - 1.25 * cm, "Manual técnico y de usuario")
        lienzo.drawString(2.1 * cm, 1.12 * cm, "Restaurante La Casita")
        lienzo.drawRightString(A4[0] - 2.1 * cm, 1.12 * cm, "Página " + str(documento.page - 1))
        lienzo.restoreState()

    def afterFlowable(self, flujo):
        if isinstance(flujo, Paragraph) and flujo.style.name == "H1":
            texto = flujo.getPlainText()
            self.notify("TOCEntry", (0, texto, self.page - 1))


def portada():
    elementos = [
        Spacer(1, 0.3 * cm),
        parrafo("R", "PortadaCredito"),
        Spacer(1, 0.1 * cm),
        parrafo("RESTAURANTE LA CASITA", "PortadaCredito"),
        Spacer(1, 1.4 * cm),
        parrafo("Sistema de Gestión<br/>de Restaurante", "PortadaTitulo"),
        parrafo(
            "Aplicación de escritorio para el control de mesas, pedidos y cobros del rol de Mesero.",
            "PortadaSubtitulo",
        ),
        Spacer(1, 0.2 * cm),
        parrafo("Java Swing · SQLite · JDBC · IntelliJ IDEA", "PortadaCredito"),
        Spacer(1, 0.9 * cm),
        figura("captura_principal.png", ancho_max=15.4 * cm, alto_max=8.4 * cm),
        Spacer(1, 1.1 * cm),
        parrafo("Manual técnico y de usuario", "PortadaCredito"),
        parrafo("Mesero: Juan Pérez", "PortadaPie"),
        parrafo("Noviembre de 2026", "PortadaPie"),
        NextPageTemplate("Contenido"),
        PageBreak(),
    ]
    return elementos


def declaracion():
    elementos = [
        parrafo("Declaración de autoría y roles", "H1"),
        parrafo(
            "Los integrantes del equipo declaramos que este proyecto fue elaborado de forma original para la "
            "asignatura de desarrollo de aplicaciones de escritorio. El sistema, la base de datos, los diagramas "
            "y la documentación fueron desarrollados por el equipo y se entregan junto con este manual.",
        ),
        Spacer(1, 0.3 * cm),
        subtitulo("Distribución del trabajo"),
        tabla(
            [
                ["Integrante", "Rol principal", "Actividades realizadas", "Firma"],
                [
                    "Integrante 1<br/>Nombre:<br/>____________",
                    "Análisis y base de datos",
                    "Diseño del script SQL, definición de las tablas mesas y pedidos, llaves foráneas, "
                    "clase ConexionBD, MesaDAO y PedidoDAO. Validación de la integridad de la base.",
                    "<br/><br/>____________",
                ],
                [
                    "Integrante 2<br/>Nombre:<br/>____________",
                    "Lógica de negocio y pruebas",
                    "Implementación de ServicioRestaurante, reglas de ocupación de mesas, entrega de platillos, "
                    "cálculo de totales y registro de pagos. Pruebas de integración con una copia de la base.",
                    "<br/><br/>____________",
                ],
                [
                    "Integrante 3<br/>Nombre:<br/>____________",
                    "Interfaz y documentación",
                    "Diseño de la ventana principal con Swing y GUI Designer, eventos de la interfaz, "
                    "iconos vectoriales, capturas, diagramas UML y elaboración de este manual.",
                    "<br/><br/>____________",
                ],
            ],
            anchos=[4.4 * cm, 3.0 * cm, 7.2 * cm, 2.0 * cm],
        ),
        Spacer(1, 0.8 * cm),
        parrafo(
            "Declaramos que las fuentes consultadas se encuentran citadas en la bibliografía y que el código "
            "entregado funciona de acuerdo con las pruebas descritas en este documento.",
        ),
        Spacer(1, 0.4 * cm),
        parrafo("Lugar y fecha: ____________________________________________"),
        parrafo("Firma de los integrantes: __________________________________"),
        PageBreak(),
    ]
    return elementos


def indice():
    contenido = TableOfContents()
    contenido.levelStyles = [ESTILOS["TOC"]]
    contenido.dotsMinLevel = 0
    return [parrafo("Índice", "H1"), Spacer(1, 0.2 * cm), contenido, PageBreak()]


def introduccion():
    return [
        parrafo("1. Introducción", "H1"),
        parrafo(
            "Un restaurante con varias mesas necesita registrar los platillos que consume cada cliente, conocer "
            "el total de la cuenta en todo momento y liberar la mesa cuando el pago se completa. Cuando estas "
            "tareas se hacen en papel o en hojas de cálculo, es fácil perder un pedido, cobrar dos veces o "
            "equivocar el estado de una mesa.",
        ),
        parrafo(
            "El Sistema de Gestión de Restaurante resuelve ese problema con una aplicación de escritorio para el "
            "rol de Mesero. La aplicación muestra las nueve mesas del salón con un código de color, permite "
            "agregar platillos a la orden, marca los platillos entregados, calcula el total y registra el pago en "
            "efectivo o con terminal. Toda la información se guarda en una base SQLite local, de modo que no se "
            "necesita un servidor externo ni una conexión a internet.",
        ),
        subtitulo("Alcance del sistema"),
        vineta("Control de nueve mesas con estado Disponible u Ocupada."),
        vineta("Registro de pedidos por mesa, con cantidad, precio unitario y subtotal."),
        vineta("Cambio de estado de cada platillo a Pendiente o Entregado."),
        vineta("Cálculo automático del subtotal y del total de la mesa."),
        vineta("Generación de la cuenta y registro del pago en efectivo o con terminal."),
        vineta("Consulta de pedidos activos, ocupación del salón y total pendiente de cobro."),
        vineta("Persistencia local en SQLite y conservación del historial de pagos."),
    ]


def objetivos():
    return [
        parrafo("2. Objetivos", "H1"),
        subtitulo("Objetivo general"),
        parrafo(
            "Desarrollar una aplicación de escritorio en Java que permita al mesero administrar las mesas, los "
            "pedidos y los cobros de un restaurante, utilizando una base de datos SQLite y una interfaz Swing "
            "clara, rápida y adaptada al rol.",
        ),
        subtitulo("Objetivos específicos"),
        vineta("Modelar la información del restaurante con las tablas mesas y pedidos."),
        vineta("Implementar el acceso a datos con JDBC, PreparedStatement y el patrón DAO."),
        vineta("Construir la interfaz con IntelliJ IDEA GUI Designer y componentes Swing."),
        vineta("Aplicar las reglas de negocio de ocupación, entrega, cuenta y pago."),
        vineta("Mostrar los errores de base de datos y de selección con JOptionPane."),
        vineta("Documentar el diseño con diagramas UML y un manual de ejecución."),
        vineta("Empaquetar la aplicación en un JAR que incluya el controlador SQLite."),
    ]


def tecnologias():
    return [
        parrafo("3. Tecnologías utilizadas", "H1"),
        parrafo(
            "El proyecto se desarrolló con herramientas gratuitas y multiplataforma. La siguiente tabla resume "
            "las tecnologías y su función dentro del sistema.",
        ),
        tabla(
            [
                ["Tecnología", "Versión", "Uso en el proyecto"],
                ["Java", "17 o superior", "Lenguaje de programación y ejecución de la aplicación."],
                ["Swing", "Incluido en Java", "Ventana, paneles, tablas, botones y cuadros de diálogo."],
                ["IntelliJ IDEA GUI Designer", "Formulario .form", "Diseño visual de la ventana principal."],
                ["SQLite", "3", "Base de datos local en el archivo restaurante.db."],
                ["sqlite-jdbc", "3.54.1.0", "Controlador JDBC para conectar Java con SQLite."],
                ["PlantUML", "1.2025", "Diagramas de casos de uso, clases, objetos y componentes."],
                ["PowerShell", "5 o superior", "Script de compilación y empaquetado del JAR."],
            ],
            anchos=[4.6 * cm, 3.4 * cm, 8.4 * cm],
        ),
        Spacer(1, 0.3 * cm),
        parrafo(
            "El código fuente se organizó en cuatro paquetes: modelo, datos, negocio y vista. Los nombres de "
            "clases, métodos, variables y archivos están en español, y el código no contiene comentarios.",
        ),
    ]


def arquitectura():
    return [
        parrafo("4. Arquitectura del sistema", "H1"),
        parrafo(
            "La aplicación sigue una arquitectura por capas. La vista no ejecuta SQL: solicita operaciones al "
            "servicio, el servicio aplica las reglas y los DAO ejecutan las sentencias preparadas. Esta separación "
            "facilita las pruebas y permite cambiar la interfaz sin reescribir la lógica.",
        ),
        tabla(
            [
                ["Capa", "Paquete", "Responsabilidad"],
                ["Vista", "vista", "Ventana principal, tabla de la orden, botones, eventos y mensajes al usuario."],
                ["Negocio", "negocio", "Reglas de mesas, pedidos, totales, entrega y pago."],
                ["Acceso a datos", "datos", "Conexión JDBC, DAO de mesas y pedidos, excepción de base de datos."],
                ["Modelo", "modelo", "Clases Mesa, Pedido y Platillo con sus atributos y cálculos."],
            ],
            anchos=[3.2 * cm, 2.6 * cm, 10.6 * cm],
        ),
        Spacer(1, 0.3 * cm),
        subtitulo("Flujo de una operación"),
        parrafo(
            "Cuando el mesero agrega un platillo, el botón de la vista llama a ServicioRestaurante. El servicio "
            "consulta las líneas pendientes de la mesa, decide si incrementa una línea existente o inserta una "
            "nueva y delega la escritura en PedidoDAO. Al terminar, la vista vuelve a leer la orden y actualiza la "
            "tabla y el total. Si ocurre una SQLException, el DAO la convierte en ExcepcionBaseDatos y la vista "
            "muestra un JOptionPane con el mensaje en español.",
        ),
        subtitulo("Paquetes y archivos"),
        tabla(
            [
                ["Archivo", "Función"],
                ["src/modelo/Mesa.java", "Mesa con número, capacidad, estado y ubicación."],
                ["src/modelo/Pedido.java", "Línea de pedido con cantidad, precio, subtotal y pago."],
                ["src/modelo/Platillo.java", "Platillo del menú en memoria, con precio y categoría."],
                ["src/datos/ConexionBD.java", "Localiza restaurante.db y abre conexiones con PRAGMA."],
                ["src/datos/MesaDAO.java", "CRUD de mesas y cambio de estado."],
                ["src/datos/PedidoDAO.java", "CRUD de pedidos, entrega, pago y totales."],
                ["src/negocio/ServicioRestaurante.java", "Reglas de negocio y coordinación de los DAO."],
                ["src/vista/VentanaPrincipal.java", "Interfaz principal y manejo de eventos."],
                ["src/vista/Principal.java", "Método main y arranque de la aplicación."],
                ["src/vista/IconoUsuario.java", "Icono vectorial del usuario en la barra superior."],
            ],
            anchos=[6.2 * cm, 10.2 * cm],
        ),
    ]


def modelo_datos():
    return [
        parrafo("5. Modelo de datos", "H1"),
        parrafo(
            "La base restaurante.db contiene exactamente dos tablas de usuario: mesas y pedidos. Cada fila de "
            "pedidos representa una línea de la orden de una mesa, lo que permite mostrar la tabla de la interfaz "
            "y mantener el CRUD sin tablas adicionales. El índice idx_pedidos_mesa_pagado acelera las consultas "
            "por mesa y estado de pago, pero no es una tabla.",
        ),
        subtitulo("Tabla mesas"),
        tabla(
            [
                ["Campo", "Tipo", "Restricción"],
                ["id_mesa", "INTEGER", "Llave primaria autoincremental."],
                ["numero", "INTEGER", "Obligatorio, único y mayor que cero."],
                ["capacidad", "INTEGER", "Obligatorio y mayor que cero."],
                ["estado", "TEXT", "Disponible u Ocupada."],
                ["ubicacion", "TEXT", "Obligatorio; valor inicial Salón principal."],
            ],
            anchos=[3.4 * cm, 2.8 * cm, 10.2 * cm],
        ),
        Spacer(1, 0.3 * cm),
        subtitulo("Tabla pedidos"),
        tabla(
            [
                ["Campo", "Tipo", "Restricción"],
                ["id_pedido", "INTEGER", "Llave primaria autoincremental."],
                ["id_mesa", "INTEGER", "Llave foránea a mesas.id_mesa con ON DELETE RESTRICT."],
                ["platillo", "TEXT", "Nombre del platillo; obligatorio."],
                ["cantidad", "INTEGER", "Obligatorio y mayor que cero."],
                ["precio_unitario", "REAL", "Mayor o igual que cero."],
                ["subtotal", "REAL", "Mayor o igual que cero; cantidad por precio."],
                ["estado", "TEXT", "Pendiente o Entregado."],
                ["pagado", "INTEGER", "Cero o uno."],
                ["metodo_pago", "TEXT", "Efectivo, Terminal o nulo."],
                ["fecha_hora", "TEXT", "Fecha de creación; valor automático."],
                ["fecha_pago", "TEXT", "Fecha del cobro; nula mientras no se pague."],
            ],
            anchos=[3.4 * cm, 2.8 * cm, 10.2 * cm],
        ),
        Spacer(1, 0.3 * cm),
        parrafo(
            "El script script.sql crea las tablas, el índice, las restricciones CHECK y los datos iniciales: "
            "nueve mesas, con las mesas 1 a 3 ocupadas, y cinco líneas de pedido por un total pendiente de "
            "$625.00. La llave foránea se activa en cada conexión con PRAGMA foreign_keys = ON.",
        ),
    ]


def diagramas():
    elementos = [
        parrafo("6. Diagramas UML", "H1"),
        parrafo(
            "Los diagramas se elaboraron con PlantUML y se entregan en archivos .puml y en imágenes PNG. "
            "El diagrama de casos de uso muestra la interacción del mesero con el sistema y los actores externos.",
        ),
        KeepTogether([
            figura("casos_uso.png", ancho_max=12.6 * cm, alto_max=17.2 * cm),
            parrafo("Figura 1. Diagrama de casos de uso.", "Caption"),
        ]),
        KeepTogether([
            parrafo(
                "El diagrama de clases refleja la separación por capas, las relaciones entre la vista, el servicio, "
                "los DAO y el modelo, y las excepciones que viajan desde la base hasta la interfaz.",
            ),
            figura("clases.png", ancho_max=15.0 * cm, alto_max=20.6 * cm),
            parrafo("Figura 2. Diagrama de clases.", "Caption"),
        ]),
        parrafo(
            "El diagrama de objetos presenta un ejemplo de la mesa 1 con dos líneas de pedido. El diagrama de "
            "componentes muestra la dependencia del controlador SQLite y su inclusión dentro del JAR.",
        ),
        KeepTogether([
            figura("objetos.png", ancho_max=16.0 * cm, alto_max=7.0 * cm),
            parrafo("Figura 3. Diagrama de objetos de la mesa 1.", "Caption"),
        ]),
        KeepTogether([
            figura("componentes.png", ancho_max=11.2 * cm, alto_max=13.0 * cm),
            parrafo("Figura 4. Diagrama de componentes.", "Caption"),
        ]),
    ]
    return elementos


def funcionalidades():
    return [
        parrafo("7. Funcionalidades y reglas de negocio", "H1"),
        parrafo(
            "La interfaz se organiza en una barra lateral oscura, una barra superior con el usuario y un área "
            "central con cuatro tarjetas: Mesas, Orden de la mesa, Platillos y Acciones. Las mesas disponibles se "
            "muestran en verde y las ocupadas en rojo; la mesa seleccionada tiene un borde naranja.",
        ),
        tabla(
            [
                ["Acción del mesero", "Resultado en la aplicación", "Resultado en la base"],
                ["Clic en una mesa disponible", "La mesa pasa a ocupada y se abre su orden.", "UPDATE mesas.estado a Ocupada."],
                ["Clic en una mesa ocupada", "Se muestra la orden pendiente de esa mesa.", "SELECT de pedidos con pagado = 0."],
                ["Agregar un platillo", "Aparece la línea y se recalcula el total.", "INSERT o UPDATE de la línea."],
                ["Marcar platillo como entregado", "La fila cambia a verde y estado Entregado.", "UPDATE pedidos.estado."],
                [
                    "Clic derecho o tecla Supr sobre una fila",
                    "Se confirma y se quita el platillo de la orden.",
                    "DELETE de la línea; la mesa se libera si la orden queda vacía.",
                ],
                ["Generar cuenta", "Se muestra el detalle y el total en un diálogo.", "Sin cambios."],
                ["Registrar pago", "Se elige efectivo o terminal y se confirma.", "UPDATE pagado, metodo_pago y fecha_pago."],
                ["Pago completado", "La mesa vuelve a verde y la orden queda vacía.", "UPDATE mesas.estado a Disponible."],
            ],
            anchos=[4.4 * cm, 7.1 * cm, 6.2 * cm],
        ),
        Spacer(1, 0.3 * cm),
        subtitulo("Reglas principales"),
        vineta("Una mesa disponible se ocupa en el momento en que el mesero hace clic sobre ella."),
        vineta("Agregar el mismo platillo pendiente incrementa la cantidad en lugar de duplicar la línea."),
        vineta("El subtotal se calcula como cantidad por precio unitario y se redondea a dos decimales."),
        vineta("El total de la mesa se obtiene sumando los subtotales de las líneas no pagadas."),
        vineta("El pago conserva el historial: no borra pedidos, solo marca pagado, método y fecha."),
        vineta("Un platillo se puede eliminar con el menú contextual o la tecla Supr, con confirmación previa."),
        vineta("Si al eliminar líneas la orden queda vacía, la mesa se libera automáticamente."),
        vineta("Al registrar el pago, la mesa se libera y queda disponible para un nuevo cliente."),
        vineta("Los errores de base de datos y de selección se informan con JOptionPane."),
        subtitulo("Manejo de errores"),
        parrafo(
            "Los DAO capturan SQLException y lanzan ExcepcionBaseDatos con un mensaje en español. La ventana "
            "captura esa excepción y muestra un cuadro de error con la operación que falló y el detalle técnico. "
            "Los avisos de selección evitan que el usuario marque, elimine un platillo o registre un pago sin "
            "haber elegido una mesa o una fila.",
        ),
    ]


def capturas():
    return [
        parrafo("8. Capturas de la aplicación", "H1"),
        parrafo(
            "Las siguientes capturas corresponden a la aplicación en ejecución sobre una copia de la base. La "
            "primera muestra el estado inicial, con las mesas 1 a 3 ocupadas y la orden de la mesa 1.",
        ),
        KeepTogether([
            figura("captura_principal.png", ancho_max=16.0 * cm, alto_max=9.0 * cm),
            parrafo(
                "Figura 5. Vista general. Mesa 1 ocupada, dos platillos entregados y total de $240.00.",
                "Caption",
            ),
        ]),
        parrafo(
            "Al agregar Tacos al Pastor se crea una línea pendiente. La fila se muestra en naranja, la mesa "
            "continúa ocupada y el total se actualiza de inmediato.",
        ),
        KeepTogether([
            figura("captura_pedido_pendiente.png", ancho_max=16.0 * cm, alto_max=9.0 * cm),
            parrafo(
                "Figura 6. Pedido pendiente agregado a la mesa 1; el total cambia a $325.00.",
                "Caption",
            ),
        ]),
        parrafo(
            "Si el mesero selecciona una mesa disponible, el sistema la ocupa y abre su orden. En la captura se "
            "eligió la mesa 4 y se agregaron dos unidades de Tacos al Pastor.",
        ),
        KeepTogether([
            figura("captura_mesa_ocupada.png", ancho_max=16.0 * cm, alto_max=9.0 * cm),
            parrafo(
                "Figura 7. Mesa 4 ocupada desde el grid, con una línea pendiente por $170.00.",
                "Caption",
            ),
        ]),
        parrafo(
            "El menú contextual de la orden permite eliminar un platillo o marcarlo como entregado sin usar los "
            "botones inferiores. También se puede eliminar la fila seleccionada con la tecla Supr.",
        ),
        KeepTogether([
            figura("captura_menu_orden.png", ancho_max=16.0 * cm, alto_max=9.0 * cm),
            parrafo(
                "Figura 8. Menú contextual sobre la orden de la mesa 1.",
                "Caption",
            ),
        ]),
    ]


def ejecucion():
    return [
        parrafo("9. Manual de ejecución", "H1"),
        subtitulo("Requisitos"),
        vineta("Java 17 o superior instalado y configurado en el PATH."),
        vineta("IntelliJ IDEA para abrir el proyecto y editar el formulario, opcional para ejecutar el JAR."),
        vineta("El archivo restaurante.db junto al JAR o en el directorio de trabajo."),
        subtitulo("Ejecución desde IntelliJ IDEA"),
        parrafo(
            "Abrir la carpeta del proyecto, verificar que el módulo RestauranteApp tenga el JDK y la biblioteca "
            "sqlite-jdbc-3.54.1.0.jar, y ejecutar la clase vista.Principal. El directorio de trabajo debe ser la "
            "raíz del proyecto para encontrar restaurante.db.",
        ),
        subtitulo("Ejecución del JAR"),
        parrafo(
            "Abrir una terminal en la carpeta que contiene RestauranteApp.jar y restaurante.db, y ejecutar el "
            "siguiente comando:",
        ),
        parrafo('<font name="Courier" size="9.5">java -jar RestauranteApp.jar</font>'),
        parrafo(
            "También se puede hacer doble clic sobre el JAR si Java está asociado a los archivos ejecutables JAR. "
            "La clase ConexionBD busca la base primero en el directorio de trabajo y luego junto al JAR.",
        ),
        subtitulo("Construcción del JAR"),
        parrafo(
            "El script empaquetar.ps1 compila las fuentes, extrae el contenido de sqlite-jdbc-3.54.1.0.jar y "
            "genera RestauranteApp.jar con el manifiesto correspondiente. Para ejecutarlo:",
        ),
        parrafo('<font name="Courier" size="9.5">powershell -ExecutionPolicy Bypass -File .\\empaquetar.ps1</font>'),
        parrafo(
            "En IntelliJ IDEA también se puede crear un artefacto JAR desde Project Structure, eligiendo la "
            "clase principal vista.Principal y la opción extract to the target JAR para incluir el controlador "
            "SQLite y sus bibliotecas nativas.",
        ),
        subtitulo("Solución de problemas"),
        tabla(
            [
                ["Mensaje o síntoma", "Causa probable", "Solución"],
                [
                    "No such table: mesas",
                    "La base no está en la carpeta esperada o está vacía.",
                    "Colocar restaurante.db junto al JAR o ejecutar desde la raíz del proyecto y recrear la base con script.sql.",
                ],
                [
                    "No se encontró el controlador JDBC de SQLite",
                    "El JAR se generó sin extraer las clases del controlador.",
                    "Volver a construir con extract to the target JAR o ejecutar empaquetar.ps1.",
                ],
                [
                    "La ventana no muestra las mesas",
                    "Error de conexión o base sin datos iniciales.",
                    "Revisar el mensaje de JOptionPane, la ruta que muestra Configuración y los registros de mesas.",
                ],
                [
                    "El pago no libera la mesa",
                    "La mesa no tenía líneas pendientes o se canceló la confirmación.",
                    "Verificar que la orden tenga platillos y confirmar el pago con Sí.",
                ],
            ],
            anchos=[4.6 * cm, 5.6 * cm, 7.5 * cm],
        ),
        Spacer(1, 0.3 * cm),
    ]


def pruebas():
    return [
        parrafo("10. Pruebas realizadas", "H1"),
        parrafo(
            "Las pruebas se ejecutaron sobre copias de restaurante.db para no alterar la base de entrega. Se "
            "comprobaron las operaciones de las fases 2 y 4, la interfaz de la fase 3 y el JAR de la fase 5.",
        ),
        tabla(
            [
                ["Caso de prueba", "Resultado esperado", "Estado"],
                ["Carga inicial", "9 mesas y 5 pedidos sin errores de llave foránea.", "Correcto"],
                ["Ocupar mesa 4", "Estado Ocupada en la base y botón rojo en la vista.", "Correcto"],
                ["Agregar dos tacos y una horchata", "2 líneas y total de $205.00.", "Correcto"],
                ["Marcar platillo entregado", "Estado Entregado en la base y fila verde.", "Correcto"],
                ["Eliminar un platillo de la orden", "Línea borrada y total recalculado.", "Correcto"],
                ["Liberar mesa sin pedidos", "La mesa vuelve a Disponible al quedar vacía.", "Correcto"],
                ["Pago en efectivo de la mesa 4", "Pago de $205.00 y mesa liberada.", "Correcto"],
                ["Total pendiente global", "$625.00 después del cobro.", "Correcto"],
                ["Eliminar mesa con pedidos", "Operación bloqueada por la llave foránea.", "Correcto"],
                ["Abrir el JAR con la base externa", "Ventana, mesas y orden visibles.", "Correcto"],
                ["JAR con el controlador extraído", "Conexión SQLite sin bibliotecas externas.", "Correcto"],
            ],
            anchos=[5.6 * cm, 8.3 * cm, 3.2 * cm],
        ),
        Spacer(1, 0.3 * cm),
        parrafo(
            "La prueba del servicio imprimió el mensaje PRUEBA DEL SERVICIO CORRECTA y confirmó que la base "
            "original conserva nueve mesas, cinco pedidos y ningún pago. La captura del JAR se tomó con el "
            "archivo ejecutable en una carpeta distinta a la base y con restaurante.db junto al JAR.",
        ),
        parrafo(
            "También se automatizó la eliminación desde la interfaz: con la acción de la tecla Supr y la "
            "confirmación aceptada, la mesa 1 pasó de dos líneas a una con total de $70.00 y, al borrar la "
            "segunda, la mesa volvió a Disponible sin pedidos pendientes.",
        ),
    ]


def mantenimiento():
    return [
        parrafo("11. Mantenimiento y recomendaciones", "H1"),
        subtitulo("Agregar o cambiar platillos"),
        parrafo(
            "El menú se define en el método inicializarMenu de VentanaPrincipal, mediante objetos Platillo con "
            "nombre, precio y categoría. Para agregar un platillo basta con crear una instancia nueva; el botón "
            "Agregar se genera automáticamente. El pedido guarda una copia del nombre y del precio, por lo que "
            "los cambios futuros de precio no alteran las cuentas anteriores.",
        ),
        subtitulo("Cambiar el número de mesas"),
        parrafo(
            "Las mesas se leen de la base. Para agregar una mesa se puede insertar un registro con el script SQL "
            "o ampliar los datos iniciales y volver a ejecutar script.sql en una base nueva. El grid calcula sus "
            "filas a partir de la cantidad de mesas cargadas.",
        ),
        subtitulo("Ampliar la base de datos"),
        parrafo(
            "Si en el futuro se requiere un catálogo de platillos o un historial de clientes, se pueden agregar "
            "tablas nuevas y sus DAO. Las reglas actuales se mantienen: las operaciones críticas deben usar "
            "transacciones y PreparedStatement, y los errores deben llegar a la vista como ExcepcionBaseDatos.",
        ),
        subtitulo("Respaldo de la información"),
        parrafo(
            "La base es un solo archivo. Para respaldarla, cerrar la aplicación y copiar restaurante.db a una "
            "carpeta segura. Para restaurar, reemplazar el archivo por la copia y volver a abrir el sistema.",
        ),
        subtitulo("Recomendaciones"),
        vineta("Respaldar restaurante.db antes de cada jornada o cambio de versión."),
        vineta("No editar la base mientras la aplicación está registrando pagos."),
        vineta("Mantener el JAR y la base en la misma carpeta para simplificar el soporte."),
        vineta("Documentar cualquier cambio de reglas en la sección correspondiente del manual."),
    ]


def conclusiones():
    return [
        parrafo("12. Conclusiones", "H1"),
        parrafo(
            "El sistema cumple con el flujo completo del mesero: abrir una mesa, levantar el pedido, marcar los "
            "platillos entregados, generar la cuenta y registrar el pago. La separación en capas permitió probar "
            "la lógica sin la interfaz y mantener las consultas SQL en un solo lugar.",
        ),
        parrafo(
            "SQLite resultó adecuada para un restaurante pequeño o mediano: no requiere instalación, el respaldo "
            "es un archivo y las operaciones son rápidas en local. Swing, combinado con GUI Designer, permitió "
            "construir una interfaz clara con poco código de infraestructura. El empaquetado del controlador "
            "dentro del JAR simplifica la entrega y evita configuraciones adicionales en el equipo del usuario.",
        ),
        parrafo(
            "Como trabajo futuro se propone agregar autenticación de usuarios, un catálogo de platillos en la "
            "base, impresión de tickets y reportes por rango de fechas. La estructura actual deja preparado el "
            "punto de extensión en la capa de servicio y en los DAO.",
        ),
        Spacer(1, 1.0 * cm),
        parrafo("Fin del manual", "Caption"),
    ]


def main():
    documento = Manual(
        str(SALIDA),
        pagesize=A4,
        leftMargin=2.1 * cm,
        rightMargin=2.1 * cm,
        topMargin=2.0 * cm,
        bottomMargin=2.2 * cm,
        title="Sistema de Gestión de Restaurante",
        author="Equipo de desarrollo",
        subject="Manual técnico y de usuario",
    )
    historia = []
    historia += portada()
    historia += declaracion()
    historia += indice()
    for seccion in [
        introduccion,
        objetivos,
        tecnologias,
        arquitectura,
        modelo_datos,
        diagramas,
        funcionalidades,
        capturas,
        ejecucion,
        pruebas,
        mantenimiento,
        conclusiones,
    ]:
        historia += seccion()
        historia.append(PageBreak())
    documento.multiBuild(historia)
    print("PDF generado:", SALIDA)


if __name__ == "__main__":
    main()
