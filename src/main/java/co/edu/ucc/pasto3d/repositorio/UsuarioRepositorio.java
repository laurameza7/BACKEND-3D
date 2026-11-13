package co.edu.ucc.pasto3d.repositorio;

import co.edu.ucc.pasto3d.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** Patrón Repository: acceso a los usuarios registrados. */
public interface UsuarioRepositorio extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByCorreoIgnoreCase(String correo);
    boolean existsByCorreoIgnoreCase(String correo);
}
