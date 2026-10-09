package co.edu.ucc.pasto3d.repositorio;

import co.edu.ucc.pasto3d.modelo.Conversacion;
import org.springframework.data.jpa.repository.JpaRepository;

/** Patrón Repository: abstrae el acceso a datos de Conversacion. */
public interface ConversacionRepositorio extends JpaRepository<Conversacion, Integer> {
}


