package co.edu.ucc.pasto3d.ia;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TextoUtilTests {

    @Test
    void normalizarQuitaTildesSignosYMayusculas() {
        assertThat(TextoUtil.normalizar("¿Dónde queda la CAFETERÍA?")).isEqualTo("donde queda la cafeteria");
        assertThat(TextoUtil.normalizar("Ey  Coopi,   llévame…")).isEqualTo("ey coopi llevame");
        assertThat(TextoUtil.normalizar("Odontología – Bloque B")).isEqualTo("odontologia bloque b");
    }

    @Test
    void normalizarConservaLosNumerosYTrataLaEñeComoN() {
        // "año" y "ano" se comparan igual: así da lo mismo si el teclado no tiene ñ
        assertThat(TextoUtil.normalizar("Año 2026, salón 409")).isEqualTo("ano 2026 salon 409");
    }

    @Test
    void normalizarToleraNuloYVacio() {
        assertThat(TextoUtil.normalizar(null)).isEmpty();
        assertThat(TextoUtil.normalizar("   ")).isEmpty();
    }

    @Test
    void nombreDelPiso() {
        assertThat(TextoUtil.nombrePiso(3)).isEqualTo("piso 3");
        assertThat(TextoUtil.nombrePiso(-2)).isEqualTo("sótano 2");
        assertThat(TextoUtil.nombrePiso(null)).isEqualTo("piso sin registrar");
    }
}
