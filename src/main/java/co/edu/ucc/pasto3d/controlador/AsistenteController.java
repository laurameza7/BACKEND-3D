package co.edu.ucc.pasto3d.controlador;

import co.edu.ucc.pasto3d.dto.Dtos.MensajeHistorialDTO;
import co.edu.ucc.pasto3d.dto.Dtos.PreguntaDTO;
import co.edu.ucc.pasto3d.dto.Dtos.RespuestaDTO;
import co.edu.ucc.pasto3d.seguridad.AutenticacionInterceptor;
import co.edu.ucc.pasto3d.seguridad.Sesion;
import co.edu.ucc.pasto3d.servicio.AsistenteFacade;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** Rutas de Coopi. Todas exigen sesión (usuario o invitado), la valida el AutenticacionInterceptor. */
@RestController
@RequestMapping("/api/asistente")
public class AsistenteController {

    private final AsistenteFacade asistente;

    public AsistenteController(AsistenteFacade asistente) { this.asistente = asistente; }

    @PostMapping("/preguntar")
    public RespuestaDTO preguntar(@Valid @RequestBody PreguntaDTO cuerpo,
                                  @RequestAttribute(AutenticacionInterceptor.ATRIBUTO) Sesion sesion) {
        return asistente.preguntar(cuerpo.pregunta().trim(), cuerpo.programaId(), sesion);
    }

    /** Primer mensaje de Coopi: "¡Hola! ¿Cómo estás? ¿En qué te puedo ayudar hoy?" */
    @GetMapping("/saludo")
    public Map<String, String> saludo(@RequestAttribute(AutenticacionInterceptor.ATRIBUTO) Sesion sesion) {
        return Map.of("respuesta", asistente.saludo(sesion));
    }

    /** Últimas 20 preguntas del usuario registrado (los invitados no tienen historial). */
    @GetMapping("/historial")
    public List<MensajeHistorialDTO> historial(@RequestAttribute(AutenticacionInterceptor.ATRIBUTO) Sesion sesion) {
        return asistente.historial(sesion);
    }
}
