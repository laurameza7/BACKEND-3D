package co.edu.ucc.pasto3d.ia;

/** Formateador de la familia Gemini: el modelo a veces responde con Markdown y el chat muestra texto plano. */
public class FormateadorMarkdown implements FormateadorRespuesta {
    @Override
    public String formatear(String t) {
        if (t == null) return "";
        return t.replace("**", "")
                .replace("__", "")
                .replaceAll("(?m)^#{1,6}[ \\t]*", "")              // títulos
                .replaceAll("(?m)^[ \\t]*[*-][ \\t]+", "• ")         // viñetas
                .replaceAll("(?<![\\w*])\\*([^*\\n]+)\\*(?![\\w*])", "$1")   // *cursiva*
                .replaceAll("`([^`\\n]+)`", "$1")                    // `código`
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
    }
}
