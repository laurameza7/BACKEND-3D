package co.edu.ucc.pasto3d.intent;

import co.edu.ucc.pasto3d.dto.Dtos.AnswerDTO;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * First link of the chain: insults and rude messages are not sent to the AI.
 * Coopi only talks about the university, so it answers with {@link #ONLY_UNIVERSITY}.
 */
@Component
@Order(0)
public class OffTopicHandler implements IntentHandler {

    public static final String ONLY_UNIVERSITY = "Lo siento, únicamente respondo preguntas sobre la Universidad "
            + "Cooperativa de Colombia, campus Pasto: programas, lugares del campus, trámites y servicios. "
            + "¿En qué te puedo ayudar sobre la universidad?";

    private static final String[] RUDE_WORDS = {"mierda", "puta", "puto", "hijueputa", "hijo de puta", "hp", "gonorrea",
            "malparido", "marica", "carajo", "verga", "culo", "pendejo", "idiota", "estupido", "imbecil", "perra",
            "joder", "cono", "chupa", "maldito", "maldita"};

    public static AnswerDTO onlyUniversity() {
        return new AnswerDTO(ONLY_UNIVERSITY, "coopi", null, null);
    }

    @Override
    public Optional<AnswerDTO> handle(Query c) {
        return c.contains(RUDE_WORDS) ? Optional.of(onlyUniversity()) : Optional.empty();
    }
}
