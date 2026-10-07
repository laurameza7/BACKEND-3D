package co.edu.ucc.pasto3d.repositorio;

import co.edu.ucc.pasto3d.modelo.Edificio;
import org.springframework.data.jpa.repository.JpaRepository;

/** Patrón Repository: abstrae el acceso a datos de Edificio. */
public interface EdificioRepositorio extends JpaRepository<Edificio, Integer> {
}
