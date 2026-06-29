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
 * Terminal de Estación de Trabajo para el Operario / Lavador - Super Car Wash
 * Proporciona el entorno visual simplificado y táctil para que el personal de los
 * boxes de lavado pueda visualizar sus tareas asignadas y actualizar el estado en MySQL.
 * * ESTRUCTURA DETALLADA DE LA CLASE:
 * 1. Inicialización y Aplicación del Look & Feel Premium Dark Mode
 * 2. Construcción de Paneles: Cabecera, Área de Monitoreo y Barra de Estado
 * 3. Diseño de la Grid Táctica de Control de Estados (Pendiente / En Proceso / Terminado)
 * 4. Sincronización JDBC en Background con Hilos Asíncronos
 * * @author Herrera, Limo, Mino
 * @version 1.4
 */
public class PanelOperario extends JFrame {

    // Capa de persistencia directa y caché local
    private final IAtencionDAO atencionDAO;
    private final String operarioNombre;
    private List<Servicio> serviciosAsignadosCache = new ArrayList<>();

    // Componentes visuales de la interfaz Swing
    private DefaultTableModel modeloServicios;
    private JTable tablaServicios;
    private JLabel lblEstadisticas, lblMensajeAccion;
    private JButton btnIniciarServicio, btnFinalizarServicio, btnRefrescar;
    private JTextArea txtAreaDetalles;

    /**
     * Constructor principal para el terminal de boxes del operario.
     * @param nombre Nombre del operario autenticado en el sistema.
     */
    public PanelOperario(String nombre) {
        this.operarioNombre = nombre;
        this.atencionDAO = new AtencionDAOImpl();

        EstiloUI.aplicarLookAndFeel();
        configurarVentanaOperador();
        inicializarComponentesVisuales();

        // Carga inicial asíncrona de las tareas asignadas
        actualizarColaDeBoxes();
    }

    private void configurarVentanaOperador() {
        setTitle("Super Car Wash — Terminal Técnico de Boxes de Lavado");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(980, 660);
        setLocationRelativeTo(null);
        getContentPane().setBackground(EstiloUI.AZUL_OSCURO);
        setLayout(new BorderLayout(0, 0));
    }

