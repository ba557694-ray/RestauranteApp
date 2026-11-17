package vista;

import datos.ConexionBD;
import datos.ExcepcionBaseDatos;
import modelo.Mesa;
import modelo.Pedido;
import modelo.Platillo;
import negocio.ServicioRestaurante;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class VentanaPrincipal extends JFrame {
    private static final Color COLOR_LATERAL = new Color(0x1E1E2D);
    private static final Color COLOR_LATERAL_SUAVE = new Color(0x2A2A3D);
    private static final Color COLOR_ACENTO = new Color(0xE67E22);
    private static final Color COLOR_FONDO = new Color(0xF5F6FA);
    private static final Color COLOR_TEXTO = new Color(0x1E1E2D);
    private static final Color COLOR_TEXTO_SECUNDARIO = new Color(0x6C6C80);
    private static final Color COLOR_BORDE = new Color(0xE4E6EF);
    private static final Color COLOR_VERDE_FONDO = new Color(0xC8E6C9);
    private static final Color COLOR_VERDE_TEXTO = new Color(0x1B5E20);
    private static final Color COLOR_ROJO_FONDO = new Color(0xFFCDD2);
    private static final Color COLOR_ROJO_TEXTO = new Color(0xB71C1C);
    private static final Color COLOR_VERDE_BOTON = new Color(0x27AE60);
    private static final Color COLOR_AZUL_BOTON = new Color(0x2980B9);
    private static final Color COLOR_MORADO_BOTON = new Color(0x8E44AD);
    private static final Color COLOR_NARANJA_CLARO = new Color(0xFFF3E0);
    private static final Color COLOR_NAV_TEXTO = new Color(0xB8B8C7);
    private static final Color COLOR_TOTAL_FONDO = new Color(0xFAFAFC);

    private JPanel panelPrincipal;
    private JPanel panelLateral;
    private JPanel panelLogo;
    private JLabel etiquetaLogo;
    private JLabel etiquetaNombreRestaurante;
    private JLabel etiquetaEslogan;
    private JPanel panelNavegacion;
    private JButton botonMesas;
    private JButton botonPedidos;
    private JButton botonReportes;
    private JButton botonConfiguracion;
    private JButton botonCerrarSesion;
    private JPanel panelDerecha;
    private JPanel panelSuperior;
    private JLabel etiquetaTitulo;
    private JLabel etiquetaUsuario;
    private JPanel panelCentral;
    private JPanel panelMesas;
    private JLabel etiquetaTituloMesas;
    private JPanel panelBotonesMesas;
    private JPanel panelOrden;
    private JLabel etiquetaTituloOrden;
    private JScrollPane panelDesplazamientoOrden;
    private JTable tablaOrden;
    private JPanel panelTotal;
    private JLabel etiquetaTextoTotal;
    private JLabel etiquetaTotal;
    private JPanel panelPlatillos;
    private JLabel etiquetaTituloPlatillos;
    private JScrollPane panelDesplazamientoPlatillos;
    private JPanel panelListaPlatillos;
    private JPanel panelAcciones;
    private JLabel etiquetaTituloAcciones;
    private JPanel panelBotonesAcciones;
    private JButton botonMarcarEntregado;
    private JButton botonGenerarCuenta;
    private JButton botonRegistrarPago;
    private JButton botonPagoEfectivo;
    private JButton botonPagoTerminal;

    private final ServicioRestaurante servicio = new ServicioRestaurante();
    private final List<Mesa> mesas = new ArrayList<>();
    private final List<Pedido> pedidosVisibles = new ArrayList<>();
    private final Map<Integer, JButton> botonesMesas = new LinkedHashMap<>();
    private DefaultTableModel modeloOrden;
    private JPopupMenu menuOrden;
    private JMenuItem opcionEliminarPlatillo;
    private JMenuItem opcionMarcarEntregado;
    private int numeroMesaSeleccionada;

    public VentanaPrincipal() {
        super("Sistema de Gestión de Restaurante");
        $$$setupUI$$$();
        configurarVentana();
        configurarTabla();
        aplicarEstilos();
        inicializarMenu();
        iniciarServicio();
    }

    private void configurarVentana() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setContentPane(panelPrincipal);
        setSize(new Dimension(1280, 780));
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);
    }

    private void iniciarServicio() {
        try {
            mesas.clear();
            mesas.addAll(servicio.cargarMesas());
        } catch (ExcepcionBaseDatos e) {
            mostrarError("No se pudo abrir la base de datos del restaurante", e);
        }
        crearBotonesMesas();
        configurarEventos();
        if (!mesas.isEmpty()) {
            mostrarMesa(mesas.get(0).getNumero());
        } else {
            etiquetaTituloOrden.setText("Sin mesas registradas");
            actualizarTotal();
        }
    }

    private void configurarTabla() {
        modeloOrden = new DefaultTableModel(new Object[]{"Platillo", "Cantidad", "Estado", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tablaOrden.setModel(modeloOrden);
        tablaOrden.setRowHeight(36);
        tablaOrden.setShowGrid(false);
        tablaOrden.setShowHorizontalLines(true);
        tablaOrden.setShowVerticalLines(false);
        tablaOrden.setIntercellSpacing(new Dimension(0, 1));
        tablaOrden.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaOrden.setFillsViewportHeight(true);
        tablaOrden.setBackground(Color.WHITE);
        tablaOrden.setForeground(COLOR_TEXTO);
        tablaOrden.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaOrden.setGridColor(COLOR_BORDE);
        tablaOrden.setSelectionBackground(COLOR_NARANJA_CLARO);
        tablaOrden.setSelectionForeground(COLOR_TEXTO);

        JTableHeader cabecera = tablaOrden.getTableHeader();
        cabecera.setReorderingAllowed(false);
        cabecera.setPreferredSize(new Dimension(0, 38));
        cabecera.setBackground(COLOR_FONDO);
        cabecera.setForeground(COLOR_TEXTO_SECUNDARIO);
        cabecera.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cabecera.setBorder(new MatteBorder(0, 0, 1, 0, COLOR_BORDE));

        DefaultTableCellRenderer renderizadorTexto = new DefaultTableCellRenderer();
        renderizadorTexto.setBorder(new EmptyBorder(0, 10, 0, 10));
        tablaOrden.getColumnModel().getColumn(0).setCellRenderer(renderizadorTexto);
        tablaOrden.getColumnModel().getColumn(0).setPreferredWidth(300);

        DefaultTableCellRenderer renderizadorCentro = new DefaultTableCellRenderer();
        renderizadorCentro.setHorizontalAlignment(SwingConstants.CENTER);
        renderizadorCentro.setBorder(new EmptyBorder(0, 6, 0, 6));
        tablaOrden.getColumnModel().getColumn(1).setCellRenderer(renderizadorCentro);
        tablaOrden.getColumnModel().getColumn(1).setPreferredWidth(90);

        DefaultTableCellRenderer renderizadorDerecha = new DefaultTableCellRenderer();
        renderizadorDerecha.setHorizontalAlignment(SwingConstants.RIGHT);
        renderizadorDerecha.setBorder(new EmptyBorder(0, 10, 0, 12));
        tablaOrden.getColumnModel().getColumn(3).setCellRenderer(renderizadorDerecha);
        tablaOrden.getColumnModel().getColumn(3).setPreferredWidth(120);

        tablaOrden.getColumnModel().getColumn(2).setCellRenderer(new RenderizadorEstado());
        tablaOrden.getColumnModel().getColumn(2).setPreferredWidth(110);

        panelDesplazamientoOrden.setBorder(null);
        panelDesplazamientoOrden.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panelDesplazamientoOrden.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panelDesplazamientoOrden.getViewport().setBackground(Color.WHITE);
        panelDesplazamientoPlatillos.setBorder(null);
        panelDesplazamientoPlatillos.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panelDesplazamientoPlatillos.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panelDesplazamientoPlatillos.getViewport().setBackground(Color.WHITE);
        configurarMenuOrden();
    }

    private void configurarMenuOrden() {
        menuOrden = new JPopupMenu();
        menuOrden.setBorder(new MatteBorder(1, 1, 1, 1, COLOR_BORDE));

        opcionEliminarPlatillo = new JMenuItem("Eliminar platillo de la orden");
        opcionEliminarPlatillo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        opcionEliminarPlatillo.setForeground(COLOR_ROJO_TEXTO);
        opcionEliminarPlatillo.setBackground(Color.WHITE);
        opcionEliminarPlatillo.setBorder(new EmptyBorder(8, 14, 8, 18));
        opcionEliminarPlatillo.addActionListener(evento -> eliminarPlatilloSeleccionado());
        menuOrden.add(opcionEliminarPlatillo);

        opcionMarcarEntregado = new JMenuItem("Marcar como entregado");
        opcionMarcarEntregado.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        opcionMarcarEntregado.setForeground(COLOR_TEXTO);
        opcionMarcarEntregado.setBackground(Color.WHITE);
        opcionMarcarEntregado.setBorder(new EmptyBorder(8, 14, 8, 18));
        opcionMarcarEntregado.addActionListener(evento -> marcarPlatilloEntregado());
        menuOrden.add(opcionMarcarEntregado);

        tablaOrden.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evento) {
                mostrarMenuOrden(evento);
            }

            @Override
            public void mouseReleased(MouseEvent evento) {
                mostrarMenuOrden(evento);
            }
        });

        tablaOrden.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "eliminarPlatillo");
        tablaOrden.getActionMap().put("eliminarPlatillo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent evento) {
                eliminarPlatilloSeleccionado();
            }
        });
    }

    private void mostrarMenuOrden(MouseEvent evento) {
        if (!evento.isPopupTrigger()) {
            return;
        }
        int fila = tablaOrden.rowAtPoint(evento.getPoint());
        if (fila < 0 || fila >= pedidosVisibles.size()) {
            return;
        }
        tablaOrden.setRowSelectionInterval(fila, fila);
        actualizarOpcionesMenu();
        menuOrden.show(tablaOrden, evento.getX(), evento.getY());
    }

    private void actualizarOpcionesMenu() {
        int fila = tablaOrden.getSelectedRow();
        boolean haySeleccion = fila >= 0 && fila < pedidosVisibles.size();
        boolean entregado = haySeleccion
                && Pedido.ENTREGADO.equalsIgnoreCase(pedidosVisibles.get(fila).getEstado());
        opcionEliminarPlatillo.setEnabled(haySeleccion);
        opcionMarcarEntregado.setEnabled(haySeleccion && !entregado);
    }

    private void aplicarEstilos() {
        panelPrincipal.setBackground(COLOR_FONDO);
        panelLateral.setBackground(COLOR_LATERAL);
        panelLogo.setBackground(COLOR_LATERAL);
        panelNavegacion.setBackground(COLOR_LATERAL);
        panelDerecha.setBackground(COLOR_FONDO);
        panelCentral.setBackground(COLOR_FONDO);

        etiquetaLogo.setForeground(Color.WHITE);
        etiquetaLogo.setHorizontalAlignment(SwingConstants.CENTER);
        etiquetaNombreRestaurante.setForeground(Color.WHITE);
        etiquetaNombreRestaurante.setHorizontalAlignment(SwingConstants.CENTER);
        etiquetaEslogan.setForeground(COLOR_NAV_TEXTO);
        etiquetaEslogan.setHorizontalAlignment(SwingConstants.CENTER);

        panelSuperior.setBackground(Color.WHITE);
        panelSuperior.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, COLOR_BORDE),
                new EmptyBorder(16, 24, 16, 24)
        ));
        etiquetaTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        etiquetaTitulo.setForeground(COLOR_TEXTO);
        etiquetaUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        etiquetaUsuario.setForeground(COLOR_TEXTO_SECUNDARIO);
        etiquetaUsuario.setIcon(new IconoUsuario(20, COLOR_TEXTO_SECUNDARIO));
        etiquetaUsuario.setIconTextGap(8);

        Border bordeTarjeta = new MatteBorder(1, 1, 1, 1, COLOR_BORDE);
        panelMesas.setBackground(Color.WHITE);
        panelMesas.setBorder(bordeTarjeta);
        panelOrden.setBackground(Color.WHITE);
        panelOrden.setBorder(bordeTarjeta);
        panelPlatillos.setBackground(Color.WHITE);
        panelPlatillos.setBorder(bordeTarjeta);
        panelAcciones.setBackground(Color.WHITE);
        panelAcciones.setBorder(bordeTarjeta);

        configurarTituloTarjeta(etiquetaTituloMesas);
        configurarTituloTarjeta(etiquetaTituloOrden);
        configurarTituloTarjeta(etiquetaTituloPlatillos);
        configurarTituloTarjeta(etiquetaTituloAcciones);

        panelBotonesMesas.setBackground(Color.WHITE);
        panelBotonesMesas.setBorder(new EmptyBorder(0, 16, 16, 16));
        panelListaPlatillos.setBackground(Color.WHITE);
        panelListaPlatillos.setBorder(new EmptyBorder(0, 16, 16, 16));
        panelListaPlatillos.setLayout(new BoxLayout(panelListaPlatillos, BoxLayout.Y_AXIS));
        panelBotonesAcciones.setBackground(Color.WHITE);
        panelBotonesAcciones.setBorder(new EmptyBorder(0, 16, 16, 16));

        panelTotal.setBackground(COLOR_TOTAL_FONDO);
        panelTotal.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, COLOR_BORDE),
                new EmptyBorder(10, 16, 12, 16)
        ));
        etiquetaTextoTotal.setFont(new Font("Segoe UI", Font.BOLD, 15));
        etiquetaTextoTotal.setForeground(COLOR_TEXTO_SECUNDARIO);
        etiquetaTotal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        etiquetaTotal.setForeground(COLOR_ACENTO);
        etiquetaTotal.setHorizontalAlignment(SwingConstants.RIGHT);

        estilizarBotonNavegacion(botonMesas);
        estilizarBotonNavegacion(botonPedidos);
        estilizarBotonNavegacion(botonReportes);
        estilizarBotonNavegacion(botonConfiguracion);
        estilizarBotonCerrarSesion(botonCerrarSesion);
        marcarBotonActivo(botonMesas);

        estilizarBotonAccion(botonMarcarEntregado, COLOR_ACENTO);
        estilizarBotonAccion(botonGenerarCuenta, COLOR_LATERAL);
        estilizarBotonAccion(botonRegistrarPago, COLOR_VERDE_BOTON);
        estilizarBotonAccion(botonPagoEfectivo, COLOR_AZUL_BOTON);
        estilizarBotonAccion(botonPagoTerminal, COLOR_MORADO_BOTON);
    }

    private void configurarTituloTarjeta(JLabel etiqueta) {
        etiqueta.setFont(new Font("Segoe UI", Font.BOLD, 16));
        etiqueta.setForeground(COLOR_TEXTO);
        etiqueta.setBorder(new EmptyBorder(16, 16, 10, 16));
    }

    private void estilizarBotonNavegacion(JButton boton) {
        boton.setOpaque(true);
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(true);
        boton.setFocusPainted(false);
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        boton.setForeground(COLOR_NAV_TEXTO);
        boton.setBackground(COLOR_LATERAL);
        boton.setBorder(new EmptyBorder(12, 20, 12, 12));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evento) {
                if (boton != botonMesas) {
                    boton.setBackground(COLOR_LATERAL_SUAVE);
                    boton.setForeground(Color.WHITE);
                }
            }

            @Override
            public void mouseExited(MouseEvent evento) {
                if (boton != botonMesas) {
                    boton.setBackground(COLOR_LATERAL);
                    boton.setForeground(COLOR_NAV_TEXTO);
                }
            }
        });
    }

    private void estilizarBotonCerrarSesion(JButton boton) {
        boton.setOpaque(true);
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(true);
        boton.setFocusPainted(false);
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        boton.setForeground(COLOR_NAV_TEXTO);
        boton.setBackground(COLOR_LATERAL);
        boton.setBorder(new EmptyBorder(12, 20, 12, 12));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evento) {
                boton.setBackground(new Color(0x7F1D1D));
                boton.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent evento) {
                boton.setBackground(COLOR_LATERAL);
                boton.setForeground(COLOR_NAV_TEXTO);
            }
        });
    }

    private void marcarBotonActivo(JButton boton) {
        boton.setBackground(COLOR_LATERAL_SUAVE);
        boton.setForeground(Color.WHITE);
        boton.setBorder(new CompoundBorder(
                new MatteBorder(0, 3, 0, 0, COLOR_ACENTO),
                new EmptyBorder(12, 17, 12, 12)
        ));
    }

    private void estilizarBotonAccion(JButton boton, Color colorFondo) {
        boton.setOpaque(true);
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(true);
        boton.setFocusPainted(false);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        boton.setForeground(Color.WHITE);
        boton.setBackground(colorFondo);
        boton.setBorder(new EmptyBorder(12, 18, 12, 18));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evento) {
                boton.setBackground(ajustarColor(colorFondo, 1.14f));
            }

            @Override
            public void mouseExited(MouseEvent evento) {
                boton.setBackground(colorFondo);
            }
        });
    }

    private void inicializarMenu() {
        List<Platillo> menu = new ArrayList<>();
        menu.add(new Platillo("Tacos al Pastor", 85.00, "Platillo fuerte"));
        menu.add(new Platillo("Enchiladas Verdes", 120.00, "Platillo fuerte"));
        menu.add(new Platillo("Pozole Rojo", 110.00, "Platillo fuerte"));
        menu.add(new Platillo("Chiles en Nogada", 145.00, "Platillo fuerte"));
        menu.add(new Platillo("Tostadas de Tinga", 75.00, "Entrada"));
        menu.add(new Platillo("Flan Napolitano", 45.00, "Postre"));
        menu.add(new Platillo("Agua de Horchata", 35.00, "Bebida"));
        menu.add(new Platillo("Café de Olla", 30.00, "Bebida"));
        for (Platillo platillo : menu) {
            agregarFilaPlatillo(platillo);
        }
    }

    private void agregarFilaPlatillo(Platillo platillo) {
        JPanel fila = new JPanel(new BorderLayout(12, 0));
        fila.setBackground(Color.WHITE);
        fila.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, COLOR_BORDE),
                new EmptyBorder(9, 8, 9, 8)
        ));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel etiqueta = new JLabel("<html><b>" + platillo.getNombre()
                + "</b><br><span style='color:#6C6C80'>"
                + platillo.getPrecioFormateado() + " · " + platillo.getCategoria()
                + "</span></html>");
        etiqueta.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        etiqueta.setForeground(COLOR_TEXTO);
        fila.add(etiqueta, BorderLayout.CENTER);

        JButton botonAgregar = new JButton("Agregar");
        botonAgregar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        botonAgregar.setForeground(COLOR_ACENTO);
        botonAgregar.setBackground(COLOR_NARANJA_CLARO);
        botonAgregar.setOpaque(true);
        botonAgregar.setBorderPainted(false);
        botonAgregar.setContentAreaFilled(true);
        botonAgregar.setFocusPainted(false);
        botonAgregar.setBorder(new EmptyBorder(7, 12, 7, 12));
        botonAgregar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botonAgregar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evento) {
                botonAgregar.setBackground(new Color(0xFFE0B2));
            }

            @Override
            public void mouseExited(MouseEvent evento) {
                botonAgregar.setBackground(COLOR_NARANJA_CLARO);
            }
        });
        botonAgregar.addActionListener(evento -> agregarPlatillo(platillo));
        fila.add(botonAgregar, BorderLayout.EAST);
        panelListaPlatillos.add(fila);
    }

    private void crearBotonesMesas() {
        botonesMesas.clear();
        panelBotonesMesas.removeAll();
        int columnas = 3;
        int filas = Math.max(1, (int) Math.ceil(mesas.size() / (double) columnas));
        panelBotonesMesas.setLayout(new GridLayout(filas, columnas, 10, 10));
        for (Mesa mesa : mesas) {
            JButton botonMesa = new JButton();
            botonMesa.setFont(new Font("Segoe UI", Font.BOLD, 14));
            botonMesa.setFocusPainted(false);
            botonMesa.setBorderPainted(false);
            botonMesa.setContentAreaFilled(true);
            botonMesa.setOpaque(true);
            botonMesa.setCursor(new Cursor(Cursor.HAND_CURSOR));
            int numero = mesa.getNumero();
            botonMesa.addActionListener(evento -> seleccionarMesa(numero));
            botonesMesas.put(numero, botonMesa);
            panelBotonesMesas.add(botonMesa);
        }
        for (Mesa mesa : mesas) {
            actualizarBotonMesa(mesa.getNumero());
        }
        panelBotonesMesas.revalidate();
        panelBotonesMesas.repaint();
    }

    private void actualizarBotonMesa(int numero) {
        JButton botonMesa = botonesMesas.get(numero);
        Mesa mesa = servicio.obtenerMesa(numero);
        if (botonMesa == null || mesa == null) {
            return;
        }
        boolean ocupada = !mesa.estaDisponible();
        String estado = mesa.getEstado();
        botonMesa.setText("<html><center><b>Mesa " + numero
                + "</b><br><span style='font-size:9px'>" + estado
                + " · " + mesa.getCapacidad() + " pers.</span></center></html>");
        botonMesa.setBackground(ocupada ? COLOR_ROJO_FONDO : COLOR_VERDE_FONDO);
        botonMesa.setForeground(ocupada ? COLOR_ROJO_TEXTO : COLOR_VERDE_TEXTO);
        botonMesa.setToolTipText("Mesa " + numero + " · " + mesa.getUbicacion() + " · " + estado);
    }

    private void seleccionarMesa(int numero) {
        Mesa mesa = servicio.obtenerMesa(numero);
        if (mesa == null) {
            return;
        }
        if (mesa.estaDisponible()) {
            try {
                servicio.abrirMesa(numero);
            } catch (ExcepcionBaseDatos e) {
                mostrarError("No se pudo ocupar la mesa " + numero, e);
                return;
            }
            actualizarBotonMesa(numero);
        }
        mostrarMesa(numero);
    }

    private void mostrarMesa(int numero) {
        Mesa mesa = servicio.obtenerMesa(numero);
        if (mesa == null) {
            return;
        }
        numeroMesaSeleccionada = numero;
        etiquetaTituloOrden.setText("Orden de la Mesa " + numero);
        resaltarMesaSeleccionada();
        cargarPedidos();
    }

    private void resaltarMesaSeleccionada() {
        for (Map.Entry<Integer, JButton> entrada : botonesMesas.entrySet()) {
            JButton botonMesa = entrada.getValue();
            if (entrada.getKey() == numeroMesaSeleccionada) {
                botonMesa.setBorder(new CompoundBorder(
                        new MatteBorder(3, 3, 3, 3, COLOR_ACENTO),
                        new EmptyBorder(8, 8, 8, 8)
                ));
            } else {
                botonMesa.setBorder(new EmptyBorder(11, 11, 11, 11));
            }
        }
    }

    private void cargarPedidos() {
        modeloOrden.setRowCount(0);
        pedidosVisibles.clear();
        if (numeroMesaSeleccionada == 0) {
            actualizarTotal();
            return;
        }
        try {
            pedidosVisibles.addAll(servicio.obtenerPedidosMesa(numeroMesaSeleccionada));
        } catch (ExcepcionBaseDatos e) {
            mostrarError("No se pudo cargar la orden de la mesa " + numeroMesaSeleccionada, e);
            actualizarTotal();
            return;
        }
        for (Pedido pedido : pedidosVisibles) {
            modeloOrden.addRow(new Object[]{
                    pedido.getPlatillo(),
                    pedido.getCantidad(),
                    pedido.getEstado(),
                    formatearMoneda(pedido.getSubtotal())
            });
        }
        actualizarTotal();
        tablaOrden.clearSelection();
    }

    private void actualizarTotal() {
        double total = 0.0;
        for (Pedido pedido : pedidosVisibles) {
            total += pedido.getSubtotal();
        }
        etiquetaTotal.setText(formatearMoneda(total));
    }

    private void agregarPlatillo(Platillo platillo) {
        if (numeroMesaSeleccionada == 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione una mesa antes de agregar platillos.",
                    "Mesa no seleccionada",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        try {
            servicio.agregarPlatillo(numeroMesaSeleccionada, platillo);
        } catch (ExcepcionBaseDatos e) {
            mostrarError("No se pudo agregar el platillo a la orden", e);
            return;
        }
        actualizarBotonMesa(numeroMesaSeleccionada);
        cargarPedidos();
    }

    private void marcarPlatilloEntregado() {
        int filaSeleccionada = tablaOrden.getSelectedRow();
        if (filaSeleccionada < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un platillo de la orden para marcarlo como entregado.",
                    "Platillo no seleccionado",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        if (filaSeleccionada >= pedidosVisibles.size()) {
            return;
        }
        Pedido pedido = pedidosVisibles.get(filaSeleccionada);
        if (Pedido.ENTREGADO.equalsIgnoreCase(pedido.getEstado())) {
            JOptionPane.showMessageDialog(
                    this,
                    "El platillo " + pedido.getPlatillo() + " ya fue entregado.",
                    "Platillo entregado",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        try {
            servicio.marcarEntregado(pedido);
        } catch (ExcepcionBaseDatos e) {
            mostrarError("No se pudo marcar el platillo como entregado", e);
            return;
        }
        cargarPedidos();
        if (filaSeleccionada < tablaOrden.getRowCount()) {
            tablaOrden.setRowSelectionInterval(filaSeleccionada, filaSeleccionada);
        }
    }

    private void eliminarPlatilloSeleccionado() {
        int filaSeleccionada = tablaOrden.getSelectedRow();
        if (filaSeleccionada < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un platillo de la orden para eliminarlo.",
                    "Platillo no seleccionado",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        if (filaSeleccionada >= pedidosVisibles.size()) {
            return;
        }
        Pedido pedido = pedidosVisibles.get(filaSeleccionada);
        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar " + pedido.getCantidad() + " x " + pedido.getPlatillo()
                        + " de la orden de la mesa " + numeroMesaSeleccionada + "?",
                "Eliminar platillo",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }
        boolean mesaVacia;
        try {
            servicio.eliminarPedido(pedido);
            mesaVacia = servicio.liberarMesaSiEstaVacia(numeroMesaSeleccionada);
        } catch (ExcepcionBaseDatos e) {
            mostrarError("No se pudo eliminar el platillo de la orden", e);
            return;
        }
        actualizarBotonMesa(numeroMesaSeleccionada);
        cargarPedidos();
        if (tablaOrden.getRowCount() > 0) {
            int nuevaFila = Math.min(filaSeleccionada, tablaOrden.getRowCount() - 1);
            tablaOrden.setRowSelectionInterval(nuevaFila, nuevaFila);
        }
        if (mesaVacia) {
            JOptionPane.showMessageDialog(
                    this,
                    "La orden de la mesa " + numeroMesaSeleccionada + " quedó vacía y la mesa está disponible.",
                    "Mesa liberada",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private void generarCuenta() {
        if (pedidosVisibles.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "La mesa " + numeroMesaSeleccionada + " no tiene platillos registrados.",
                    "Cuenta vacía",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        StringBuilder detalle = new StringBuilder();
        detalle.append("Mesa ").append(numeroMesaSeleccionada).append("\n\n");
        double total = 0.0;
        for (Pedido pedido : pedidosVisibles) {
            detalle.append(pedido.getCantidad())
                    .append(" x ")
                    .append(pedido.getPlatillo())
                    .append("    ")
                    .append(formatearMoneda(pedido.getSubtotal()))
                    .append("\n");
            total += pedido.getSubtotal();
        }
        detalle.append("\nTotal: ").append(formatearMoneda(total));
        JOptionPane.showMessageDialog(
                this,
                detalle.toString(),
                "Cuenta de la Mesa " + numeroMesaSeleccionada,
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void registrarPagoSeleccionado() {
        Object[] opciones = {"Efectivo", "Terminal", "Cancelar"};
        int opcion = JOptionPane.showOptionDialog(
                this,
                "Seleccione el método de pago de la mesa " + numeroMesaSeleccionada + ".",
                "Registrar pago",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]
        );
        if (opcion == 0) {
            registrarPago(Pedido.EFECTIVO);
        } else if (opcion == 1) {
            registrarPago(Pedido.TERMINAL);
        }
    }

    private void registrarPago(String metodoPago) {
        if (numeroMesaSeleccionada == 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione una mesa antes de registrar el pago.",
                    "Mesa no seleccionada",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        if (pedidosVisibles.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "La mesa " + numeroMesaSeleccionada + " no tiene una cuenta pendiente.",
                    "Sin cuenta pendiente",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        double total = 0.0;
        for (Pedido pedido : pedidosVisibles) {
            total += pedido.getSubtotal();
        }
        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Registrar el pago de " + formatearMoneda(total)
                        + " para la mesa " + numeroMesaSeleccionada
                        + " con método " + metodoPago + "?",
                "Confirmar pago",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }
        double totalPagado;
        try {
            totalPagado = servicio.registrarPago(numeroMesaSeleccionada, metodoPago);
        } catch (ExcepcionBaseDatos e) {
            mostrarError("No se pudo registrar el pago de la mesa " + numeroMesaSeleccionada, e);
            return;
        }
        if (totalPagado <= 0.0) {
            JOptionPane.showMessageDialog(
                    this,
                    "La mesa " + numeroMesaSeleccionada + " no tiene una cuenta pendiente.",
                    "Sin cuenta pendiente",
                    JOptionPane.INFORMATION_MESSAGE
            );
            cargarPedidos();
            return;
        }
        actualizarBotonMesa(numeroMesaSeleccionada);
        cargarPedidos();
        JOptionPane.showMessageDialog(
                this,
                "Pago de " + formatearMoneda(totalPagado) + " registrado con método " + metodoPago
                        + ".\nLa mesa " + numeroMesaSeleccionada + " quedó disponible.",
                "Pago completado",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void pagarEnEfectivo() {
        registrarPago(Pedido.EFECTIVO);
    }

    private void pagarConTerminal() {
        registrarPago(Pedido.TERMINAL);
    }

    private void recargarMesas() {
        try {
            mesas.clear();
            mesas.addAll(servicio.cargarMesas());
        } catch (ExcepcionBaseDatos e) {
            mostrarError("No se pudieron actualizar las mesas", e);
            return;
        }
        int anterior = numeroMesaSeleccionada;
        crearBotonesMesas();
        if (servicio.obtenerMesa(anterior) != null) {
            mostrarMesa(anterior);
        } else if (!mesas.isEmpty()) {
            mostrarMesa(mesas.get(0).getNumero());
        } else {
            numeroMesaSeleccionada = 0;
            pedidosVisibles.clear();
            modeloOrden.setRowCount(0);
            etiquetaTituloOrden.setText("Sin mesas registradas");
            actualizarTotal();
        }
    }

    private void mostrarPedidosActivos() {
        List<Pedido> activos;
        try {
            activos = servicio.obtenerPedidosActivos();
        } catch (ExcepcionBaseDatos e) {
            mostrarError("No se pudieron consultar los pedidos activos", e);
            return;
        }
        if (activos.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "No hay pedidos activos en este momento.",
                    "Pedidos activos",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        Map<Integer, Integer> numerosPorId = new HashMap<>();
        for (Mesa mesa : mesas) {
            numerosPorId.put(mesa.getIdMesa(), mesa.getNumero());
        }
        Map<Integer, List<Pedido>> pedidosPorMesa = new TreeMap<>();
        for (Pedido pedido : activos) {
            int numero = numerosPorId.getOrDefault(pedido.getIdMesa(), 0);
            pedidosPorMesa.computeIfAbsent(numero, clave -> new ArrayList<>()).add(pedido);
        }
        StringBuilder resumen = new StringBuilder();
        for (Map.Entry<Integer, List<Pedido>> entrada : pedidosPorMesa.entrySet()) {
            double totalMesa = 0.0;
            for (Pedido pedido : entrada.getValue()) {
                totalMesa += pedido.getSubtotal();
            }
            resumen.append("Mesa ").append(entrada.getKey())
                    .append(": ")
                    .append(entrada.getValue().size())
                    .append(" platillo(s), total ")
                    .append(formatearMoneda(totalMesa))
                    .append("\n");
        }
        JOptionPane.showMessageDialog(
                this,
                resumen.toString(),
                "Pedidos activos",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void mostrarReportes() {
        double totalPendiente;
        try {
            totalPendiente = servicio.calcularTotalPendiente();
        } catch (ExcepcionBaseDatos e) {
            mostrarError("No se pudo generar el reporte general", e);
            return;
        }
        int ocupadas = servicio.contarMesasOcupadas();
        int disponibles = mesas.size() - ocupadas;
        String reporte = "Mesas registradas: " + mesas.size()
                + "\nMesas ocupadas: " + ocupadas
                + "\nMesas disponibles: " + disponibles
                + "\nTotal pendiente de cobro: " + formatearMoneda(totalPendiente);
        JOptionPane.showMessageDialog(
                this,
                reporte,
                "Reporte general",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void abrirConfiguracion() {
        ConexionBD conexionBD = new ConexionBD();
        JOptionPane.showMessageDialog(
                this,
                "Sistema de Gestión de Restaurante\n"
                        + "Mesero: Juan Pérez\n"
                        + "Base de datos local: " + conexionBD.obtenerRutaBaseDatos(),
                "Configuración",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void cerrarSesion() {
        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Desea cerrar la sesión y salir del sistema?",
                "Cerrar sesión",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (confirmacion == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }

    private void configurarEventos() {
        botonMesas.addActionListener(evento -> recargarMesas());
        botonPedidos.addActionListener(evento -> mostrarPedidosActivos());
        botonReportes.addActionListener(evento -> mostrarReportes());
        botonConfiguracion.addActionListener(evento -> abrirConfiguracion());
        botonCerrarSesion.addActionListener(evento -> cerrarSesion());
        botonMarcarEntregado.addActionListener(evento -> marcarPlatilloEntregado());
        botonGenerarCuenta.addActionListener(evento -> generarCuenta());
        botonRegistrarPago.addActionListener(evento -> registrarPagoSeleccionado());
        botonPagoEfectivo.addActionListener(evento -> pagarEnEfectivo());
        botonPagoTerminal.addActionListener(evento -> pagarConTerminal());
    }

    private void mostrarError(String mensaje, Exception excepcion) {
        JOptionPane.showMessageDialog(
                this,
                mensaje + "\n" + excepcion.getMessage(),
                "Error de base de datos",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private String formatearMoneda(double valor) {
        return String.format(Locale.ROOT, "$%,.2f", valor);
    }

    private Color ajustarColor(Color color, float factor) {
        int rojo = Math.min(255, Math.max(0, (int) (color.getRed() * factor)));
        int verde = Math.min(255, Math.max(0, (int) (color.getGreen() * factor)));
        int azul = Math.min(255, Math.max(0, (int) (color.getBlue() * factor)));
        return new Color(rojo, verde, azul);
    }

    private static class RenderizadorEstado extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable tabla,
                Object valor,
                boolean seleccionado,
                boolean enfocado,
                int fila,
                int columna
        ) {
            super.getTableCellRendererComponent(tabla, valor, seleccionado, enfocado, fila, columna);
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setBorder(new EmptyBorder(0, 8, 0, 8));
            if (Pedido.ENTREGADO.equalsIgnoreCase(String.valueOf(valor))) {
                setBackground(seleccionado ? COLOR_NARANJA_CLARO : COLOR_VERDE_FONDO);
                setForeground(COLOR_VERDE_TEXTO);
            } else {
                setBackground(COLOR_NARANJA_CLARO);
                setForeground(COLOR_ACENTO);
            }
            setOpaque(true);
            return this;
        }
    }

    private void $$$setupUI$$$() {
        panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BorderLayout(0, 0));
        panelPrincipal.setBackground(new Color(16119546));
        panelPrincipal.setMinimumSize(new Dimension(1100, 700));
        panelPrincipal.setPreferredSize(new Dimension(1280, 780));

        panelLateral = new JPanel();
        panelLateral.setLayout(new BorderLayout(0, 0));
        panelLateral.setBackground(new Color(1973805));
        panelLateral.setMinimumSize(new Dimension(230, -1));
        panelLateral.setPreferredSize(new Dimension(230, -1));
        panelPrincipal.add(panelLateral, BorderLayout.WEST);

        panelLogo = new JPanel();
        panelLogo.setLayout(new GridBagLayout());
        panelLogo.setBackground(new Color(1973805));
        panelLogo.setBorder(new EmptyBorder(28, 18, 18, 18));
        panelLateral.add(panelLogo, BorderLayout.NORTH);

        etiquetaLogo = new JLabel();
        etiquetaLogo.setFont(new Font("Segoe UI", Font.BOLD, 40));
        etiquetaLogo.setForeground(new Color(15105570));
        etiquetaLogo.setHorizontalAlignment(SwingConstants.CENTER);
        etiquetaLogo.setText("R");
        panelLogo.add(etiquetaLogo, new GridBagConstraints(
                0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0
        ));

        etiquetaNombreRestaurante = new JLabel();
        etiquetaNombreRestaurante.setFont(new Font("Segoe UI", Font.BOLD, 18));
        etiquetaNombreRestaurante.setForeground(new Color(16777215));
        etiquetaNombreRestaurante.setHorizontalAlignment(SwingConstants.CENTER);
        etiquetaNombreRestaurante.setText("RESTAURANTE");
        panelLogo.add(etiquetaNombreRestaurante, new GridBagConstraints(
                0, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0
        ));

        etiquetaEslogan = new JLabel();
        etiquetaEslogan.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        etiquetaEslogan.setForeground(new Color(12105927));
        etiquetaEslogan.setHorizontalAlignment(SwingConstants.CENTER);
        etiquetaEslogan.setText("LA CASITA");
        panelLogo.add(etiquetaEslogan, new GridBagConstraints(
                0, 2, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0
        ));

        panelNavegacion = new JPanel();
        panelNavegacion.setLayout(new GridBagLayout());
        panelNavegacion.setBackground(new Color(1973805));
        panelNavegacion.setBorder(new EmptyBorder(0, 14, 18, 14));
        panelLateral.add(panelNavegacion, BorderLayout.CENTER);

        botonMesas = new JButton();
        botonMesas.setBackground(new Color(1973805));
        botonMesas.setBorderPainted(false);
        botonMesas.setContentAreaFilled(true);
        botonMesas.setFocusPainted(false);
        botonMesas.setFont(new Font("Segoe UI", Font.BOLD, 14));
        botonMesas.setForeground(new Color(12105927));
        botonMesas.setHorizontalAlignment(SwingConstants.LEFT);
        botonMesas.setPreferredSize(new Dimension(-1, 44));
        botonMesas.setText("Mesas");
        panelNavegacion.add(botonMesas, new GridBagConstraints(
                0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0
        ));

        botonPedidos = new JButton();
        botonPedidos.setBackground(new Color(1973805));
        botonPedidos.setBorderPainted(false);
        botonPedidos.setContentAreaFilled(true);
        botonPedidos.setFocusPainted(false);
        botonPedidos.setFont(new Font("Segoe UI", Font.BOLD, 14));
        botonPedidos.setForeground(new Color(12105927));
        botonPedidos.setHorizontalAlignment(SwingConstants.LEFT);
        botonPedidos.setPreferredSize(new Dimension(-1, 44));
        botonPedidos.setText("Pedidos");
        panelNavegacion.add(botonPedidos, new GridBagConstraints(
                0, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0
        ));

        botonReportes = new JButton();
        botonReportes.setBackground(new Color(1973805));
        botonReportes.setBorderPainted(false);
        botonReportes.setContentAreaFilled(true);
        botonReportes.setFocusPainted(false);
        botonReportes.setFont(new Font("Segoe UI", Font.BOLD, 14));
        botonReportes.setForeground(new Color(12105927));
        botonReportes.setHorizontalAlignment(SwingConstants.LEFT);
        botonReportes.setPreferredSize(new Dimension(-1, 44));
        botonReportes.setText("Reportes");
        panelNavegacion.add(botonReportes, new GridBagConstraints(
                0, 2, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0
        ));

        botonConfiguracion = new JButton();
        botonConfiguracion.setBackground(new Color(1973805));
        botonConfiguracion.setBorderPainted(false);
        botonConfiguracion.setContentAreaFilled(true);
        botonConfiguracion.setFocusPainted(false);
        botonConfiguracion.setFont(new Font("Segoe UI", Font.BOLD, 14));
        botonConfiguracion.setForeground(new Color(12105927));
        botonConfiguracion.setHorizontalAlignment(SwingConstants.LEFT);
        botonConfiguracion.setPreferredSize(new Dimension(-1, 44));
        botonConfiguracion.setText("Configuración");
        panelNavegacion.add(botonConfiguracion, new GridBagConstraints(
                0, 3, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0
        ));

        JPanel espaciadorNavegacion = new JPanel();
        espaciadorNavegacion.setBackground(new Color(1973805));
        panelNavegacion.add(espaciadorNavegacion, new GridBagConstraints(
                0, 4, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        ));

        botonCerrarSesion = new JButton();
        botonCerrarSesion.setBackground(new Color(1973805));
        botonCerrarSesion.setBorderPainted(false);
        botonCerrarSesion.setContentAreaFilled(true);
        botonCerrarSesion.setFocusPainted(false);
        botonCerrarSesion.setFont(new Font("Segoe UI", Font.BOLD, 14));
        botonCerrarSesion.setForeground(new Color(12105927));
        botonCerrarSesion.setHorizontalAlignment(SwingConstants.LEFT);
        botonCerrarSesion.setPreferredSize(new Dimension(-1, 44));
        botonCerrarSesion.setText("Cerrar sesión");
        panelNavegacion.add(botonCerrarSesion, new GridBagConstraints(
                0, 5, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0
        ));

        panelDerecha = new JPanel();
        panelDerecha.setLayout(new BorderLayout(0, 0));
        panelDerecha.setBackground(new Color(16119546));
        panelPrincipal.add(panelDerecha, BorderLayout.CENTER);

        panelSuperior = new JPanel();
        panelSuperior.setLayout(new BorderLayout(0, 0));
        panelSuperior.setBackground(new Color(16777215));
        panelSuperior.setMinimumSize(new Dimension(-1, 72));
        panelSuperior.setPreferredSize(new Dimension(-1, 72));
        panelSuperior.setBorder(new EmptyBorder(18, 24, 18, 24));
        panelDerecha.add(panelSuperior, BorderLayout.NORTH);

        etiquetaTitulo = new JLabel();
        etiquetaTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        etiquetaTitulo.setForeground(new Color(1973805));
        etiquetaTitulo.setText("Sistema de Gestión de Restaurante");
        panelSuperior.add(etiquetaTitulo, BorderLayout.WEST);

        etiquetaUsuario = new JLabel();
        etiquetaUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        etiquetaUsuario.setForeground(new Color(7105664));
        etiquetaUsuario.setHorizontalAlignment(SwingConstants.RIGHT);
        etiquetaUsuario.setText("Mesero: Juan Pérez");
        panelSuperior.add(etiquetaUsuario, BorderLayout.EAST);

        panelCentral = new JPanel();
        panelCentral.setLayout(new GridBagLayout());
        panelCentral.setBackground(new Color(16119546));
        panelCentral.setBorder(new EmptyBorder(16, 16, 16, 16));
        panelDerecha.add(panelCentral, BorderLayout.CENTER);

        panelMesas = new JPanel();
        panelMesas.setLayout(new BorderLayout(0, 0));
        panelMesas.setBackground(new Color(16777215));
        panelCentral.add(panelMesas, new GridBagConstraints(
                0, 0, 1, 1, 0.38, 0.56,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        ));

        etiquetaTituloMesas = new JLabel();
        etiquetaTituloMesas.setFont(new Font("Segoe UI", Font.BOLD, 16));
        etiquetaTituloMesas.setForeground(new Color(1973805));
        etiquetaTituloMesas.setText("Mesas");
        panelMesas.add(etiquetaTituloMesas, BorderLayout.NORTH);

        panelBotonesMesas = new JPanel();
        panelBotonesMesas.setLayout(new BorderLayout(0, 0));
        panelBotonesMesas.setBackground(new Color(16777215));
        panelBotonesMesas.setBorder(new EmptyBorder(0, 16, 16, 16));
        panelMesas.add(panelBotonesMesas, BorderLayout.CENTER);

        panelOrden = new JPanel();
        panelOrden.setLayout(new BorderLayout(0, 0));
        panelOrden.setBackground(new Color(16777215));
        panelCentral.add(panelOrden, new GridBagConstraints(
                1, 0, 1, 1, 0.62, 0.56,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        ));

        etiquetaTituloOrden = new JLabel();
        etiquetaTituloOrden.setFont(new Font("Segoe UI", Font.BOLD, 16));
        etiquetaTituloOrden.setForeground(new Color(1973805));
        etiquetaTituloOrden.setText("Orden de la Mesa 1");
        panelOrden.add(etiquetaTituloOrden, BorderLayout.NORTH);

        panelDesplazamientoOrden = new JScrollPane();
        panelDesplazamientoOrden.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panelDesplazamientoOrden.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panelDesplazamientoOrden.setBorder(null);
        panelOrden.add(panelDesplazamientoOrden, BorderLayout.CENTER);

        tablaOrden = new JTable();
        tablaOrden.setBackground(new Color(16777215));
        tablaOrden.setGridColor(new Color(15001327));
        tablaOrden.setShowGrid(false);
        panelDesplazamientoOrden.setViewportView(tablaOrden);

        panelTotal = new JPanel();
        panelTotal.setLayout(new BorderLayout(0, 0));
        panelTotal.setBackground(new Color(16448252));
        panelTotal.setBorder(new EmptyBorder(10, 16, 12, 16));
        panelOrden.add(panelTotal, BorderLayout.SOUTH);

        etiquetaTextoTotal = new JLabel();
        etiquetaTextoTotal.setFont(new Font("Segoe UI", Font.BOLD, 15));
        etiquetaTextoTotal.setForeground(new Color(7105664));
        etiquetaTextoTotal.setText("Total:");
        panelTotal.add(etiquetaTextoTotal, BorderLayout.WEST);

        etiquetaTotal = new JLabel();
        etiquetaTotal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        etiquetaTotal.setForeground(new Color(15105570));
        etiquetaTotal.setHorizontalAlignment(SwingConstants.RIGHT);
        etiquetaTotal.setText("$0.00");
        panelTotal.add(etiquetaTotal, BorderLayout.EAST);

        panelPlatillos = new JPanel();
        panelPlatillos.setLayout(new BorderLayout(0, 0));
        panelPlatillos.setBackground(new Color(16777215));
        panelCentral.add(panelPlatillos, new GridBagConstraints(
                0, 1, 1, 1, 0.38, 0.44,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        ));

        etiquetaTituloPlatillos = new JLabel();
        etiquetaTituloPlatillos.setFont(new Font("Segoe UI", Font.BOLD, 16));
        etiquetaTituloPlatillos.setForeground(new Color(1973805));
        etiquetaTituloPlatillos.setText("Platillos");
        panelPlatillos.add(etiquetaTituloPlatillos, BorderLayout.NORTH);

        panelDesplazamientoPlatillos = new JScrollPane();
        panelDesplazamientoPlatillos.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panelDesplazamientoPlatillos.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panelDesplazamientoPlatillos.setBorder(null);
        panelPlatillos.add(panelDesplazamientoPlatillos, BorderLayout.CENTER);

        panelListaPlatillos = new JPanel();
        panelListaPlatillos.setLayout(new BorderLayout(0, 0));
        panelListaPlatillos.setBackground(new Color(16777215));
        panelDesplazamientoPlatillos.setViewportView(panelListaPlatillos);

        panelAcciones = new JPanel();
        panelAcciones.setLayout(new BorderLayout(0, 0));
        panelAcciones.setBackground(new Color(16777215));
        panelCentral.add(panelAcciones, new GridBagConstraints(
                1, 1, 1, 1, 0.62, 0.44,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        ));

        etiquetaTituloAcciones = new JLabel();
        etiquetaTituloAcciones.setFont(new Font("Segoe UI", Font.BOLD, 16));
        etiquetaTituloAcciones.setForeground(new Color(1973805));
        etiquetaTituloAcciones.setText("Acciones");
        panelAcciones.add(etiquetaTituloAcciones, BorderLayout.NORTH);

        panelBotonesAcciones = new JPanel();
        panelBotonesAcciones.setLayout(new GridBagLayout());
        panelBotonesAcciones.setBackground(new Color(16777215));
        panelBotonesAcciones.setBorder(new EmptyBorder(0, 16, 16, 16));
        panelAcciones.add(panelBotonesAcciones, BorderLayout.CENTER);

        botonMarcarEntregado = new JButton();
        botonMarcarEntregado.setBackground(new Color(15105570));
        botonMarcarEntregado.setBorderPainted(false);
        botonMarcarEntregado.setContentAreaFilled(true);
        botonMarcarEntregado.setFocusPainted(false);
        botonMarcarEntregado.setFont(new Font("Segoe UI", Font.BOLD, 15));
        botonMarcarEntregado.setForeground(new Color(16777215));
        botonMarcarEntregado.setText("Marcar platillo como entregado");
        panelBotonesAcciones.add(botonMarcarEntregado, new GridBagConstraints(
                0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        ));

        botonGenerarCuenta = new JButton();
        botonGenerarCuenta.setBackground(new Color(1973805));
        botonGenerarCuenta.setBorderPainted(false);
        botonGenerarCuenta.setContentAreaFilled(true);
        botonGenerarCuenta.setFocusPainted(false);
        botonGenerarCuenta.setFont(new Font("Segoe UI", Font.BOLD, 15));
        botonGenerarCuenta.setForeground(new Color(16777215));
        botonGenerarCuenta.setText("Generar cuenta");
        panelBotonesAcciones.add(botonGenerarCuenta, new GridBagConstraints(
                0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        ));

        botonRegistrarPago = new JButton();
        botonRegistrarPago.setBackground(new Color(2600544));
        botonRegistrarPago.setBorderPainted(false);
        botonRegistrarPago.setContentAreaFilled(true);
        botonRegistrarPago.setFocusPainted(false);
        botonRegistrarPago.setFont(new Font("Segoe UI", Font.BOLD, 15));
        botonRegistrarPago.setForeground(new Color(16777215));
        botonRegistrarPago.setText("Registrar pago");
        panelBotonesAcciones.add(botonRegistrarPago, new GridBagConstraints(
                0, 2, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        ));

        botonPagoEfectivo = new JButton();
        botonPagoEfectivo.setBackground(new Color(2719929));
        botonPagoEfectivo.setBorderPainted(false);
        botonPagoEfectivo.setContentAreaFilled(true);
        botonPagoEfectivo.setFocusPainted(false);
        botonPagoEfectivo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        botonPagoEfectivo.setForeground(new Color(16777215));
        botonPagoEfectivo.setText("Pagar en efectivo");
        panelBotonesAcciones.add(botonPagoEfectivo, new GridBagConstraints(
                0, 3, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        ));

        botonPagoTerminal = new JButton();
        botonPagoTerminal.setBackground(new Color(9323693));
        botonPagoTerminal.setBorderPainted(false);
        botonPagoTerminal.setContentAreaFilled(true);
        botonPagoTerminal.setFocusPainted(false);
        botonPagoTerminal.setFont(new Font("Segoe UI", Font.BOLD, 15));
        botonPagoTerminal.setForeground(new Color(16777215));
        botonPagoTerminal.setText("Pagar con terminal");
        panelBotonesAcciones.add(botonPagoTerminal, new GridBagConstraints(
                0, 4, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        ));
    }

    private JComponent $$$getRootComponent$$$() {
        return panelPrincipal;
    }
}
