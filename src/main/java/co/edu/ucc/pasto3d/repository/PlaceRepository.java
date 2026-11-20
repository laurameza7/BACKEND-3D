package co.edu.ucc.pasto3d.repository;

import co.edu.ucc.pasto3d.model.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/** Repository pattern: abstracts data access for Place. */
public interface PlaceRepository extends JpaRepository<Place, Integer> {
    List<Place> findByBuildingIdOrderByFloorAsc(Integer buildingId);
}

