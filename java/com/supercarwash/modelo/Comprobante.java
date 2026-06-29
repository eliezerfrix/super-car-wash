package com.supercarwash.modelo;

/**
 * Segunda jerarquía de herencia (POO-01) para el cumplimiento fiscal peruano.
 */
public abstract class Comprobante {
    private int idComprobante;
    private String codigoComprobante;
    private double subtotal;
    private double igv;
    private double total;
    private Pago pago;

    public Comprobante() {}

    public Comprobante(int idComprobante, String codigoComprobante, Pago pago) {
        this.idComprobante = idComprobante;
        this.codigoComprobante = codigoComprobante;
        this.pago = pago;
        if (pago != null) {
            this.total = pago.getMontoTotal();
        }
    }

    // Método polimórfico dinámico obligatorio (POO-05)
    public abstract void calcularMontos();

    // Getters y Setters
    public int getIdComprobante() { return idComprobante; }
    public void setIdComprobante(int idComprobante) { this.idComprobante = idComprobante; }

    public String getCodigoComprobante() { return codigoComprobante; }
    public void setCodigoComprobante(String codigoComprobante) { this.codigoComprobante = codigoComprobante; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getIgv() { return igv; }
    public void setIgv(double igv) { this.igv = igv; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public Pago getPago() { return pago; }
    public void setPago(Pago pago) { this.pago = pago; }
}