package com.supercarwash.ui;

import com.supercarwash.modelo.*;
import com.supercarwash.repositorio.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

/**
 * Módulo Central de Operaciones del Cajero - Super Car Wash
 * Proporciona el flujo operativo completo: Control de Clientes, Vehículos,
 * Asignación de Boxes, Órdenes de Lavado y Facturación Polimórfica Integrada.
 * * ESTRUCTURA DETALLADA DE LA CLASE:
 * 1. Inicialización de Componentes Visuales y Estilos Dark Mode
 * 2. Pestaña 1: Registro y Búsqueda de Clientes + Vehículos
 * 3. Pestaña 2: Control de Servicios y Caja (Orden de Lavado)
 * 4. Pestaña 3: Facturación (Emisión Polimórfica de Boletas y Facturas)
 * 5. Lógica de Sincronización JDBC en Hilos Asíncronos (SwingWorker)
 * * @author Herrera, Limo, Mino
 * @version 1.4
 */
public class PanelCajero extends JFrame {

    // Capas de Datos y Acceso Físico a MySQL
    private final IAtencionDAO atencionDAO;
    private final IUsuarioDAO usuarioDAO;
    private final String cajeroNombre;

    // Listas de Caché Local de Datos para ComboBoxes y Tablas
    private List<Cliente> clientesCache = new ArrayList<>();
    private List<Usuario> operariosCache = new ArrayList<>();
    private List<Servicio> serviciosCache = new ArrayList<>();

    // Componentes Gráficos - Pestaña 1: Clientes y Vehículos
    private DefaultTableModel modeloClientes, modeloVehiculos;
    private JTable tablaClientes, tablaVehiculos;
    private JTextField txtCliNombre, txtCliDoc, txtCliTel, txtCliCorreo;
    private JTextField txtVehPlaca, txtVehMarca, txtVehModelo;
    private JComboBox<String> cbVehTipo;
    private JLabel lblEstadoCli, lblEstadoVeh;

    // Componentes Gráficos - Pestaña 2: Gestión de Servicios/Orden
    private DefaultTableModel modeloServicios;
    private JTable tablaServicios;
    private JComboBox<String> cbServTipo;
    private JTextField txtServPrecio;
    private JComboBox<String> cbServOperario;
    private JLabel lblEstadoServ;

    // Componentes Gráficos - Pestaña 3: Módulo de Caja y Facturación
    private JTextField txtFacMonto, txtFacDocFiscal, txtFacRazonSocial;
    private JComboBox<String> cbFacTipoComprobante, cbFacMetodoPago;
    private JTextArea areaImpresionTicket;
    private JLabel lblEstadoFactura;

    /**
     * Constructor principal del panel de operaciones de caja.
     * @param nombre Nombre del cajero autenticado.
     */
    public PanelCajero(String nombre) {
        this.cajeroNombre = nombre;
        this.atencionDAO = new AtencionDAOImpl();
        this.usuarioDAO = new UsuarioDAOImpl();

        EstiloUI.aplicarLookAndFeel();
        configurarVentana();
        inicializarComponentes();

        // Sincronización inicial asíncrona de datos desde MySQL
        recargarDatosDesdeBD();
    }

