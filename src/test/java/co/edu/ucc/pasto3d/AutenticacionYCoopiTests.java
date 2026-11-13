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

@SpringBootTest(properties = "ia.proveedor=local")
@ActiveProfiles("local")
@AutoConfigureMockMvc
class AutenticacionYCoopiTests {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    /** Lo mismo que hace el navegador con crypto.subtle.digest("SHA-256", ...). */
    static String huella(String clave) throws Exception {
        byte[] h = MessageDigest.getInstance("SHA-256").digest(clave.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(h);
    }

    private JsonNode post(String ruta, String cuerpo, String token, int estado) throws Exception {
        var req = post(ruta).contentType(MediaType.APPLICATION_JSON).content(cuerpo);
        if (token != null) req.header("Authorization", "Bearer " + token);
        String r = mvc.perform(req).andExpect(status().is(estado)).andReturn().getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        return r.isBlank() ? null : json.readTree(r);
    }

    @Test
    void registroIngresoYContrasenaIncorrecta() throws Exception {
        String h = huella("MiClave#2026");
        JsonNode s = post("/api/auth/registro",
                "{\"nombre\":\"Laura Meza\",\"correo\":\"laura@ucc.edu.co\",\"huella\":\"" + h + "\"}", null, 201);
        assertThat(s.get("token").asText()).isNotBlank();
        assertThat(s.get("nombre").asText()).isEqualTo("Laura Meza");

        // Mismo correo otra vez → 409
        post("/api/auth/registro", "{\"nombre\":\"Otra\",\"correo\":\"LAURA@ucc.edu.co\",\"huella\":\"" + h + "\"}", null, 409);
        // Ingreso correcto
        JsonNode i = post("/api/auth/ingresar", "{\"correo\":\"laura@ucc.edu.co\",\"huella\":\"" + h + "\"}", null, 200);
        assertThat(i.get("token").asText()).isNotBlank();
        // Contraseña incorrecta → 401
        post("/api/auth/ingresar", "{\"correo\":\"laura@ucc.edu.co\",\"huella\":\"" + huella("otra") + "\"}", null, 401);
        // Contraseña en texto plano (no cifrada en el navegador) → 400
        post("/api/auth/ingresar", "{\"correo\":\"laura@ucc.edu.co\",\"huella\":\"MiClave#2026\"}", null, 400);

        // El saludo usa el nombre del usuario
        String saludo = mvc.perform(get("/api/asistente/saludo").header("Authorization", "Bearer " + i.get("token").asText()))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertThat(saludo).contains("Hola, Laura").contains("¿En qué te puedo ayudar hoy?");
    }

    @Test
    void sinSesionCoopiNoResponde() throws Exception {
        post("/api/asistente/preguntar", "{\"pregunta\":\"hola\"}", null, 401);
        post("/api/asistente/preguntar", "{\"pregunta\":\"hola\"}", "token.falso", 401);
    }

    @Test
    void coopiSaludaYLlevaAProgramasYLugares() throws Exception {
        String token = post("/api/auth/invitado", "{}", null, 200).get("token").asText();

        JsonNode hola = post("/api/asistente/preguntar", "{\"pregunta\":\"Hola\"}", token, 200);
        assertThat(hola.get("respuesta").asText()).contains("¿Cómo estás? ¿En qué te puedo ayudar hoy?");

        JsonNode sw = post("/api/asistente/preguntar",
                "{\"pregunta\":\"Ey Coopi, llévame al programa de ingeniería de software\"}", token, 200);
        assertThat(sw.at("/accion/tipo").asText()).isEqualTo("IR_PROGRAMA");
        assertThat(sw.at("/accion/etiqueta").asText()).isEqualTo("Ingeniería de Software");

        JsonNode resto = post("/api/asistente/preguntar", "{\"pregunta\":\"quiero ver el restaurante\"}", token, 200);
        assertThat(resto.at("/accion/tipo").asText()).isEqualTo("IR_LUGAR");
        assertThat(resto.at("/accion/etiqueta").asText()).isEqualTo("Cafetería");

        JsonNode todos = post("/api/asistente/preguntar", "{\"pregunta\":\"¿qué programas hay?\"}", token, 200);
        assertThat(todos.at("/accion/tipo").asText()).isEqualTo("VER_PROGRAMAS");

        JsonNode med = post("/api/asistente/preguntar", "{\"pregunta\":\"llévame a medicina\"}", token, 200);
        assertThat(med.at("/accion/etiqueta").asText()).isEqualTo("Medicina");
    }
}
