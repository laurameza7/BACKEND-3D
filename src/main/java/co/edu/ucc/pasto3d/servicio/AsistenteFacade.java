package co.edu.ucc.pasto3d.servicio;

import co.edu.ucc.pasto3d.dto.Dtos.EdificioDTO;
import co.edu.ucc.pasto3d.dto.Dtos.RespuestaDTO;
import co.edu.ucc.pasto3d.ia.ContextoCampus;
import co.edu.ucc.pasto3d.ia.ProveedorIA;
import co.edu.ucc.pasto3d.ia.ProveedorIAFactory;
import co.edu.ucc.pasto3d.modelo.Conversacion;
import co.edu.ucc.pasto3d.repositorio.ConversacionRepositorio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static co.edu.ucc.pasto3d.ia.TextoUtil.normalizar;

/**
 * Patrón FACADE: un único punto de entrada ("preguntar") que oculta toda la
 * complejidad: armar el contexto desde la BD, elegir el proveedor de IA,
 * reintentar con el respaldo si falla, detectar el edificio y guardar la conversación.
 */
@Service
public class AsistenteFacade {

    private static final Logger log = LoggerFactory.getLogger(AsistenteFacade.class);

    private final CampusService campus;
    private final ProveedorIAFactory fabrica;
    private final ConversacionRepositorio conversaciones;

    public AsistenteFacade(CampusService campus, ProveedorIAFactory fabrica, ConversacionRepositorio conversaciones) {
        this.campus = campus;
        this.fabrica = fabrica;
        this.conversaciones = conversaciones;
    }

    public RespuestaDTO preguntar(String pregunta) {
        ContextoCampus contexto = campus.contexto();

        String respuesta = null;
        String usado = null;
        for (ProveedorIA proveedor : fabrica.crearCadena()) {   // Cadena de respaldo
            try {
                respuesta = proveedor.responder(pregunta, contexto);
                usado = proveedor.nombre();
                break;
            } catch (Exception e) {
                log.warn("El proveedor {} falló: {}", proveedor.nombre(), e.getMessage());
            }
        }

        try {
            conversaciones.save(new Conversacion(pregunta, respuesta, usado));
        } catch (Exception e) {
            log.warn("No se pudo guardar la conversación: {}", e.getMessage());
        }
        return new RespuestaDTO(respuesta, usado, detectarEdificio(respuesta, contexto));
    }

    /** Busca el primer bloque mencionado en la respuesta para resaltarlo en el mapa. */
    private Integer detectarEdificio(String respuesta, ContextoCampus contexto) {
        String r = normalizar(respuesta);
        Integer id = null;
        int primeraPos = Integer.MAX_VALUE;
        for (EdificioDTO e : contexto.edificios()) {
            String corto = normalizar(e.nombre().split(" - ")[0]);
            int pos = r.indexOf(corto);
            if (pos >= 0 && pos < primeraPos) { primeraPos = pos; id = e.id(); }
        }
        return id;
    }
}

