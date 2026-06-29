package com.supercarwash.ui;

import com.supercarwash.modelo.Usuario;
import com.supercarwash.repositorio.UsuarioDAOImpl;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * Ventana de autenticación del sistema optimizada estéticamente.
 * Se mantiene intacta la conexión directa a MySQL y lógica de negocio.
 */
public class LoginVista extends JFrame {

    private JTextField campoUsuario;
    private JPasswordField campoClave;
    private JButton btnEntrar;
    private JLabel lblMensaje;
    private final UsuarioDAOImpl usuarioDAO;

    public LoginVista() {
        this.usuarioDAO = new UsuarioDAOImpl();
        EstiloUI.aplicarLookAndFeel();
        construirUI();
    }

    private void construirUI() {
        setTitle("Super Car Wash — Acceso al Sistema");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        // Ampliado el ancho para dar espacio al diseño moderno lateral
        setSize(780, 560);
        setLocationRelativeTo(null);

        // Contenedor principal con fondo oscuro unificado
        JPanel raiz = new JPanel(null);
        raiz.setBackground(EstiloUI.AZUL_OSCURO);
        setContentPane(raiz);

        // ---------------------------------------------------------------------
        // PANEL IZQUIERDO: Ilustración publicitaria y marca (Estilo Premium)
        // ---------------------------------------------------------------------
        JPanel panelIzquierdo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Degradado dinámico futurista
                GradientPaint gp = new GradientPaint(0, 0, new Color(15, 23, 42), getWidth(), getHeight(), new Color(8, 47, 73));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Círculos de luz decorativos de fondo (Glow)
                g2.setColor(new Color(6, 182, 212, 15));
                g2.fillOval(-50, -50, 300, 300);
                g2.fillOval(150, 300, 250, 250);

                // --- DIBUJO VECTORIAL MINIMALISTA DE UN AUTO ---
                g2.setColor(new Color(6, 182, 212, 40)); // Silueta de fondo
                // Chasis superior / Techo
                Path2D.Double car = new Path2D.Double();
                car.moveTo(80, 300);
                car.lineTo(130, 260);
                car.lineTo(240, 260);
                car.lineTo(290, 300);
                car.lineTo(320, 310);
                car.lineTo(320, 335);
                car.lineTo(50, 335);
                car.lineTo(50, 310);
                car.closePath();
                g2.fill(car);

                // Línea de brillo de velocidad de Neón (Cian)
                g2.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(EstiloUI.CIAN_ACENTO);
                g2.drawLine(60, 330, 310, 330); // Base línea de luz
                g2.drawLine(125, 265, 235, 265); // Techo línea de luz

                // Destellos de lavado (Efecto burbujas/limpieza)
                g2.setColor(new Color(248, 250, 252, 120));
                g2.fillOval(90, 230, 10, 10);
                g2.fillOval(260, 240, 14, 14);
                g2.setColor(EstiloUI.CIAN_ACENTO);
                g2.fillOval(290, 210, 8, 8);

                g2.dispose();
            }
        };
        panelIzquierdo.setBounds(0, 0, 360, 560);
        panelIzquierdo.setLayout(null);

        // Logo Circular SCW Interno del panel izquierdo
        JPanel panelLogo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(EstiloUI.AZUL_PANEL);
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.setStroke(new BasicStroke(2.5f));
                g2.setColor(EstiloUI.CIAN_ACENTO);
                g2.drawOval(1, 1, getWidth() - 3, getHeight() - 3);
                g2.dispose();
            }
        };
        panelLogo.setBounds(135, 60, 90, 90);
        panelLogo.setLayout(new BorderLayout());
        JLabel lblLogoText = new JLabel("SCW", SwingConstants.CENTER);
        lblLogoText.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblLogoText.setForeground(EstiloUI.CIAN_ACENTO);
        panelLogo.add(lblLogoText, BorderLayout.CENTER);
        panelIzquierdo.add(panelLogo);

        // Textos del panel izquierdo
        JLabel lblTitulo = new JLabel("SUPER CAR WASH", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(EstiloUI.BLANCO);
        lblTitulo.setBounds(20, 170, 320, 30);
        panelIzquierdo.add(lblTitulo);

        JLabel lblSub = new JLabel("Gestión Centralizada e Integral", SwingConstants.CENTER);
        lblSub.setFont(EstiloUI.FUENTE_SMALL);
        lblSub.setForeground(EstiloUI.GRIS_TEXTO);
        lblSub.setBounds(20, 200, 320, 20);
        panelIzquierdo.add(lblSub);

        raiz.add(panelIzquierdo);

        // ---------------------------------------------------------------------
        // PANEL DERECHO: El Formulario con efecto de sombra y bordes curvos
        // ---------------------------------------------------------------------
        // Contenedor estético del Formulario (Tarjeta flotante avanzada)
        JPanel panelForm = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Sombra difuminada (Glow) exterior simulada
                g2.setColor(new Color(0, 0, 0, 90));
                g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 16, 16);

                // Fondo del panel curvo
                g2.setColor(EstiloUI.AZUL_PANEL);
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 16, 16);

                // Borde suave pulido
                g2.setStroke(new BasicStroke(1f));
                g2.setColor(EstiloUI.AZUL_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 16, 16);
                g2.dispose();
            }
        };
        panelForm.setBackground(EstiloUI.AZUL_OSCURO); // Transparente al dibujo curvo
        panelForm.setBounds(395, 75, 345, 330);
        panelForm.setLayout(null);

        // Título de bienvenida interno
        JLabel lblLoginHeader = new JLabel("Iniciar Sesión");
        lblLoginHeader.setFont(EstiloUI.FUENTE_TITULO);
        lblLoginHeader.setForeground(EstiloUI.BLANCO);
        lblLoginHeader.setBounds(25, 25, 200, 30);
        panelForm.add(lblLoginHeader);

        // Campos de Texto
        JLabel lblUser = EstiloUI.labelForm("Usuario");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUser.setForeground(EstiloUI.GRIS_TEXTO);
        lblUser.setBounds(25, 80, 100, 20);
        panelForm.add(lblUser);

        campoUsuario = EstiloUI.crearCampoTexto();
        campoUsuario.setBounds(25, 105, 295, 40); // Ligeramente más alto para mejor UX
        panelForm.add(campoUsuario);

        JLabel lblPass = EstiloUI.labelForm("Contraseña");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPass.setForeground(EstiloUI.GRIS_TEXTO);
        lblPass.setBounds(25, 165, 100, 20);
        panelForm.add(lblPass);

        campoClave = EstiloUI.crearCampoClave();
        campoClave.setBounds(25, 190, 295, 40);
        panelForm.add(campoClave);

        // Zona de Mensajes de error/éxito
        lblMensaje = new JLabel(" ", SwingConstants.CENTER);
        lblMensaje.setFont(EstiloUI.FUENTE_NORMAL);
        lblMensaje.setBounds(25, 255, 295, 25);
        panelForm.add(lblMensaje);

        raiz.add(panelForm);

        // Botón de Acceso (Usa nuestro nuevo renderizado redondeado premium de EstiloUI)
        btnEntrar = EstiloUI.crearBotonLoginPremium("Ingresar al sistema");
        btnEntrar.setBounds(395, 430, 345, 48);
        raiz.add(btnEntrar);

        // Eventos de teclado y ratón
        btnEntrar.addActionListener(e -> ejecutarLogin());
        campoClave.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) ejecutarLogin();
            }
        });
    }

    private void ejecutarLogin() {
        String usuario = campoUsuario.getText().trim();
        String clave = new String(campoClave.getPassword());

        if (usuario.isEmpty() || clave.isEmpty()) {
            mostrarError("Por favor, complete ambos campos.");
            return;
        }

        btnEntrar.setEnabled(false);
        btnEntrar.setText("Autenticando...");
        lblMensaje.setText(" ");

        SwingWorker<Usuario, Void> worker = new SwingWorker<>() {
            @Override
            protected Usuario doInBackground() {
                return usuarioDAO.obtenerPorCredenciales(usuario, clave);
            }

            @Override
            protected void done() {
                try {
                    Usuario userLogueado = get();
                    if (userLogueado != null) {
                        lblMensaje.setForeground(EstiloUI.VERDE_OK);
                        lblMensaje.setText("✓ Bienvenido · " + userLogueado.getNombre());

                        Timer t = new Timer(600, ev -> {
                            dispose();
                            abrirPanelSegunRol(userLogueado.getRol(), userLogueado.getNombre());
                        });
                        t.setRepeats(false);
                        t.start();
                    } else {
                        mostrarError("Credenciales incorrectas. Intente de nuevo.");
                        campoClave.setText("");
                        campoUsuario.requestFocus();
                    }
                } catch (Exception ex) {
                    mostrarError("Error en la conexión con MySQL.");
                } finally {
                    btnEntrar.setEnabled(true);
                    btnEntrar.setText("Ingresar al sistema");
                }
            }
        };
        worker.execute();
    }

    private void mostrarError(String msg) {
        lblMensaje.setForeground(EstiloUI.ROJO_ERROR);
        lblMensaje.setText(msg);
    }

    private void abrirPanelSegunRol(String rol, String nombre) {
        switch (rol) {
            case "Administrador" -> new PanelAdministrador(nombre).setVisible(true);
            case "Cajero"        -> new PanelCajero(nombre).setVisible(true);
            default              -> new PanelOperario(nombre).setVisible(true);
        }
    }
}