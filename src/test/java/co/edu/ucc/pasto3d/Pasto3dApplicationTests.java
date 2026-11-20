package co.edu.ucc.pasto3d;

import co.edu.ucc.pasto3d.dto.Dtos.AnswerDTO;
import co.edu.ucc.pasto3d.service.AssistantFacade;
import co.edu.ucc.pasto3d.service.CampusService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "ai.provider=local")
@ActiveProfiles("local")
class Pasto3dApplicationTests {

    @Autowired CampusService campus;
    @Autowired AssistantFacade assistant;

    @Test
    void loadsTheCampusData() {
        assertThat(campus.buildings()).isNotEmpty();
        assertThat(campus.programs()).extracting("name").contains("Ingeniería de Software");
        // Every undergraduate program with a registered faculty knows where it is
        assertThat(campus.programs()).filteredOn(p -> p.name().equals("Ingeniería de Software"))
                .first().extracting("place").isEqualTo("Facultad de Ingeniería");
    }

    @Test
    void localAssistantAnswersAboutAProgram() {
        AnswerDTO r = assistant.ask("¿Qué es medicina?");
        assertThat(r.answer()).contains("Medicina").doesNotContain("null");
    }

    @Test
    void localAssistantPlacesTheLibraryInBlockA() {
        AnswerDTO r = assistant.ask("¿Dónde queda la biblioteca?");
        assertThat(r.answer()).contains("Bloque A");
        assertThat(r.buildingId()).isNotNull();
    }

    @Test
    void campusHasTwoBlocksWithBasements() {
        assertThat(campus.buildings()).filteredOn(e -> e.type().equals("BUILDING")).hasSize(2)
                .allMatch(e -> e.basements() == 2);
    }

    @Test
    void localAssistantAnswersAboutPayments() {
        AnswerDTO r = assistant.ask("dónde pago la matrícula");
        assertThat(r.answer()).containsIgnoringCase("tesorería");
    }
}
