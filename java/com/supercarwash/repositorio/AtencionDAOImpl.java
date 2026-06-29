package com.supercarwash.repositorio;

import com.supercarwash.modelo.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AtencionDAOImpl implements IAtencionDAO {

    @Override
    public boolean insertarCliente(Cliente cliente) {
        String sql = "INSERT INTO clientes (nombre, documento, telefono, correo) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getDocumento());
            ps.setString(3, cliente.getTelefono());
            ps.setString(4, cliente.getCorreo());

            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) cliente.setIdCliente(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean insertarVehiculo(Vehiculo vehiculo) {
        String sql = "INSERT INTO vehiculos (placa, marca, modelo, tipo, id_cliente) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, vehiculo.getPlaca());
            ps.setString(2, vehiculo.getMarca());
            ps.setString(3, vehiculo.getModelo());
            ps.setString(4, vehiculo.getTipo());
            ps.setInt(5, vehiculo.getPropietario().getIdCliente());

            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) vehiculo.setIdVehiculo(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Cliente> buscarClientes(String criterio) {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT id_cliente, nombre, documento, telefono, correo FROM clientes WHERE nombre LIKE ? OR documento LIKE ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + criterio + "%");
            ps.setString(2, "%" + criterio + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Cliente(
                            rs.getInt("id_cliente"),
                            rs.getString("nombre"),
                            rs.getString("documento"),
                            rs.getString("telefono"),
                            rs.getString("correo")
                    ));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<Vehiculo> listarVehiculosPorCliente(int idCliente) {
        List<Vehiculo> lista = new ArrayList<>();
        String sql = "SELECT id_vehiculo, placa, marca, modelo, tipo FROM vehiculos WHERE id_cliente = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Vehiculo(
                            rs.getInt("id_vehiculo"),
                            rs.getString("placa"),
                            rs.getString("marca"),
                            rs.getString("modelo"),
                            rs.getString("tipo"),
                            null // Evitamos bucle recursivo, se mapea en la capa superior si se requiere
                    ));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean insertarServicio(Servicio servicio) {
        String sql = "INSERT INTO servicios (tipo_servicio, precio, estado, id_vehiculo, id_lavador) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, servicio.getTipoServicio());
            ps.setDouble(2, servicio.getPrecio());
            ps.setString(3, servicio.getEstado());
            ps.setInt(4, servicio.getVehiculo().getIdVehiculo());
            if (servicio.getLavadorAsignado() != null) {
                ps.setInt(5, servicio.getLavadorAsignado().getIdUsuario());
            } else {
                ps.setNull(5, Types.INTEGER);
            }

            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) servicio.setIdServicio(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean actualizarEstadoServicio(int idServicio, String nuevoEstado) {
        String sql = "UPDATE servicios SET estado = ? WHERE id_servicio = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idServicio);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Servicio> listarServiciosPorLavador(int idLavador) {
        List<Servicio> lista = new ArrayList<>();
        String sql = "SELECT s.id_servicio, s.tipo_servicio, s.precio, s.estado, s.id_vehiculo, v.placa, v.marca " +
                "FROM servicios s INNER JOIN vehiculos v ON s.id_vehiculo = v.id_vehiculo WHERE s.id_lavador = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idLavador);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Vehiculo v = new Vehiculo();
                    v.setIdVehiculo(rs.getInt("id_vehiculo"));
                    v.setPlaca(rs.getString("placa"));
                    v.setMarca(rs.getString("marca"));

                    Servicio s = new Servicio(
                            rs.getInt("id_servicio"),
                            rs.getString("tipo_servicio"),
                            rs.getDouble("precio"),
                            rs.getString("estado"),
                            v,
                            null
                    );
                    lista.add(s);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<Servicio> listarTodosLosServicios() {
        List<Servicio> lista = new ArrayList<>();
        String sql = "SELECT s.id_servicio, s.tipo_servicio, s.precio, s.estado, s.id_vehiculo, v.placa, s.id_lavador, u.nombre " +
                "FROM servicios s INNER JOIN vehiculos v ON s.id_vehiculo = v.id_vehiculo " +
                "LEFT JOIN usuarios u ON s.id_lavador = u.id_usuario";
        try (Connection con = ConexionBD.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Vehiculo v = new Vehiculo();
                v.setIdVehiculo(rs.getInt("id_vehiculo"));
                v.setPlaca(rs.getString("placa"));

                Lavador lav = null;
                int idLav = rs.getInt("id_lavador");
                if (!rs.wasNull()) {
                    lav = new Lavador(idLav, rs.getString("nombre"), "", "");
                }

                lista.add(new Servicio(
                        rs.getInt("id_servicio"),
                        rs.getString("tipo_servicio"),
                        rs.getDouble("precio"),
                        rs.getString("estado"),
                        v,
                        lav
                ));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean insertarPago(Pago pago) {
        // CORREGIDO: Se cambió 'fecha' por 'fecha_pago'
        String sql = "INSERT INTO pagos (fecha_pago, monto_total, metodo_pago, id_servicio) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setTimestamp(1, new Timestamp(pago.getFecha().getTime()));
            ps.setDouble(2, pago.getMontoTotal());
            ps.setString(3, pago.getMetodoPago());
            ps.setInt(4, pago.getServicio().getIdServicio());

            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) pago.setIdPago(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean insertarComprobante(Comprobante comprobante, int idPago) {
        // CORREGIDO: Nombres de columnas mapeados exactamente según el script .sql
        String sql = "INSERT INTO comprobantes (codigo_comprobante, subtotal, igv, total, tipo_comprobante, documento_fiscal, razon_social, id_pago) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, comprobante.getCodigoComprobante());
            ps.setDouble(2, comprobante.getSubtotal());
            ps.setDouble(3, comprobante.getIgv());
            ps.setDouble(4, comprobante.getTotal());

            if (comprobante instanceof Factura f) {
                ps.setString(5, "Factura");
                ps.setString(6, f.getRuc());
                ps.setString(7, f.getRazonSocial());
            } else {
                Boleta b = (Boleta) comprobante;
                ps.setString(5, "Boleta");
                ps.setString(6, b.getDniCliente());
                ps.setNull(7, Types.VARCHAR);
            }
            ps.setInt(8, idPago);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Pago> listarTodosLosPagos() {
        List<Pago> lista = new ArrayList<>();
        // CORREGIDO: Se cambió 'fecha' por 'fecha_pago' en la consulta SQL
        String sql = "SELECT id_pago, fecha_pago, monto_total, metodo_pago, id_servicio FROM pagos";
        try (Connection con = ConexionBD.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Servicio s = new Servicio();
                s.setIdServicio(rs.getInt("id_servicio"));

                lista.add(new Pago(
                        rs.getInt("id_pago"),
                        rs.getTimestamp("fecha_pago"), // CORREGIDO: Se lee 'fecha_pago' del ResultSet
                        rs.getDouble("monto_total"),
                        rs.getString("metodo_pago"),
                        s
                ));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }
}