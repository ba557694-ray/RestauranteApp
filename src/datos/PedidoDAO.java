package datos;

import modelo.Pedido;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {
    private static final String COLUMNAS_PEDIDO =
            "id_pedido, id_mesa, platillo, cantidad, precio_unitario, subtotal, estado, pagado, metodo_pago, fecha_hora, fecha_pago";
    private static final String CONSULTA_INSERTAR =
            "INSERT INTO pedidos (id_mesa, platillo, cantidad, precio_unitario, subtotal, estado, pagado, metodo_pago, fecha_pago) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String CONSULTA_ACTUALIZAR =
            "UPDATE pedidos SET id_mesa = ?, platillo = ?, cantidad = ?, precio_unitario = ?, subtotal = ?, estado = ?, pagado = ?, metodo_pago = ?, fecha_pago = ? WHERE id_pedido = ?";
    private static final String CONSULTA_ELIMINAR =
            "DELETE FROM pedidos WHERE id_pedido = ?";
    private static final String CONSULTA_OBTENER_TODOS =
            "SELECT " + COLUMNAS_PEDIDO + " FROM pedidos ORDER BY id_mesa, id_pedido";
    private static final String CONSULTA_OBTENER_POR_ID =
            "SELECT " + COLUMNAS_PEDIDO + " FROM pedidos WHERE id_pedido = ?";
    private static final String CONSULTA_OBTENER_POR_MESA =
            "SELECT " + COLUMNAS_PEDIDO + " FROM pedidos WHERE id_mesa = ? ORDER BY id_pedido";
    private static final String CONSULTA_OBTENER_PENDIENTES_POR_MESA =
            "SELECT " + COLUMNAS_PEDIDO + " FROM pedidos WHERE id_mesa = ? AND pagado = 0 ORDER BY id_pedido";
    private static final String CONSULTA_MARCAR_ENTREGADO =
            "UPDATE pedidos SET estado = 'Entregado' WHERE id_pedido = ? AND pagado = 0";
    private static final String CONSULTA_REGISTRAR_PAGO_MESA =
            "UPDATE pedidos SET pagado = 1, metodo_pago = ?, fecha_pago = CURRENT_TIMESTAMP WHERE id_mesa = ? AND pagado = 0";
    private static final String CONSULTA_CALCULAR_TOTAL_MESA =
            "SELECT COALESCE(SUM(subtotal), 0) AS total FROM pedidos WHERE id_mesa = ? AND pagado = 0";

    private final ConexionBD conexionBD;

    public PedidoDAO() {
        this(new ConexionBD());
    }

    public PedidoDAO(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    public boolean insertar(Pedido pedido) throws ExcepcionBaseDatos {
        try (Connection conexion = conexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(CONSULTA_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {
            sentencia.setInt(1, pedido.getIdMesa());
            sentencia.setString(2, pedido.getPlatillo());
            sentencia.setInt(3, pedido.getCantidad());
            sentencia.setDouble(4, pedido.getPrecioUnitario());
            sentencia.setDouble(5, pedido.getSubtotal());
            sentencia.setString(6, pedido.getEstado());
            sentencia.setInt(7, pedido.isPagado() ? 1 : 0);
            asignarTextoNulo(sentencia, 8, pedido.getMetodoPago());
            asignarTextoNulo(sentencia, 9, pedido.getFechaPago());
            boolean insertado = sentencia.executeUpdate() > 0;
            if (insertado) {
                try (ResultSet llaves = sentencia.getGeneratedKeys()) {
                    if (llaves.next()) {
                        pedido.setIdPedido(llaves.getInt(1));
                    }
                }
            }
            return insertado;
        } catch (SQLException e) {
            throw new ExcepcionBaseDatos("No se pudo insertar el pedido", e);
        }
    }

    public boolean actualizar(Pedido pedido) throws ExcepcionBaseDatos {
        try (Connection conexion = conexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(CONSULTA_ACTUALIZAR)) {
            sentencia.setInt(1, pedido.getIdMesa());
            sentencia.setString(2, pedido.getPlatillo());
            sentencia.setInt(3, pedido.getCantidad());
            sentencia.setDouble(4, pedido.getPrecioUnitario());
            sentencia.setDouble(5, pedido.getSubtotal());
            sentencia.setString(6, pedido.getEstado());
            sentencia.setInt(7, pedido.isPagado() ? 1 : 0);
            asignarTextoNulo(sentencia, 8, pedido.getMetodoPago());
            asignarTextoNulo(sentencia, 9, pedido.getFechaPago());
            sentencia.setInt(10, pedido.getIdPedido());
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new ExcepcionBaseDatos("No se pudo actualizar el pedido", e);
        }
    }

    public boolean eliminar(int idPedido) throws ExcepcionBaseDatos {
        try (Connection conexion = conexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(CONSULTA_ELIMINAR)) {
            sentencia.setInt(1, idPedido);
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new ExcepcionBaseDatos("No se pudo eliminar el pedido", e);
        }
    }

    public List<Pedido> obtenerTodos() throws ExcepcionBaseDatos {
        List<Pedido> pedidos = new ArrayList<>();
        try (Connection conexion = conexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(CONSULTA_OBTENER_TODOS);
             ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                pedidos.add(mapearPedido(resultado));
            }
            return pedidos;
        } catch (SQLException e) {
            throw new ExcepcionBaseDatos("No se pudieron obtener los pedidos", e);
        }
    }

    public Pedido obtenerPorId(int idPedido) throws ExcepcionBaseDatos {
        try (Connection conexion = conexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(CONSULTA_OBTENER_POR_ID)) {
            sentencia.setInt(1, idPedido);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return mapearPedido(resultado);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new ExcepcionBaseDatos("No se pudo obtener el pedido", e);
        }
    }

    public List<Pedido> obtenerPorMesa(int idMesa) throws ExcepcionBaseDatos {
        List<Pedido> pedidos = new ArrayList<>();
        try (Connection conexion = conexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(CONSULTA_OBTENER_POR_MESA)) {
            sentencia.setInt(1, idMesa);
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    pedidos.add(mapearPedido(resultado));
                }
            }
            return pedidos;
        } catch (SQLException e) {
            throw new ExcepcionBaseDatos("No se pudieron obtener los pedidos de la mesa", e);
        }
    }

    public List<Pedido> obtenerPendientesPorMesa(int idMesa) throws ExcepcionBaseDatos {
        List<Pedido> pedidos = new ArrayList<>();
        try (Connection conexion = conexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(CONSULTA_OBTENER_PENDIENTES_POR_MESA)) {
            sentencia.setInt(1, idMesa);
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    pedidos.add(mapearPedido(resultado));
                }
            }
            return pedidos;
        } catch (SQLException e) {
            throw new ExcepcionBaseDatos("No se pudieron obtener los pedidos pendientes de la mesa", e);
        }
    }

    public boolean marcarEntregado(int idPedido) throws ExcepcionBaseDatos {
        try (Connection conexion = conexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(CONSULTA_MARCAR_ENTREGADO)) {
            sentencia.setInt(1, idPedido);
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new ExcepcionBaseDatos("No se pudo marcar el platillo como entregado", e);
        }
    }

    public boolean registrarPagoMesa(int idMesa, String metodoPago) throws ExcepcionBaseDatos {
        try (Connection conexion = conexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(CONSULTA_REGISTRAR_PAGO_MESA)) {
            sentencia.setString(1, metodoPago);
            sentencia.setInt(2, idMesa);
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new ExcepcionBaseDatos("No se pudo registrar el pago de la mesa", e);
        }
    }

    public double calcularTotalMesa(int idMesa) throws ExcepcionBaseDatos {
        try (Connection conexion = conexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(CONSULTA_CALCULAR_TOTAL_MESA)) {
            sentencia.setInt(1, idMesa);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return resultado.getDouble("total");
                }
            }
            return 0.0;
        } catch (SQLException e) {
            throw new ExcepcionBaseDatos("No se pudo calcular el total de la mesa", e);
        }
    }

    private Pedido mapearPedido(ResultSet resultado) throws SQLException {
        Pedido pedido = new Pedido();
        pedido.setIdPedido(resultado.getInt("id_pedido"));
        pedido.setIdMesa(resultado.getInt("id_mesa"));
        pedido.setPlatillo(resultado.getString("platillo"));
        pedido.setCantidad(resultado.getInt("cantidad"));
        pedido.setPrecioUnitario(resultado.getDouble("precio_unitario"));
        pedido.setSubtotal(resultado.getDouble("subtotal"));
        pedido.setEstado(resultado.getString("estado"));
        pedido.setPagado(resultado.getInt("pagado") == 1);
        pedido.setMetodoPago(resultado.getString("metodo_pago"));
        pedido.setFechaHora(resultado.getString("fecha_hora"));
        pedido.setFechaPago(resultado.getString("fecha_pago"));
        return pedido;
    }

    private void asignarTextoNulo(PreparedStatement sentencia, int indice, String valor) throws SQLException {
        if (valor == null || valor.isBlank()) {
            sentencia.setNull(indice, Types.VARCHAR);
        } else {
            sentencia.setString(indice, valor);
        }
    }
}
