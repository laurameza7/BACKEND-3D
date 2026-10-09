package co.edu.ucc.pasto3d.ia;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * Patrón ADAPTER: adapta la API REST de Google Gemini a la interfaz ProveedorIA
 * que entiende el resto del sistema.
 */
@Component
public class GeminiProveedorIA implements ProveedorIA {

    private final String apiKey;
    private final String modelo;
    private final RestClient http;

    public GeminiProveedorIA(@Value("${ia.gemini.api-key:}") String apiKey,
                             @Value("${ia.gemini.modelo:gemini-3.5-flash}") String modelo) {
        this.apiKey = apiKey;
        this.modelo = modelo;
        SimpleClientHttpRequestFactory f = new SimpleClientHttpRequestFactory();
        f.setConnectTimeout(5_000);
        f.setReadTimeout(25_000);
        this.http = RestClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com/v1beta")
                .requestFactory(f)
                .build();
    }

    @Override public String nombre() { return "gemini"; }

    @Override public boolean disponible() { return apiKey != null && !apiKey.isBlank(); }

    @Override
    public String responder(String pregunta, ContextoCampus contexto) {
        String sistema = new ContextoPromptBuilder()
                .instrucciones()
                .edificios(contexto)
                .lugares(contexto)
                .programas(contexto)
                .preguntasFrecuentes(contexto)
                .construir();

        Map<String, Object> cuerpo = Map.of(
                "systemInstruction", Map.of("parts", List.of(Map.of("text", sistema))),
                "contents", List.of(Map.of("role", "user", "parts", List.of(Map.of("text", pregunta)))),
                "generationConfig", Map.of("temperature", 0.3, "maxOutputTokens", 4096)
        );

        JsonNode r = http.post()
                .uri("/models/{modelo}:generateContent", modelo)
                .header("x-goog-api-key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(cuerpo)
                .retrieve()
                .body(JsonNode.class);

        StringBuilder texto = new StringBuilder();
        if (r != null)
            for (JsonNode parte : r.path("candidates").path(0).path("content").path("parts"))
                texto.append(parte.path("text").asText(""));
        if (texto.isEmpty()) throw new IllegalStateException("Gemini no devolvió texto");
        return limpiarMarkdown(texto.toString());
    }

    /** El chat muestra texto plano: quita negritas, títulos y viñetas de Markdown. */
    static String limpiarMarkdown(String t) {
        return t.replace("**", "")
                .replaceAll("(?m)^#{1,6}[ \\t]*", "")
                .replaceAll("(?m)^[ \\t]*[*-][ \\t]+", "• ")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
    }
}



