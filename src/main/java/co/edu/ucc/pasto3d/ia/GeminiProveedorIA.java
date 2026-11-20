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
    /** PROTOTYPE: plantilla con el conocimiento del campus, se arma una vez y se clona por pregunta. */
    private PlantillaPrompt prototipo;
    private ContextoCampus contextoDelPrototipo;

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
        return responder(pregunta, contexto, null);
    }

    @Override
    public String responder(String pregunta, ContextoCampus contexto, String indicaciones) {
        String sistema = prototipo(contexto).copiar()          // clon: el prototipo no se toca
                .agregar(indicaciones)                         // perfil de quien pregunta (BRIDGE)
                .agregar("Fecha y hora actual en Pasto: " + java.time.ZonedDateTime.now(java.time.ZoneId.of("America/Bogota"))
                        .format(java.time.format.DateTimeFormatter.ofPattern("EEEE d 'de' MMMM yyyy, h:mm a", new java.util.Locale("es", "CO"))))
                .texto();

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
        return texto.toString();      // el FormateadorMarkdown de su familia lo deja en texto plano
    }

    /** Arma el prototipo con el BUILDER solo cuando cambian los datos del campus. */
    synchronized PlantillaPrompt prototipo(ContextoCampus contexto) {
        if (prototipo == null || !contexto.equals(contextoDelPrototipo)) {
            prototipo = new PlantillaPrompt(new ContextoPromptBuilder()
                    .instrucciones()
                    .edificios(contexto)
                    .lugares(contexto)
                    .programas(contexto)
                    .preguntasFrecuentes(contexto)
                    .construir());
            contextoDelPrototipo = contexto;
        }
        return prototipo;
    }
}



