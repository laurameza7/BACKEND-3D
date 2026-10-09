package co.edu.ucc.pasto3d.ia;

import co.edu.ucc.pasto3d.dto.Dtos.*;

/**
 * Patrón BUILDER: arma paso a paso el texto de instrucciones + conocimiento
 * que se envía al modelo de lenguaje.
 */
public class ContextoPromptBuilder {
    private final StringBuilder sb = new StringBuilder();

    public ContextoPromptBuilder instrucciones() {
        sb.append("""
            Eres "Coopi", el asistente virtual de la Universidad Cooperativa de Colombia, campus Pasto (Nariño).
            Respondes en español, de forma amable, clara y breve (máximo 6 frases o una lista corta).
            Escribe en texto plano: NO uses Markdown (nada de asteriscos, # ni negritas). Para listas usa "• " al inicio de cada línea.
            Usa SOLO la información del campus que aparece abajo. Si algo no está, dilo con honestidad y
            sugiere comunicarse al 602 7370660 o acercarse a Admisiones (Bloque A).
            Cuando la respuesta tenga un lugar físico, menciona el nombre exacto del bloque (por ejemplo "Bloque A")
            para que el mapa 3D lo pueda resaltar. No inventes precios, fechas ni nombres de personas.
            """).append('\n');
        return this;
    }

    public ContextoPromptBuilder edificios(ContextoCampus c) {
        sb.append("## Edificios del campus\n");
        for (EdificioDTO e : c.edificios())
            sb.append("- ").append(e.nombre()).append(": ").append(e.descripcion())
              .append(" (").append(e.pisos()).append(" pisos)\n");
        return this;
    }

    public ContextoPromptBuilder lugares(ContextoCampus c) {
        sb.append("\n## Oficinas, laboratorios y servicios\n");
        for (LugarDTO l : c.lugares()) {
            sb.append("- ").append(l.nombre()).append(" — ").append(l.edificio()).append(", piso ").append(l.piso());
            if (l.descripcion() != null) sb.append(". ").append(l.descripcion());
            if (l.horario() != null) sb.append(" Horario: ").append(l.horario()).append('.');
            if (l.telefono() != null) sb.append(" Tel: ").append(l.telefono()).append('.');
            if (l.correo() != null) sb.append(" Correo: ").append(l.correo()).append('.');
            sb.append('\n');
        }
        return this;
    }

    public ContextoPromptBuilder programas(ContextoCampus c) {
        sb.append("\n## Programas académicos\n");
        for (ProgramaDTO p : c.programas())
            sb.append("- ").append(p.nombre()).append(" (").append(p.nivel()).append(", ").append(p.modalidad())
              .append(", ").append(p.duracionSemestres()).append(" semestres, título: ").append(p.titulo())
              .append(", ").append(p.facultad()).append(", clases en ").append(p.edificio()).append("). ")
              .append(p.descripcion()).append('\n');
        return this;
    }

    public ContextoPromptBuilder preguntasFrecuentes(ContextoCampus c) {
        sb.append("\n## Preguntas frecuentes\n");
        for (PreguntaFrecuenteDTO f : c.preguntas())
            sb.append("P: ").append(f.pregunta()).append("\nR: ").append(f.respuesta()).append('\n');
        return this;
    }

    public String construir() {
        return sb.toString();
    }
}
