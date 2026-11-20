package co.edu.ucc.pasto3d.controller;

import co.edu.ucc.pasto3d.model.AppUser;
import co.edu.ucc.pasto3d.repository.AppUserRepository;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Registration, login, guest, /me and history, including the rule that the password is never stored. */
@SpringBootTest(properties = "ai.provider=local")
@ActiveProfiles("local")
@AutoConfigureMockMvc
class AuthApiTests {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired AppUserRepository users;

    static String fingerprint(String password) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(password.getBytes(StandardCharsets.UTF_8)));
    }

    private static String newEmail() { return "test-" + UUID.randomUUID().toString().substring(0, 8) + "@ucc.edu.co"; }

    private JsonNode register(String name, String email, String password) throws Exception {
        String r = mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + name + "\",\"email\":\"" + email + "\",\"fingerprint\":\"" + fingerprint(password) + "\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        return json.readTree(r);
    }

    @Test
    void passwordAndItsFingerprintAreNeverStored() throws Exception {
        String email = newEmail();
        String password = "Coopi2026!";
        register("Manuel Villarreal", email, password);

        AppUser u = users.findByEmailIgnoreCase(email).orElseThrow();
        assertThat(u.getPasswordHash())
                .doesNotContain(password)
                .isNotEqualTo(fingerprint(password))
                .startsWith("$2");                         // BCrypt
        assertThat(u.getPasswordHash()).hasSize(60);
    }

    @Test
    void emailIsStoredLowercaseAndNameTrimmed() throws Exception {
        String email = newEmail();
        JsonNode s = register("  Juan Moreno  ", email.toUpperCase(), "safe-password");
        assertThat(s.get("email").asText()).isEqualTo(email);
        assertThat(s.get("name").asText()).isEqualTo("Juan Moreno");
        assertThat(s.get("role").asText()).isEqualTo("STUDENT");

        // Logging in works with the email written in capitals
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\" " + email.toUpperCase() + " \",\"fingerprint\":\"" + fingerprint("safe-password") + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", not(emptyString())));
    }

    @Test
    void registrationValidations() throws Exception {
        String h = fingerprint("x");
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Ana\",\"email\":\"no-es-email\",\"fingerprint\":\"" + h + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("El correo no es válido.")));
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"   \",\"email\":\"ana@ucc.edu.co\",\"fingerprint\":\"" + h + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("El campo «nombre» es obligatorio.")));
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + "a".repeat(81) + "\",\"email\":\"ana@ucc.edu.co\",\"fingerprint\":\"" + h + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("El campo «nombre» es demasiado largo.")));
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Ana\",\"email\":\"ana@ucc.edu.co\",\"fingerprint\":\"MiClaveEnTextoPlano\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("cifrada")));
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content("{esto no es json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("El cuerpo de la solicitud no es un JSON válido.")));
    }

    @Test
    void loginWithUnknownEmailGivesTheSameMessageAsWrongPassword() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"nadie@ucc.edu.co\",\"fingerprint\":\"" + fingerprint("x") + "\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", is("Correo o contraseña incorrectos.")));
    }

    @Test
    void meReturnsTheTokenOwner() throws Exception {
        String token = register("Laura Meza", newEmail(), "password1234").get("token").asText();
        mvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Laura Meza")))
                .andExpect(jsonPath("$.guest", is(false)));

        String guest = json.readTree(mvc.perform(post("/api/auth/guest")).andReturn().getResponse().getContentAsString())
                .get("token").asText();
        mvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + guest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role", is("GUEST")))
                .andExpect(jsonPath("$.guest", is(true)));

        mvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", containsString("iniciar sesión")));
        mvc.perform(get("/api/auth/me").header("Authorization", "Basic abc")).andExpect(status().isUnauthorized());
    }

    @Test
    void historyBelongsToTheUserAndGuestsHaveNone() throws Exception {
        String laura = register("Laura", newEmail(), "laura-password").get("token").asText();
        String juan = register("Juan", newEmail(), "juan-password").get("token").asText();

        for (String p : new String[]{"hola", "llévame a la biblioteca"})
            mvc.perform(post("/api/assistant/ask").header("Authorization", "Bearer " + laura)
                    .contentType(MediaType.APPLICATION_JSON).content("{\"question\":\"" + p + "\"}")).andExpect(status().isOk());

        mvc.perform(get("/api/assistant/history").header("Authorization", "Bearer " + laura))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].question", is("hola")))                 // in chronological order
                .andExpect(jsonPath("$[1].question", is("llévame a la biblioteca")))
                .andExpect(jsonPath("$[1].date", matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}")));

        mvc.perform(get("/api/assistant/history").header("Authorization", "Bearer " + juan))
                .andExpect(jsonPath("$.length()", is(0)));

        String guest = json.readTree(mvc.perform(post("/api/auth/guest")).andReturn().getResponse().getContentAsString())
                .get("token").asText();
        mvc.perform(get("/api/assistant/history").header("Authorization", "Bearer " + guest))
                .andExpect(jsonPath("$.length()", is(0)));
    }

    @Test
    void emptyOrLongQuestionsAreRejected() throws Exception {
        String t = json.readTree(mvc.perform(post("/api/auth/guest")).andReturn().getResponse().getContentAsString())
                .get("token").asText();
        mvc.perform(post("/api/assistant/ask").header("Authorization", "Bearer " + t)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"question\":\"   \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("El campo «pregunta» es obligatorio.")));
        mvc.perform(post("/api/assistant/ask").header("Authorization", "Bearer " + t)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"question\":\"" + "x".repeat(501) + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("El campo «pregunta» es demasiado largo.")));
    }

    @Test
    void askAboutTheProgramBeingViewed() throws Exception {
        String t = json.readTree(mvc.perform(post("/api/auth/guest")).andReturn().getResponse().getContentAsString())
                .get("token").asText();
        mvc.perform(post("/api/assistant/ask").header("Authorization", "Bearer " + t)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"question\":\"¿cuánto dura?\",\"programId\":99999}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer", not(emptyString())));
    }
}
