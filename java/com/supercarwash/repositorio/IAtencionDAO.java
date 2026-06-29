package com.supercarwash.repositorio;

import com.supercarwash.modelo.*;
import java.util.List;

public interface IAtencionDAO {
    // Clientes y Vehículos
    boolean insertarCliente(Cliente cliente);
    boolean insertarVehiculo(Vehiculo vehiculo);
    List<Cliente> buscarClientes(String criterio);
    List<Vehiculo> listarVehiculosPorCliente(int idCliente);

    // Servicios y Boxes
    boolean insertarServicio(Servicio servicio);
    boolean actualizarEstadoServicio(int idServicio, String nuevoEstado);
    List<Servicio> listarServiciosPorLavador(int idLavador);
    List<Servicio> listarTodosLosServicios();

    // Pagos y Comprobantes Fiscales
    boolean insertarPago(Pago pago);
    boolean insertarComprobante(Comprobante comprobante, int idPago);
    List<Pago> listarTodosLosPagos();
}