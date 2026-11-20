package co.edu.ucc.pasto3d.ia;

/** Formateador de la familia Gemini: el modelo a veces responde con Markdown y el chat muestra texto plano. */
public class FormateadorMarkdown implements FormateadorRespuesta {
    @Override
    public String formatear(String t) {
        return t.replace("**", "")
                .replaceAll("(?m)^#{1,6}[ \\t]*", "")
                .replaceAll("(?m)^[ \\t]*[*-][ \\t]+", "• ")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
    }
}
