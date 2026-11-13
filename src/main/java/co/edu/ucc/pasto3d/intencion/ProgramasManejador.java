package co.edu.ucc.pasto3d.intencion;

import co.edu.ucc.pasto3d.dto.Dtos.AccionDTO;
import co.edu.ucc.pasto3d.dto.Dtos.ProgramaDTO;
import co.edu.ucc.pasto3d.dto.Dtos.RespuestaDTO;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static co.edu.ucc.pasto3d.ia.TextoUtil.normalizar;

/**
 * "Llévame al programa de Ingeniería de Software" → abre el programa en la página.
 * "¿Qué programas hay?" → abre la lista de todos los programas.
 */
@Component
@Order(2)
public class ProgramasManejador implements ManejadorIntencion {

    /** Palabras que no distinguen un programa de otro. */
    private static final Set<String> GENERICAS = Set.of("ingenieria", "especializacion", "en", "de", "del", "la", "el",
            "y", "con", "por", "tecnico", "laboral", "competencias", "auxiliar", "sistemas");

    @Override
    public Optional<RespuestaDTO> atender(Consulta c) {
        if (pideLista(c)) {
            String nombres = c.contexto().programas().stream()
                    .filter(p -> "PREGRADO".equals(p.nivel())).map(ProgramaDTO::nombre).collect(Collectors.joining(", "));
            long total = c.contexto().programas().size();
            return Optional.of(new RespuestaDTO(
                    "En el campus Pasto hay " + total + " programas. De pregrado: " + nombres
                    + ". También hay especializaciones en Derecho, Medicina, Odontología y otras áreas. "
                    + "Te abrí la lista completa: toca cualquiera y te llevo a él.",
                    "coopi", null, AccionDTO.de("VER_PROGRAMAS")));
        }
        Optional<ProgramaDTO> programa = buscar(c.normal(), c.contexto().programas());
        if (programa.isEmpty() || !(c.pideIr() || c.contiene("programa", "carrera"))) return Optional.empty();
        // "¿Cuánto dura Medicina?" sin pedir ir → lo responde la IA con el contexto completo.
        if (!c.pideIr() && esPreguntaConcreta(c)) return Optional.empty();

        ProgramaDTO p = programa.get();
        String donde = p.edificio() != null ? " Sus prácticas y clínicas están en el " + p.edificio() + "." : "";
        String texto = "¡Listo! Te llevo al programa de " + p.nombre() + " (" + p.facultad() + ")."
                + (p.duracionSemestres() != null ? " Dura " + p.duracionSemestres() + " semestres" : "")
                + (p.titulo() != null ? " y otorgas el título de " + p.titulo() + "." : ".")
                + donde + " ¿En qué te puedo ayudar con " + p.nombre() + "?";
        texto = texto.replace("otorgas", "otorga");
        return Optional.of(new RespuestaDTO(texto, "coopi", p.edificioId(),
                new AccionDTO("IR_PROGRAMA", p.id(), null, p.edificioId(), null, p.nombre())));
    }

    private static boolean pideLista(Consulta c) {
        return c.contiene("todos los programas", "que programas", "cuales programas", "cuales son los programas",
                "lista de programas", "programas hay", "programas tienen", "programas ofrece", "que carreras",
                "cuales carreras", "carreras hay", "carreras tienen", "oferta academica", "ver programas", "ver los programas");
    }

    private static boolean esPreguntaConcreta(Consulta c) {
        return c.contiene("cuanto", "cuantos", "costo", "precio", "valor", "requisitos", "como me inscribo",
                "inscripcion", "duracion", "dura", "titulo", "que se estudia", "materias", "pensum", "horario");
    }

    /** Busca el programa nombrado en el texto: gana el que tenga más palabras distintivas coincidentes. */
    public static Optional<ProgramaDTO> buscar(String texto, List<ProgramaDTO> programas) {
        Set<String> palabras = new HashSet<>(Arrays.asList(texto.split(" ")));
        boolean quiereEspecializacion = palabras.contains("especializacion") || palabras.contains("posgrado");
        ProgramaDTO mejor = null;
        double mejorPuntaje = 0;
        for (ProgramaDTO p : programas) {
            List<String> claves = Arrays.stream(normalizar(p.nombre()).split(" "))
                    .filter(w -> !GENERICAS.contains(w) && w.length() > 2).toList();
            if (claves.isEmpty()) continue;
            long coinciden = claves.stream().filter(w -> palabras.contains(w) || palabras.contains(w + "s")).count();
            if (coinciden == 0) continue;
            double puntaje = coinciden + (double) coinciden / claves.size();
            boolean esEsp = "ESPECIALIZACION".equals(p.nivel());
            if (esEsp != quiereEspecializacion) puntaje -= 0.5;
            if (puntaje > mejorPuntaje) { mejorPuntaje = puntaje; mejor = p; }
        }
        return Optional.ofNullable(mejor);
    }
}
