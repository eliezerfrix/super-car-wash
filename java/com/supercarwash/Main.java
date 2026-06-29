package com.supercarwash;

import javax.swing.SwingUtilities;

import com.supercarwash.ui.EstiloUI;
import com.supercarwash.ui.LoginVista;

/**
 * Clase Principal de Arranque del Sistema Integrado Super Car Wash.
 * Coordina la inicialización asíncrona de la Interfaz Gráfica de Usuario (GUI)
 * bajo el hilo seguro de Swing.
 * * @author Herrera, Limo, Mino
 * @version 1.3
 */
public class Main {

    /**
     * Punto de entrada principal a la máquina virtual de Java (JVM).
     * @param args Argumentos opcionales de consola.
     */
    public static void main(String[] args) {

        // Ejecución segura de interfaces gráficas en el Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            try {
                // 1. Aplicar la configuración global de Look & Feel Dark Mode Premium
                EstiloUI.aplicarLookAndFeel();

                // 2. Inicializar la Vista de Login directamente utilizando su constructor base.
                // Como LoginVista ya maneja su propio usuarioDAO internamente,
                // no necesita recibir ninguna expresión lambda por parámetro.
                LoginVista login = new LoginVista();

                // 3. Centrar y desplegar la ventana de acceso
                login.setVisible(true);

            } catch (Exception e) {
                System.err.println("🚨 Error fatal al inicializar el entorno Super Car Wash: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}