package co.edu.ucc.pasto3d.controller;

import co.edu.ucc.pasto3d.dto.Dtos.HistoryMessageDTO;
import co.edu.ucc.pasto3d.dto.Dtos.QuestionDTO;
import co.edu.ucc.pasto3d.dto.Dtos.AnswerDTO;
import co.edu.ucc.pasto3d.security.AuthenticationInterceptor;
import co.edu.ucc.pasto3d.security.Session;
import co.edu.ucc.pasto3d.service.AssistantFacade;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** Coopi routes. All of them require a session (user or guest), validated by the AuthenticationInterceptor. */
@RestController
@RequestMapping("/api/assistant")
public class AssistantController {

    private final AssistantFacade assistant;

    public AssistantController(AssistantFacade assistant) { this.assistant = assistant; }

    @PostMapping("/ask")
    public AnswerDTO ask(@Valid @RequestBody QuestionDTO body,
                                  @RequestAttribute(AuthenticationInterceptor.ATTRIBUTE) Session session) {
        return assistant.ask(body.question().trim(), body.programId(), session);
    }

    /** First message from Coopi: "¡Hola! ¿Cómo estás? ¿En qué te puedo ayudar hoy?" */
    @GetMapping("/greeting")
    public Map<String, String> greeting(@RequestAttribute(AuthenticationInterceptor.ATTRIBUTE) Session session) {
        return Map.of("answer", assistant.greeting(session));
    }

    /** Last 20 questions of the registered user (guests have no history). */
    @GetMapping("/history")
    public List<HistoryMessageDTO> history(@RequestAttribute(AuthenticationInterceptor.ATTRIBUTE) Session session) {
        return assistant.history(session);
    }
}
