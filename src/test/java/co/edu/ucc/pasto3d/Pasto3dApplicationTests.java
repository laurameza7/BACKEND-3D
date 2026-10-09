package co.edu.ucc.pasto3d;

import co.edu.ucc.pasto3d.dto.Dtos.RespuestaDTO;
import co.edu.ucc.pasto3d.servicio.AsistenteFacade;
import co.edu.ucc.pasto3d.servicio.CampusService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "ia.proveedor=local")
@ActiveProfiles("local")
class Pasto3dApplicationTests {

    @Autowired CampusService campus;
    @Autowired AsistenteFacade asistente;

    @Test
    void cargaLosDatosDelCampus() {
        assertThat(campus.edificios()).isNotEmpty();
        assertThat(campus.programas()).extracting("nombre").contains("Ingeniería de Software");
    }

    @Test
    void elAsistenteLocalRespondeSobreUnPrograma() {
        RespuestaDTO r = asistente.preguntar("¿Qué es medicina?");
        assertThat(r.respuesta()).contains("Medicina").doesNotContain("null");
    }

    @Test
    void elAsistenteLocalUbicaLaBibliotecaEnElBloqueA() {
        RespuestaDTO r = asistente.preguntar("¿Dónde queda la biblioteca?");
        assertThat(r.respuesta()).contains("Bloque A");
        assertThat(r.edificioId()).isNotNull();
    }

    @Test
    void elCampusTieneDosBloquesConSotanos() {
        assertThat(campus.edificios()).filteredOn(e -> e.tipo().equals("EDIFICIO")).hasSize(2)
                .allMatch(e -> e.sotanos() == 2);
    }

    @Test
    void elAsistenteLocalRespondeSobrePagos() {
        RespuestaDTO r = asistente.preguntar("dónde pago la matrícula");
        assertThat(r.respuesta()).containsIgnoringCase("tesorería");
    }
}
