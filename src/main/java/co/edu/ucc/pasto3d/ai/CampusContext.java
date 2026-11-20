package co.edu.ucc.pasto3d.ai;

import co.edu.ucc.pasto3d.dto.Dtos.*;
import java.util.List;

/** Campus information the assistant uses as knowledge (it comes from the database). */
public record CampusContext(List<BuildingDTO> buildings,
                             List<PlaceDTO> places,
                             List<ProgramDTO> programs,
                             List<FaqDTO> questions) { }


