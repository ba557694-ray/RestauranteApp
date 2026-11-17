package modelo;

public class Mesa {
    public static final String DISPONIBLE = "Disponible";
    public static final String OCUPADA = "Ocupada";

    private int idMesa;
    private int numero;
    private int capacidad;
    private String estado;
    private String ubicacion;

    public Mesa() {
        this(0, 0, 4, DISPONIBLE, "");
    }

    public Mesa(int numero, int capacidad, String estado, String ubicacion) {
        this(0, numero, capacidad, estado, ubicacion);
    }

    public Mesa(int idMesa, int numero, int capacidad, String estado, String ubicacion) {
        this.idMesa = idMesa;
        this.numero = numero;
        this.capacidad = capacidad;
        this.estado = estado;
        this.ubicacion = ubicacion;
    }

    public int getIdMesa() {
        return idMesa;
    }

    public void setIdMesa(int idMesa) {
        this.idMesa = idMesa;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public boolean estaDisponible() {
        return DISPONIBLE.equalsIgnoreCase(estado);
    }

    public void ocupar() {
        estado = OCUPADA;
    }

    public void liberar() {
        estado = DISPONIBLE;
    }

    @Override
    public String toString() {
        return "Mesa " + numero;
    }
}
