package co.edu.ucc.pasto3d.repositorio;

import co.edu.ucc.pasto3d.modelo.Programa;
import org.springframework.data.jpa.repository.JpaRepository;

/** Patrón Repository: abstrae el acceso a datos de Programa. */
public interface ProgramaRepositorio extends JpaRepository<Programa, Integer> {
}
