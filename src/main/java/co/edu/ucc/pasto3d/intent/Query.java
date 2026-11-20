package co.edu.ucc.pasto3d.intent;

import co.edu.ucc.pasto3d.dto.Dtos.ProgramDTO;
import co.edu.ucc.pasto3d.ai.CampusContext;
import co.edu.ucc.pasto3d.security.Session;

import static co.edu.ucc.pasto3d.ai.TextUtil.normalize;

/** Everything a handler needs to decide whether it handles the message. */
public record Query(String original, String normalized, CampusContext context, Session session, ProgramDTO currentProgram) {

    public static Query of(String text, CampusContext context, Session session, ProgramDTO currentProgram) {
        String n = normalize(text)
                .replaceAll("^(oye|ey|hey|hola) ?coopi\\b ?", "")   // voice wake word
                .trim();
        return new Query(text, n, context, session, currentProgram);
    }

    /**
     * Does the message contain any of the phrases? Long phrases are accepted as a prefix
     * ("laboratorio" → "laboratorios"); short ones (4 letters or less) only as a whole word
     * or in plural, so that "ami" does not match "amigo" nor "cafe" match "cafeteria".
     */
    public boolean contains(String... phrases) {
        String t = " " + normalized + " ";
        for (String f : phrases) {
            if (t.contains(" " + f + " ")) return true;
            if (f.length() <= 4 ? t.contains(" " + f + "s ") || t.contains(" " + f + "es ") : t.contains(" " + f)) return true;
        }
        return false;
    }

    /** "llévame", "quiero ir", "muéstrame", "dónde queda", "cómo llego"... ("take me", "where is"...) */
    public boolean asksToGo() {
        return contains("llevame", "llevarme", "lleveme", "llevanos", "quiero ir", "vamos a", "vamos al", "ir a", "ir al",
                "muestrame", "mostrame", "ensename", "quiero ver", "ver el", "ver la", "donde queda", "donde esta",
                "donde se encuentra", "como llego", "ubicame", "guiame", "dirigeme", "abre", "abrir", "entrar a", "entrar al");
    }

    public String userName() {
        if (session == null || session.isGuest() || session.name() == null || session.name().isBlank()) return null;
        return session.name().trim().split("\\s+")[0];
    }
}
