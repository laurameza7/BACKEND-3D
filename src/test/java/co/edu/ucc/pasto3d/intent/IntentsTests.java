package co.edu.ucc.pasto3d.intent;

import co.edu.ucc.pasto3d.dto.Dtos.AnswerDTO;
import co.edu.ucc.pasto3d.security.Session;
import co.edu.ucc.pasto3d.service.AssistantFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/** The chain of intents with the real campus data (H2 with the migrations). */
@SpringBootTest(properties = "ai.provider=local")
@ActiveProfiles("local")
class IntentsTests {

    private static final Session GUEST = new Session(null, "Invitado", "GUEST");

    @Autowired AssistantFacade coopi;

    private AnswerDTO ask(String text) {
        return coopi.ask(text, null, GUEST);
    }

    @ParameterizedTest(name = "«{0}» lleva a {1}")
    @CsvSource(delimiter = ';', value = {
            "llévame a la biblioteca; Biblioteca",
            "llévame a la facultad de ingeniería; Facultad de Ingeniería",
            "llevame a la facultad de medicina; Facultad de Medicina",
            "¿dónde queda tesorería?; Tesorería",
            "quiero ir a pagar la matrícula; Admisiones, Registro y Control Académico (DARC)",
            "llévame al café; Café Bonne Vie",
            "llévame a la cafetería; Cafetería",
            "¿dónde está el cajero?; Cajero BBVA",
            "llévame a las salas de informática; Salas de Informática",
            "quiero ver la cancha de voleibol; Cancha de voleibol",
            "¿cómo llego a la clínica de posgrado?; Clínica Odontológica de Posgrado",
            "llévame al laboratorio de morfología; Laboratorio de Morfología",
            "ey coopi, llévame a los lockers; Lockers de estudiantes",
            "muéstrame la papelería; Papelería",
            "dónde queda el AMI; AMI (Atención Más Integral)",
            "llévame a la sala de profesores de ingeniería; Sala de profesores de Ingeniería",
            "quiero ir al laboratorio de realidad mixta; Laboratorio de Realidad Mixta"
    })
    void takesYouToTheRequestedPlace(String question, String place) {
        AnswerDTO r = ask(question);
        assertThat(r.action()).as(question).isNotNull();
        assertThat(r.action().type()).isEqualTo("GO_TO_PLACE");
        assertThat(r.action().label()).isEqualTo(place);
        assertThat(r.action().placeId()).isNotNull();
        assertThat(r.answer()).contains(place);
    }

    @ParameterizedTest(name = "«{0}» abre {1}")
    @CsvSource(delimiter = ';', value = {
            "llévame al programa de derecho; Derecho",
            "quiero ver la carrera de enfermería; Enfermería",
            "llévame a ingeniería industrial; Ingeniería Industrial",
            "muéstrame odontología; Odontología",
            "llévame a la especialización en pediatría; Especialización en Pediatría",
            "quiero ver la especialización en derecho médico; Especialización en Derecho Médico"
    })
    void opensTheRequestedProgram(String question, String program) {
        AnswerDTO r = ask(question);
        assertThat(r.action()).as(question).isNotNull();
        assertThat(r.action().type()).isEqualTo("GO_TO_PROGRAM");
        assertThat(r.action().label()).isEqualTo(program);
        assertThat(r.answer()).startsWith("¡Listo! Te llevo al programa de " + program)
                .doesNotContain("otorgas").doesNotContain("..");
    }

    @Test
    void takesYouToAWholeBlock() {
        AnswerDTO r = ask("llévame al bloque A");
        assertThat(r.action().type()).isEqualTo("GO_TO_BUILDING");
        assertThat(r.buildingId()).isEqualTo(r.action().buildingId());
    }

    @ParameterizedTest
    @ValueSource(strings = {"hola", "Hola Coopi", "buenas tardes", "ey coopi", "necesito ayuda", "hola, buenos días"})
    void recognizesGreetings(String greeting) {
        AnswerDTO r = ask(greeting);
        assertThat(r.action().type()).isEqualTo("GREETING");
        assertThat(r.answer()).startsWith("¡Hola!");
    }

    @Test
    void greetingUsesTheUserName() {
        assertThat(coopi.greeting(new Session(5, "  Juan   Moreno ", "STUDENT"))).startsWith("¡Hola, Juan!");
        assertThat(coopi.greeting(new Session(5, "", "STUDENT"))).startsWith("¡Hola!");
        assertThat(coopi.greeting(GUEST)).startsWith("¡Hola!");
    }

    @ParameterizedTest
    @ValueSource(strings = {"¿qué programas hay?", "cuáles carreras tienen", "quiero ver todos los programas", "oferta académica"})
    void opensTheProgramList(String question) {
        assertThat(ask(question).action().type()).isEqualTo("SHOW_PROGRAMS");
    }

    @Test
    void specificQuestionAnswersInsteadOfNavigating() {
        AnswerDTO r = ask("¿cuánto dura el programa de medicina?");
        assertThat(r.action()).isNull();
        assertThat(r.provider()).isEqualTo("local");
        assertThat(r.answer()).isNotBlank();
    }

    @Test
    void shortWordsDoNotMatchInsideOtherWords() {
        // "amigos" contains "ami" and "cafeteria" contains "cafe", but they are not those places
        Query c = Query.of("vamos con mis amigos", null, GUEST, null);
        assertThat(c.contains("ami")).isFalse();
        assertThat(Query.of("la cafeteria", null, null, null).contains("cafe")).isFalse();
        assertThat(Query.of("los lockers", null, null, null).contains("locker")).isTrue();
        assertThat(Query.of("las canchas", null, null, null).contains("cancha")).isTrue();
        assertThat(Query.of("dos cafes", null, null, null).contains("cafe")).isTrue();
    }

    @Test
    void wakeWordIsRemovedFromTheMessage() {
        assertThat(Query.of("Ey Coopi, ¿dónde queda la biblioteca?", null, null, null).normalized())
                .isEqualTo("donde queda la biblioteca");
        assertThat(Query.of("Oye Coopi llévame", null, null, null).normalized()).isEqualTo("llevame");
    }

    @Test
    void anUnknownPlaceIsNotConfusedWithAPlaceThatContainsItsLetters() {
        // "marte" contains "arte", but it is not the Salón de Arte, Música y Cultura
        AnswerDTO r = ask("llévame a marte");
        assertThat(r.answer()).doesNotContain("Salón de Arte");
    }

    @ParameterizedTest
    @ValueSource(strings = {"¿a qué hora abre la biblioteca?", "¿cuál es el horario de tesorería?", "¿hasta qué hora abre la cafetería?"})
    void aQuestionAboutTheScheduleIsAnsweredInsteadOfNavigating(String question) {
        AnswerDTO r = ask(question);
        assertThat(r.action()).isNull();
        assertThat(r.answer()).isNotBlank();
    }
}
