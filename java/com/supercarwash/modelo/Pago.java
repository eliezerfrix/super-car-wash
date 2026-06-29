package com.supercarwash.modelo;

import java.util.Date;

public class Pago {
    private int idPago;
    private Date fecha;
    private double montoTotal;
    private String metodoPago; // Efectivo, Tarjeta, Yape/Plin
    private Servicio servicio;

    public Pago() {}

    public Pago(int idPago, Date fecha, double montoTotal, String metodoPago, Servicio servicio) {
        this.idPago = idPago;
        this.fecha = fecha;
        this.montoTotal = montoTotal;
        this.metodoPago = metodoPago;
        this.servicio = servicio;
    }

    public int getIdPago() { return idPago; }
    public void setIdPago(int idPago) { this.idPago = idPago; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public double getMontoTotal() { return montoTotal; }
    public void setMontoTotal(double montoTotal) { this.montoTotal = montoTotal; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public Servicio getServicio() { return servicio; }
    public void setServicio(Servicio servicio) { this.servicio = servicio; }
}