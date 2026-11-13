package co.edu.ucc.pasto3d.intencion;

import co.edu.ucc.pasto3d.dto.Dtos.RespuestaDTO;

import java.util.Optional;

/**
 * Patrón CHAIN OF RESPONSIBILITY: cada manejador revisa el mensaje y, si lo
 * reconoce (saludo, "llévame a...", "qué programas hay"...), responde y genera
 * una acción para el frontend. Si ninguno lo atiende, responde la IA.
 */
public interface ManejadorIntencion {
    Optional<RespuestaDTO> atender(Consulta consulta);
}