    private void inicializarComponentesVisuales() {
        // 1. Encabezado Superior de Sesión
        add(construirHeaderOperario(), BorderLayout.NORTH);

        // 2. Panel Central Dividido (Tabla Izquierda - Panel de Control Derecho)
        JPanel panelCuerpo = new JPanel(new BorderLayout(15, 0));
        panelCuerpo.setBackground(EstiloUI.AZUL_OSCURO);
        panelCuerpo.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Sub-área Izquierda: La Tabla de Tareas
        String[] columnas = {"ID Servicio", "Placa Vehículo", "Tipo de Servicio", "Estado Actual"};
        modeloServicios = new DefaultTableModel(columnas, 0);
        tablaServicios = new JTable(modeloServicios);
        JScrollPane scrollTabla = EstiloUI.estilizarTabla(tablaServicios);
        scrollTabla.setBorder(new TitledBorder(new LineBorder(EstiloUI.AZUL_BORDE),
                "COLA DE TRABAJO ASIGNADA A BOXES", TitledBorder.LEFT, TitledBorder.TOP,
                EstiloUI.FUENTE_SUB, EstiloUI.BLANCO));

        tablaServicios.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cargarDetallesServicioSeleccionado();
            }
        });

        panelCuerpo.add(scrollTabla, BorderLayout.CENTER);

        // Sub-área Derecha: Consola de Control de Tiempos y Cambios de Estado
        panelCuerpo.add(construirConsolaControlDerecha(), BorderLayout.EAST);

        add(panelCuerpo, BorderLayout.CENTER);

        // 3. Barra de Estado de Planta Inferior
        add(construirBarraEstadoPlanta(), BorderLayout.SOUTH);
    }

    private JPanel construirHeaderOperario() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(EstiloUI.AZUL_PANEL);
        h.setPreferredSize(new Dimension(0, 70));
        h.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, EstiloUI.CIAN_ACENTO));

        // Identificación del Box de Lavado
        JLabel titulo = new JLabel("   🛠️ ESTACIÓN DE TRABAJO EN BOX");
        titulo.setFont(EstiloUI.FUENTE_TITULO);
        titulo.setForeground(EstiloUI.BLANCO);
        h.add(titulo, BorderLayout.WEST);

        // Panel de información técnica a la derecha
        JPanel panelDerecho = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 18));
        panelDerecho.setOpaque(false);

        lblEstadisticas = new JLabel("Pendientes: 0 | En Proceso: 0");
        lblEstadisticas.setFont(EstiloUI.FUENTE_SUB);
        lblEstadisticas.setForeground(EstiloUI.CIAN_ACENTO);

        JButton btnLogout = EstiloUI.crearBotonalPrincipal("Salir de Box");
        btnLogout.setPreferredSize(new Dimension(110, 30));
        btnLogout.addActionListener(e -> salirDeEstacion());

        panelDerecho.add(lblEstadisticas);
        panelDerecho.add(Box.createHorizontalStrut(10));
        panelDerecho.add(btnLogout);

        h.add(panelDerecho, BorderLayout.EAST);
        return h;
    }

    private JPanel construirConsolaControlDerecha() {
        JPanel consola = new JPanel(new GridBagLayout());
        consola.setBackground(EstiloUI.AZUL_PANEL);
        consola.setBorder(new CompoundBorder(
                new LineBorder(EstiloUI.AZUL_BORDE),
                new EmptyBorder(20, 20, 20, 20)));
        consola.setPreferredSize(new Dimension(360, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.weightx = 1.0;

        // Visualizador de Datos del Vehículo
        JLabel lblDetalles = EstiloUI.labelForm("Ficha Técnica del Servicio:");
        txtAreaDetalles = new JTextArea();
        txtAreaDetalles.setEditable(false);
        txtAreaDetalles.setBackground(EstiloUI.AZUL_OSCURO);
        txtAreaDetalles.setForeground(EstiloUI.BLANCO);
        txtAreaDetalles.setFont(EstiloUI.FUENTE_NORMAL);
        txtAreaDetalles.setMargin(new Insets(8, 8, 8, 8));
        JScrollPane scrollDetalles = new JScrollPane(txtAreaDetalles);
        scrollDetalles.setPreferredSize(new Dimension(0, 160));
        scrollDetalles.setBorder(new LineBorder(EstiloUI.AZUL_BORDE));

        gbc.gridy = 0; consola.add(lblDetalles, gbc);
        gbc.gridy = 1; consola.add(scrollDetalles, gbc);

        // Botonera Táctil de Estados de Planta
        btnIniciarServicio = EstiloUI.crearBotonalPrincipal("▶️ INICIAR LAVADO (PROCESO)");
        btnFinalizarServicio = EstiloUI.crearBotonalPrincipal("🏁 MARCAR COMO TERMINADO");
        btnRefrescar = EstiloUI.crearBotonalPrincipal("🔄 REFRESCAR TABLA");

        btnIniciarServicio.setEnabled(false);
        btnFinalizarServicio.setEnabled(false);

        // Asignación de Estilos de Alerta según Flujo Operativo
        btnIniciarServicio.setBackground(new Color(234, 179, 8)); // Amarillo preventivo
        btnIniciarServicio.setForeground(EstiloUI.AZUL_OSCURO);
        btnFinalizarServicio.setBackground(EstiloUI.VERDE_OK);
        btnFinalizarServicio.setForeground(EstiloUI.AZUL_OSCURO);

        btnIniciarServicio.addActionListener(e -> cambiarEstadoServicioSeleccionado("En Proceso"));
        btnFinalizarServicio.addActionListener(e -> cambiarEstadoServicioSeleccionado("Terminado"));
        btnRefrescar.addActionListener(e -> actualizarColaDeBoxes());

        gbc.gridy = 2; gbc.insets = new Insets(15, 0, 5, 0);
        consola.add(btnIniciarServicio, gbc);
        gbc.gridy = 3; gbc.insets = new Insets(5, 0, 5, 0);
        consola.add(btnFinalizarServicio, gbc);
        gbc.gridy = 4; gbc.insets = new Insets(20, 0, 5, 0);
        consola.add(btnRefrescar, gbc);

        // Label Informativo de Eventos Recientes
        lblMensajeAccion = new JLabel("Seleccione una orden para comenzar.", SwingConstants.CENTER);
        lblMensajeAccion.setFont(EstiloUI.FUENTE_SMALL);
        lblMensajeAccion.setForeground(EstiloUI.GRIS_TEXTO);
        gbc.gridy = 5; gbc.insets = new Insets(15, 0, 0, 0);
        consola.add(lblMensajeAccion, gbc);

        return consola;
    }

    private JPanel construirBarraEstadoPlanta() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(new Color(10, 15, 30));
        barra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, EstiloUI.AZUL_BORDE),
                BorderFactory.createEmptyBorder(8, 24, 8, 24)
        ));

        JLabel info = new JLabel("Super Car Wash S.A.C. · Sistema Sincronizado con MySQL Direct JDBC");
        info.setFont(EstiloUI.FUENTE_SMALL);
        info.setForeground(EstiloUI.GRIS_TEXTO);

        JLabel operarioLbl = new JLabel("Técnico Asignado: " + operarioNombre + "   ");
        operarioLbl.setFont(EstiloUI.FUENTE_SMALL);
        operarioLbl.setForeground(EstiloUI.CIAN_ACENTO);

        barra.add(info, BorderLayout.WEST);
        barra.add(operarioLbl, BorderLayout.EAST);
        return barra;
    }

    // --- CAPA DE LOGICA DE NEGOCIO Y CONEXION RELACIONAL ASINCRONA ---

    private void actualizarColaDeBoxes() {
        lblMensajeAccion.setText("Sincronizando con el servidor MySQL...");
        lblMensajeAccion.setForeground(EstiloUI.CIAN_ACENTO);

        SwingWorker<List<Servicio>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Servicio> doInBackground() {
                // Filtramos y traemos todos los servicios registrados en la base de datos
                return atencionDAO.listarTodosLosServicios();
            }

            @Override
            protected void done() {
                try {
                    List<Servicio> todosLosServicios = get();
                    serviciosAsignadosCache.clear();
                    modeloServicios.setRowCount(0);

                    int pendientes = 0;
                    int enProceso = 0;

                    for (Servicio s : todosLosServicios) {
                        // El operario visualiza servicios pendientes o en proceso en su box
                        if ("Pendiente".equalsIgnoreCase(s.getEstado()) || "En Proceso".equalsIgnoreCase(s.getEstado())) {
                            serviciosAsignadosCache.add(s);
                            modeloServicios.addRow(new Object[]{
                                    s.getIdServicio(),
                                    s.getVehiculo().getPlaca(),
                                    s.getTipoServicio(),
                                    s.getEstado()
                            });

                            if ("Pendiente".equalsIgnoreCase(s.getEstado())) pendientes++;
                            if ("En Proceso".equalsIgnoreCase(s.getEstado())) enProceso++;
                        }
                    }

                    lblEstadisticas.setText("Pendientes: " + pendientes + " | En Proceso: " + enProceso + " ");
                    lblMensajeAccion.setText("Última sincronización: " + new Date().toString().substring(11, 19));
                    lblMensajeAccion.setForeground(EstiloUI.GRIS_TEXTO);

                    // Resetear controles
                    txtAreaDetalles.setText("");
                    btnIniciarServicio.setEnabled(false);
                    btnFinalizarServicio.setEnabled(false);

                } catch (Exception ex) {
                    lblMensajeAccion.setText("Error en la conexión con MySQL.");
                    lblMensajeAccion.setForeground(EstiloUI.ROJO_ERROR);
                }
            }
        };
        worker.execute();
    }

    private void cargarDetallesServicioSeleccionado() {
        int fila = tablaServicios.getSelectedRow();
        if (fila == -1) return;

        Servicio s = serviciosAsignadosCache.get(fila);

        StringBuilder sb = new StringBuilder();
        sb.append(" CÓDIGO ORDEN: ").append(s.getIdServicio()).append("\n");
        sb.append("--------------------------------------\n");
        sb.append(" PLACA VEHÍCULO: ").append(s.getVehiculo().getPlaca()).append("\n");
        sb.append(" TIPO TRABAJO: ").append(s.getTipoServicio()).append("\n");
        sb.append(" ESTADO ACTUAL: ").append(s.getEstado()).append("\n");
        sb.append(" PRECIO BASE: S/. ").append(s.getPrecio()).append("\n");
        sb.append("--------------------------------------\n");
        sb.append(" ESTADO BOX: Listo para intervención.");

        txtAreaDetalles.setText(sb.toString());

        // Habilitar botones de acuerdo al ciclo de vida del servicio
        if ("Pendiente".equalsIgnoreCase(s.getEstado())) {
            btnIniciarServicio.setEnabled(true);
            btnFinalizarServicio.setEnabled(false);
        } else if ("En Proceso".equalsIgnoreCase(s.getEstado())) {
            btnIniciarServicio.setEnabled(false);
            btnFinalizarServicio.setEnabled(true);
        }
    }

    private void cambiarEstadoServicioSeleccionado(String nuevoEstado) {
        int fila = tablaServicios.getSelectedRow();
        if (fila == -1) return;

        Servicio s = serviciosAsignadosCache.get(fila);
        int idServicio = s.getIdServicio();

        btnIniciarServicio.setEnabled(false);
        btnFinalizarServicio.setEnabled(false);

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                // Actualización directa en la tabla 'servicios' mediante JDBC
                return atencionDAO.actualizarEstadoServicio(idServicio, nuevoEstado);
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        lblMensajeAccion.setText("✓ Transición a '" + nuevoEstado + "' exitosa.");
                        lblMensajeAccion.setForeground(EstiloUI.VERDE_OK);

                        Timer t = new Timer(500, e -> actualizarColaDeBoxes());
                        t.setRepeats(false);
                        t.start();
                    } else {
                        EstiloUI.mostrarError(PanelOperario.this, "No se pudo actualizar el estado en MySQL.");
                        actualizarColaDeBoxes();
                    }
                } catch (Exception ex) {
                    EstiloUI.mostrarError(PanelOperario.this, "Error de comunicación transaccional.");
                }
            }
        };
        worker.execute();
    }

    private void salirDeEstacion() {
        if (EstiloUI.confirmar(this, "¿Cerrar sesión en este Box de Lavado?")) {
            this.dispose();
            new LoginVista().setVisible(true);
        }
    }
}