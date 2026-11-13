package co.edu.ucc.pasto3d.intencion;

import co.edu.ucc.pasto3d.dto.Dtos.AccionDTO;
import co.edu.ucc.pasto3d.dto.Dtos.RespuestaDTO;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

/** Atiende saludos cortos: "hola", "buenas tardes", "ey Coopi"... */
@Component
@Order(1)
public class SaludoManejador implements ManejadorIntencion {

    private static final Set<String> SALUDOS = Set.of("hola", "holi", "buenas", "buenos dias", "buenas tardes",
            "buenas noches", "hey", "ey", "oye", "que mas", "quiubo", "saludos", "coopi", "como estas", "hola coopi",
            "quiero que me des informacion", "quiero informacion", "necesito informacion", "dame informacion",
            "necesito ayuda", "ayuda", "ayudame");

    public static String saludo(String nombre) {
        return "¡Hola" + (nombre == null ? "" : ", " + nombre) + "! ¿Cómo estás? ¿En qué te puedo ayudar hoy? "
                + "Puedo contarte sobre los programas, llevarte a cualquier lugar del campus o resolver tus dudas.";
    }

    @Override
    public Optional<RespuestaDTO> atender(Consulta c) {
        String t = c.normal().replaceAll("\\b(por favor|porfa|coopi|ey|oye|hey)\\b", "").replaceAll("\\s+", " ").trim();
        boolean soloSaludo = t.isEmpty() || SALUDOS.contains(t)
                || (t.split(" ").length <= 4 && SALUDOS.stream().anyMatch(s -> t.startsWith(s + " ") && !t.contains("?")
                    && SALUDOS.contains(t.substring(s.length()).trim())));
        if (!soloSaludo) return Optional.empty();
        return Optional.of(new RespuestaDTO(saludo(c.nombreUsuario()), "coopi", null, AccionDTO.de("SALUDO")));
    }
}
