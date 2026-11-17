package modelo;

import java.util.Locale;

public class Platillo {
    private String nombre;
    private double precio;
    private String categoria;
    private boolean disponible;

    public Platillo(String nombre, double precio, String categoria) {
        this(nombre, precio, categoria, true);
    }

    public Platillo(String nombre, double precio, String categoria, boolean disponible) {
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
        this.disponible = disponible;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public String getPrecioFormateado() {
        return String.format(Locale.ROOT, "$%.2f", precio);
    }

    @Override
    public String toString() {
        return nombre + " - " + getPrecioFormateado();
    }
}
