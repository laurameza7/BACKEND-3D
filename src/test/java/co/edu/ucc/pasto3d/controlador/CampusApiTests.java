package co.edu.ucc.pasto3d.controlador;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Rutas públicas de información del campus (no exigen sesión). */
@SpringBootTest(properties = "ia.proveedor=local")
@ActiveProfiles("local")
@AutoConfigureMockMvc
class CampusApiTests {

    @Autowired MockMvc mvc;

    @Test
    void listaLosEdificiosConSusMedidas() throws Exception {
        mvc.perform(get("/api/edificios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(5)))
                .andExpect(jsonPath("$[?(@.codigo == 'A')].pisos", contains(greaterThan(0))))
                .andExpect(jsonPath("$[?(@.codigo == 'B')].nombre", contains("Bloque B")))
                .andExpect(jsonPath("$[0].ancho", greaterThan(0.0)));
    }

    @Test
    void listaTodosLosLugaresConSuEdificio() throws Exception {
        mvc.perform(get("/api/lugares"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(50)))
                .andExpect(jsonPath("$[*].edificio", everyItem(notNullValue())))
                .andExpect(jsonPath("$[*].nombre", hasItems("Biblioteca", "Gimnasio", "Auditorio Urcunina", "Tesorería")));
    }

    @Test
    void lugaresDeUnEdificioOrdenadosPorPiso() throws Exception {
        String id = com.jayway.jsonpath.JsonPath.read(
                mvc.perform(get("/api/edificios")).andReturn().getResponse().getContentAsString(),
                "$[?(@.codigo == 'B')].id").toString().replaceAll("[\\[\\]]", "");
        mvc.perform(get("/api/edificios/" + id + "/lugares"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThan(10)))
                .andExpect(jsonPath("$[0].piso", lessThan(0)))          // empieza por los sótanos
                .andExpect(jsonPath("$[*].edificioId", everyItem(is(Integer.valueOf(id)))));
    }

    @Test
    void unEdificioQueNoExisteNoTieneLugares() throws Exception {
        mvc.perform(get("/api/edificios/99999/lugares"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(0)));
    }

    @Test
    void losProgramasTraenSuFacultadYUbicacion() throws Exception {
        mvc.perform(get("/api/programas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(15)))
                .andExpect(jsonPath("$[?(@.nombre == 'Ingeniería de Software')].lugar", contains("Facultad de Ingeniería")))
                .andExpect(jsonPath("$[?(@.nombre == 'Ingeniería de Software')].ubicacion", contains("Bloque B, piso 4")))
                .andExpect(jsonPath("$[?(@.nivel == 'PREGRADO')]", hasSize(greaterThanOrEqualTo(6))));
    }

    @Test
    void unProgramaPorIdYUnoQueNoExiste() throws Exception {
        String id = com.jayway.jsonpath.JsonPath.read(
                mvc.perform(get("/api/programas")).andReturn().getResponse().getContentAsString(), "$[0].id").toString();
        mvc.perform(get("/api/programas/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(Integer.valueOf(id))));
        mvc.perform(get("/api/programas/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("No existe el programa 99999")));
        mvc.perform(get("/api/programas/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("abc")));
    }

    @Test
    void preguntasFrecuentesConCategoria() throws Exception {
        mvc.perform(get("/api/preguntas-frecuentes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThan(0)))
                .andExpect(jsonPath("$[*].categoria", everyItem(notNullValue())))
                .andExpect(jsonPath("$[*].respuesta", everyItem(not(emptyString()))));
    }

    @Test
    void saludIncluyeLasMetricasDeCoopi() throws Exception {
        mvc.perform(get("/api/salud"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("ok")))
                .andExpect(jsonPath("$.metricasCoopi").exists())
                .andExpect(jsonPath("$.fecha").isString());
    }

    @Test
    void rutaInexistenteResponde404EnJson() throws Exception {
        mvc.perform(get("/api/no-existe"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").isString());
    }

    @Test
    void corsPermiteAlFrontendDeVercel() throws Exception {
        mvc.perform(options("/api/asistente/preguntar")
                        .header("Origin", "https://ucc-pasto3d.vercel.app")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "authorization,content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "https://ucc-pasto3d.vercel.app"));
    }
}
