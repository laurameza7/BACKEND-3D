package co.edu.ucc.pasto3d.intencion;

import co.edu.ucc.pasto3d.dto.Dtos.AccionDTO;
import co.edu.ucc.pasto3d.dto.Dtos.EdificioDTO;
import co.edu.ucc.pasto3d.dto.Dtos.LugarDTO;
import co.edu.ucc.pasto3d.dto.Dtos.RespuestaDTO;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;

import static co.edu.ucc.pasto3d.ia.TextoUtil.nombrePiso;
import static co.edu.ucc.pasto3d.ia.TextoUtil.normalizar;

/** "Llévame al restaurante", "quiero ver la biblioteca", "¿dónde queda tesorería?" → navega en el 3D. */
@Component
@Order(3)
public class LugaresManejador implements ManejadorIntencion {

    /** Formas en que la gente nombra los lugares → nombre (o parte del nombre) del lugar en la BD. */
    private static final Map<String, String> SINONIMOS = new LinkedHashMap<>();
    static {
        SINONIMOS.put("restaurante", "cafeteria");
        SINONIMOS.put("comedor", "cafeteria");
        SINONIMOS.put("almorzar", "cafeteria");
        SINONIMOS.put("comer", "cafeteria");
        SINONIMOS.put("cafeteria", "cafeteria");
        SINONIMOS.put("cafe", "cafe bonne vie");
        SINONIMOS.put("bonne vie", "cafe bonne vie");
        SINONIMOS.put("biblioteca", "biblioteca");
        SINONIMOS.put("libros", "biblioteca");
        SINONIMOS.put("auditorio", "auditorio");
        SINONIMOS.put("cancha sintetica", "cancha sintetica");
        SINONIMOS.put("futbol", "cancha sintetica");
        SINONIMOS.put("voley", "cancha de voleibol");
        SINONIMOS.put("voleibol", "cancha de voleibol");
        SINONIMOS.put("canchas", "cancha sintetica");
        SINONIMOS.put("cancha", "cancha sintetica");
        SINONIMOS.put("clinica de posgrado", "clinica odontologica de posgrado");
        SINONIMOS.put("clinica", "clinica odontologica pregrado");
        SINONIMOS.put("odontologica", "clinica odontologica pregrado");
        SINONIMOS.put("dentista", "clinica odontologica pregrado");
        SINONIMOS.put("admisiones", "admisiones");
        SINONIMOS.put("registro", "admisiones");
        SINONIMOS.put("darc", "admisiones");
        SINONIMOS.put("inscripciones", "admisiones");
        SINONIMOS.put("matricula", "admisiones");
        SINONIMOS.put("tesoreria", "tesoreria");
        SINONIMOS.put("pagar", "tesoreria");
        SINONIMOS.put("contabilidad", "contabilidad");
        SINONIMOS.put("icetex", "icetex");
        SINONIMOS.put("credito", "icetex");
        SINONIMOS.put("ami", "ami");
        SINONIMOS.put("lockers", "lockers");
        SINONIMOS.put("casilleros", "lockers");
        SINONIMOS.put("morfologia", "laboratorio de morfologia");
        SINONIMOS.put("laboratorio", "laboratorio de morfologia");
        SINONIMOS.put("anfiteatro", "laboratorio de morfologia");
        SINONIMOS.put("comuna", "cooperativa comuna");
        SINONIMOS.put("cooperativa", "cooperativa comuna");
        SINONIMOS.put("porteria", "porteria");
        SINONIMOS.put("torniquetes", "porteria");
        SINONIMOS.put("entrada", "porteria");
        SINONIMOS.put("minimarket", "mini market");
        SINONIMOS.put("mini market", "mini market");
        SINONIMOS.put("cajero", "corresponsales");
        SINONIMOS.put("bicicletas", "urkubici");
        SINONIMOS.put("urkubici", "urkubici");
        SINONIMOS.put("ping pong", "sala de juegos");
        SINONIMOS.put("sala de juegos", "sala de juegos");
        SINONIMOS.put("banos", "banos");
        SINONIMOS.put("historias clinicas", "archivo de historias");
        SINONIMOS.put("dental deals", "dental deals");
    }

    @Override
    public Optional<RespuestaDTO> atender(Consulta c) {
        if (!c.pideIr()) return Optional.empty();

        for (Map.Entry<String, String> s : SINONIMOS.entrySet()) {
            if (!c.contiene(s.getKey())) continue;
            Optional<LugarDTO> lugar = c.contexto().lugares().stream()
                    .filter(l -> normalizar(l.nombre()).contains(s.getValue()))
                    .findFirst();
            if (lugar.isPresent()) return Optional.of(irALugar(lugar.get(), s.getKey()));
        }
        // "Llévame al Bloque A"
        for (EdificioDTO e : c.contexto().edificios()) {
            String corto = normalizar(e.nombre().split(" - ")[0]);
            if (c.normal().contains(corto) || c.contiene("bloque " + e.codigo().toLowerCase())) {
                return Optional.of(new RespuestaDTO("¡Vamos! Te llevo al " + e.nombre() + ". " + e.descripcion()
                        + " ¿Quieres que entremos o que te cuente qué hay en cada piso?",
                        "coopi", e.id(), new AccionDTO("IR_EDIFICIO", null, null, e.id(), null, e.nombre())));
            }
        }
        return Optional.empty();
    }

    private static RespuestaDTO irALugar(LugarDTO l, String comoLoPidio) {
        String nombre = l.nombre();
        String intro = (comoLoPidio.equals("restaurante") || comoLoPidio.equals("comedor"))
                ? "El restaurante del campus es la " + nombre + ". " : "";
        String texto = intro + "¡Vamos! Te llevo a " + nombre + ", en el " + nombrePiso(l.piso()) + " del " + l.edificio() + ". "
                + (l.descripcion() != null ? l.descripcion() + " " : "")
                + (l.horario() != null ? "Horario: " + l.horario() + ". " : "")
                + (l.telefono() != null ? "Contacto: " + l.telefono() + ". " : "")
                + "¿Te ayudo con algo más?";
        return new RespuestaDTO(texto, "coopi", l.edificioId(),
                new AccionDTO("IR_LUGAR", null, l.id(), l.edificioId(), l.piso(), nombre));
    }
}
