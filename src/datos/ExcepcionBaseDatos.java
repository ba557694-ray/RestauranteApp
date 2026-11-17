package datos;

public class ExcepcionBaseDatos extends Exception {
    public ExcepcionBaseDatos(String mensaje) {
        super(mensaje);
    }

    public ExcepcionBaseDatos(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
