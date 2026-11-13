package co.edu.ucc.pasto3d.controlador;

import co.edu.ucc.pasto3d.dto.Dtos.*;
import co.edu.ucc.pasto3d.servicio.CampusService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Patrón MVC: el controlador expone la información del campus como API REST. */
@RestController
@RequestMapping("/api")
public class CampusController {

    private final CampusService campus;

    public CampusController(CampusService campus) { this.campus = campus; }

    @GetMapping("/edificios")
    public List<EdificioDTO> edificios() { return campus.edificios(); }

    @GetMapping("/edificios/{id}/lugares")
    public List<LugarDTO> lugaresDeEdificio(@PathVariable Integer id) { return campus.lugaresDeEdificio(id); }

    @GetMapping("/lugares")
    public List<LugarDTO> lugares() { return campus.lugares(); }

    @GetMapping("/programas")
    public List<ProgramaDTO> programas() { return campus.programas(); }

    @GetMapping("/programas/{id}")
    public ProgramaDTO programa(@PathVariable Integer id) {
        return campus.programas().stream().filter(p -> p.id().equals(id)).findFirst()
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "No existe el programa " + id));
    }


    
    @GetMapping("/preguntas-frecuentes")
    public List<PreguntaFrecuenteDTO> preguntasFrecuentes() { return campus.preguntasFrecuentes(); }
}
