package com.supercarwash.modelo;

/**
 * Comprobante corporativo con desglose estricto del IGV (18%).
 */
public class Factura extends Comprobante {
    private String ruc;
    private String razonSocial;

    public Factura() { super(); }

    public Factura(int idComprobante, String codigoComprobante, Pago pago, String ruc, String razonSocial) {
        super(idComprobante, codigoComprobante, pago);
        this.ruc = ruc;
        this.razonSocial = razonSocial;
        calcularMontos();
    }

    @Override
    public void calcularMontos() {
        // Desglose matemático estricto del 18% del IGV en base al total transaccionado
        double totalPago = getPago().getMontoTotal();
        double calculadoSubtotal = totalPago / 1.18;
        double calculadoIgv = totalPago - calculadoSubtotal;

        setTotal(totalPago);
        setSubtotal(calculadoSubtotal);
        setIgv(calculadoIgv);
    }

    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }
}