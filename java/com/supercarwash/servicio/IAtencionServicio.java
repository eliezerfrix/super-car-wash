package com.supercarwash.servicio;

import com.supercarwash.modelo.Cliente;
import com.supercarwash.modelo.Vehiculo;
import com.supercarwash.modelo.Servicio;
import com.supercarwash.modelo.Pago;
import com.supercarwash.modelo.Comprobante;
import java.util.List;

/**
 * Contrato que rige el flujo completo operativo del Car Wash:
 * Registro -> Lavado -> Pago y Facturación.
 */
public interface IAtencionServicio {
    // Gestión de Clientes y Vehículos (Módulo del Cajero)
    boolean registrarClienteConVehiculo(Cliente cliente, Vehiculo vehiculo);
    List<Cliente> buscarClientes(String criterio);
    List<Vehiculo> listarVehiculosPorCliente(int idCliente);

    // Operaciones de Boxes de Lavado
    boolean registrarOrdenServicio(Servicio servicio);
    boolean actualizarEstadoLavado(int idServicio, String nuevoEstado);
    List<Servicio> listarServiciosPorLavador(int idLavador);
    List<Servicio> listarTodosLosServicios();

    // Procesamiento de Transacciones Financieras y Facturación Polimórfica
    Comprobante procesarPagoYFacturar(Pago pago, String tipoComprobante, String documentoFiscal, String razonSocial);
    List<Pago> generarReporteIngresos();
}