package co.edu.ucc.pasto3d.intent;

import co.edu.ucc.pasto3d.dto.Dtos.ActionDTO;
import co.edu.ucc.pasto3d.dto.Dtos.AnswerDTO;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

/** Handles short greetings: "hola", "buenas tardes", "ey Coopi"... */
@Component
@Order(1)
public class GreetingHandler implements IntentHandler {

    private static final Set<String> GREETINGS = Set.of("hola", "holi", "buenas", "buenos dias", "buenas tardes",
            "buenas noches", "hey", "ey", "oye", "que mas", "quiubo", "saludos", "coopi", "como estas", "hola coopi",
            "quiero que me des informacion", "quiero informacion", "necesito informacion", "dame informacion",
            "necesito ayuda", "ayuda", "ayudame");

    public static String greeting(String name) {
        return "¡Hola" + (name == null ? "" : ", " + name) + "! ¿Cómo estás? ¿En qué te puedo ayudar hoy? "
                + "Puedo contarte sobre los programas, llevarte a cualquier lugar del campus o resolver tus dudas.";
    }

    @Override
    public Optional<AnswerDTO> handle(Query c) {
        String t = c.normalized().replaceAll("\\b(por favor|porfa|coopi|ey|oye|hey)\\b", "").replaceAll("\\s+", " ").trim();
        boolean onlyGreeting = t.isEmpty() || GREETINGS.contains(t)
                || (t.split(" ").length <= 4 && GREETINGS.stream().anyMatch(s -> t.startsWith(s + " ") && !t.contains("?")
                    && GREETINGS.contains(t.substring(s.length()).trim())));
        if (!onlyGreeting) return Optional.empty();
        return Optional.of(new AnswerDTO(greeting(c.userName()), "coopi", null, ActionDTO.of("GREETING")));
    }
}
