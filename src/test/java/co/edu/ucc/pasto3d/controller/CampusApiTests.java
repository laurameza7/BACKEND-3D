package co.edu.ucc.pasto3d.controller;

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

/** Public routes with campus information (no session required). */
@SpringBootTest(properties = "ai.provider=local")
@ActiveProfiles("local")
@AutoConfigureMockMvc
class CampusApiTests {

    @Autowired MockMvc mvc;

    @Test
    void listsBuildingsWithTheirSize() throws Exception {
        mvc.perform(get("/api/buildings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(5)))
                .andExpect(jsonPath("$[?(@.code == 'A')].floors", contains(greaterThan(0))))
                .andExpect(jsonPath("$[?(@.code == 'B')].name", contains("Bloque B")))
                .andExpect(jsonPath("$[0].width", greaterThan(0.0)));
    }

    @Test
    void listsAllPlacesWithTheirBuilding() throws Exception {
        mvc.perform(get("/api/places"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(50)))
                .andExpect(jsonPath("$[*].building", everyItem(notNullValue())))
                .andExpect(jsonPath("$[*].name", hasItems("Biblioteca", "Gimnasio", "Auditorio Urcunina", "Tesorería")));
    }

    @Test
    void placesOfABuildingSortedByFloor() throws Exception {
        String id = com.jayway.jsonpath.JsonPath.read(
                mvc.perform(get("/api/buildings")).andReturn().getResponse().getContentAsString(),
                "$[?(@.code == 'B')].id").toString().replaceAll("[\\[\\]]", "");
        mvc.perform(get("/api/buildings/" + id + "/places"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThan(10)))
                .andExpect(jsonPath("$[0].floor", lessThan(0)))          // basements come first
                .andExpect(jsonPath("$[*].buildingId", everyItem(is(Integer.valueOf(id)))));
    }

    @Test
    void unknownBuildingHasNoPlaces() throws Exception {
        mvc.perform(get("/api/buildings/99999/places"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(0)));
    }

    @Test
    void programsIncludeTheirFacultyAndLocation() throws Exception {
        mvc.perform(get("/api/programs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(15)))
                .andExpect(jsonPath("$[?(@.name == 'Ingeniería de Software')].place", contains("Facultad de Ingeniería")))
                .andExpect(jsonPath("$[?(@.name == 'Ingeniería de Software')].location", contains("Bloque B, piso 4")))
                .andExpect(jsonPath("$[?(@.level == 'UNDERGRADUATE')]", hasSize(greaterThanOrEqualTo(6))));
    }

    @Test
    void programByIdAndUnknownProgram() throws Exception {
        String id = com.jayway.jsonpath.JsonPath.read(
                mvc.perform(get("/api/programs")).andReturn().getResponse().getContentAsString(), "$[0].id").toString();
        mvc.perform(get("/api/programs/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(Integer.valueOf(id))));
        mvc.perform(get("/api/programs/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("No existe el programa 99999")));
        mvc.perform(get("/api/programs/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("abc")));
    }

    @Test
    void faqsHaveACategory() throws Exception {
        mvc.perform(get("/api/faqs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThan(0)))
                .andExpect(jsonPath("$[*].category", everyItem(notNullValue())))
                .andExpect(jsonPath("$[*].answer", everyItem(not(emptyString()))));
    }

    @Test
    void healthIncludesCoopiMetrics() throws Exception {
        mvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("ok")))
                .andExpect(jsonPath("$.coopiMetrics").exists())
                .andExpect(jsonPath("$.date").isString());
    }

    @Test
    void unknownPathReturns404AsJson() throws Exception {
        mvc.perform(get("/api/does-not-exist"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").isString());
    }

    @Test
    void corsAllowsTheVercelFrontend() throws Exception {
        mvc.perform(options("/api/assistant/ask")
                        .header("Origin", "https://ucc-pasto3d.vercel.app")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "authorization,content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "https://ucc-pasto3d.vercel.app"));
    }

    @Test
    void errorsAlsoCarryTheCorsHeaders() throws Exception {
        mvc.perform(get("/api/auth/login").header("Origin", "https://ucc-pasto3d.vercel.app"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(header().string("Access-Control-Allow-Origin", "https://ucc-pasto3d.vercel.app"))
                .andExpect(jsonPath("$.error", is("Esta ruta no acepta GET.")));
        mvc.perform(get("/api/programs/99999").header("Origin", "https://ucc-pasto3d.vercel.app"))
                .andExpect(status().isNotFound())
                .andExpect(header().string("Access-Control-Allow-Origin", "https://ucc-pasto3d.vercel.app"));
    }
}
