package com.supercarwash.ui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;

/**
 * Clase de soporte que centraliza el diseño visual, la paleta de colores corporativa
 * y la consistencia de la interfaz gráfica Swing para Super Car Wash.
 * @author Herrera, Limo, Mino
 * @version 1.2
 */
public class EstiloUI {

    // ── Paleta de Colores Dark Mode Premium ──────────────────────────────────
    public static final Color AZUL_OSCURO   = new Color(15,  23,  42);   // Fondo principal de ventanas
    public static final Color AZUL_PANEL    = new Color(30,  41,  59);   // Paneles internos y formularios
    public static final Color AZUL_BORDE    = new Color(51,  65,  85);   // Bordes y divisores lógicos
    public static final Color CIAN_ACENTO   = new Color(6,  182, 212);   // Color de acento principal (Líneas/Botones)
    public static final Color CIAN_HOVER    = new Color(8,  145, 178);   // Estado hover para botones
    public static final Color BLANCO        = new Color(248, 250, 252);  // Texto principal
    public static final Color GRIS_TEXTO    = new Color(148, 163, 184);  // Subtítulos o texto secundario
    public static final Color VERDE_OK      = new Color(34,  197, 94);   // Estados exitosos
    public static final Color ROJO_ERROR    = new Color(239, 68,  68);   // Alertas y errores

    // ── Tipografías Corporativas ─────────────────────────────────────────────
    public static final Font FUENTE_TITULO  = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FUENTE_SUB     = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FUENTE_NORMAL  = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FUENTE_SMALL   = new Font("Segoe UI", Font.ITALIC, 11);

    /**
     * Establece el aspecto base del sistema operativo para evitar fuentes toscas.
     */
    public static void aplicarLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
    }

    // ── Factory de Componentes Estilizados ───────────────────────────────────
    public static JTextField crearCampoTexto() {
        JTextField f = new JTextField();
        f.setBackground(AZUL_OSCURO);
        f.setForeground(BLANCO);
        f.setCaretColor(CIAN_ACENTO);
        f.setFont(FUENTE_NORMAL);
        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AZUL_BORDE, 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return f;
    }

    public static JPasswordField crearCampoClave() {
        JPasswordField f = new JPasswordField();
        f.setBackground(AZUL_OSCURO);
        f.setForeground(BLANCO);
        f.setCaretColor(CIAN_ACENTO);
        f.setFont(FUENTE_NORMAL);
        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AZUL_BORDE, 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return f;
    }

    public static JButton crearBotonalPrincipal(String texto) {
        JButton b = new JButton(texto);
        b.setBackground(CIAN_ACENTO);
        b.setForeground(AZUL_OSCURO);
        b.setFont(FUENTE_SUB);
        b.setFocusPainted(false);
        b.setBorder(new LineBorder(CIAN_ACENTO, 1, true));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));

        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setBackground(CIAN_HOVER);
                b.setBorder(new LineBorder(CIAN_HOVER, 1, true));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(CIAN_ACENTO);
                b.setBorder(new LineBorder(CIAN_ACENTO, 1, true));
            }
        });
        return b;
    }

    // Colocar este método dentro de la clase EstiloUI sin eliminar nada de lo que ya tienes.

    /**
     * Genera un botón ultra-premium redondeado con degradado interactivo específico para el Login.
     * No interfiere con las funciones nativas usadas por los otros componentes.
     */
    public static JButton crearBotonLoginPremium(String texto) {
        JButton b = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Determinar color base según interacciones del mouse
                Color colorUno = model.isRollover() ? CIAN_HOVER : CIAN_ACENTO;
                Color colorDos = model.isRollover() ? new Color(6, 140, 170) : new Color(8, 145, 178);

                if (model.isPressed()) {
                    colorUno = colorUno.darker();
                    colorDos = colorDos.darker();
                }

                // Aplicar un sutil degradado horizontal moderno al botón
                GradientPaint gp = new GradientPaint(0, 0, colorUno, getWidth(), 0, colorDos);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                // Pintar texto de forma manual para evitar fallos de renderizado curvo
                g2.setColor(AZUL_OSCURO);
                g2.setFont(FUENTE_SUB);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };

        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setForeground(AZUL_OSCURO);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Listener limpio únicamente para repintar estados hover de animación
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { b.repaint(); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { b.repaint(); }
        });

        return b;
    }

    public static JLabel labelForm(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(FUENTE_SUB);
        l.setForeground(BLANCO);
        return l;
    }

    public static JScrollPane estilizarTabla(JTable tabla) {
        tabla.setBackground(AZUL_OSCURO);
        tabla.setForeground(BLANCO);
        tabla.setGridColor(AZUL_BORDE);
        tabla.setFont(FUENTE_NORMAL);
        tabla.setRowHeight(25);
        tabla.setSelectionBackground(CIAN_ACENTO);
        tabla.setSelectionForeground(AZUL_OSCURO);

        JTableHeader header = tabla.getTableHeader();
        header.setBackground(AZUL_OSCURO);
        header.setForeground(CIAN_ACENTO);
        header.setFont(FUENTE_SUB);
        header.setBorder(new LineBorder(AZUL_BORDE));

        JScrollPane sp = new JScrollPane(tabla);
        sp.setBorder(new LineBorder(AZUL_BORDE));
        sp.getViewport().setBackground(AZUL_OSCURO);
        return sp;
    }

    public static JSeparator separador() {
        JSeparator s = new JSeparator();
        s.setForeground(AZUL_BORDE);
        s.setBackground(AZUL_BORDE);
        return s;
    }

    // ── Ventanas Emergentes y Diálogos de Control ────────────────────────────
    public static void mostrarError(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Error Operativo", JOptionPane.ERROR_MESSAGE);
    }

    public static void mostrarExito(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Éxito en Sistema", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirmar(Component parent, String msg) {
        return JOptionPane.showConfirmDialog(parent, msg, "Confirmar Acción", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }


}