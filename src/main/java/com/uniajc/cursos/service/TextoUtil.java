package com.uniajc.cursos.service;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Utilidades para limpiar texto y evitar inconsistencias
 * (tildes, mayusculas/minusculas, espacios de mas).
 */
public final class TextoUtil {

    private static final Locale ES = Locale.forLanguageTag("es-CO");

    private TextoUtil() {
    }

    /** "  programación   iii " -> "programación iii" */
    public static String limpiarEspacios(String texto) {
        return texto == null ? null : texto.trim().replaceAll("\\s+", " ");
    }

    /** "  progRAMACIÓN   iii " -> "Programación Iii" (primera letra de cada palabra en mayuscula) */
    public static String formatoTitulo(String texto) {
        String limpio = limpiarEspacios(texto);
        if (limpio == null || limpio.isEmpty()) return limpio;
        return Arrays.stream(limpio.toLowerCase(ES).split(" "))
                .map(p -> p.substring(0, 1).toUpperCase(ES) + p.substring(1))
                .collect(Collectors.joining(" "));
    }

    /** "Programación  III" -> "programacion iii" (clave para comparar sin importar tildes ni mayusculas) */
    public static String clave(String texto) {
        String limpio = limpiarEspacios(texto);
        if (limpio == null) return null;
        String sinTildes = Normalizer.normalize(limpio, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return sinTildes.toLowerCase(ES);
    }

    /** " prog3 " -> "PROG3" */
    public static String codigo(String texto) {
        String limpio = limpiarEspacios(texto);
        return limpio == null ? null : limpio.replace(" ", "").toUpperCase(ES);
    }
}
