package co.edu.ucc.pasto3d.ai;

import java.text.Normalizer;
import java.util.Locale;

public final class TextUtil {
    private TextUtil() { }

    /** -1 → "sótano 1", 3 → "piso 3" (shown to the user in Spanish). */
    public static String floorName(Integer floor) {
        if (floor == null) return "piso sin registrar";
        return floor < 0 ? "sótano " + (-floor) : "piso " + floor;
    }

    /** IN_PERSON → "presencial", HYBRID → "híbrida", VIRTUAL → "virtual" (shown to the user in Spanish). */
    public static String modalityName(String modality) {
        if (modality == null) return "sin registrar";
        return switch (modality) {
            case "IN_PERSON" -> "presencial";
            case "HYBRID" -> "híbrida";
            case "VIRTUAL" -> "virtual";
            default -> modality.toLowerCase(Locale.ROOT);
        };
    }

    /** Lowercase and without accents, to compare texts written in different ways. */
    public static String normalize(String s) {
        if (s == null) return "";
        String withoutAccents = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return withoutAccents.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9ñ ]", " ").replaceAll("\\s+", " ").trim();
    }
}


