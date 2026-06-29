package com.supercarwash.modelo;

/**
 * Especialización de Usuario para funciones administrativas.
 * Cumple con la Herencia de nivel 1 (POO-01).
 */
public class Administrador extends Usuario {

    public Administrador() {
        super();
        setRol("Administrador");
    }

    public Administrador(int idUsuario, String nombre, String usuario, String contrasena) {
        super(idUsuario, nombre, usuario, contrasena, "Administrador");
    }

    @Override
    public String obtenerDescripcionPerfil() {
        return "Acceso total - Gestión de usuarios, tarifas y reportes estratégicos.";
    }
}