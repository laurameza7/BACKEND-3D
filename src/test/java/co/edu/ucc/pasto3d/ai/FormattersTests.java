package co.edu.ucc.pasto3d.ai;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FormattersTests {

    private final AnswerFormatter markdown = new MarkdownFormatter();
    private final AnswerFormatter plain = new PlainTextFormatter();

    @Test
    void removesBoldHeadingsAndItalics() {
        String r = markdown.format("## Biblioteca\n**Horario:** de *lunes* a viernes en el `Bloque A`.");
        assertThat(r).isEqualTo("Biblioteca\nHorario: de lunes a viernes en el Bloque A.");
    }

    @Test
    void convertsBulletsToDots() {
        assertThat(markdown.format("Programas:\n* Medicina\n- Derecho\n  * Enfermería"))
                .isEqualTo("Programas:\n• Medicina\n• Derecho\n• Enfermería");
    }

    @Test
    void leavesMultiplicationsAndPlainTextAlone() {
        assertThat(markdown.format("2*3 = 6")).isEqualTo("2*3 = 6");
        assertThat(markdown.format("Hola, ¿cómo estás?")).isEqualTo("Hola, ¿cómo estás?");
    }

    @Test
    void collapsesBlankLinesAndSpaces() {
        assertThat(markdown.format("\n\nuno\n\n\n\ndos\n")).isEqualTo("uno\n\ndos");
        assertThat(plain.format("  uno   \ndos  ")).isEqualTo("uno\ndos");
    }

    @Test
    void nullAnswersBecomeEmpty() {
        assertThat(markdown.format(null)).isEmpty();
        assertThat(plain.format(null)).isEmpty();
    }
}
