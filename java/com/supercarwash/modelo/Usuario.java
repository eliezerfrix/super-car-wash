package com.supercarwash.modelo;

/**
 * Clase abstracta base que representa a un usuario genérico del sistema.
 * Cumple con el pilar de Abstracción y Encapsulamiento (POO-04).
 */
public abstract class Usuario {
    private int idUsuario;
    private String nombre;
    private String usuario;
    private String contrasena;
    private String rol;

    public Usuario() {}

    public Usuario(int idUsuario, String nombre, String usuario, String contrasena, String rol) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    // Método abstracto para cumplir con el polimorfismo dinámico (POO-05)
    public abstract String obtenerDescripcionPerfil();

    // Getters y Setters estrictos
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}