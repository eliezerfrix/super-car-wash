package com.supercarwash.servicio;

import com.supercarwash.modelo.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Núcleo lógico donde se ejecutan las validaciones del negocio automotriz.
 * Aplica orquestación polimórfica para la emisión de comprobantes.
 */
public class AtencionServicioImpl implements IAtencionServicio {

    private static final List<Cliente> clientesData = new ArrayList<>();
    private static final List<Vehiculo> vehiculosData = new ArrayList<>();
    private static final List<Servicio> serviciosData = new ArrayList<>();
    private static final List<Pago> pagosData = new ArrayList<>();

    @Override
    public boolean registrarClienteConVehiculo(Cliente cliente, Vehiculo vehiculo) {
        if (cliente == null || vehiculo == null) return false;
        if (cliente.getDocumento().trim().isEmpty() || vehiculo.getPlaca().trim().isEmpty()) return false;

        // Vinculación bidireccional en memoria/objeto antes de persistir
        vehiculo.setPropietario(cliente);
        clientesData.add(cliente);
        vehiculosData.add(vehiculo);
        return true;
    }

    @Override
    public List<Cliente> buscarClientes(String criterio) {
        List<Cliente> filtrados = new ArrayList<>();
        for (Cliente c : clientesData) {
            if (c.getNombre().toLowerCase().contains(criterio.toLowerCase()) ||
                    c.getDocumento().contains(criterio)) {
                filtrados.add(c);
            }
        }
        return filtrados;
    }

    @Override
    public List<Vehiculo> listarVehiculosPorCliente(int idCliente) {
        List<Vehiculo> filtrados = new ArrayList<>();
        for (Vehiculo v : vehiculosData) {
            if (v.getPropietario().getIdCliente() == idCliente) {
                filtrados.add(v);
            }
        }
        return filtrados;
    }

    @Override
    public boolean registrarOrdenServicio(Servicio servicio) {
        if (servicio == null || servicio.getVehiculo() == null) return false;
        servicio.setEstado("Pendiente"); // Forzado inicial por regla de negocio
        return serviciosData.add(servicio);
    }

    @Override
    public boolean actualizarEstadoLavado(int idServicio, String nuevoEstado) {
        // Regla de Negocio: Validar estados de la máquina de transición del Car Wash
        if (!nuevoEstado.equals("Pendiente") && !nuevoEstado.equals("En proceso") && !nuevoEstado.equals("Finalizado")) {
            return false;
        }

        for (Servicio s : serviciosData) {
            if (s.getIdServicio() == idServicio) {
                s.setEstado(nuevoEstado);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Servicio> listarServiciosPorLavador(int idLavador) {
        List<Servicio> filtrados = new ArrayList<>();
        for (Servicio s : serviciosData) {
            if (s.getLavadorAsignado() != null && s.getLavadorAsignado().getIdUsuario() == idLavador) {
                filtrados.add(s);
            }
        }
        return filtrados;
    }

    @Override
    public List<Servicio> listarTodosLosServicios() {
        return new ArrayList<>(serviciosData);
    }

    @Override
    public Comprobante procesarPagoYFacturar(Pago pago, String tipoComprobante, String documentoFiscal, String razonSocial) {
        if (pago == null || pago.getServicio() == null) return null;

        pagosData.add(pago);
        String codigoGenerado = "CP-" + (int)(Math.random() * 90000 + 10000);
        Comprobante comprobanteEmitido;

        // Uso del polimorfismo dinámico para la creación del documento (POO-05)
        if ("Factura".equalsIgnoreCase(tipoComprobante)) {
            comprobanteEmitido = new Factura(0, codigoGenerado, pago, documentoFiscal, razonSocial);
        } else {
            comprobanteEmitido = new Boleta(0, codigoGenerado, pago, documentoFiscal);
        }

        // El método interno calcularMontos() se ejecuta polimórficamente según la subclase
        comprobanteEmitido.calcularMontos();
        return comprobanteEmitido;
    }

    @Override
    public List<Pago> generarReporteIngresos() {
        return new ArrayList<>(pagosData);
    }
}