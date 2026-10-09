package co.edu.ucc.pasto3d.repositorio;

import co.edu.ucc.pasto3d.modelo.Lugar;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/** Patrón Repository: abstrae el acceso a datos de Lugar. */
public interface LugarRepositorio extends JpaRepository<Lugar, Integer> {
    List<Lugar> findByEdificioIdOrderByPisoAsc(Integer edificioId);
}

