package co.edu.ucc.pasto3d.intent;

import co.edu.ucc.pasto3d.ai.CampusContext;
import co.edu.ucc.pasto3d.dto.Dtos.AnswerDTO;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static co.edu.ucc.pasto3d.ai.TextUtil.normalize;

/**
 * Last link before the AI: "llévame a Bogotá", "llévame a la playa"... asks to go somewhere
 * that is not on the campus (no place, block or program handled it and the message names
 * nothing of the university), so Coopi says it only helps with the university.
 */
@Component
@Order(4)
public class OutsideCampusHandler implements IntentHandler {

    private static final Set<String> CAMPUS_WORDS = Set.of("campus", "universidad", "ucc", "cooperativa", "bloque",
            "piso", "sotano", "facultad", "programa", "carrera", "oficina", "sala", "salon", "aula", "laboratorio",
            "biblioteca", "cafeteria", "restaurante", "bano", "banos", "auditorio", "parqueadero", "cancha", "coliseo",
            "entrada", "salida", "porteria", "recepcion", "escaleras", "ascensor", "clinica", "consultorio", "mapa",
            "admisiones", "matricula", "tesoreria", "bienestar", "decanatura", "coordinacion", "profesores", "docentes");

    @Override
    public Optional<AnswerDTO> handle(Query c) {
        if (!c.asksToGo() || namesTheCampus(c)) return Optional.empty();
        return Optional.of(OffTopicHandler.onlyUniversity());
    }

    private static boolean namesTheCampus(Query c) {
        Set<String> words = new HashSet<>(Arrays.asList(c.normalized().split("[^a-z0-9]+")));
        if (words.stream().anyMatch(CAMPUS_WORDS::contains)) return true;
        CampusContext ctx = c.context();
        Stream<String> names = Stream.of(
                ctx.places().stream().map(p -> p.name()),
                ctx.buildings().stream().map(b -> b.name()),
                ctx.programs().stream().map(p -> p.name())).flatMap(s -> s);
        return names.flatMap(n -> Arrays.stream(normalize(n).split("[^a-z0-9]+")))
                .filter(w -> w.length() > 3)
                .anyMatch(words::contains);
    }
}
