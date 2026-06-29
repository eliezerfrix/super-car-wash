package com.supercarwash.modelo;

/**
 * Especialización de Usuario para el personal operativo de boxes de lavado.
 */
public class Lavador extends Usuario {

    public Lavador() {
        super();
        setRol("Operario");
    }

    public Lavador(int idUsuario, String nombre, String usuario, String contrasena) {
        super(idUsuario, nombre, usuario, contrasena, "Operario");
    }

    @Override
    public String obtenerDescripcionPerfil() {
        return "Línea operativa - Ejecución de servicios estéticos y actualización de estados.";
    }
}