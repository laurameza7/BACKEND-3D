package co.edu.ucc.pasto3d.repositorio;

import co.edu.ucc.pasto3d.modelo.PreguntaFrecuente;
import org.springframework.data.jpa.repository.JpaRepository;

/** Patrón Repository: abstrae el acceso a datos de PreguntaFrecuente. */
public interface PreguntaFrecuenteRepositorio extends JpaRepository<PreguntaFrecuente, Integer> {
}

