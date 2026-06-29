package com.supercarwash.modelo;

/**
 * Comprobante simplificado para persona natural.
 */
public class Boleta extends Comprobante {
    private String dniCliente;

    public Boleta() { super(); }

    public Boleta(int idComprobante, String codigoComprobante, Pago pago, String dniCliente) {
        super(idComprobante, codigoComprobante, pago);
        this.dniCliente = dniCliente;
        calcularMontos();
    }

    @Override
    public void calcularMontos() {
        // En la boleta de venta en consumo directo peruano, el precio final incluye el impuesto de forma implícita
        setTotal(getPago().getMontoTotal());
        setSubtotal(getTotal());
        setIgv(0.0);
    }

    public String getDniCliente() { return dniCliente; }
    public void setDniCliente(String dniCliente) { this.dniCliente = dniCliente; }
}