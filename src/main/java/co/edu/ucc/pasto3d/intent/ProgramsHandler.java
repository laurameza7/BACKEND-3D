package co.edu.ucc.pasto3d.intent;

import co.edu.ucc.pasto3d.dto.Dtos.ActionDTO;
import co.edu.ucc.pasto3d.dto.Dtos.ProgramDTO;
import co.edu.ucc.pasto3d.dto.Dtos.AnswerDTO;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static co.edu.ucc.pasto3d.ai.TextUtil.normalize;

/**
 * "Llévame al programa de Ingeniería de Software" → opens the program on the page.
 * "¿Qué programas hay?" → opens the list of all programs.
 */
@Component
@Order(2)
public class ProgramsHandler implements IntentHandler {

    /** Words that do not tell one program from another. */
    private static final Set<String> GENERIC_WORDS = Set.of("ingenieria", "especializacion", "en", "de", "del", "la", "el",
            "y", "con", "por", "tecnico", "laboral", "competencias", "auxiliar", "sistemas");

    @Override
    public Optional<AnswerDTO> handle(Query c) {
        if (asksForList(c)) {
            String names = c.context().programs().stream()
                    .filter(p -> "UNDERGRADUATE".equals(p.level())).map(ProgramDTO::name).collect(Collectors.joining(", "));
            long total = c.context().programs().size();
            return Optional.of(new AnswerDTO(
                    "En el campus Pasto hay " + total + " programas. De pregrado: " + names
                    + ". También hay especializaciones en Derecho, Medicina, Odontología y otras áreas. "
                    + "Te abrí la lista completa: toca cualquiera y te llevo a él.",
                    "coopi", null, ActionDTO.of("SHOW_PROGRAMS")));
        }
        Optional<ProgramDTO> program = find(c.normalized(), c.context().programs());
        // "llévame a la facultad de medicina" asks for the faculty (a place), not for the program
        if (c.contains("facultad") && !c.contains("programa", "carrera")) return Optional.empty();
        if (program.isEmpty() || !(c.asksToGo() || c.contains("programa", "carrera"))) return Optional.empty();
        // "¿Cuánto dura Medicina?" without asking to go → the AI answers it with the full context.
        if (!c.asksToGo() && isSpecificQuestion(c)) return Optional.empty();

        ProgramDTO p = program.get();
        String where = p.place() != null
                ? " Te llevo a la " + p.place() + ", que queda en el " + p.location() + "."
                : p.building() != null ? " Sus prácticas y clínicas están en el " + p.building() + "." : "";
        String text = "¡Listo! Te llevo al programa de " + p.name() + " (" + p.faculty() + ")."
                + detail(p.durationSemesters(), p.degreeTitle())
                + where + " ¿En qué te puedo ayudar con " + p.name() + "?";
        return Optional.of(new AnswerDTO(text, "coopi", p.buildingId(),
                new ActionDTO("GO_TO_PROGRAM", p.id(), p.placeId(), p.buildingId(), null, p.name())));
    }

    /** " Dura 9 semestres y otorga el título de Abogado(a)." (skips whatever is not registered). */
    static String detail(Integer semesters, String degreeTitle) {
        if (semesters == null && degreeTitle == null) return "";
        if (semesters == null) return " Otorga el título de " + degreeTitle + ".";
        return " Dura " + semesters + " semestres" + (degreeTitle == null ? "." : " y otorga el título de " + degreeTitle + ".");
    }

    private static boolean asksForList(Query c) {
        return c.contains("todos los programas", "que programas", "cuales programas", "cuales son los programas",
                "lista de programas", "programas hay", "programas tienen", "programas ofrece", "que carreras",
                "cuales carreras", "carreras hay", "carreras tienen", "oferta academica", "ver programas", "ver los programas");
    }

    private static boolean isSpecificQuestion(Query c) {
        return c.contains("cuanto", "cuantos", "costo", "precio", "valor", "requisitos", "como me inscribo",
                "inscripcion", "duracion", "dura", "titulo", "que se estudia", "materias", "pensum", "schedule");
    }

    /** Finds the program named in the text: the one with more matching distinctive words wins. */
    public static Optional<ProgramDTO> find(String text, List<ProgramDTO> programs) {
        Set<String> words = new HashSet<>(Arrays.asList(text.split(" ")));
        boolean wantsSpecialization = words.contains("especializacion") || words.contains("posgrado");
        ProgramDTO best = null;
        double bestScore = 0;
        for (ProgramDTO p : programs) {
            List<String> keys = Arrays.stream(normalize(p.name()).split(" "))
                    .filter(w -> !GENERIC_WORDS.contains(w) && w.length() > 2).toList();
            if (keys.isEmpty()) continue;
            long matching = keys.stream().filter(w -> words.contains(w) || words.contains(w + "s")).count();
            if (matching == 0) continue;
            double score = matching + (double) matching / keys.size();
            boolean isSpecialization = "SPECIALIZATION".equals(p.level());
            // A specialization is only chosen without saying "especialización" if at least half
            // of its words are named: "llévame al café" is the cafeteria, not the Specialization in Coffee Production and Trade.
            if (isSpecialization && !wantsSpecialization && matching * 2 < keys.size()) continue;
            if (isSpecialization != wantsSpecialization) score -= 0.5;
            if (score > bestScore) { bestScore = score; best = p; }
        }
        return Optional.ofNullable(best);
    }
}
