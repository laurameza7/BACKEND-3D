package co.edu.ucc.pasto3d.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ADAPTER pattern: adapts the Google Gemini REST API to the AIProvider interface
 * that the rest of the system understands.
 */
@Component
public class GeminiAIProvider implements AIProvider {

    private static final ObjectMapper JSON = new ObjectMapper();

    private final String apiKey;
    private final String model;
    private final RestClient http;
    /** PROTOTYPE: template with the campus knowledge, built once and cloned for every question. */
    private PromptTemplate prototype;
    private CampusContext prototypeContext;

    public GeminiAIProvider(@Value("${ai.gemini.api-key:}") String apiKey,
                             @Value("${ai.gemini.model:gemini-3.5-flash}") String model) {
        this.apiKey = apiKey;
        this.model = model;
        SimpleClientHttpRequestFactory f = new SimpleClientHttpRequestFactory();
        f.setConnectTimeout(5_000);
        f.setReadTimeout(25_000);
        this.http = RestClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com/v1beta")
                .requestFactory(f)
                .build();
    }

    @Override public String name() { return "gemini"; }

    @Override public boolean available() { return apiKey != null && !apiKey.isBlank(); }

    @Override
    public String answer(String question, CampusContext context) {
        return answer(question, context, null);
    }

    @Override
    public String answer(String question, CampusContext context, String hints) {
        String system = prototype(context).copy()          // clone: the prototype is never touched
                .add(hints)                         // profile of the person asking (BRIDGE)
                .add("Current date and time in Pasto: " + java.time.ZonedDateTime.now(java.time.ZoneId.of("America/Bogota"))
                        .format(java.time.format.DateTimeFormatter.ofPattern("EEEE d 'de' MMMM yyyy, h:mm a", new java.util.Locale("es", "CO"))))
                .text();

        String raw;
        try {
            raw = call(system, question, true);
        } catch (HttpClientErrorException.BadRequest e) {
            raw = call(system, question, false);        // a model without thinking levels: ask again without them
        }

        StringBuilder text = new StringBuilder();
        JsonNode r = readJson(raw);
        for (JsonNode part : r.path("candidates").path(0).path("content").path("parts"))
            if (!part.path("thought").asBoolean(false)) text.append(part.path("text").asText(""));
        if (text.isEmpty()) throw new IllegalStateException("Gemini returned no text");
        return text.toString();      // the MarkdownFormatter of its family turns it into plain text
    }

    /**
     * Calls generateContent. With lowThinking the model thinks briefly before answering, which keeps
     * the answer time short (the default "medium" level made some answers take more than 20 s).
     * The body is read as text and parsed here, so it works whatever content type Google sends.
     */
    private String call(String system, String question, boolean lowThinking) {
        Map<String, Object> config = new LinkedHashMap<>();
        config.put("maxOutputTokens", 4096);
        if (lowThinking) config.put("thinkingConfig", Map.of("thinkingLevel", "low"));
        Map<String, Object> body = Map.of(
                "systemInstruction", Map.of("parts", List.of(Map.of("text", system))),
                "contents", List.of(Map.of("role", "user", "parts", List.of(Map.of("text", question)))),
                "generationConfig", config);
        return http.post()
                .uri("/models/{model}:generateContent", model)
                .header("x-goog-api-key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(String.class);
    }

    private static JsonNode readJson(String raw) {
        try {
            return JSON.readTree(raw == null ? "{}" : raw);
        } catch (Exception e) {
            throw new IllegalStateException("Gemini returned an invalid answer", e);
        }
    }

    /** Builds the prototype with the BUILDER only when the campus data changes. */
    synchronized PromptTemplate prototype(CampusContext context) {
        if (prototype == null || !context.equals(prototypeContext)) {
            prototype = new PromptTemplate(new PromptContextBuilder()
                    .instructions()
                    .buildings(context)
                    .places(context)
                    .programs(context)
                    .faqs(context)
                    .build());
            prototypeContext = context;
        }
        return prototype;
    }
}



