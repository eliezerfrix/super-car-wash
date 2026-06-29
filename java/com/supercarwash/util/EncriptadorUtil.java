package com.supercarwash.util;

import com.supercarwash.excepcion.CarWashException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Utilidad criptográfica encargada del hashing seguro de las contraseñas
 * del personal utilizando el estándar de la industria SHA-256.
 * * @author Herrera, Limo, Mino
 * @version 1.0
 */
public class EncriptadorUtil {

    /**
     * Convierte una cadena de texto plano en un hash hexadecimal SHA-256 inmutable.
     * @param textoPlano Contraseña ingresada por el usuario.
     * @return Hash seguro de 64 caracteres representados en hexadecimal.
     */
    public static String encriptarString(String textoPlano) {
        if (textoPlano == null || textoPlano.isEmpty()) {
            return "";
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(textoPlano.getBytes(StandardCharsets.UTF_8));

            // Convertir el arreglo de bytes en representación Hexadecimal
            StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (Exception e) {
            throw new CarWashException("Error crítico al procesar el cifrado de seguridad de la credencial.", "ERR_CRYPTO", e);
        }
    }

    /**
     * Compara una contraseña en texto plano contra un hash almacenado para validar el login.
     */
    public static boolean verificarPassword(String textoPlano, String hashAlmacenado) {
        if (textoPlano == null || hashAlmacenado == null) return false;
        String hashedInput = encriptarString(textoPlano);
        return hashedInput.equalsIgnoreCase(hashAlmacenado);
    }
}