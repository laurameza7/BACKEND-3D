package co.edu.ucc.pasto3d.intent;

import co.edu.ucc.pasto3d.dto.Dtos.ActionDTO;
import co.edu.ucc.pasto3d.dto.Dtos.BuildingDTO;
import co.edu.ucc.pasto3d.dto.Dtos.PlaceDTO;
import co.edu.ucc.pasto3d.dto.Dtos.AnswerDTO;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;

import static co.edu.ucc.pasto3d.ai.TextUtil.floorName;
import static co.edu.ucc.pasto3d.ai.TextUtil.normalize;

/** "Llévame al restaurante", "quiero ver la biblioteca", "¿dónde queda tesorería?" → navigates in 3D. */
@Component
@Order(3)
public class PlacesHandler implements IntentHandler {

    /** Ways people name the places → name (or part of the name) of the place in the database. */
    private static final Map<String, String> SYNONYMS = new LinkedHashMap<>();
    static {
        // Most specific first (the first one that appears in the message wins)
        SYNONYMS.put("auditorio urcunina", "auditorio urcunina");
        SYNONYMS.put("urcunina", "auditorio urcunina");
        SYNONYMS.put("facultad de ingenieria", "facultad de ingenieria");
        SYNONYMS.put("facultad de medicina", "facultad de medicina");
        SYNONYMS.put("facultad de enfermeria", "facultad de enfermeria");
        SYNONYMS.put("facultad de derecho", "facultad de derecho");
        SYNONYMS.put("facultad de odontologia", "facultad de odontologia");
        SYNONYMS.put("sala de profesores de ingenieria", "sala de profesores de ingenieria");
        SYNONYMS.put("sala de profesores de medicina", "sala de profesores de medicina");
        SYNONYMS.put("laboratorio de innovacion", "laboratorio de innovacion");
        SYNONYMS.put("impresoras 3d", "laboratorio de innovacion");
        SYNONYMS.put("laboratorio de operaciones", "laboratorio de operaciones");
        SYNONYMS.put("laboratorios de ingenieria industrial", "laboratorios de ingenieria industrial");
        SYNONYMS.put("laboratorio de fisica", "laboratorios de ingenieria industrial");
        SYNONYMS.put("laboratorio de quimica", "laboratorios de ingenieria industrial");
        SYNONYMS.put("preclinica", "laboratorio de preclinica");
        SYNONYMS.put("simulacion clinica", "laboratorio de simulacion clinica");
        SYNONYMS.put("biologia molecular", "laboratorio de biologia molecular");
        SYNONYMS.put("realidad mixta", "laboratorio de realidad mixta");
        SYNONYMS.put("salas de audiencia", "salas de audiencia");
        SYNONYMS.put("sala de audiencia", "salas de audiencia");
        SYNONYMS.put("sala de informatica", "salas de informatica");
        SYNONYMS.put("salas de informatica", "salas de informatica");
        SYNONYMS.put("sala de sistemas", "salas de informatica");
        SYNONYMS.put("computadores", "salas de informatica");
        SYNONYMS.put("open language", "open language center");
        SYNONYMS.put("centro de ingles", "open language center");
        SYNONYMS.put("ingles", "open language center");
        SYNONYMS.put("gimnasio", "gimnasio");
        SYNONYMS.put("gym", "gimnasio");
        SYNONYMS.put("bienestar", "bienestar universitario");
        SYNONYMS.put("psicosocial", "apoyo psicosocial");
        SYNONYMS.put("psicologo", "apoyo psicosocial");
        SYNONYMS.put("puffs", "zona de descanso");
        SYNONYMS.put("descansar", "zona de descanso");
        SYNONYMS.put("papeleria", "papeleria");
        SYNONYMS.put("fotocopias", "papeleria");
        SYNONYMS.put("cajero", "cajero bbva");
        SYNONYMS.put("bbva", "cajero bbva");
        SYNONYMS.put("centro de investigacion", "centro de investigacion");
        SYNONYMS.put("sociojuridicas", "centro de investigaciones sociojuridicas");
        SYNONYMS.put("arte", "salon de arte");
        SYNONYMS.put("musica", "salon de arte");
        SYNONYMS.put("coordinacion clinica", "coordinacion clinica");
        SYNONYMS.put("restaurante", "cafeteria");
        SYNONYMS.put("comedor", "cafeteria");
        SYNONYMS.put("almorzar", "cafeteria");
        SYNONYMS.put("comer", "cafeteria");
        SYNONYMS.put("cafeteria", "cafeteria");
        SYNONYMS.put("cafe", "cafe bonne vie");
        SYNONYMS.put("bonne vie", "cafe bonne vie");
        SYNONYMS.put("biblioteca", "biblioteca");
        SYNONYMS.put("libros", "biblioteca");
        SYNONYMS.put("auditorio", "auditorio");
        SYNONYMS.put("cancha sintetica", "cancha sintetica");
        SYNONYMS.put("futbol", "cancha sintetica");
        SYNONYMS.put("voley", "cancha de voleibol");
        SYNONYMS.put("voleibol", "cancha de voleibol");
        SYNONYMS.put("canchas", "cancha sintetica");
        SYNONYMS.put("cancha", "cancha sintetica");
        SYNONYMS.put("clinica de posgrado", "clinica odontologica de posgrado");
        SYNONYMS.put("clinica", "clinica odontologica pregrado");
        SYNONYMS.put("odontologica", "clinica odontologica pregrado");
        SYNONYMS.put("dentista", "clinica odontologica pregrado");
        SYNONYMS.put("admisiones", "admisiones");
        SYNONYMS.put("registro", "admisiones");
        SYNONYMS.put("darc", "admisiones");
        SYNONYMS.put("inscripciones", "admisiones");
        SYNONYMS.put("matricula", "admisiones");
        SYNONYMS.put("tesoreria", "tesoreria");
        SYNONYMS.put("pagar", "tesoreria");
        SYNONYMS.put("contabilidad", "contabilidad");
        SYNONYMS.put("icetex", "icetex");
        SYNONYMS.put("credito", "icetex");
        SYNONYMS.put("ami", "ami");
        SYNONYMS.put("lockers", "lockers");
        SYNONYMS.put("casilleros", "lockers");
        SYNONYMS.put("morfologia", "laboratorio de morfologia");
        SYNONYMS.put("laboratorio", "laboratorio de morfologia");
        SYNONYMS.put("anfiteatro", "laboratorio de morfologia");
        SYNONYMS.put("comuna", "cooperativa comuna");
        SYNONYMS.put("cooperativa", "cooperativa comuna");
        SYNONYMS.put("porteria", "porteria");
        SYNONYMS.put("torniquetes", "porteria");
        SYNONYMS.put("entrada", "porteria");
        SYNONYMS.put("minimarket", "mini market");
        SYNONYMS.put("mini market", "mini market");
        SYNONYMS.put("bicicletas", "urkubici");
        SYNONYMS.put("urkubici", "urkubici");
        SYNONYMS.put("ping pong", "sala de juegos");
        SYNONYMS.put("sala de juegos", "sala de juegos");
        SYNONYMS.put("banos", "banos");
        SYNONYMS.put("historias clinicas", "archivo de historias");
        SYNONYMS.put("dental deals", "dental deals");
    }

