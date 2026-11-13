package co.edu.ucc.pasto3d.servicio;

import co.edu.ucc.pasto3d.dto.Dtos.*;
import co.edu.ucc.pasto3d.ia.ContextoCampus;
import co.edu.ucc.pasto3d.ia.ProveedorIA;
import co.edu.ucc.pasto3d.ia.ProveedorIAFactory;
import co.edu.ucc.pasto3d.intencion.Consulta;
import co.edu.ucc.pasto3d.intencion.ManejadorIntencion;
import co.edu.ucc.pasto3d.intencion.ProgramasManejador;
import co.edu.ucc.pasto3d.intencion.SaludoManejador;
import co.edu.ucc.pasto3d.modelo.Conversacion;
import co.edu.ucc.pasto3d.repositorio.ConversacionRepositorio;
import co.edu.ucc.pasto3d.seguridad.Sesion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static co.edu.ucc.pasto3d.ia.TextoUtil.normalizar;

/**
 * Patrón FACADE: un único punto de entrada ("preguntar") que oculta toda la complejidad:
 * 1) la cadena de intenciones (saludo, llévame a un programa / lugar),
 * 2) si ninguna aplica, la cadena de proveedores de IA (Gemini → local),
 * 3) detectar el edificio mencionado y guardar la conversación del usuario.
 */
@Service
public class AsistenteFacade {

    private static final Logger log = LoggerFactory.getLogger(AsistenteFacade.class);
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final CampusService campus;
    private final ProveedorIAFactory fabrica;
    private final ConversacionRepositorio conversaciones;
    private final List<ManejadorIntencion> intenciones;   // Spring las entrega ordenadas por @Order

    public AsistenteFacade(CampusService campus, ProveedorIAFactory fabrica, ConversacionRepositorio conversaciones,
                           List<ManejadorIntencion> intenciones) {
        this.campus = campus;
        this.fabrica = fabrica;
        this.conversaciones = conversaciones;
        this.intenciones = intenciones;
    }

    /** Compatibilidad: pregunta sin sesión ni programa. */
    public RespuestaDTO preguntar(String pregunta) {
        return preguntar(pregunta, null, null);
    }

    public RespuestaDTO preguntar(String pregunta, Integer programaId, Sesion sesion) {
        ContextoCampus contexto = campus.contexto();
        ProgramaDTO programaActual = programaId == null ? null
                : contexto.programas().stream().filter(p -> p.id().equals(programaId)).findFirst().orElse(null);
        Consulta consulta = Consulta.de(pregunta, contexto, sesion, programaActual);

        RespuestaDTO resultado = null;
        for (ManejadorIntencion m : intenciones) {               // Cadena de intenciones
            Optional<RespuestaDTO> r = m.atender(consulta);
            if (r.isPresent()) { resultado = r.get(); break; }
        }
        if (resultado == null) resultado = preguntarALaIA(pregunta, consulta, contexto);

        try {
            conversaciones.save(new Conversacion(pregunta, resultado.respuesta(), resultado.proveedor(),
                    sesion == null ? null : sesion.usuarioId(),
                    resultado.accion() == null ? null : resultado.accion().tipo()));
        } catch (Exception e) {
            log.warn("No se pudo guardar la conversación: {}", e.getMessage());
        }
        return resultado;
    }

    public String saludo(Sesion sesion) {
        String nombre = sesion == null || sesion.esInvitado() ? null : sesion.nombre().split(" ")[0];
        return SaludoManejador.saludo(nombre);
    }

    public List<MensajeHistorialDTO> historial(Sesion sesion) {
        if (sesion == null || sesion.esInvitado()) return List.of();
        return conversaciones.findTop20ByUsuarioIdOrderByIdDesc(sesion.usuarioId()).stream()
                .map(c -> new MensajeHistorialDTO(c.getPregunta(), c.getRespuesta(),
                        c.getCreadoEn() == null ? "" : c.getCreadoEn().format(FECHA)))
                .toList()
                .reversed();
    }

    private RespuestaDTO preguntarALaIA(String pregunta, Consulta consulta, ContextoCampus contexto) {
        // Si el usuario está viendo un programa y no nombra otro, la pregunta es sobre ese programa.
        String enviada = pregunta;
        ProgramaDTO actual = consulta.programaActual();
        if (actual != null && ProgramasManejador.buscar(consulta.normal(), contexto.programas()).isEmpty()) {
            enviada = pregunta + " (pregunta sobre el programa " + actual.nombre() + ")";
        }

        String respuesta = null;
        String usado = null;
        for (ProveedorIA proveedor : fabrica.crearCadena()) {   // Cadena de respaldo
            try {
                respuesta = proveedor.responder(enviada, contexto);
                usado = proveedor.nombre();
                break;
            } catch (Exception e) {
                log.warn("El proveedor {} falló: {}", proveedor.nombre(), e.getMessage());
            }
        }
        return new RespuestaDTO(respuesta, usado, detectarEdificio(respuesta, contexto), null);
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