    private void configurarVentana() {
        setTitle("Super Car Wash — Terminal Operativo de Caja y Facturación");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 780);
        setLocationRelativeTo(null);
        getContentPane().setBackground(EstiloUI.AZUL_OSCURO);
        setLayout(new BorderLayout());
    }

    private void inicializarComponentes() {
        // 1. Barra superior de Información Corporativa y Usuario
        add(crearCabeceraCajero(), BorderLayout.NORTH);

        // 2. Contenedor de Pestañas Operativas
        JTabbedPane tabsCajero = new JTabbedPane();
        tabsCajero.setBackground(EstiloUI.AZUL_OSCURO);
        tabsCajero.setForeground(EstiloUI.GRIS_TEXTO);
        tabsCajero.setFont(EstiloUI.FUENTE_SUB);

        tabsCajero.addTab("🚗 Admisión (Cliente/Vehículo)", crearPestañaAdmision());
        tabsCajero.addTab("🧼 Cola de Boxes y Servicios", crearPestañaServicios());
        tabsCajero.addTab("💳 Terminal de Pago y Facturación", crearPestañaFacturacion());

        add(tabsCajero, BorderLayout.CENTER);

        // 3. Barra de Estado de Red
        add(crearBarraEstadoInferior(), BorderLayout.SOUTH);
    }

    private JPanel crearCabeceraCajero() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(EstiloUI.AZUL_PANEL);
        header.setPreferredSize(new Dimension(0, 65));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, EstiloUI.CIAN_ACENTO));

        JLabel titulo = new JLabel("  MÓDULO DE CAJA AUTOMOTRIZ");
        titulo.setFont(EstiloUI.FUENTE_TITULO);
        titulo.setForeground(EstiloUI.BLANCO);
        header.add(titulo, BorderLayout.WEST);

        JPanel sesionInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        sesionInfo.setOpaque(false);

        JLabel lblUser = new JLabel("💵 Operador: " + cajeroNombre);
        lblUser.setFont(EstiloUI.FUENTE_SUB);
        lblUser.setForeground(EstiloUI.BLANCO);

        JButton btnSalir = EstiloUI.crearBotonalPrincipal("Cerrar Caja");
        btnSalir.setPreferredSize(new Dimension(120, 30));
        btnSalir.addActionListener(e -> cerrarCaja());

        sesionInfo.add(lblUser);
        sesionInfo.add(btnSalir);
        header.add(sesionInfo, BorderLayout.EAST);

        return header;
    }

    /**
     * PESTAÑA 1: ADMISIÓN DE CLIENTES Y VEHÍCULOS
     */
    private JPanel crearPestañaAdmision() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(EstiloUI.AZUL_OSCURO);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Formulario Izquierdo (Dividido en Cliente y Vehículo)
        JPanel formIzquierdo = new JPanel();
        formIzquierdo.setLayout(new BoxLayout(formIzquierdo, BoxLayout.Y_AXIS));
        formIzquierdo.setOpaque(false);
        formIzquierdo.setPreferredSize(new Dimension(380, 0));

        // Sub-Sección Cliente
        JPanel panelFormCli = new JPanel(new GridBagLayout());
        panelFormCli.setBackground(EstiloUI.AZUL_PANEL);
        panelFormCli.setBorder(new TitledBorder(new LineBorder(EstiloUI.AZUL_BORDE), "DATOS DEL CLIENTE",
                TitledBorder.LEFT, TitledBorder.TOP, EstiloUI.FUENTE_SUB, EstiloUI.BLANCO));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

        txtCliNombre = EstiloUI.crearCampoTexto();
        txtCliDoc = EstiloUI.crearCampoTexto();
        txtCliTel = EstiloUI.crearCampoTexto();
        txtCliCorreo = EstiloUI.crearCampoTexto();

        gbc.gridy = 0; panelFormCli.add(EstiloUI.labelForm("Documento (DNI/RUC):"), gbc);
        gbc.gridy = 1; panelFormCli.add(txtCliDoc, gbc);
        gbc.gridy = 2; panelFormCli.add(EstiloUI.labelForm("Nombre / Razón Social:"), gbc);
        gbc.gridy = 3; panelFormCli.add(txtCliNombre, gbc);
        gbc.gridy = 4; panelFormCli.add(EstiloUI.labelForm("Teléfono Celular:"), gbc);
        gbc.gridy = 5; panelFormCli.add(txtCliTel, gbc);
        gbc.gridy = 6; panelFormCli.add(EstiloUI.labelForm("Correo Electrónico:"), gbc);
        gbc.gridy = 7; panelFormCli.add(txtCliCorreo, gbc);

        lblEstadoCli = new JLabel(" ");
        lblEstadoCli.setFont(EstiloUI.FUENTE_SMALL);
        gbc.gridy = 8; panelFormCli.add(lblEstadoCli, gbc);

        // Sub-Sección Vehículo
        JPanel panelFormVeh = new JPanel(new GridBagLayout());
        panelFormVeh.setBackground(EstiloUI.AZUL_PANEL);
        panelFormVeh.setBorder(new TitledBorder(new LineBorder(EstiloUI.AZUL_BORDE), "DATOS DEL VEHÍCULO",
                TitledBorder.LEFT, TitledBorder.TOP, EstiloUI.FUENTE_SUB, EstiloUI.BLANCO));

        txtVehPlaca = EstiloUI.crearCampoTexto();
        txtVehMarca = EstiloUI.crearCampoTexto();
        txtVehModelo = EstiloUI.crearCampoTexto();
        String[] tipos = {"Automóvil", "Camioneta SUV", "Pick-up", "Motocicleta", "Furgón"};
        cbVehTipo = new JComboBox<>(tipos);
        cbVehTipo.setBackground(EstiloUI.AZUL_OSCURO);
        cbVehTipo.setForeground(EstiloUI.BLANCO);

        gbc.gridy = 0; panelFormVeh.add(EstiloUI.labelForm("Número de Placa:"), gbc);
        gbc.gridy = 1; panelFormVeh.add(txtVehPlaca, gbc);
        gbc.gridy = 2; panelFormVeh.add(EstiloUI.labelForm("Marca:"), gbc);
        gbc.gridy = 3; panelFormVeh.add(txtVehMarca, gbc);
        gbc.gridy = 4; panelFormVeh.add(EstiloUI.labelForm("Modelo:"), gbc);
        gbc.gridy = 5; panelFormVeh.add(txtVehModelo, gbc);
        gbc.gridy = 6; panelFormVeh.add(EstiloUI.labelForm("Tipo de Carrocería:"), gbc);
        gbc.gridy = 7; panelFormVeh.add(cbVehTipo, gbc);

        lblEstadoVeh = new JLabel(" ");
        lblEstadoVeh.setFont(EstiloUI.FUENTE_SMALL);
        gbc.gridy = 8; panelFormVeh.add(lblEstadoVeh, gbc);

        // Botonera de Registro Conjunto
        JButton btnRegistrarAdmision = EstiloUI.crearBotonalPrincipal("💾 REGISTRAR ADMISIÓN COMPLETA");
        btnRegistrarAdmision.setPreferredSize(new Dimension(0, 45));
        btnRegistrarAdmision.addActionListener(e -> registrarAdmisionCompleta());

        formIzquierdo.add(panelFormCli);
        formIzquierdo.add(Box.createVerticalStrut(10));
        formIzquierdo.add(panelFormVeh);
        formIzquierdo.add(Box.createVerticalStrut(10));
        formIzquierdo.add(btnRegistrarAdmision);

        // Tablas Derechas (Visualización en tiempo real)
        JPanel panelTablas = new JPanel(new GridLayout(2, 1, 0, 15));
        panelTablas.setOpaque(false);

        modeloClientes = new DefaultTableModel(new String[]{"ID", "Documento", "Nombre", "Celular"}, 0);
        tablaClientes = new JTable(modeloClientes);
        JScrollPane scCli = EstiloUI.estilizarTabla(tablaClientes);
        scCli.setBorder(new TitledBorder(new LineBorder(EstiloUI.AZUL_BORDE), "CLIENTES PERSISTIDOS"));

        modeloVehiculos = new DefaultTableModel(new String[]{"ID", "Placa", "Marca", "Modelo", "Tipo"}, 0);
        tablaVehiculos = new JTable(modeloVehiculos);
        JScrollPane scVeh = EstiloUI.estilizarTabla(tablaVehiculos);
        scVeh.setBorder(new TitledBorder(new LineBorder(EstiloUI.AZUL_BORDE), "VEHÍCULOS DEL CLIENTE SELECCIONADO"));

        tablaClientes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cargarVehiculosClienteSeleccionado();
            }
        });

        panelTablas.add(scCli);
        panelTablas.add(scVeh);

        panel.add(formIzquierdo, BorderLayout.WEST);
        panel.add(panelTablas, BorderLayout.CENTER);

        return panel;
    }

    /**
     * PESTAÑA 2: GESTIÓN DE ORDENES DE SERVICIO Y ASIGNACIÓN DE BOXES
     */
    private JPanel crearPestañaServicios() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(EstiloUI.AZUL_OSCURO);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JPanel formServ = new JPanel(new GridBagLayout());
        formServ.setBackground(EstiloUI.AZUL_PANEL);
        formServ.setBorder(new CompoundBorder(new LineBorder(EstiloUI.AZUL_BORDE), new EmptyBorder(15, 15, 15, 15)));
        formServ.setPreferredSize(new Dimension(380, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.weightx = 1.0;

        String[] catalogo = {"Lavado Básico Exterior", "Lavado Premium Salon", "Aspirado Profundo", "Encerado Orbital", "Lavado de Motor"};
        cbServTipo = new JComboBox<>(catalogo);
        cbServTipo.setBackground(EstiloUI.AZUL_OSCURO);
        cbServTipo.setForeground(EstiloUI.BLANCO);

        txtServPrecio = EstiloUI.crearCampoTexto();
        txtServPrecio.setText("45.00"); // Precio base estimado

        cbServOperario = new JComboBox<>();
        cbServOperario.setBackground(EstiloUI.AZUL_OSCURO);
        cbServOperario.setForeground(EstiloUI.BLANCO);

        cbServTipo.addActionListener(e -> ajustarPrecioServicio());

        gbc.gridy = 0; formServ.add(EstiloUI.labelForm("Tipo de Servicio Automotriz:"), gbc);
        gbc.gridy = 1; formServ.add(cbServTipo, gbc);
        gbc.gridy = 2; formServ.add(EstiloUI.labelForm("Precio Sugerido (S/.):"), gbc);
        gbc.gridy = 3; formServ.add(txtServPrecio, gbc);
        gbc.gridy = 4; formServ.add(EstiloUI.labelForm("Asignar Operario / Lavador:"), gbc);
        gbc.gridy = 5; formServ.add(cbServOperario, gbc);

        JButton btnCrearOrden = EstiloUI.crearBotonalPrincipal("🚀 GENERAR ORDEN EN BOX DE LAVADO");
        btnCrearOrden.setPreferredSize(new Dimension(0, 45));
        btnCrearOrden.addActionListener(e -> registrarOrdenServicioBD());

        gbc.gridy = 6; gbc.insets = new Insets(25, 5, 5, 5);
        formServ.add(btnCrearOrden, gbc);

        lblEstadoServ = new JLabel("Seleccione un vehículo en la pestaña 1 para asociar la orden.");
        lblEstadoServ.setFont(EstiloUI.FUENTE_SMALL);
        lblEstadoServ.setForeground(EstiloUI.GRIS_TEXTO);
        gbc.gridy = 7; formServ.add(lblEstadoServ, gbc);

        // Tabla de Monitoreo Central de Boxes
        modeloServicios = new DefaultTableModel(new String[]{"ID Servicio", "Placa", "Tipo Servicio", "Precio", "Estado", "Lavador"}, 0);
        tablaServicios = new JTable(modeloServicios);
        JScrollPane scServ = EstiloUI.estilizarTabla(tablaServicios);
        scServ.setBorder(new TitledBorder(new LineBorder(EstiloUI.AZUL_BORDE), "MONITOREO DE OPERACIONES EN TIEMPO REAL (BOXES)"));

        panel.add(formServ, BorderLayout.WEST);
        panel.add(scServ, BorderLayout.CENTER);

        return panel;
    }

    /**
     * PESTAÑA 3: TERMINAL DE FACTURACIÓN Y PROCESAMIENTO DE PAGO
     */
    private JPanel crearPestañaFacturacion() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(EstiloUI.AZUL_OSCURO);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JPanel formPago = new JPanel(new GridBagLayout());
        formPago.setBackground(EstiloUI.AZUL_PANEL);
        formPago.setBorder(new CompoundBorder(new LineBorder(EstiloUI.AZUL_BORDE), new EmptyBorder(15, 15, 15, 15)));
        formPago.setPreferredSize(new Dimension(380, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 5, 6, 5);
        gbc.weightx = 1.0;

        txtFacMonto = EstiloUI.crearCampoTexto();
        txtFacMonto.setEditable(false);
        txtFacMonto.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtFacMonto.setForeground(EstiloUI.CIAN_ACENTO);

        cbFacTipoComprobante = new JComboBox<>(new String[]{"Boleta", "Factura"});
        cbFacTipoComprobante.setBackground(EstiloUI.AZUL_OSCURO);
        cbFacTipoComprobante.setForeground(EstiloUI.BLANCO);

        cbFacMetodoPago = new JComboBox<>(new String[]{"Efectivo", "Tarjeta de Crédito/Débito", "Transferencia / Yape / Plin"});
        cbFacMetodoPago.setBackground(EstiloUI.AZUL_OSCURO);
        cbFacMetodoPago.setForeground(EstiloUI.BLANCO);

        txtFacDocFiscal = EstiloUI.crearCampoTexto();
        txtFacRazonSocial = EstiloUI.crearCampoTexto();
        txtFacRazonSocial.setEnabled(false);

        cbFacTipoComprobante.addActionListener(e -> {
            boolean esFactura = cbFacTipoComprobante.getSelectedItem().toString().equals("Factura");
            txtFacRazonSocial.setEnabled(esFactura);
        });

        gbc.gridy = 0; formPago.add(EstiloUI.labelForm("Monto Liquidación Total (S/.):"), gbc);
        gbc.gridy = 1; formPago.add(txtFacMonto, gbc);
        gbc.gridy = 2; formPago.add(EstiloUI.labelForm("Tipo Comprobante (Sunat):"), gbc);
        gbc.gridy = 3; formPago.add(cbFacTipoComprobante, gbc);
        gbc.gridy = 4; formPago.add(EstiloUI.labelForm("Método de Cancelación:"), gbc);
        gbc.gridy = 5; formPago.add(cbFacMetodoPago, gbc);
        gbc.gridy = 6; formPago.add(EstiloUI.labelForm("Documento de Identidad Fiscal (DNI/RUC):"), gbc);
        gbc.gridy = 7; formPago.add(txtFacDocFiscal, gbc);
        gbc.gridy = 8; formPago.add(EstiloUI.labelForm("Razón Social (Solo Facturas):"), gbc);
        gbc.gridy = 9; formPago.add(txtFacRazonSocial, gbc);

        JButton btnProcesarFactura = EstiloUI.crearBotonalPrincipal("💳 PROCESAR TRANSACCIÓN EMITIR");
        btnProcesarFactura.setPreferredSize(new Dimension(0, 45));
        btnProcesarFactura.addActionListener(e -> procesarTransaccionYFacturar());

        gbc.gridy = 10; gbc.insets = new Insets(20, 5, 5, 5);
        formPago.add(btnProcesarFactura, gbc);

        lblEstadoFactura = new JLabel("Seleccione un servicio en la tabla de boxes para liquidar.");
        lblEstadoFactura.setFont(EstiloUI.FUENTE_SMALL);
        lblEstadoFactura.setForeground(EstiloUI.GRIS_TEXTO);
        gbc.gridy = 11; formPago.add(lblEstadoFactura, gbc);

        // Vista de Previsualización del Ticket Virtual
        areaImpresionTicket = new JTextArea();
        areaImpresionTicket.setEditable(false);
        areaImpresionTicket.setBackground(new Color(15, 23, 42));
        areaImpresionTicket.setForeground(new Color(34, 197, 94));
        areaImpresionTicket.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane scTicket = new JScrollPane(areaImpresionTicket);
        scTicket.setBorder(new TitledBorder(new LineBorder(EstiloUI.AZUL_BORDE), "VISTA PREVIA DEL TICKET ELECTRONICO (VIRTUAL)"));

        tablaServicios.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cargarServicioParaLiquidar();
            }
        });

        panel.add(formPago, BorderLayout.WEST);
        panel.add(scTicket, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearBarraEstadoInferior() {
        JPanel b = new JPanel(new FlowLayout(FlowLayout.LEFT));
        b.setBackground(new Color(10, 15, 30));
        b.setPreferredSize(new Dimension(0, 25));
        JLabel texto = new JLabel("  Caja Abierta  |  Base de datos Sincronizada OK  |  Super Car Wash v1.2");
        texto.setForeground(EstiloUI.GRIS_TEXTO);
        texto.setFont(EstiloUI.FUENTE_SMALL);
        b.add(texto);
        return b;
    }

    // --- SECCIÓN DE CONTROLADORES Y LÓGICA DE PERSISTENCIA DIRECTA A BASE DE DATOS ---

    private void recargarDatosDesdeBD() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            private List<Cliente> clientes;
            private List<Usuario> usuarios;
            private List<Servicio> servicios;

            @Override
            protected Void doInBackground() {
                clientes = atencionDAO.buscarClientes("");
                usuarios = usuarioDAO.listarTodos();
                servicios = atencionDAO.listarTodosLosServicios();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    clientesCache = clientes;
                    serviciosCache = servicios;

                    // Llenar tabla de clientes
                    modeloClientes.setRowCount(0);
                    for (Cliente c : clientesCache) {
                        modeloClientes.addRow(new Object[]{c.getIdCliente(), c.getDocumento(), c.getNombre(), c.getTelefono()});
                    }

                    // Llenar tabla de servicios en boxes
                    modeloServicios.setRowCount(0);
                    for (Servicio s : serviciosCache) {
                        modeloServicios.addRow(new Object[]{
                                s.getIdServicio(), s.getVehiculo().getPlaca(), s.getTipoServicio(),
                                s.getPrecio(), s.getEstado(), (s.getLavadorAsignado() != null ? s.getLavadorAsignado().getNombre() : "No asignado")
                        });
                    }

                    // Llenar combo de operarios
                    cbServOperario.removeAllItems();
                    operariosCache.clear();
                    for (Usuario u : usuarios) {
                        if ("Operario".equalsIgnoreCase(u.getRol()) || "Lavador".equalsIgnoreCase(u.getRol())) {
                            cbServOperario.addItem(u.getNombre());
                            operariosCache.add(u);
                        }
                    }
                } catch (Exception ex) {
                    EstiloUI.mostrarError(PanelCajero.this, "Error crítico de sincronización con MySQL.");
                }
            }
        };
        worker.execute();
    }

    private void cargarVehiculosClienteSeleccionado() {
        int fila = tablaClientes.getSelectedRow();
        if (fila == -1) return;

        int idCliente = (int) modeloClientes.getValueAt(fila, 0);
        String nombre = modeloClientes.getValueAt(fila, 2).toString();
        txtCliDoc.setText(modeloClientes.getValueAt(fila, 1).toString());
        txtCliNombre.setText(nombre);

        lblEstadoCli.setForeground(EstiloUI.VERDE_OK);
        lblEstadoCli.setText("✓ Cliente cargado: " + nombre);

        SwingWorker<List<Vehiculo>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Vehiculo> doInBackground() {
                return atencionDAO.listarVehiculosPorCliente(idCliente);
            }

            @Override
            protected void done() {
                try {
                    List<Vehiculo> vehiculos = get();
                    modeloVehiculos.setRowCount(0);
                    for (Vehiculo v : vehiculos) {
                        modeloVehiculos.addRow(new Object[]{v.getIdVehiculo(), v.getPlaca(), v.getMarca(), v.getModelo(), v.getTipo()});
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void registrarAdmisionCompleta() {
        String doc = txtCliDoc.getText().trim();
        String nom = txtCliNombre.getText().trim();
        String placa = txtVehPlaca.getText().trim();

        if (doc.isEmpty() || nom.isEmpty() || placa.isEmpty()) {
            EstiloUI.mostrarError(this, "Debe completar como mínimo Documento, Nombre y Placa vehicular.");
            return;
        }

        Cliente nuevoCliente = new Cliente(0, nom, doc, txtCliTel.getText().trim(), txtCliCorreo.getText().trim());
        Vehiculo nuevoVehiculo = new Vehiculo(0, placa, txtVehMarca.getText().trim(), txtVehModelo.getText().trim(), cbVehTipo.getSelectedItem().toString(), nuevoCliente);

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                if (atencionDAO.insertarCliente(nuevoCliente)) {
                    nuevoVehiculo.setPropietario(nuevoCliente);
                    return atencionDAO.insertarVehiculo(nuevoVehiculo);
                }
                return false;
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        EstiloUI.mostrarExito(PanelCajero.this, "Admisión Guardada Correctamente en la Base de Datos.");
                        limpiarFormulariosAdmision();
                        recargarDatosDesdeBD();
                    } else {
                        EstiloUI.mostrarError(PanelCajero.this, "Fallo el mapeo e inserción relacional.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void ajustarPrecioServicio() {
        switch (cbServTipo.getSelectedIndex()) {
            case 0 -> txtServPrecio.setText("45.00");
            case 1 -> txtServPrecio.setText("120.00");
            case 2 -> txtServPrecio.setText("35.00");
            case 3 -> txtServPrecio.setText("85.00");
            default -> txtServPrecio.setText("60.00");
        }
    }

    private void registrarOrdenServicioBD() {
        int filaVeh = tablaVehiculos.getSelectedRow();
        if (filaVeh == -1) {
            EstiloUI.mostrarError(this, "Debe seleccionar un vehículo de la lista derecha.");
            return;
        }

        if (cbServOperario.getSelectedItem() == null) {
            EstiloUI.mostrarError(this, "No hay operarios registrados en el sistema para asignar.");
            return;
        }

        int idVehiculo = (int) modeloVehiculos.getValueAt(filaVeh, 0);
        String placa = modeloVehiculos.getValueAt(filaVeh, 1).toString();
        Vehiculo v = new Vehiculo();
        v.setIdVehiculo(idVehiculo);
        v.setPlaca(placa);

        Usuario operarioAsignado = operariosCache.get(cbServOperario.getSelectedIndex());
        Lavador lav = new Lavador(operarioAsignado.getIdUsuario(), operarioAsignado.getNombre(), "", "");

        double precio = Double.parseDouble(txtServPrecio.getText());
        Servicio s = new Servicio(0, cbServTipo.getSelectedItem().toString(), precio, "Pendiente", v, lav);

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                return atencionDAO.insertarServicio(s);
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        EstiloUI.mostrarExito(PanelCajero.this, "Orden enviada a boxes exitosamente.");
                        recargarDatosDesdeBD();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void cargarServicioParaLiquidar() {
        int fila = tablaServicios.getSelectedRow();
        if (fila == -1) return;

        String idServ = modeloServicios.getValueAt(fila, 0).toString();
        String precio = modeloServicios.getValueAt(fila, 3).toString();
        String estado = modeloServicios.getValueAt(fila, 4).toString();

        txtFacMonto.setText(precio);
        lblEstadoFactura.setForeground(EstiloUI.CIAN_ACENTO);
        lblEstadoFactura.setText("Servicio ID [" + idServ + "] cargado (" + estado + ")");
    }

    private void procesarTransaccionYFacturar() {
        int fila = tablaServicios.getSelectedRow();
        if (fila == -1) {
            EstiloUI.mostrarError(this, "Seleccione un servicio de la tabla central.");
            return;
        }

        int idServicio = (int) modeloServicios.getValueAt(fila, 0);
        String docFiscal = txtFacDocFiscal.getText().trim();
        String tipoComp = cbFacTipoComprobante.getSelectedItem().toString();

        if (docFiscal.isEmpty()) {
            EstiloUI.mostrarError(this, "El documento fiscal es obligatorio para la facturación SUNAT.");
            return;
        }

        // Buscar objeto completo en cache
        Servicio servSeleccionado = null;
        for (Servicio s : serviciosCache) {
            if (s.getIdServicio() == idServicio) {
                servSeleccionado = s;
                break;
            }
        }

        if (servSeleccionado == null) return;

        Pago pago = new Pago(0, new Date(), servSeleccionado.getPrecio(), cbFacMetodoPago.getSelectedItem().toString(), servSeleccionado);
        String razonSoc = txtFacRazonSocial.getText().trim();

        String codigoGen = "CP-" + (int)(Math.random() * 90000 + 10000);
        Comprobante comprobanteEmitido = "Factura".equals(tipoComp) ?
                new Factura(0, codigoGen, pago, docFiscal, razonSoc) :
                new Boleta(0, codigoGen, pago, docFiscal);

        comprobanteEmitido.calcularMontos(); // Polimorfismo Dinámico

        Servicio finalServ = servSeleccionado;
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                if (atencionDAO.insertarPago(pago)) {
                    atencionDAO.insertarComprobante(comprobanteEmitido, pago.getIdPago());
                    atencionDAO.actualizarEstadoServicio(idServicio, "Finalizado");
                    return true;
                }
                return false;
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        EstiloUI.mostrarExito(PanelCajero.this, "Transacción Procesada y Cerrada.");
                        imprimirTicketVirtual(comprobanteEmitido, finalServ);
                        recargarDatosDesdeBD();
                        txtFacDocFiscal.setText("");
                        txtFacRazonSocial.setText("");
                        txtFacMonto.setText("");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void imprimirTicketVirtual(Comprobante comp, Servicio serv) {
        StringBuilder sb = new StringBuilder();
        sb.append("==========================================\n");
        sb.append("            SUPER CAR WASH S.A.C          \n");
        sb.append("    Av. Las Américas Mz D Lt 43, Chiclayo \n");
        sb.append("==========================================\n");
        sb.append("CÓDIGO COMPROBANTE: ").append(comp.getCodigoComprobante()).append("\n");
        sb.append("FECHA EMISIÓN: ").append(new Date()).append("\n");
        sb.append("ATENDIDO POR: ").append(cajeroNombre).append("\n");
        sb.append("------------------------------------------\n");
        if (comp instanceof Factura f) {
            sb.append("TIPO: FACTURA ELECTRÓNICA\n");
            sb.append("RUC RECEPTOR: ").append(f.getRuc()).append("\n");
            sb.append("RAZÓN SOCIAL: ").append(f.getRazonSocial()).append("\n");
        } else {
            Boleta b = (Boleta) comp;
            sb.append("TIPO: BOLETA DE VENTA\n");
            sb.append("DNI CLIENTE: ").append(b.getDniCliente()).append("\n");
        }
        sb.append("VEHÍCULO PLACA: ").append(serv.getVehiculo().getPlaca()).append("\n");
        sb.append("------------------------------------------\n");
        sb.append("DESCRIPCIÓN             | IMPORTE         \n");
        sb.append(String.format("%-24s| S/. %.2f\n", serv.getTipoServicio(), serv.getPrecio()));
        sb.append("------------------------------------------\n");
        sb.append(String.format("SUBTOTAL:                 | S/. %.2f\n", comp.getSubtotal()));
        sb.append(String.format("IGV (18%%):                | S/. %.2f\n", comp.getIgv()));
        sb.append(String.format("TOTAL CANCELADO:          | S/. %.2f\n", comp.getTotal()));
        sb.append("==========================================\n");
        sb.append("     ¡GRACIAS POR SU PREFERENCIA!         \n");
        sb.append("==========================================\n");

        areaImpresionTicket.setText(sb.toString());
    }

    private void limpiarFormulariosAdmision() {
        txtCliDoc.setText("");
        txtCliNombre.setText("");
        txtCliTel.setText("");
        txtCliCorreo.setText("");
        txtVehPlaca.setText("");
        txtVehMarca.setText("");
        txtVehModelo.setText("");
        cbVehTipo.setSelectedIndex(0);
        lblEstadoCli.setText(" ");
    }

    private void cerrarCaja() {
        if (EstiloUI.confirmar(this, "¿Está seguro de cerrar el turno y la caja actual?")) {
            dispose();
            new LoginVista().setVisible(true);
        }
    }
}