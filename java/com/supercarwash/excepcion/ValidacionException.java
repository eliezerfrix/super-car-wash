package com.supercarwash.excepcion;

/**
 * Excepción lanzada cuando los datos ingresados por la interfaz gráfica Swing
 * no superan las reglas de validación de negocio.
 * * @author Herrera, Limo, Mino
 * @version 1.0
 */
public class ValidacionException extends CarWashException {

    private final String campoAfectado;

    public ValidacionException(String mensaje, String campoAfectado) {
        super(mensaje, "ERR_BUSINESS_VALIDATION");
        this.campoAfectado = campoAfectado;
    }

    public String getCampoAfectado() {
        return campoAfectado;
    }
}