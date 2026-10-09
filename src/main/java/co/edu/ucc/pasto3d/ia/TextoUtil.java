package co.edu.ucc.pasto3d.ia;

import java.text.Normalizer;
import java.util.Locale;

public final class TextoUtil {
    private TextoUtil() { }

    /** -1 → "sótano 1", 3 → "piso 3". */
    public static String nombrePiso(Integer piso) {
        if (piso == null) return "piso sin registrar";
        return piso < 0 ? "sótano " + (-piso) : "piso " + piso;
    }

    /** Minúsculas y sin tildes, para comparar textos escritos de distintas formas. */
    public static String normalizar(String s) {
        if (s == null) return "";
        String sinTildes = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return sinTildes.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9ñ ]", " ").replaceAll("\\s+", " ").trim();
    }
}
