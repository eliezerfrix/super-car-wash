package com.supercarwash.excepcion;

import java.util.Date;

/**
 * Excepción base global para el ecosistema Super Car Wash.
 * Permite encapsular errores en tiempo de ejecución de manera controlada.
 * * @author Herrera, Limo, Mino
 * @version 1.0
 */
public class CarWashException extends RuntimeException {

    private final Date timestamp;
    private final String codigoError;

    public CarWashException(String mensaje, String codigoError) {
        super(mensaje);
        this.codigoError = codigoError;
        this.timestamp = new Date();
    }

    public CarWashException(String mensaje, String codigoError, Throwable causa) {
        super(mensaje, causa);
        this.codigoError = codigoError;
        this.timestamp = new Date();
    }

    public String getCodigoError() {
        return codigoError;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "CarWashException [" + codigoError + "] (" + timestamp + "): " + getMessage();
    }
}