package co.edu.ucc.pasto3d.controlador;

import co.edu.ucc.pasto3d.dto.Dtos.PreguntaDTO;
import co.edu.ucc.pasto3d.dto.Dtos.RespuestaDTO;
import co.edu.ucc.pasto3d.servicio.AsistenteFacade;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/asistente")
public class AsistenteController {

    private final AsistenteFacade asistente;

    public AsistenteController(AsistenteFacade asistente) { this.asistente = asistente; }

    @PostMapping("/preguntar")
    public RespuestaDTO preguntar(@Valid @RequestBody PreguntaDTO cuerpo) {
        return asistente.preguntar(cuerpo.pregunta().trim());
    }
}
