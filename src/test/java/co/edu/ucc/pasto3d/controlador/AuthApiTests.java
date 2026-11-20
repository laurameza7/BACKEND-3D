package co.edu.ucc.pasto3d.controlador;

import co.edu.ucc.pasto3d.modelo.Usuario;
import co.edu.ucc.pasto3d.repositorio.UsuarioRepositorio;
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

/** Registro, ingreso, invitado, /yo e historial, incluida la regla de que la contraseña nunca se guarda. */
@SpringBootTest(properties = "ia.proveedor=local")
@ActiveProfiles("local")
@AutoConfigureMockMvc
class AuthApiTests {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired UsuarioRepositorio usuarios;

    static String huella(String clave) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(clave.getBytes(StandardCharsets.UTF_8)));
    }

    private static String correoNuevo() { return "prueba-" + UUID.randomUUID().toString().substring(0, 8) + "@ucc.edu.co"; }

    private JsonNode registrar(String nombre, String correo, String clave) throws Exception {
        String r = mvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"" + nombre + "\",\"correo\":\"" + correo + "\",\"huella\":\"" + huella(clave) + "\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        return json.readTree(r);
    }

    @Test
    void laContrasenaNoSeGuardaNiSuHuella() throws Exception {
        String correo = correoNuevo();
        String clave = "Coopi2026!";
        registrar("Manuel Villarreal", correo, clave);

        Usuario u = usuarios.findByCorreoIgnoreCase(correo).orElseThrow();
        assertThat(u.getHashClave())
                .doesNotContain(clave)
                .isNotEqualTo(huella(clave))
                .startsWith("$2");                         // BCrypt
        assertThat(u.getHashClave()).hasSize(60);
    }

    @Test
    void elCorreoSeGuardaEnMinusculasYElNombreSinEspacios() throws Exception {
        String correo = correoNuevo();
        JsonNode s = registrar("  Juan Moreno  ", correo.toUpperCase(), "clave-segura");
        assertThat(s.get("correo").asText()).isEqualTo(correo);
        assertThat(s.get("nombre").asText()).isEqualTo("Juan Moreno");
        assertThat(s.get("rol").asText()).isEqualTo("ESTUDIANTE");

        // Se puede ingresar escribiendo el correo con mayúsculas
        mvc.perform(post("/api/auth/ingresar").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\" " + correo.toUpperCase() + " \",\"huella\":\"" + huella("clave-segura") + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", not(emptyString())));
    }

    @Test
    void validacionesDelRegistro() throws Exception {
        String h = huella("x");
        mvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Ana\",\"correo\":\"no-es-correo\",\"huella\":\"" + h + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("El correo no es válido.")));
        mvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"   \",\"correo\":\"ana@ucc.edu.co\",\"huella\":\"" + h + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("El campo «nombre» es obligatorio.")));
        mvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"" + "a".repeat(81) + "\",\"correo\":\"ana@ucc.edu.co\",\"huella\":\"" + h + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("El campo «nombre» es demasiado largo.")));
        mvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Ana\",\"correo\":\"ana@ucc.edu.co\",\"huella\":\"MiClaveEnTextoPlano\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("cifrada")));
        mvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON).content("{esto no es json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("El cuerpo de la solicitud no es un JSON válido.")));
    }

    @Test
    void ingresoConCorreoInexistenteDaElMismoMensajeQueClaveIncorrecta() throws Exception {
        mvc.perform(post("/api/auth/ingresar").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"nadie@ucc.edu.co\",\"huella\":\"" + huella("x") + "\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", is("Correo o contraseña incorrectos.")));
    }

    @Test
    void yoDiceQuienEsElDuenoDelToken() throws Exception {
        String token = registrar("Laura Meza", correoNuevo(), "clave1234").get("token").asText();
        mvc.perform(get("/api/auth/yo").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Laura Meza")))
                .andExpect(jsonPath("$.invitado", is(false)));

        String invitado = json.readTree(mvc.perform(post("/api/auth/invitado")).andReturn().getResponse().getContentAsString())
                .get("token").asText();
        mvc.perform(get("/api/auth/yo").header("Authorization", "Bearer " + invitado))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rol", is("INVITADO")))
                .andExpect(jsonPath("$.invitado", is(true)));

        mvc.perform(get("/api/auth/yo")).andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", containsString("iniciar sesión")));
        mvc.perform(get("/api/auth/yo").header("Authorization", "Basic abc")).andExpect(status().isUnauthorized());
    }

    @Test
    void elHistorialEsSoloDelUsuarioYLosInvitadosNoTienen() throws Exception {
        String laura = registrar("Laura", correoNuevo(), "clave-laura").get("token").asText();
        String juan = registrar("Juan", correoNuevo(), "clave-juan").get("token").asText();

        for (String p : new String[]{"hola", "llévame a la biblioteca"})
            mvc.perform(post("/api/asistente/preguntar").header("Authorization", "Bearer " + laura)
                    .contentType(MediaType.APPLICATION_JSON).content("{\"pregunta\":\"" + p + "\"}")).andExpect(status().isOk());

        mvc.perform(get("/api/asistente/historial").header("Authorization", "Bearer " + laura))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].pregunta", is("hola")))                 // en orden cronológico
                .andExpect(jsonPath("$[1].pregunta", is("llévame a la biblioteca")))
                .andExpect(jsonPath("$[1].fecha", matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}")));

        mvc.perform(get("/api/asistente/historial").header("Authorization", "Bearer " + juan))
                .andExpect(jsonPath("$.length()", is(0)));

        String invitado = json.readTree(mvc.perform(post("/api/auth/invitado")).andReturn().getResponse().getContentAsString())
                .get("token").asText();
        mvc.perform(get("/api/asistente/historial").header("Authorization", "Bearer " + invitado))
                .andExpect(jsonPath("$.length()", is(0)));
    }

    @Test
    void lasPreguntasVaciasOLargasSeRechazan() throws Exception {
        String t = json.readTree(mvc.perform(post("/api/auth/invitado")).andReturn().getResponse().getContentAsString())
                .get("token").asText();
        mvc.perform(post("/api/asistente/preguntar").header("Authorization", "Bearer " + t)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"pregunta\":\"   \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("El campo «pregunta» es obligatorio.")));
        mvc.perform(post("/api/asistente/preguntar").header("Authorization", "Bearer " + t)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"pregunta\":\"" + "x".repeat(501) + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("El campo «pregunta» es demasiado largo.")));
    }

    @Test
    void preguntarSobreElProgramaQueSeEstaViendo() throws Exception {
        String t = json.readTree(mvc.perform(post("/api/auth/invitado")).andReturn().getResponse().getContentAsString())
                .get("token").asText();
        mvc.perform(post("/api/asistente/preguntar").header("Authorization", "Bearer " + t)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"pregunta\":\"¿cuánto dura?\",\"programaId\":99999}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.respuesta", not(emptyString())));
    }
}