    @Override
    public Optional<AnswerDTO> handle(Query c) {
        if (!c.asksToGo()) return Optional.empty();

        for (Map.Entry<String, String> s : SYNONYMS.entrySet()) {
            if (!c.contains(s.getKey())) continue;
            Optional<PlaceDTO> place = c.context().places().stream()
                    .filter(l -> normalize(l.name()).contains(s.getValue()))
                    .findFirst();
            if (place.isPresent()) return Optional.of(goToPlace(place.get(), s.getKey()));
        }
        // "Llévame al Bloque A" (take me to Block A)
        for (BuildingDTO e : c.context().buildings()) {
            String shortName = normalize(e.name().split(" - ")[0]);
            if (c.normalized().contains(shortName) || c.contains("bloque " + e.code().toLowerCase())) {
                return Optional.of(new AnswerDTO("¡Vamos! Te llevo al " + e.name() + ". " + e.description()
                        + " ¿Quieres que entremos o que te cuente qué hay en cada piso?",
                        "coopi", e.id(), new ActionDTO("GO_TO_BUILDING", null, null, e.id(), null, e.name())));
            }
        }
        return Optional.empty();
    }

    private static AnswerDTO goToPlace(PlaceDTO l, String askedAs) {
        String name = l.name();
        String intro = (askedAs.equals("restaurante") || askedAs.equals("comedor"))
                ? "El restaurante del campus es la " + name + ". " : "";
        String text = intro + "¡Vamos! Te llevo a " + name + ", en el " + floorName(l.floor()) + " del " + l.building() + ". "
                + (l.description() != null ? l.description() + " " : "")
                + (l.schedule() != null ? "Horario: " + l.schedule() + ". " : "")
                + (l.phone() != null ? "Contacto: " + l.phone() + ". " : "")
                + "¿Te ayudo con algo más?";
        return new AnswerDTO(text, "coopi", l.buildingId(),
                new ActionDTO("GO_TO_PLACE", null, l.id(), l.buildingId(), l.floor(), name));
    }
}
