package co.edu.ucc.pasto3d.ia;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FormateadoresTests {

    private final FormateadorRespuesta markdown = new FormateadorMarkdown();
    private final FormateadorRespuesta plano = new FormateadorTextoPlano();

    @Test
    void quitaNegritasTitulosYCursivas() {
        String r = markdown.formatear("## Biblioteca\n**Horario:** de *lunes* a viernes en el `Bloque A`.");
        assertThat(r).isEqualTo("Biblioteca\nHorario: de lunes a viernes en el Bloque A.");
    }

    @Test
    void convierteViñetasEnPuntos() {
        assertThat(markdown.formatear("Programas:\n* Medicina\n- Derecho\n  * Enfermería"))
                .isEqualTo("Programas:\n• Medicina\n• Derecho\n• Enfermería");
    }

    @Test
    void noTocaMultiplicacionesNiTextoNormal() {
        assertThat(markdown.formatear("2*3 = 6")).isEqualTo("2*3 = 6");
        assertThat(markdown.formatear("Hola, ¿cómo estás?")).isEqualTo("Hola, ¿cómo estás?");
    }

    @Test
    void reduceLineasEnBlancoYEspacios() {
        assertThat(markdown.formatear("\n\nuno\n\n\n\ndos\n")).isEqualTo("uno\n\ndos");
        assertThat(plano.formatear("  uno   \ndos  ")).isEqualTo("uno\ndos");
    }

    @Test
    void respuestasNulasQuedanVacias() {
        assertThat(markdown.formatear(null)).isEmpty();
        assertThat(plano.formatear(null)).isEmpty();
    }
}
