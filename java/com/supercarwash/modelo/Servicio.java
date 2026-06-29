package com.supercarwash.modelo;

public class Servicio {
    private int idServicio;
    private String tipoServicio; // Lavado Simple, Salón, Encerado, etc.
    private double precio;
    private String estado; // Pendiente, En proceso, Finalizado
    private Vehiculo vehiculo;
    private Lavador lavadorAsignado;

    public Servicio() {}

    public Servicio(int idServicio, String tipoServicio, double precio, String estado, Vehiculo vehiculo, Lavador lavadorAsignado) {
        this.idServicio = idServicio;
        this.tipoServicio = tipoServicio;
        this.precio = precio;
        this.estado = estado;
        this.vehiculo = vehiculo;
        this.lavadorAsignado = lavadorAsignado;
    }

    public int getIdServicio() { return idServicio; }
    public void setIdServicio(int idServicio) { this.idServicio = idServicio; }

    public String getTipoServicio() { return tipoServicio; }
    public void setTipoServicio(String tipoServicio) { this.tipoServicio = tipoServicio; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Vehiculo getVehiculo() { return vehiculo; }
    public void setVehiculo(Vehiculo vehiculo) { this.vehiculo = vehiculo; }

    public Lavador getLavadorAsignado() { return lavadorAsignado; }
    public void setLavadorAsignado(Lavador lavadorAsignado) { this.lavadorAsignado = lavadorAsignado; }
}