package com.supercarwash.repositorio;

import com.supercarwash.modelo.*;
import com.supercarwash.util.EncriptadorUtil; // IMPORTACIÓN AGREGADA
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAOImpl implements IUsuarioDAO {

    @Override
    public Usuario obtenerPorCredenciales(String username, String contrasena) {
        String sql = "SELECT id_usuario, nombre, usuario, clave, rol FROM usuarios WHERE usuario = ? AND clave = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // SOLUCIÓN: Encriptamos la contraseña recibida en texto plano de la UI
            // para compararla correctamente con el hash SHA-256 almacenado en MySQL.
            String contrasenaEncriptada = EncriptadorUtil.encriptarString(contrasena);

            ps.setString(1, username);
            ps.setString(2, contrasenaEncriptada); // Ahora sí buscará el Hash largo correspondientemente

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String rol = rs.getString("rol");
                    int id = rs.getInt("id_usuario");
                    String nombre = rs.getString("nombre");

                    // Instanciación polimórfica según la arquitectura del modelo
                    return switch (rol) {
                        case "Administrador" -> new Administrador(id, nombre, username, contrasenaEncriptada);
                        case "Cajero" -> new Cajero(id, nombre, username, contrasenaEncriptada);
                        default -> new Lavador(id, nombre, username, contrasenaEncriptada);
                    };
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean insertar(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nombre, usuario, clave, rol) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getUsuario());
            ps.setString(3, usuario.getContrasena());
            ps.setString(4, usuario.getRol());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) usuario.setIdUsuario(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean modificar(Usuario usuario) {
        String sql = "UPDATE usuarios SET nombre = ?, usuario = ?, clave = ?, rol = ? WHERE id_usuario = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getUsuario());
            ps.setString(3, usuario.getContrasena());
            ps.setString(4, usuario.getRol());
            ps.setInt(5, usuario.getIdUsuario());

            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean eliminar(int idUsuario) {
        String sql = "DELETE FROM usuarios WHERE id_usuario = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT id_usuario, nombre, usuario, clave, rol FROM usuarios";
        try (Connection con = ConexionBD.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String rol = rs.getString("rol");
                int id = rs.getInt("id_usuario");
                String nombre = rs.getString("nombre");
                String user = rs.getString("usuario");
                String pass = rs.getString("clave");

                Usuario u = switch (rol) {
                    case "Administrador" -> new Administrador(id, nombre, user, pass);
                    case "Cajero" -> new Cajero(id, nombre, user, pass);
                    default -> new Lavador(id, nombre, user, pass);
                };
                lista.add(u);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }
}