package co.edu.ucc.pasto3d.repository;

import co.edu.ucc.pasto3d.model.Program;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repository pattern: abstracts data access for Program. */
public interface ProgramRepository extends JpaRepository<Program, Integer> {
}

