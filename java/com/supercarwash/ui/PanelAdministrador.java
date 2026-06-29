package com.supercarwash.ui;

import com.supercarwash.modelo.*;
import com.supercarwash.repositorio.*;
import com.supercarwash.servicio.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Date;

/**
 * Módulo Central de Administración - Super Car Wash
 * Proporciona control total sobre personal, reportes financieros y auditoría.
 *
 * ESTRUCTURA:
 * 1. Gestión de Personal (CRUD JDBC)
 * 2. Reportes Gerenciales (Métricas en Tiempo Real)
 * 3. Configuración de Sistema
 *
 * @author Herrera, Limo, Mino
 * @version 1.3
 */
public class PanelAdministrador extends JFrame {

    // Capas de Servicio y Repositorio (Inyección de dependencias interna)
    private final IUsuarioDAO usuarioDAO;
    private final IAtencionDAO atencionDAO;
    private final String adminNombre;

    // Componentes de la Interfaz - Usuarios
    private DefaultTableModel modeloUsuarios;
    private JTable tablaUsuarios;
    private JTextField txtId, txtNombre, txtUser;
    private JPasswordField txtClave;
    private JComboBox<String> cbRol;
    private JLabel lblMsgUsuarios;

    // Componentes de la Interfaz - Reportes
    private JLabel lblTotalVentas, lblServiciosHoy, lblTicketPromedio;
    private JTextArea areaLogAuditoria;

    /**
     * Constructor principal del panel administrativo.
     * @param nombre Nombre del administrador que inicia sesión.
     */
    public PanelAdministrador(String nombre) {
        this.adminNombre = nombre;
        this.usuarioDAO = new UsuarioDAOImpl();
        this.atencionDAO = new AtencionDAOImpl();

        EstiloUI.aplicarLookAndFeel();
        configurarVentanaPrincipal();
        inicializarComponentes();

        // Carga inicial de datos
        actualizarTablaUsuarios();
        actualizarMetricasDashboard();
    }

