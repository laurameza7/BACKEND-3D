package co.edu.ucc.pasto3d.ai;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TextUtilTests {

    @Test
    void normalizeRemovesAccentsSignsAndCapitals() {
        assertThat(TextUtil.normalize("¿Dónde queda la CAFETERÍA?")).isEqualTo("donde queda la cafeteria");
        assertThat(TextUtil.normalize("Ey  Coopi,   llévame…")).isEqualTo("ey coopi llevame");
        assertThat(TextUtil.normalize("Odontología – Bloque B")).isEqualTo("odontologia bloque b");
    }

    @Test
    void normalizeKeepsNumbersAndTreatsEnyeAsN() {
        // "año" and "ano" compare equal: it does not matter if the keyboard has no ñ
        assertThat(TextUtil.normalize("Año 2026, salón 409")).isEqualTo("ano 2026 salon 409");
    }

    @Test
    void normalizeToleratesNullAndBlank() {
        assertThat(TextUtil.normalize(null)).isEmpty();
        assertThat(TextUtil.normalize("   ")).isEmpty();
    }

    @Test
    void floorNameText() {
        assertThat(TextUtil.floorName(3)).isEqualTo("piso 3");
        assertThat(TextUtil.floorName(-2)).isEqualTo("sótano 2");
        assertThat(TextUtil.floorName(null)).isEqualTo("piso sin registrar");
    }
}
