package com.supercarwash.servicio;

import com.supercarwash.modelo.Usuario;
import java.util.List;

public interface IUsuarioServicio {
    String autenticarUsuario(String username, String contrasena);
    boolean registrarUsuario(Usuario usuario);
    boolean actualizarUsuario(Usuario usuario);
    boolean eliminarUsuario(int idUsuario);
    List<Usuario> listarUsuarios();
}