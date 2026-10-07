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
        RespuestaDTO r = asistente.preguntar("¿Qué es medicina y dónde estudian?");
        assertThat(r.respuesta()).contains("Medicina");
        assertThat(r.edificioId()).isNotNull();
    }

    @Test
    void elAsistenteLocalRespondeSobrePagos() {
        RespuestaDTO r = asistente.preguntar("dónde pago la matrícula");
        assertThat(r.respuesta()).containsIgnoringCase("tesorería");
    }
}
