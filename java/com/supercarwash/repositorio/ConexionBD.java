package com.supercarwash.repositorio;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Clase utilitaria encargada de administrar el ciclo de vida de la conexión física
 * con el servidor MySQL local o remoto en Lambayeque.
 */
public class ConexionBD {
    private static String URL;
    private static String USER;
    private static String PASSWORD;

    static {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            props.load(fis);
            URL = props.getProperty("db.url");
            USER = props.getProperty("db.user");
            PASSWORD = props.getProperty("db.password");
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar config.properties. Verifique que el archivo exista en la raíz del proyecto.", e);
        }
    }

    /**
     * Obtiene una instancia activa de conexión JDBC.
     * @return Connection objeto de conexión nativo de Java.
     * @throws SQLException si falla la comunicación de red o las credenciales.
     */
    public static Connection getConexion() throws SQLException {
        try {
            // Registro explícito del Driver nativo de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Error crítico: Driver JDBC de MySQL no encontrado en el classpath.", e);
        }
    }
}