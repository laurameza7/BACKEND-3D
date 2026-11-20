package co.edu.ucc.pasto3d.intent;

import co.edu.ucc.pasto3d.dto.Dtos.AnswerDTO;

import java.util.Optional;

/**
 * CHAIN OF RESPONSIBILITY pattern: each handler checks the message and, if it
 * recognizes it (greeting, "llévame a...", "qué programas hay"...), answers and creates
 * an action for the frontend. If none handles it, the AI answers.
 */
public interface IntentHandler {
    Optional<AnswerDTO> handle(Query query);
}