    private void configurarVentanaPrincipal() {
        setTitle("Super Car Wash — Módulo Administrativo Premium");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1150, 750);
        setLocationRelativeTo(null);
        getContentPane().setBackground(EstiloUI.AZUL_OSCURO);
        setLayout(new BorderLayout());
    }

    private void inicializarComponentes() {
        // 1. Header Superior
        add(crearCabeceraSistema(), BorderLayout.NORTH);

        // 2. Panel Central con Pestañas (Tabs)
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(EstiloUI.AZUL_OSCURO);
        tabs.setForeground(EstiloUI.GRIS_TEXTO);
        tabs.setFont(EstiloUI.FUENTE_SUB);

        tabs.addTab("👤 Gestión de Personal", crearModuloPersonal());
        tabs.addTab("📊 Dashboard & Reportes", crearModuloReportes());
        tabs.addTab("⚙️ Configuración", crearModuloConfiguracion());

        add(tabs, BorderLayout.CENTER);

        // 3. Barra de Estado Inferior
        add(crearBarraEstado(), BorderLayout.SOUTH);
    }

    /**
     * Crea la cabecera con el logo y el nombre del usuario logueado.
     */
    private JPanel crearCabeceraSistema() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(EstiloUI.AZUL_PANEL);
        header.setPreferredSize(new Dimension(0, 70));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, EstiloUI.CIAN_ACENTO));

        // Título Izquierdo
        JLabel titulo = new JLabel("  SUPER CAR WASH — Chiclayo");
        titulo.setFont(EstiloUI.FUENTE_TITULO);
        titulo.setForeground(EstiloUI.BLANCO);
        header.add(titulo, BorderLayout.WEST);

        // Información de Sesión Derecha
        JPanel sesion = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 18));
        sesion.setOpaque(false);

        JLabel userIcon = new JLabel("🛡️ Sesión: " + adminNombre + " [ADMIN]");
        userIcon.setForeground(EstiloUI.CIAN_ACENTO);
        userIcon.setFont(EstiloUI.FUENTE_SUB);

        JButton btnLogout = EstiloUI.crearBotonalPrincipal("Cerrar Sesión");
        btnLogout.setPreferredSize(new Dimension(140, 30));
        btnLogout.addActionListener(e -> cerrarSesion());

        sesion.add(userIcon);
        sesion.add(btnLogout);
        header.add(sesion, BorderLayout.EAST);

        return header;
    }

    /**
     * MODULO: GESTIÓN DE PERSONAL
     * Implementa el CRUD completo conectado a MySQL.
     */
    private JPanel crearModuloPersonal() {
        JPanel main = new JPanel(new BorderLayout(20, 20));
        main.setBackground(EstiloUI.AZUL_OSCURO);
        main.setBorder(new EmptyBorder(20, 25, 20, 25));

        // --- Panel de Formulario (Izquierdo) ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(EstiloUI.AZUL_PANEL);
        formPanel.setBorder(new CompoundBorder(
                new LineBorder(EstiloUI.AZUL_BORDE),
                new EmptyBorder(25, 25, 25, 25)));
        formPanel.setPreferredSize(new Dimension(380, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);

        // Campos del formulario
        txtId = EstiloUI.crearCampoTexto();
        txtId.setEditable(false);
        txtId.setToolTipText("ID Autogenerado");

        txtNombre = EstiloUI.crearCampoTexto();
        txtUser = EstiloUI.crearCampoTexto();
        txtClave = EstiloUI.crearCampoClave();

        String[] roles = {"Cajero", "Operario", "Administrador"};
        cbRol = new JComboBox<>(roles);
        cbRol.setBackground(EstiloUI.AZUL_OSCURO);
        cbRol.setForeground(EstiloUI.BLANCO);

        // Construcción del Layout del Formulario
        gbc.gridy = 0; formPanel.add(EstiloUI.labelForm("ID Registro:"), gbc);
        gbc.gridy = 1; formPanel.add(txtId, gbc);
        gbc.gridy = 2; formPanel.add(EstiloUI.labelForm("Nombre Completo:"), gbc);
        gbc.gridy = 3; formPanel.add(txtNombre, gbc);
        gbc.gridy = 4; formPanel.add(EstiloUI.labelForm("Usuario de Acceso:"), gbc);
        gbc.gridy = 5; formPanel.add(txtUser, gbc);
        gbc.gridy = 6; formPanel.add(EstiloUI.labelForm("Contraseña Privada:"), gbc);
        gbc.gridy = 7; formPanel.add(txtClave, gbc);
        gbc.gridy = 8; formPanel.add(EstiloUI.labelForm("Rol en Empresa:"), gbc);
        gbc.gridy = 9; formPanel.add(cbRol, gbc);

        // Botonera de Acciones CRUD
        JPanel botones = new JPanel(new GridLayout(2, 2, 10, 10));
        botones.setOpaque(false);

        JButton btnGuardar = EstiloUI.crearBotonalPrincipal("💾 GUARDAR");
        JButton btnEditar = EstiloUI.crearBotonalPrincipal("✏️ EDITAR");
        JButton btnEliminar = EstiloUI.crearBotonalPrincipal("🗑️ ELIMINAR");
        JButton btnLimpiar = EstiloUI.crearBotonalPrincipal("🧹 LIMPIAR");

        btnGuardar.addActionListener(e -> accionGuardarUsuario());
        btnEditar.addActionListener(e -> accionEditarUsuario());
        btnEliminar.addActionListener(e -> accionEliminarUsuario());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        botones.add(btnGuardar);
        botones.add(btnEditar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);

        gbc.gridy = 10; gbc.insets = new Insets(20, 0, 0, 0);
        formPanel.add(botones, gbc);

        lblMsgUsuarios = new JLabel("Estado: Listo");
        lblMsgUsuarios.setForeground(EstiloUI.GRIS_TEXTO);
        gbc.gridy = 11; formPanel.add(lblMsgUsuarios, gbc);

        // --- Panel de Tabla (Derecho) ---
        String[] columnas = {"ID", "Nombre", "Usuario", "Rol"};
        modeloUsuarios = new DefaultTableModel(columnas, 0);
        tablaUsuarios = new JTable(modeloUsuarios);
        JScrollPane scroll = EstiloUI.estilizarTabla(tablaUsuarios);

        tablaUsuarios.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cargarDatosSeleccionados();
            }
        });

        main.add(formPanel, BorderLayout.WEST);
        main.add(scroll, BorderLayout.CENTER);

        return main;
    }

    /**
     * MODULO: REPORTES GERENCIALES
     * Muestra indicadores clave de rendimiento (KPIs).
     */
    private JPanel crearModuloReportes() {
        JPanel main = new JPanel(new BorderLayout(20, 20));
        main.setBackground(EstiloUI.AZUL_OSCURO);
        main.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Paneles de Métricas (Top Cards)
        JPanel cards = new JPanel(new GridLayout(1, 3, 20, 0));
        cards.setOpaque(false);
        cards.setPreferredSize(new Dimension(0, 150));

        lblTotalVentas = crearCardMetrica(cards, "INGRESOS TOTALES", "S/. 0.00", EstiloUI.VERDE_OK);
        lblServiciosHoy = crearCardMetrica(cards, "SERVICIOS REALIZADOS", "0", EstiloUI.CIAN_ACENTO);
        lblTicketPromedio = crearCardMetrica(cards, "TICKET PROMEDIO", "S/. 0.00", EstiloUI.BLANCO);

        // Area de Auditoría (Centro)
        areaLogAuditoria = new JTextArea();
        areaLogAuditoria.setEditable(false);
        areaLogAuditoria.setBackground(new Color(15, 23, 42));
        areaLogAuditoria.setForeground(EstiloUI.CIAN_ACENTO);
        areaLogAuditoria.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollLog = new JScrollPane(areaLogAuditoria);
        scrollLog.setBorder(new TitledBorder(new LineBorder(EstiloUI.AZUL_BORDE),
                "LOG DE OPERACIONES - AUDITORÍA", TitledBorder.LEFT, TitledBorder.TOP,
                EstiloUI.FUENTE_SUB, EstiloUI.BLANCO));

        JButton btnActualizar = EstiloUI.crearBotonalPrincipal("REGENERAR REPORTES Y MÉTRICAS");
        btnActualizar.addActionListener(e -> actualizarMetricasDashboard());

        main.add(cards, BorderLayout.NORTH);
        main.add(scrollLog, BorderLayout.CENTER);
        main.add(btnActualizar, BorderLayout.SOUTH);

        return main;
    }

    private JLabel crearCardMetrica(JPanel container, String titulo, String valor, Color colorValor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(EstiloUI.AZUL_PANEL);
        card.setBorder(new LineBorder(EstiloUI.AZUL_BORDE, 1, true));

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setForeground(EstiloUI.GRIS_TEXTO);
        lblTitulo.setFont(EstiloUI.FUENTE_SMALL);

        JLabel lblValor = new JLabel(valor, SwingConstants.CENTER);
        lblValor.setForeground(colorValor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 32));

        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);
        container.add(card);

        return lblValor;
    }

    private JPanel crearModuloConfiguracion() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 100));
        p.setBackground(EstiloUI.AZUL_OSCURO);
        JLabel aviso = new JLabel("Ajustes de Sistema: Precios, Promociones e Impuestos (Mantenimiento)");
        aviso.setForeground(EstiloUI.GRIS_TEXTO);
        p.add(aviso);
        return p;
    }

    private JPanel crearBarraEstado() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barra.setBackground(new Color(10, 15, 30));
        barra.setPreferredSize(new Dimension(0, 30));
        JLabel info = new JLabel("  Conectado a MySQL: supercarwash_db  |  Sede: Chiclayo, Perú  |  V1.2.0");
        info.setForeground(EstiloUI.GRIS_TEXTO);
        info.setFont(EstiloUI.FUENTE_SMALL);
        barra.add(info);
        return barra;
    }

    // --- LÓGICA DE NEGOCIO Y PERSISTENCIA ---

    private void actualizarTablaUsuarios() {
        modeloUsuarios.setRowCount(0);
        List<Usuario> lista = usuarioDAO.listarTodos();
        for (Usuario u : lista) {
            modeloUsuarios.addRow(new Object[]{
                    u.getIdUsuario(), u.getNombre(), u.getUsuario(), u.getRol()
            });
        }
        registrarLog("Sincronización exitosa con la tabla 'usuarios' de MySQL.");
    }

    private void cargarDatosSeleccionados() {
        int fila = tablaUsuarios.getSelectedRow();
        if (fila != -1) {
            txtId.setText(modeloUsuarios.getValueAt(fila, 0).toString());
            txtNombre.setText(modeloUsuarios.getValueAt(fila, 1).toString());
            txtUser.setText(modeloUsuarios.getValueAt(fila, 2).toString());
            cbRol.setSelectedItem(modeloUsuarios.getValueAt(fila, 3).toString());
            txtClave.setText(""); // Por seguridad no se muestra
            lblMsgUsuarios.setText("Modo: Edición de " + txtUser.getText());
        }
    }

    private void accionGuardarUsuario() {
        String nombre = txtNombre.getText();
        String user = txtUser.getText();
        String clave = new String(txtClave.getPassword());
        String rol = cbRol.getSelectedItem().toString();

        if (nombre.isEmpty() || user.isEmpty() || clave.isEmpty()) {
            EstiloUI.mostrarError(this, "Todos los campos son obligatorios para el registro.");
            return;
        }

        Usuario nuevo = switch (rol) {
            case "Administrador" -> new Administrador(0, nombre, user, clave);
            case "Cajero" -> new Cajero(0, nombre, user, clave);
            default -> new Lavador(0, nombre, user, clave);
        };

        if (usuarioDAO.insertar(nuevo)) {
            EstiloUI.mostrarExito(this, "Personal registrado correctamente.");
            registrarLog("Usuario '" + user + "' creado por " + adminNombre);
            actualizarTablaUsuarios();
            limpiarFormulario();
        }
    }

    private void accionEditarUsuario() {
        if (txtId.getText().isEmpty()) return;

        Usuario mod = switch (cbRol.getSelectedItem().toString()) {
            case "Administrador" -> new Administrador(Integer.parseInt(txtId.getText()), txtNombre.getText(), txtUser.getText(), new String(txtClave.getPassword()));
            case "Cajero" -> new Cajero(Integer.parseInt(txtId.getText()), txtNombre.getText(), txtUser.getText(), new String(txtClave.getPassword()));
            default -> new Lavador(Integer.parseInt(txtId.getText()), txtNombre.getText(), txtUser.getText(), new String(txtClave.getPassword()));
        };

        if (usuarioDAO.modificar(mod)) {
            EstiloUI.mostrarExito(this, "Datos actualizados exitosamente.");
            registrarLog("Usuario ID " + txtId.getText() + " modificado.");
            actualizarTablaUsuarios();
        }
    }

    private void accionEliminarUsuario() {
        if (txtId.getText().isEmpty()) return;

        if (EstiloUI.confirmar(this, "¿Está seguro de eliminar a " + txtUser.getText() + "?")) {
            if (usuarioDAO.eliminar(Integer.parseInt(txtId.getText()))) {
                registrarLog("Usuario ID " + txtId.getText() + " eliminado permanentemente.");
                actualizarTablaUsuarios();
                limpiarFormulario();
            }
        }
    }

    private void actualizarMetricasDashboard() {
        // Consultas directas al repositorio AtencionDAO
        List<Pago> pagos = atencionDAO.listarTodosLosPagos();
        List<Servicio> servicios = atencionDAO.listarTodosLosServicios();

        double total = 0;
        for (Pago p : pagos) total += p.getMontoTotal();

        lblTotalVentas.setText(String.format("S/. %.2f", total));
        lblServiciosHoy.setText(String.valueOf(servicios.size()));

        double promedio = servicios.isEmpty() ? 0 : total / servicios.size();
        lblTicketPromedio.setText(String.format("S/. %.2f", promedio));

        registrarLog("Métricas financieras actualizadas a las " + new Date());
    }

    private void limpiarFormulario() {
        txtId.setText("");
        txtNombre.setText("");
        txtUser.setText("");
        txtClave.setText("");
        cbRol.setSelectedIndex(0);
        lblMsgUsuarios.setText("Estado: Listo");
    }

    private void registrarLog(String msg) {
        areaLogAuditoria.append("[" + new Date() + "] >> " + msg + "\n");
    }

    private void cerrarSesion() {
        if (EstiloUI.confirmar(this, "¿Desea cerrar la sesión administrativa?")) {
            this.dispose();
            new LoginVista().setVisible(true);
        }
    }
}
