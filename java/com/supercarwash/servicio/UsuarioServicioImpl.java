package com.supercarwash.servicio;

import com.supercarwash.modelo.Usuario;
import com.supercarwash.repositorio.IUsuarioDAO;
import com.supercarwash.repositorio.UsuarioDAOImpl;
import com.supercarwash.util.EncriptadorUtil;
import java.util.List;

/**
 * Implementación real conectada a MySQL mediante JDBC.
 */
public class UsuarioServicioImpl implements IUsuarioServicio {

    private final IUsuarioDAO usuarioDAO;

    public UsuarioServicioImpl() {
        this.usuarioDAO = new UsuarioDAOImpl();
    }

    @Override
    public String autenticarUsuario(String username, String contrasena) {
        if (username == null || contrasena == null || username.trim().isEmpty()) {
            return null;
        }

        // 1. Encriptamos la contraseña ingresada en el Login a SHA-256
        //    ya que en la base de datos se insertó con SHA2()
        String contrasenaEncriptada = EncriptadorUtil.encriptarString(contrasena);

        // 2. Buscamos directamente en MySQL pasándole la clave encriptada
        Usuario usuario = usuarioDAO.obtenerPorCredenciales(username, contrasenaEncriptada);

        if (usuario != null) {
            return usuario.getRol(); // Retorna "Administrador", "Cajero" u "Operario"
        }

        return null;
    }

    @Override
    public boolean registrarUsuario(Usuario usuario) {
        if (usuario == null || usuario.getUsuario().trim().isEmpty()) return false;
        // Encriptar antes de guardar
        usuario.setContrasena(EncriptadorUtil.encriptarString(usuario.getContrasena()));
        return usuarioDAO.insertar(usuario);
    }

    @Override
    public boolean actualizarUsuario(Usuario usuario) {
        if (usuario == null) return false;
        return usuarioDAO.modificar(usuario);
    }

    @Override
    public boolean eliminarUsuario(int idUsuario) {
        return usuarioDAO.eliminar(idUsuario);
    }

    @Override
    public List<Usuario> listarUsuarios() {
        return usuarioDAO.listarTodos();
    }
}