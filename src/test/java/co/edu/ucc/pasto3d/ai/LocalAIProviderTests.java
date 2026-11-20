package co.edu.ucc.pasto3d.ai;

import co.edu.ucc.pasto3d.dto.Dtos.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** Fallback AI with a small hand-made campus (no database). */
class LocalAIProviderTests {

    private final LocalAIProvider ai = new LocalAIProvider();

    private final CampusContext campus = new CampusContext(
            List.of(new BuildingDTO(1, "A", "Bloque A", "Edificio principal con biblioteca y cafetería.",
                    0, 0, 30, 20, 6, 2, "#fff", "BUILDING")),
            List.of(new PlaceDTO(10, "Biblioteca", "SERVICE", 1, "Bloque A", 2,
                            "Colecciones y salas de estudio", "Lunes a viernes 7:00 a 21:00", "ext. 2240", null),
                    new PlaceDTO(11, "Tesorería", "OFFICE", 1, "Bloque A", -1,
                            "Pagos y recibos.", null, null, null)),
            List.of(new ProgramDTO(100, "Ingeniería de Software", "Facultad de Ingeniería", "UNDERGRADUATE", "IN_PERSON",
                    9, "Ingeniero(a) de Software", "Desarrollo de software.", 1, "Bloque A", null, null, null)),
            List.of(new FaqDTO(1000, "PAYMENTS", "¿Cómo pago la matrícula?",
                    "Puedes pagar en línea o en el cajero del campus.", "pagar,matricula,recibo")));

    @Test
    void isAlwaysAvailable() {
        assertThat(ai.available()).isTrue();
        assertThat(ai.name()).isEqualTo("local");
    }

    @Test
    void greetsWhenTheMessageIsShort() {
        assertThat(ai.answer("hola", campus)).startsWith("¡Hola! Soy Coopi");
    }

    @Test
    void listsThePrograms() {
        assertThat(ai.answer("¿qué carreras ofrecen?", campus)).contains("• Ingeniería de Software (Facultad de Ingeniería)");
    }

    @Test
    void describesAProgramByItsShortName() {
        assertThat(ai.answer("cuéntame de software", campus))
                .startsWith("Ingeniería de Software:")
                .contains("9 semestres")
                .contains("Ingeniero(a) de Software");
    }

    @Test
    void locatesAPlaceWithFloorScheduleAndPhone() {
        String r = ai.answer("¿dónde queda la biblioteca?", campus);
        assertThat(r).isEqualTo("Biblioteca queda en el Bloque A, piso 2. Colecciones y salas de estudio. "
                + "Horario: Lunes a viernes 7:00 a 21:00. Teléfono: ext. 2240.");
    }

    @Test
    void basementsAreNamedAsBasements() {
        assertThat(ai.answer("tesorería", campus)).contains("sótano 1").doesNotContain("..");
    }

    @Test
    void usesTheFaqWhenKeywordsMatch() {
        assertThat(ai.answer("necesito pagar el recibo", campus)).isEqualTo("Puedes pagar en línea o en el cajero del campus.");
    }

    @Test
    void recognizesABuilding() {
        assertThat(ai.answer("bloque a", campus)).startsWith("Bloque A:");
    }

    @Test
    void whenItDoesNotKnowItSaysSoAndGivesThePhone() {
        assertThat(ai.answer("¿quién ganó el partido de ayer?", campus))
                .startsWith("No encontré esa información").contains("602 7370660");
    }
}
