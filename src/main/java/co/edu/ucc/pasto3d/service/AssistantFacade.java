package co.edu.ucc.pasto3d.service;

import co.edu.ucc.pasto3d.dto.Dtos.*;
import co.edu.ucc.pasto3d.ai.CampusContext;
import co.edu.ucc.pasto3d.ai.AIFactory;
import co.edu.ucc.pasto3d.ai.AssistantProfile;
import co.edu.ucc.pasto3d.ai.AIProvider;
import co.edu.ucc.pasto3d.ai.AIProviderFactory;
import co.edu.ucc.pasto3d.intent.Query;
import co.edu.ucc.pasto3d.intent.IntentHandler;
import co.edu.ucc.pasto3d.intent.ProgramsHandler;
import co.edu.ucc.pasto3d.intent.GreetingHandler;
import co.edu.ucc.pasto3d.model.Conversation;
import co.edu.ucc.pasto3d.repository.ConversationRepository;
import co.edu.ucc.pasto3d.security.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static co.edu.ucc.pasto3d.ai.TextUtil.normalize;

/**
 * FACADE pattern: a single entry point ("ask") that hides all the complexity:
 * 1) the chain of intents (greeting, take me to a program / place),
 * 2) if none applies, the chain of AI providers (Gemini → local),
 * 3) detecting the mentioned building and saving the user's conversation.
 */
@Service
public class AssistantFacade {

    private static final Logger log = LoggerFactory.getLogger(AssistantFacade.class);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final CampusService campus;
    private final AIProviderFactory factory;
    private final ConversationRepository conversations;
    private final List<IntentHandler> intents;   // Spring gives them sorted by @Order

    public AssistantFacade(CampusService campus, AIProviderFactory factory, ConversationRepository conversations,
                           List<IntentHandler> intents) {
        this.campus = campus;
        this.factory = factory;
        this.conversations = conversations;
        this.intents = intents;
    }

    /** Compatibility: question without session or program. */
    public AnswerDTO ask(String question) {
        return ask(question, null, null);
    }

    public AnswerDTO ask(String question, Integer programId, Session session) {
        CampusContext context = campus.context();
        ProgramDTO currentProgram = programId == null ? null
                : context.programs().stream().filter(p -> p.id().equals(programId)).findFirst().orElse(null);
        Query query = Query.of(question, context, session, currentProgram);

        AnswerDTO result = null;
        for (IntentHandler m : intents) {               // Chain of intents
            Optional<AnswerDTO> r = m.handle(query);
            if (r.isPresent()) { result = r.get(); break; }
        }
        if (result == null) result = askTheAI(question, query, context);

        try {
            conversations.save(new Conversation(question, result.answer(), result.provider(),
                    session == null ? null : session.userId(),
                    result.action() == null ? null : result.action().type()));
        } catch (Exception e) {
            log.warn("The conversation could not be saved: {}", e.getMessage());
        }
        return result;
    }

    public String greeting(Session session) {
        String name = session == null || session.isGuest() || session.name() == null || session.name().isBlank()
                ? null : session.name().trim().split("\\s+")[0];
        return GreetingHandler.greeting(name);
    }

    public List<HistoryMessageDTO> history(Session session) {
        if (session == null || session.isGuest()) return List.of();
        return conversations.findTop20ByUserIdOrderByIdDesc(session.userId()).stream()
                .map(c -> new HistoryMessageDTO(c.getQuestion(), c.getAnswer(),
                        c.getCreatedAt() == null ? "" : c.getCreatedAt().format(DATE_FORMAT)))
                .toList()
                .reversed();
    }

    private AnswerDTO askTheAI(String question, Query query, CampusContext context) {
        // If the user is looking at a program and does not name another one, the question is about that program.
        String sent = question;
        ProgramDTO current = query.currentProgram();
        if (current != null && ProgramsHandler.find(query.normalized(), context.programs()).isEmpty()) {
            sent = question + " (pregunta sobre el programa " + current.name() + ")";
        }

        String answer = null;
        String used = null;
        for (AIFactory family : factory.createChain()) {          // Fallback chain: gemini → local
            AIProvider provider = family.createProvider();        // ABSTRACT FACTORY: provider…
            AssistantProfile profile = AssistantProfile.forSession(query.session(), provider);   // BRIDGE profile × provider
            try {
                answer = family.createFormatter().format(profile.answer(sent, context));  // …and its formatter
                used = provider.name();
                break;
            } catch (Exception e) {
                log.warn("The provider {} failed: {}", provider.name(), e.getMessage());
            }
        }
        return new AnswerDTO(answer, used, detectBuilding(answer, context), null);
    }

    /** Finds the first block mentioned in the answer to highlight it on the map. */
    private Integer detectBuilding(String answer, CampusContext context) {
        String r = normalize(answer);
        Integer id = null;
        int firstPos = Integer.MAX_VALUE;
        for (BuildingDTO e : context.buildings()) {
            String shortName = normalize(e.name().split(" - ")[0]);
            int pos = r.indexOf(shortName);
            if (pos >= 0 && pos < firstPos) { firstPos = pos; id = e.id(); }
        }
        return id;
    }
}
