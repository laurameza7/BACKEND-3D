package co.edu.ucc.pasto3d.service;

import co.edu.ucc.pasto3d.dto.Dtos.*;
import co.edu.ucc.pasto3d.ai.CampusContext;
import co.edu.ucc.pasto3d.model.*;
import co.edu.ucc.pasto3d.repository.*;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Service layer: turns entities into DTOs and gathers the campus information. */
@Service
@Transactional(readOnly = true)
public class CampusService {

    private final BuildingRepository buildings;
    private final PlaceRepository places;
    private final ProgramRepository programs;
    private final FaqRepository questions;

    public CampusService(BuildingRepository buildings, PlaceRepository places,
                         ProgramRepository programs, FaqRepository questions) {
        this.buildings = buildings;
        this.places = places;
        this.programs = programs;
        this.questions = questions;
    }

    public List<BuildingDTO> buildings() {
        return buildings.findAll(Sort.by("id")).stream().map(CampusService::toDto).toList();
    }

    public List<PlaceDTO> places() {
        return places.findAll(Sort.by("id")).stream().map(CampusService::toDto).toList();
    }

    public List<PlaceDTO> placesOfBuilding(Integer buildingId) {
        return places.findByBuildingIdOrderByFloorAsc(buildingId).stream().map(CampusService::toDto).toList();
    }

    public List<ProgramDTO> programs() {
        return programs.findAll(Sort.by("name")).stream().map(CampusService::toDto).toList();
    }

    public List<FaqDTO> faqs() {
        return questions.findAll(Sort.by("id")).stream()
                .map(f -> new FaqDTO(f.getId(), f.getCategory(), f.getQuestion(), f.getAnswer(),
                        f.getKeywords()))
                .toList();
    }

    public CampusContext context() {
        return new CampusContext(buildings(), places(), programs(), faqs());
    }

    static BuildingDTO toDto(Building e) {
        return new BuildingDTO(e.getId(), e.getCode(), e.getName(), e.getDescription(),
                e.getPosX(), e.getPosZ(), e.getWidth(), e.getDepth(), e.getFloors(), e.getBasements(), e.getColor(), e.getType());
    }

    static PlaceDTO toDto(Place l) {
        return new PlaceDTO(l.getId(), l.getName(), l.getType(), l.getBuilding().getId(), l.getBuilding().getName(),
                l.getFloor(), l.getDescription(), l.getSchedule(), l.getPhone(), l.getEmail());
    }

    static ProgramDTO toDto(Program p) {
        Building e = p.getBuilding();
        Place l = p.getPlace();
        String location = l == null ? null
                : l.getBuilding().getName().split(" - ")[0] + ", " + co.edu.ucc.pasto3d.ai.TextUtil.floorName(l.getFloor());
        return new ProgramDTO(p.getId(), p.getName(), p.getFaculty(), p.getLevel(), p.getModality(),
                p.getDurationSemesters(), p.getDegreeTitle(), p.getDescription(),
                e == null ? null : e.getId(), e == null ? null : e.getName(),
                l == null ? null : l.getId(), l == null ? null : l.getName(), location);
    }
}


