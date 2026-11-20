package co.edu.ucc.pasto3d.intencion;

import co.edu.ucc.pasto3d.dto.Dtos.RespuestaDTO;
import co.edu.ucc.pasto3d.seguridad.Sesion;
import co.edu.ucc.pasto3d.servicio.AsistenteFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/** La cadena de intenciones con los datos reales del campus (H2 con las migraciones). */
@SpringBootTest(properties = "ia.proveedor=local")
@ActiveProfiles("local")
class IntencionesTests {

    private static final Sesion INVITADO = new Sesion(null, "Invitado", "INVITADO");

    @Autowired AsistenteFacade coopi;

    private RespuestaDTO preguntar(String texto) {
        return coopi.preguntar(texto, null, INVITADO);
    }

    @ParameterizedTest(name = "«{0}» lleva a {1}")
    @CsvSource(delimiter = ';', value = {
            "llévame a la biblioteca; Biblioteca",
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
    void llevaAlLugarPedido(String pregunta, String lugar) {
        RespuestaDTO r = preguntar(pregunta);
        assertThat(r.accion()).as(pregunta).isNotNull();
        assertThat(r.accion().tipo()).isEqualTo("IR_LUGAR");
        assertThat(r.accion().etiqueta()).isEqualTo(lugar);
        assertThat(r.accion().lugarId()).isNotNull();
        assertThat(r.respuesta()).contains(lugar);
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
    void abreElProgramaPedido(String pregunta, String programa) {
        RespuestaDTO r = preguntar(pregunta);
        assertThat(r.accion()).as(pregunta).isNotNull();
        assertThat(r.accion().tipo()).isEqualTo("IR_PROGRAMA");
        assertThat(r.accion().etiqueta()).isEqualTo(programa);
        assertThat(r.respuesta()).startsWith("¡Listo! Te llevo al programa de " + programa)
                .doesNotContain("otorgas").doesNotContain("..");
    }

    @Test
    void llevaAUnBloqueCompleto() {
        RespuestaDTO r = preguntar("llévame al bloque A");
        assertThat(r.accion().tipo()).isEqualTo("IR_EDIFICIO");
        assertThat(r.edificioId()).isEqualTo(r.accion().edificioId());
    }

    @ParameterizedTest
    @ValueSource(strings = {"hola", "Hola Coopi", "buenas tardes", "ey coopi", "necesito ayuda", "hola, buenos días"})
    void reconoceSaludos(String saludo) {
        RespuestaDTO r = preguntar(saludo);
        assertThat(r.accion().tipo()).isEqualTo("SALUDO");
        assertThat(r.respuesta()).startsWith("¡Hola!");
    }

    @Test
    void elSaludoUsaElNombreDelUsuario() {
        assertThat(coopi.saludo(new Sesion(5, "  Juan   Moreno ", "ESTUDIANTE"))).startsWith("¡Hola, Juan!");
        assertThat(coopi.saludo(new Sesion(5, "", "ESTUDIANTE"))).startsWith("¡Hola!");
        assertThat(coopi.saludo(INVITADO)).startsWith("¡Hola!");
    }

    @ParameterizedTest
    @ValueSource(strings = {"¿qué programas hay?", "cuáles carreras tienen", "quiero ver todos los programas", "oferta académica"})
    void abreLaListaDeProgramas(String pregunta) {
        assertThat(preguntar(pregunta).accion().tipo()).isEqualTo("VER_PROGRAMAS");
    }

    @Test
    void unaPreguntaConcretaNoNavegaSinoQueResponde() {
        RespuestaDTO r = preguntar("¿cuánto dura el programa de medicina?");
        assertThat(r.accion()).isNull();
        assertThat(r.proveedor()).isEqualTo("local");
        assertThat(r.respuesta()).isNotBlank();
    }

    @Test
    void lasPalabrasCortasNoCoincidenDentroDeOtras() {
        // "amigos" contiene "ami" y "cafeteria" contiene "cafe", pero no son esos lugares
        Consulta c = Consulta.de("vamos con mis amigos", null, INVITADO, null);
        assertThat(c.contiene("ami")).isFalse();
        assertThat(Consulta.de("la cafeteria", null, null, null).contiene("cafe")).isFalse();
        assertThat(Consulta.de("los lockers", null, null, null).contiene("locker")).isTrue();
        assertThat(Consulta.de("las canchas", null, null, null).contiene("cancha")).isTrue();
        assertThat(Consulta.de("dos cafes", null, null, null).contiene("cafe")).isTrue();
    }

    @Test
    void laPalabraDeActivacionSeQuitaDelMensaje() {
        assertThat(Consulta.de("Ey Coopi, ¿dónde queda la biblioteca?", null, null, null).normal())
                .isEqualTo("donde queda la biblioteca");
        assertThat(Consulta.de("Oye Coopi llévame", null, null, null).normal()).isEqualTo("llevame");
    }
}
