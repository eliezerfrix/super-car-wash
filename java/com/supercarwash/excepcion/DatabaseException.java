package com.supercarwash.excepcion;

/**
 * Excepción especializada para errores de persistencia en MySQL a través de JDBC.
 * Intercepta y envuelve los molestos SQLExceptions.
 * * @author Herrera, Limo, Mino
 * @version 1.0
 */
public class DatabaseException extends CarWashException {

    public DatabaseException(String mensaje) {
        super(mensaje, "ERR_DB_JDBC");
    }

    public DatabaseException(String mensaje, Throwable causa) {
        super(mensaje, "ERR_DB_TRANSACTION", causa);
    }
}