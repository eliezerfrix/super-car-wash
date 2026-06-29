package com.supercarwash.util;

import com.supercarwash.excepcion.ValidacionException;
import java.util.regex.Pattern;

/**
 * Utilidades de validación con expresiones regulares adaptadas a las
 * normativas de identificación y rodaje en el Perú.
 * * @author Herrera, Limo, Mino
 * @version 1.1
 */
public class ValidadorUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PLACA_PATTERN = Pattern.compile("^[A-Z0-9]{3}-[A-Z0-9]{3}$|^[A-Z0-9]{6}$");

    /**
     * Valida un documento nacional de identidad (DNI - 8 dígitos) o RUC (11 dígitos).
     */
    public static void validarDocumentoIdentidad(String doc) {
        if (doc == null || doc.trim().isEmpty()) {
            throw new ValidacionException("El documento fiscal no puede estar vacío.", "Documento");
        }
        String limpio = doc.trim();
        if (limpio.length() != 8 && limpio.length() != 11) {
            throw new ValidacionException("El formato del documento debe ser DNI (8 dígitos) o RUC (11 dígitos).", "Documento");
        }
        if (!limpio.matches("\\d+")) {
            throw new ValidacionException("El documento de identidad debe contener únicamente caracteres numéricos.", "Documento");
        }
    }

    /**
     * Valida la estructura estándar de una placa de rodaje peruana (Ej: ABC-123 o X1A-345).
     */
    public static void validarPlacaVehicular(String placa) {
        if (placa == null || placa.trim().isEmpty()) {
            throw new ValidacionException("La placa vehicular es un requisito obligatorio de admisión.", "Placa");
        }
        String limpia = placa.trim().toUpperCase();
        if (!PLACA_PATTERN.matcher(limpia).matches()) {
            throw new ValidacionException("Formato de placa vehicular inválido. Use estructuras estándar (Ej: ABC-123).", "Placa");
        }
    }

    /**
     * Valida la estructura sintáctica de una dirección de correo electrónico.
     */
    public static void validarCorreo(String correo) {
        if (correo == null || correo.trim().isEmpty()) return; // Es opcional en nuestro modelo

        if (!EMAIL_PATTERN.matcher(correo.trim()).matches()) {
            throw new ValidacionException("La dirección de correo electrónico no tiene una sintaxis válida.", "Correo");
        }
    }

    /**
     * Valida que un importe de dinero sea estrictamente mayor a cero.
     */
    public static void validarMontoPositivo(double monto, String nombreCampo) {
        if (monto <= 0) {
            throw new ValidacionException("El importe para '" + nombreCampo + "' debe ser un valor estrictamente mayor a cero.", nombreCampo);
        }
    }
}