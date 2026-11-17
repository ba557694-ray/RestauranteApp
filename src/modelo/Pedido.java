package modelo;

public class Pedido {
    public static final String PENDIENTE = "Pendiente";
    public static final String ENTREGADO = "Entregado";
    public static final String EFECTIVO = "Efectivo";
    public static final String TERMINAL = "Terminal";

    private int idPedido;
    private int idMesa;
    private String platillo;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;
    private String estado;
    private boolean pagado;
    private String metodoPago;
    private String fechaHora;
    private String fechaPago;

    public Pedido() {
        this(0, 0, "", 1, 0.0, PENDIENTE, false);
    }

    public Pedido(int idMesa, String platillo, int cantidad, double precioUnitario) {
        this(0, idMesa, platillo, cantidad, precioUnitario, PENDIENTE, false);
    }

    public Pedido(int idPedido, int idMesa, String platillo, int cantidad, double precioUnitario, String estado, boolean pagado) {
        this.idPedido = idPedido;
        this.idMesa = idMesa;
        this.platillo = platillo;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.estado = estado;
        this.pagado = pagado;
        this.metodoPago = null;
        this.fechaHora = "";
        this.fechaPago = null;
        calcularSubtotal();
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public int getIdMesa() {
        return idMesa;
    }

    public void setIdMesa(int idMesa) {
        this.idMesa = idMesa;
    }

    public String getPlatillo() {
        return platillo;
    }

    public void setPlatillo(String platillo) {
        this.platillo = platillo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        calcularSubtotal();
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
        calcularSubtotal();
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double calcularSubtotal() {
        subtotal = Math.round(cantidad * precioUnitario * 100.0) / 100.0;
        return subtotal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public boolean isPagado() {
        return pagado;
    }

    public void setPagado(boolean pagado) {
        this.pagado = pagado;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(String fechaPago) {
        this.fechaPago = fechaPago;
    }

    public void marcarEntregado() {
        estado = ENTREGADO;
    }

    public boolean estaPagado() {
        return pagado;
    }

    public boolean esPendiente() {
        return !ENTREGADO.equalsIgnoreCase(estado);
    }

    @Override
    public String toString() {
        return platillo + " x" + cantidad;
    }
}
