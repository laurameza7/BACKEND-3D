package co.edu.ucc.pasto3d.repository;

import co.edu.ucc.pasto3d.model.Building;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repository pattern: abstracts data access for Building. */
public interface BuildingRepository extends JpaRepository<Building, Integer> {
}

