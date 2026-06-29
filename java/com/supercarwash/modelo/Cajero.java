package com.supercarwash.modelo;

/**
 * Especialización de Usuario para la gestión de caja y atención al cliente.
 */
public class Cajero extends Usuario {

    public Cajero() {
        super();
        setRol("Cajero");
    }

    public Cajero(int idUsuario, String nombre, String usuario, String contrasena) {
        super(idUsuario, nombre, usuario, contrasena, "Cajero");
    }

    @Override
    public String obtenerDescripcionPerfil() {
        return "Atención directa - Registro de clientes, recepción de vehículos y facturación.";
    }
}