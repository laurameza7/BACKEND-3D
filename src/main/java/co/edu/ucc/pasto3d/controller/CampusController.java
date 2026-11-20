package co.edu.ucc.pasto3d.controller;

import co.edu.ucc.pasto3d.dto.Dtos.*;
import co.edu.ucc.pasto3d.service.CampusService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** MVC pattern: the controller exposes the campus information as a REST API. */
@RestController
@RequestMapping("/api")
public class CampusController {

    private final CampusService campus;

    public CampusController(CampusService campus) { this.campus = campus; }

    @GetMapping("/buildings")
    public List<BuildingDTO> buildings() { return campus.buildings(); }

    @GetMapping("/buildings/{id}/places")
    public List<PlaceDTO> placesOfBuilding(@PathVariable Integer id) { return campus.placesOfBuilding(id); }

    @GetMapping("/places")
    public List<PlaceDTO> places() { return campus.places(); }

    @GetMapping("/programs")
    public List<ProgramDTO> programs() { return campus.programs(); }

    @GetMapping("/programs/{id}")
    public ProgramDTO program(@PathVariable Integer id) {
        return campus.programs().stream().filter(p -> p.id().equals(id)).findFirst()
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "No existe el programa " + id));
    }


    
    @GetMapping("/faqs")
    public List<FaqDTO> faqs() { return campus.faqs(); }
}
