package negocio;

import datos.ExcepcionBaseDatos;
import datos.MesaDAO;
import datos.PedidoDAO;
import modelo.Mesa;
import modelo.Pedido;
import modelo.Platillo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ServicioRestaurante {
    private final MesaDAO mesaDAO;
    private final PedidoDAO pedidoDAO;
    private final Map<Integer, Mesa> mesasPorNumero = new LinkedHashMap<>();

    public ServicioRestaurante() {
        this(new MesaDAO(), new PedidoDAO());
    }

    public ServicioRestaurante(MesaDAO mesaDAO, PedidoDAO pedidoDAO) {
        this.mesaDAO = mesaDAO;
        this.pedidoDAO = pedidoDAO;
    }

    public List<Mesa> cargarMesas() throws ExcepcionBaseDatos {
        List<Mesa> mesas = mesaDAO.obtenerTodos();
        mesas.sort(Comparator.comparingInt(Mesa::getNumero));
        mesasPorNumero.clear();
        for (Mesa mesa : mesas) {
            mesasPorNumero.put(mesa.getNumero(), mesa);
        }
        return mesas;
    }

    public List<Mesa> obtenerMesas() {
        List<Mesa> mesas = new ArrayList<>(mesasPorNumero.values());
        mesas.sort(Comparator.comparingInt(Mesa::getNumero));
        return mesas;
    }

    public Mesa obtenerMesa(int numero) {
        return mesasPorNumero.get(numero);
    }

    public Mesa abrirMesa(int numero) throws ExcepcionBaseDatos {
        Mesa mesa = buscarMesa(numero);
        if (mesa.estaDisponible()) {
            if (!mesaDAO.cambiarEstado(mesa.getIdMesa(), Mesa.OCUPADA)) {
                throw new ExcepcionBaseDatos("No se pudo ocupar la mesa " + numero);
            }
            mesa.ocupar();
        }
        return mesa;
    }

    public List<Pedido> obtenerPedidosMesa(int numero) throws ExcepcionBaseDatos {
        Mesa mesa = buscarMesa(numero);
        return pedidoDAO.obtenerPendientesPorMesa(mesa.getIdMesa());
    }

    public List<Pedido> obtenerPedidosActivos() throws ExcepcionBaseDatos {
        List<Pedido> activos = new ArrayList<>();
        for (Pedido pedido : pedidoDAO.obtenerTodos()) {
            if (!pedido.isPagado()) {
                activos.add(pedido);
            }
        }
        return activos;
    }

    public Pedido agregarPlatillo(int numero, Platillo platillo) throws ExcepcionBaseDatos {
        Mesa mesa = buscarMesa(numero);
        for (Pedido pedido : pedidoDAO.obtenerPendientesPorMesa(mesa.getIdMesa())) {
            if (pedido.getPlatillo().equalsIgnoreCase(platillo.getNombre())
                    && Pedido.PENDIENTE.equalsIgnoreCase(pedido.getEstado())) {
                pedido.setCantidad(pedido.getCantidad() + 1);
                pedidoDAO.actualizar(pedido);
                return pedido;
            }
        }
        Pedido nuevoPedido = new Pedido(mesa.getIdMesa(), platillo.getNombre(), 1, platillo.getPrecio());
        pedidoDAO.insertar(nuevoPedido);
        return nuevoPedido;
    }

    public void marcarEntregado(Pedido pedido) throws ExcepcionBaseDatos {
        if (pedido == null) {
            throw new ExcepcionBaseDatos("El pedido no existe");
        }
        if (!pedidoDAO.marcarEntregado(pedido.getIdPedido())) {
            throw new ExcepcionBaseDatos("No se pudo marcar el platillo como entregado");
        }
        pedido.marcarEntregado();
    }

    public void eliminarPedido(Pedido pedido) throws ExcepcionBaseDatos {
        if (pedido == null) {
            throw new ExcepcionBaseDatos("El pedido no existe");
        }
        if (!pedidoDAO.eliminar(pedido.getIdPedido())) {
            throw new ExcepcionBaseDatos("No se pudo eliminar el platillo de la orden");
        }
    }

    public boolean liberarMesaSiEstaVacia(int numero) throws ExcepcionBaseDatos {
        Mesa mesa = buscarMesa(numero);
        if (!pedidoDAO.obtenerPendientesPorMesa(mesa.getIdMesa()).isEmpty()) {
            return false;
        }
        if (!mesa.estaDisponible()) {
            if (!mesaDAO.cambiarEstado(mesa.getIdMesa(), Mesa.DISPONIBLE)) {
                throw new ExcepcionBaseDatos("No se pudo liberar la mesa " + numero);
            }
            mesa.liberar();
        }
        return true;
    }

    public double calcularTotalMesa(int numero) throws ExcepcionBaseDatos {
        Mesa mesa = buscarMesa(numero);
        return pedidoDAO.calcularTotalMesa(mesa.getIdMesa());
    }

    public double calcularTotalPendiente() throws ExcepcionBaseDatos {
        double total = 0.0;
        for (Pedido pedido : pedidoDAO.obtenerTodos()) {
            if (!pedido.isPagado()) {
                total += pedido.getSubtotal();
            }
        }
        return Math.round(total * 100.0) / 100.0;
    }

    public double registrarPago(int numero, String metodoPago) throws ExcepcionBaseDatos {
        if (!Pedido.EFECTIVO.equals(metodoPago) && !Pedido.TERMINAL.equals(metodoPago)) {
            throw new ExcepcionBaseDatos("El método de pago " + metodoPago + " no es válido");
        }
        Mesa mesa = buscarMesa(numero);
        double total = pedidoDAO.calcularTotalMesa(mesa.getIdMesa());
        if (total <= 0.0) {
            return 0.0;
        }
        if (!pedidoDAO.registrarPagoMesa(mesa.getIdMesa(), metodoPago)) {
            return 0.0;
        }
        if (!mesaDAO.cambiarEstado(mesa.getIdMesa(), Mesa.DISPONIBLE)) {
            throw new ExcepcionBaseDatos("No se pudo liberar la mesa " + numero);
        }
        mesa.liberar();
        return total;
    }

    public int contarMesasOcupadas() {
        int ocupadas = 0;
        for (Mesa mesa : mesasPorNumero.values()) {
            if (Mesa.OCUPADA.equalsIgnoreCase(mesa.getEstado())) {
                ocupadas++;
            }
        }
        return ocupadas;
    }

    private Mesa buscarMesa(int numero) throws ExcepcionBaseDatos {
        Mesa mesa = mesasPorNumero.get(numero);
        if (mesa == null) {
            throw new ExcepcionBaseDatos("La mesa " + numero + " no está registrada");
        }
        return mesa;
    }
}
