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
            "admisiones", "matricula", "tesoreria", "bienestar", "decanatura", "coordinacion", "profesores", "docentes",
            "profesor", "docente", "estudiante", "estudiantes", "estudiar", "clase", "clases", "semestre", "semestres",
            "inscripcion", "inscribirme", "inscribo", "admision", "requisitos", "pago", "pagos", "pagar", "beca", "becas",
            "horario", "horarios", "credito", "creditos", "nota", "notas", "carnet", "pregrado", "posgrado", "posgrados",
            "especializacion", "especializaciones", "maestria", "titulo", "costo", "precio", "valor", "telefono", "correo",
            "contacto", "extension", "wifi", "internet", "plataforma", "grado", "practicas", "intercambio", "egresado",
            "egresados", "sede", "rector", "rectoria", "decano", "coopi", "bloques", "pisos", "edificio", "edificios",
            "lugar", "lugares", "llegar", "direccion", "ubicacion", "carreras", "programas", "facultades", "oficinas",
            "laboratorios", "salones", "aulas", "servicios", "tramite", "tramites", "certificado", "certificados");

    @Override
    public Optional<AnswerDTO> handle(Query c) {
        if (!c.asksToGo() || namesTheCampus(c)) return Optional.empty();
        return Optional.of(OffTopicHandler.onlyUniversity());
    }

    private static boolean namesTheCampus(Query c) {
        return namesTheCampus(c.normalized(), c.context());
    }

    /** Does the (normalized) message mention anything of the university: a place, block, program, FAQ keyword...? */
    public static boolean namesTheCampus(String normalizedText, CampusContext ctx) {
        Set<String> words = new HashSet<>(Arrays.asList(normalizedText.split("[^a-z0-9]+")));
        if (words.stream().anyMatch(CAMPUS_WORDS::contains)) return true;
        Stream<String> names = Stream.of(
                ctx.places().stream().map(p -> p.name()),
                ctx.buildings().stream().map(b -> b.name()),
                ctx.programs().stream().map(p -> p.name() + " " + p.faculty()),
                ctx.questions().stream().map(f -> f.keywords() == null ? "" : f.keywords())).flatMap(s -> s);
        return names.flatMap(n -> Arrays.stream(normalize(n).split("[^a-z0-9]+")))
                .filter(w -> w.length() > 3 && !COMMON_WORDS.contains(w))
                .anyMatch(words::contains);
    }

    /** Words that appear in campus names but say nothing about the campus by themselves. */
    private static final Set<String> COMMON_WORDS = Set.of("para", "sobre", "como", "donde", "cuando", "quien",
            "este", "esta", "esto", "todo", "todos", "tiene", "hace", "general", "centro", "servicio", "atencion");
}
