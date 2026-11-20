package co.edu.ucc.pasto3d.ia;

/** Formateador de la familia Local: sus respuestas ya son texto plano, solo se limpian espacios. */
public class FormateadorTextoPlano implements FormateadorRespuesta {
    @Override
    public String formatear(String t) {
        return t == null ? "" : t.replaceAll("[ \\t]+\\n", "\n").trim();
    }
}
