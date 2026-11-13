package co.edu.ucc.pasto3d.repositorio;

import co.edu.ucc.pasto3d.modelo.Conversacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversacionRepositorio extends JpaRepository<Conversacion, Integer> {
    /** Últimas conversaciones de un usuario registrado (su historial con Coopi). */
    List<Conversacion> findTop20ByUsuarioIdOrderByIdDesc(Integer usuarioId);
}
