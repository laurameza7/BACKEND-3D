package co.edu.ucc.pasto3d.ia;

import co.edu.ucc.pasto3d.dto.Dtos.*;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.stream.Collectors;

import static co.edu.ucc.pasto3d.ia.TextoUtil.nombrePiso;
import static co.edu.ucc.pasto3d.ia.TextoUtil.normalizar;

/**
 * Estrategia de respaldo: responde buscando coincidencias en la base de datos
 * (programas, lugares y preguntas frecuentes). Funciona sin conexión a internet
 * ni API key, así la aplicación nunca se queda sin responder.
 */
@Component
public class LocalProveedorIA implements ProveedorIA {

    @Override public String nombre() { return "local"; }

    @Override public boolean disponible() { return true; }

    @Override
    public String responder(String pregunta, ContextoCampus c) {
        String q = " " + normalizar(pregunta) + " ";

        if (q.matches(".*\\b(hola|buenas|buenos dias|buenas tardes|hey)\\b.*") && q.length() < 25)
            return "¡Hola! Soy Coopi, el asistente del campus Pasto de la UCC. Pregúntame por programas, "
                 + "oficinas, laboratorios, pagos o cómo llegar a un bloque.";

        if (q.matches(".*\\b(programas|carreras|que estudiar|ofrecen|oferta)\\b.*"))
            return "En el campus Pasto se ofrecen estos programas:\n" + c.programas().stream()
                    .map(p -> "• " + p.nombre() + " (" + p.facultad() + ")")
                    .collect(Collectors.joining("\n"));

        // ¿Pregunta por un programa puntual?
        for (ProgramaDTO p : c.programas()) {
            String nombre = normalizar(p.nombre());
            String clave = nombre.replace("ingenieria de ", "").replace("ingenieria ", "");
            if (q.contains(" " + nombre + " ") || q.contains(" " + clave + " "))
                return p.nombre() + ": " + p.descripcion() + " Modalidad " + p.modalidad().toLowerCase()
                     + (p.duracionSemestres() != null ? ", " + p.duracionSemestres() + " semestres" : "")
                     + ". Título: " + p.titulo() + ". Pertenece a la " + p.facultad()
                     + (p.edificio() != null ? " y sus clases se dictan principalmente en el " + p.edificio() : "")
                     + ". Para inscribirte entra a www.ucc.edu.co o comunícate con Admisiones (602 7370660 ext. 2312).";
        }

        // Mejor pregunta frecuente según palabras clave
        PreguntaFrecuenteDTO mejorFaq = null;
        int mejorPuntaje = 0;
        for (PreguntaFrecuenteDTO f : c.preguntas()) {
            int puntaje = puntaje(q, f.pregunta()) + coincidenciasClave(q, f.palabrasClave());
            if (puntaje > mejorPuntaje) { mejorPuntaje = puntaje; mejorFaq = f; }
        }

        // Mejor lugar por nombre
        LugarDTO mejorLugar = c.lugares().stream()
                .max(Comparator.comparingInt(l -> puntaje(q, l.nombre())))
                .filter(l -> puntaje(q, l.nombre()) >= 1)
                .orElse(null);

        if (mejorLugar != null && puntaje(q, mejorLugar.nombre()) >= mejorPuntaje) {
            StringBuilder sb = new StringBuilder(mejorLugar.nombre()).append(" queda en el ")
                    .append(mejorLugar.edificio()).append(", ").append(nombrePiso(mejorLugar.piso())).append('.');
            if (mejorLugar.descripcion() != null)
                sb.append(' ').append(mejorLugar.descripcion()).append(mejorLugar.descripcion().endsWith(".") ? "" : ".");
            if (mejorLugar.horario() != null) sb.append(" Horario: ").append(mejorLugar.horario()).append('.');
            if (mejorLugar.telefono() != null) sb.append(" Teléfono: ").append(mejorLugar.telefono()).append('.');
            return sb.toString();
        }
        if (mejorFaq != null) return mejorFaq.respuesta();

        for (EdificioDTO e : c.edificios())
            if (puntaje(q, e.nombre()) >= 2 || q.contains(" " + normalizar(e.nombre()) + " "))
                return e.nombre() + ": " + e.descripcion();

        return "No encontré esa información en mi base de conocimiento. Puedes llamar al conmutador "
             + "602 7370660 (Admisiones, ext. 2312).";
    }

    /** Cuenta las palabras significativas (4+ letras) de 'texto' que aparecen en la pregunta. */
    private int puntaje(String preguntaNormalizada, String texto) {
        int p = 0;
        for (String palabra : normalizar(texto).split(" "))
            if (palabra.length() >= 4 && preguntaNormalizada.contains(raiz(palabra))) p++;
        return p;
    }

    /** Cada palabra clave de la FAQ que aparezca completa en la pregunta suma 2 puntos. */
    private int coincidenciasClave(String preguntaNormalizada, String claves) {
        if (claves == null) return 0;
        int p = 0;
        for (String clave : claves.split(","))
            if (!clave.isBlank() && preguntaNormalizada.contains(" " + normalizar(clave) + " ")) p += 2;
        return p;
    }

    private String raiz(String palabra) {
        return palabra.length() > 6 ? palabra.substring(0, palabra.length() - 2) : palabra;
    }
}



