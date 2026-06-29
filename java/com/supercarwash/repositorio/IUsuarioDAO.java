package com.supercarwash.repositorio;

import com.supercarwash.modelo.Usuario;
import java.util.List;

public interface IUsuarioDAO {
    Usuario obtenerPorCredenciales(String username, String contrasena);
    boolean insertar(Usuario usuario);
    boolean modificar(Usuario usuario);
    boolean eliminar(int idUsuario);
    List<Usuario> listarTodos();
}