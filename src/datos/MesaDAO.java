package datos;

import modelo.Mesa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MesaDAO {
    private static final String CONSULTA_INSERTAR =
            "INSERT INTO mesas (numero, capacidad, estado, ubicacion) VALUES (?, ?, ?, ?)";
    private static final String CONSULTA_ACTUALIZAR =
            "UPDATE mesas SET numero = ?, capacidad = ?, estado = ?, ubicacion = ? WHERE id_mesa = ?";
    private static final String CONSULTA_ELIMINAR =
            "DELETE FROM mesas WHERE id_mesa = ?";
    private static final String CONSULTA_OBTENER_TODOS =
            "SELECT id_mesa, numero, capacidad, estado, ubicacion FROM mesas ORDER BY numero";
    private static final String CONSULTA_OBTENER_POR_ID =
            "SELECT id_mesa, numero, capacidad, estado, ubicacion FROM mesas WHERE id_mesa = ?";
    private static final String CONSULTA_CAMBIAR_ESTADO =
            "UPDATE mesas SET estado = ? WHERE id_mesa = ?";

    private final ConexionBD conexionBD;

    public MesaDAO() {
        this(new ConexionBD());
    }

    public MesaDAO(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    public boolean insertar(Mesa mesa) throws ExcepcionBaseDatos {
        try (Connection conexion = conexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(CONSULTA_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {
            sentencia.setInt(1, mesa.getNumero());
            sentencia.setInt(2, mesa.getCapacidad());
            sentencia.setString(3, mesa.getEstado());
            sentencia.setString(4, mesa.getUbicacion());
            boolean insertada = sentencia.executeUpdate() > 0;
            if (insertada) {
                try (ResultSet llaves = sentencia.getGeneratedKeys()) {
                    if (llaves.next()) {
                        mesa.setIdMesa(llaves.getInt(1));
                    }
                }
            }
            return insertada;
        } catch (SQLException e) {
            throw new ExcepcionBaseDatos("No se pudo insertar la mesa", e);
        }
    }

    public boolean actualizar(Mesa mesa) throws ExcepcionBaseDatos {
        try (Connection conexion = conexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(CONSULTA_ACTUALIZAR)) {
            sentencia.setInt(1, mesa.getNumero());
            sentencia.setInt(2, mesa.getCapacidad());
            sentencia.setString(3, mesa.getEstado());
            sentencia.setString(4, mesa.getUbicacion());
            sentencia.setInt(5, mesa.getIdMesa());
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new ExcepcionBaseDatos("No se pudo actualizar la mesa", e);
        }
    }

    public boolean eliminar(int idMesa) throws ExcepcionBaseDatos {
        try (Connection conexion = conexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(CONSULTA_ELIMINAR)) {
            sentencia.setInt(1, idMesa);
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new ExcepcionBaseDatos("No se pudo eliminar la mesa", e);
        }
    }

    public List<Mesa> obtenerTodos() throws ExcepcionBaseDatos {
        List<Mesa> mesas = new ArrayList<>();
        try (Connection conexion = conexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(CONSULTA_OBTENER_TODOS);
             ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                mesas.add(mapearMesa(resultado));
            }
            return mesas;
        } catch (SQLException e) {
            throw new ExcepcionBaseDatos("No se pudieron obtener las mesas", e);
        }
    }

    public Mesa obtenerPorId(int idMesa) throws ExcepcionBaseDatos {
        try (Connection conexion = conexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(CONSULTA_OBTENER_POR_ID)) {
            sentencia.setInt(1, idMesa);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return mapearMesa(resultado);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new ExcepcionBaseDatos("No se pudo obtener la mesa", e);
        }
    }

    public boolean cambiarEstado(int idMesa, String estado) throws ExcepcionBaseDatos {
        try (Connection conexion = conexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(CONSULTA_CAMBIAR_ESTADO)) {
            sentencia.setString(1, estado);
            sentencia.setInt(2, idMesa);
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new ExcepcionBaseDatos("No se pudo cambiar el estado de la mesa", e);
        }
    }

    private Mesa mapearMesa(ResultSet resultado) throws SQLException {
        return new Mesa(
                resultado.getInt("id_mesa"),
                resultado.getInt("numero"),
                resultado.getInt("capacidad"),
                resultado.getString("estado"),
                resultado.getString("ubicacion")
        );
    }
}
