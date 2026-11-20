package co.edu.ucc.pasto3d;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "ai.provider=local")
@ActiveProfiles("local")
@AutoConfigureMockMvc
class AuthenticationAndCoopiTests {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    /** The same thing the browser does with crypto.subtle.digest("SHA-256", ...). */
    static String fingerprint(String password) throws Exception {
        byte[] h = MessageDigest.getInstance("SHA-256").digest(password.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(h);
    }

    private JsonNode send(String path, String body, String token, int status) throws Exception {
        var req = post(path).contentType(MediaType.APPLICATION_JSON).content(body);
        if (token != null) req.header("Authorization", "Bearer " + token);
        String r = mvc.perform(req).andExpect(status().is(status)).andReturn().getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        return r.isBlank() ? null : json.readTree(r);
    }

    @Test
    void registerLoginAndWrongPassword() throws Exception {
        String h = fingerprint("MiClave#2026");
        JsonNode s = send("/api/auth/register",
                "{\"name\":\"Laura Meza\",\"email\":\"laura@ucc.edu.co\",\"fingerprint\":\"" + h + "\"}", null, 201);
        assertThat(s.get("token").asText()).isNotBlank();
        assertThat(s.get("name").asText()).isEqualTo("Laura Meza");

        // Same email again → 409
        send("/api/auth/register", "{\"name\":\"Otra\",\"email\":\"LAURA@ucc.edu.co\",\"fingerprint\":\"" + h + "\"}", null, 409);
        // Correct login
        JsonNode i = send("/api/auth/login", "{\"email\":\"laura@ucc.edu.co\",\"fingerprint\":\"" + h + "\"}", null, 200);
        assertThat(i.get("token").asText()).isNotBlank();
        // Wrong password → 401
        send("/api/auth/login", "{\"email\":\"laura@ucc.edu.co\",\"fingerprint\":\"" + fingerprint("otra") + "\"}", null, 401);
        // Plain text password (not hashed in the browser) → 400
        send("/api/auth/login", "{\"email\":\"laura@ucc.edu.co\",\"fingerprint\":\"MiClave#2026\"}", null, 400);

        // The greeting uses the user's name
        String greeting = mvc.perform(get("/api/assistant/greeting").header("Authorization", "Bearer " + i.get("token").asText()))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertThat(greeting).contains("Hola, Laura").contains("¿En qué te puedo ayudar hoy?");
    }

    @Test
    void withoutSessionCoopiDoesNotAnswer() throws Exception {
        send("/api/assistant/ask", "{\"question\":\"hola\"}", null, 401);
        send("/api/assistant/ask", "{\"question\":\"hola\"}", "fake.token", 401);
    }

    @Test
    void coopiGreetsAndTakesYouToProgramsAndPlaces() throws Exception {
        String token = send("/api/auth/guest", "{}", null, 200).get("token").asText();

        JsonNode hello = send("/api/assistant/ask", "{\"question\":\"Hola\"}", token, 200);
        assertThat(hello.get("answer").asText()).contains("¿Cómo estás? ¿En qué te puedo ayudar hoy?");

        JsonNode software = send("/api/assistant/ask",
                "{\"question\":\"Ey Coopi, llévame al programa de ingeniería de software\"}", token, 200);
        assertThat(software.at("/action/type").asText()).isEqualTo("GO_TO_PROGRAM");
        assertThat(software.at("/action/label").asText()).isEqualTo("Ingeniería de Software");
        // Takes you to the Faculty of Engineering (Block B, floor 4)
        assertThat(software.at("/action/placeId").isNumber()).isTrue();
        assertThat(software.get("answer").asText()).contains("Facultad de Ingeniería").contains("piso 4");

        JsonNode rest = send("/api/assistant/ask", "{\"question\":\"quiero ver el restaurante\"}", token, 200);
        assertThat(rest.at("/action/type").asText()).isEqualTo("GO_TO_PLACE");
        assertThat(rest.at("/action/label").asText()).isEqualTo("Cafetería");

        JsonNode all = send("/api/assistant/ask", "{\"question\":\"¿qué programas hay?\"}", token, 200);
        assertThat(all.at("/action/type").asText()).isEqualTo("SHOW_PROGRAMS");

        JsonNode medicine = send("/api/assistant/ask", "{\"question\":\"llévame a medicina\"}", token, 200);
        assertThat(medicine.at("/action/label").asText()).isEqualTo("Medicina");

        JsonNode urcunina = send("/api/assistant/ask", "{\"question\":\"quiero ir al auditorio Urcunina\"}", token, 200);
        assertThat(urcunina.at("/action/label").asText()).isEqualTo("Auditorio Urcunina");
        JsonNode gym = send("/api/assistant/ask", "{\"question\":\"¿dónde queda el gimnasio?\"}", token, 200);
        assertThat(gym.at("/action/label").asText()).isEqualTo("Gimnasio");
        JsonNode english = send("/api/assistant/ask", "{\"question\":\"llévame al centro de inglés\"}", token, 200);
        assertThat(english.at("/action/label").asText()).isEqualTo("Open Language Center");
    }
}
