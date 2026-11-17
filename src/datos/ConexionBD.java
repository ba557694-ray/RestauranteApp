package datos;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionBD {
    private static final String NOMBRE_BASE_DATOS = "restaurante.db";

    private final String rutaBaseDatos;
    private final String direccionBaseDatos;

    public ConexionBD() {
        cargarControlador();
        rutaBaseDatos = determinarRutaBaseDatos();
        direccionBaseDatos = "jdbc:sqlite:" + rutaBaseDatos;
    }

    public Connection obtenerConexion() throws SQLException {
        Connection conexion = DriverManager.getConnection(direccionBaseDatos);
        try (Statement sentencia = conexion.createStatement()) {
            sentencia.execute("PRAGMA foreign_keys = ON");
            sentencia.execute("PRAGMA busy_timeout = 5000");
            return conexion;
        } catch (SQLException e) {
            conexion.close();
            throw e;
        }
    }

    public void cerrarConexion(Connection conexion) throws SQLException {
        if (conexion != null && !conexion.isClosed()) {
            conexion.close();
        }
    }

    public String obtenerRutaBaseDatos() {
        return rutaBaseDatos;
    }

    private String determinarRutaBaseDatos() {
        File directorioActual = new File(NOMBRE_BASE_DATOS);
        if (directorioActual.isFile()) {
            return directorioActual.getAbsolutePath();
        }
        File juntoAplicacion = new File(directorioAplicacion(), NOMBRE_BASE_DATOS);
        if (juntoAplicacion.isFile()) {
            return juntoAplicacion.getAbsolutePath();
        }
        return directorioActual.getAbsolutePath();
    }

    private File directorioAplicacion() {
        try {
            File ubicacion = new File(ConexionBD.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            return ubicacion.isDirectory() ? ubicacion : ubicacion.getParentFile();
        } catch (Exception excepcion) {
            return new File(".");
        }
    }

    private void cargarControlador() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("No se encontró el controlador JDBC de SQLite", e);
        }
    }
}
