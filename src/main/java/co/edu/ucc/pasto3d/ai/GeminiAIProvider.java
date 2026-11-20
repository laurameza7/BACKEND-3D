package co.edu.ucc.pasto3d.ai;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * ADAPTER pattern: adapts the Google Gemini REST API to the AIProvider interface
 * that the rest of the system understands.
 */
@Component
public class GeminiAIProvider implements AIProvider {

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

        Map<String, Object> body = Map.of(
                "systemInstruction", Map.of("parts", List.of(Map.of("text", system))),
                "contents", List.of(Map.of("role", "user", "parts", List.of(Map.of("text", question)))),
                "generationConfig", Map.of("temperature", 0.3, "maxOutputTokens", 4096)
        );

        JsonNode r = http.post()
                .uri("/models/{model}:generateContent", model)
                .header("x-goog-api-key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(JsonNode.class);

        StringBuilder text = new StringBuilder();
        if (r != null)
            for (JsonNode part : r.path("candidates").path(0).path("content").path("parts"))
                text.append(part.path("text").asText(""));
        if (text.isEmpty()) throw new IllegalStateException("Gemini returned no text");
        return text.toString();      // the MarkdownFormatter of its family turns it into plain text
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



