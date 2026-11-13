package co.edu.ucc.pasto3d.intencion;

import co.edu.ucc.pasto3d.dto.Dtos.ProgramaDTO;
import co.edu.ucc.pasto3d.ia.ContextoCampus;
import co.edu.ucc.pasto3d.seguridad.Sesion;

import static co.edu.ucc.pasto3d.ia.TextoUtil.normalizar;

/** Todo lo que un manejador necesita para decidir si atiende el mensaje. */
public record Consulta(String original, String normal, ContextoCampus contexto, Sesion sesion, ProgramaDTO programaActual) {

    public static Consulta de(String texto, ContextoCampus contexto, Sesion sesion, ProgramaDTO programaActual) {
        String n = normalizar(texto)
                .replaceAll("^(oye|ey|hey|hola) ?coopi\\b ?", "")   // palabra de activación por voz
                .trim();
        return new Consulta(texto, n, contexto, sesion, programaActual);
    }

    public boolean contiene(String... frases) {
        String t = " " + normal + " ";
        for (String f : frases) if (t.contains(" " + f + " ") || t.contains(" " + f)) return true;
        return false;
    }

    /** "llévame", "quiero ir", "muéstrame", "dónde queda", "cómo llego"... */
    public boolean pideIr() {
        return contiene("llevame", "llevarme", "lleveme", "llevanos", "quiero ir", "vamos a", "vamos al", "ir a", "ir al",
                "muestrame", "mostrame", "ensename", "quiero ver", "ver el", "ver la", "donde queda", "donde esta",
                "donde se encuentra", "como llego", "ubicame", "guiame", "dirigeme", "abre", "abrir", "entrar a", "entrar al");
    }

    public String nombreUsuario() {
        if (sesion == null || sesion.esInvitado() || sesion.nombre() == null || sesion.nombre().isBlank()) return null;
        return sesion.nombre().split(" ")[0];
    }
}
