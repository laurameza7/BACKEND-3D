package co.edu.ucc.pasto3d.ia;

import co.edu.ucc.pasto3d.dto.Dtos.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** IA de respaldo con un campus pequeño armado a mano (sin base de datos). */
class LocalProveedorIATests {

    private final LocalProveedorIA ia = new LocalProveedorIA();

    private final ContextoCampus campus = new ContextoCampus(
            List.of(new EdificioDTO(1, "A", "Bloque A", "Edificio principal con biblioteca y cafetería.",
                    0, 0, 30, 20, 6, 2, "#fff", "EDIFICIO")),
            List.of(new LugarDTO(10, "Biblioteca", "SERVICIO", 1, "Bloque A", 2,
                            "Colecciones y salas de estudio", "Lunes a viernes 7:00 a 21:00", "ext. 2240", null),
                    new LugarDTO(11, "Tesorería", "OFICINA", 1, "Bloque A", -1,
                            "Pagos y recibos.", null, null, null)),
            List.of(new ProgramaDTO(100, "Ingeniería de Software", "Facultad de Ingeniería", "PREGRADO", "PRESENCIAL",
                    9, "Ingeniero(a) de Software", "Desarrollo de software.", 1, "Bloque A", null, null, null)),
            List.of(new PreguntaFrecuenteDTO(1000, "PAGOS", "¿Cómo pago la matrícula?",
                    "Puedes pagar en línea o en el cajero del campus.", "pagar,matricula,recibo")));

    @Test
    void siempreEstaDisponible() {
        assertThat(ia.disponible()).isTrue();
        assertThat(ia.nombre()).isEqualTo("local");
    }

    @Test
    void saludaCuandoElMensajeEsCorto() {
        assertThat(ia.responder("hola", campus)).startsWith("¡Hola! Soy Coopi");
    }

    @Test
    void listaLosProgramas() {
        assertThat(ia.responder("¿qué carreras ofrecen?", campus)).contains("• Ingeniería de Software (Facultad de Ingeniería)");
    }

    @Test
    void describeUnProgramaPorSuNombreCorto() {
        assertThat(ia.responder("cuéntame de software", campus))
                .startsWith("Ingeniería de Software:")
                .contains("9 semestres")
                .contains("Ingeniero(a) de Software");
    }

    @Test
    void ubicaUnLugarConPisoHorarioYTelefono() {
        String r = ia.responder("¿dónde queda la biblioteca?", campus);
        assertThat(r).isEqualTo("Biblioteca queda en el Bloque A, piso 2. Colecciones y salas de estudio. "
                + "Horario: Lunes a viernes 7:00 a 21:00. Teléfono: ext. 2240.");
    }

    @Test
    void losSotanosSeNombranComoSotano() {
        assertThat(ia.responder("tesorería", campus)).contains("sótano 1").doesNotContain("..");
    }

    @Test
    void usaLaPreguntaFrecuenteSiLasPalabrasClaveCoinciden() {
        assertThat(ia.responder("necesito pagar el recibo", campus)).isEqualTo("Puedes pagar en línea o en el cajero del campus.");
    }

    @Test
    void reconoceUnEdificio() {
        assertThat(ia.responder("bloque a", campus)).startsWith("Bloque A:");
    }

    @Test
    void siNoSabeLoDiceYDaElConmutador() {
        assertThat(ia.responder("¿quién ganó el partido de ayer?", campus))
                .startsWith("No encontré esa información").contains("602 7370660");
    }
}
