package co.edu.ucc.pasto3d.ia;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Patrón FACTORY: decide qué estrategia de IA usar según la configuración
 * (variable IA_PROVEEDOR) y la disponibilidad de cada proveedor.
 * Devuelve la cadena en orden de preferencia: el primero que falle cede al siguiente.
 */
@Component
public class ProveedorIAFactory {

    private final GeminiProveedorIA gemini;
    private final LocalProveedorIA local;
    private final String preferido;

    public ProveedorIAFactory(GeminiProveedorIA gemini, LocalProveedorIA local,
                              @Value("${ia.proveedor:gemini}") String preferido) {
        this.gemini = gemini;
        this.local = local;
        this.preferido = preferido;
    }

    public List<ProveedorIA> crearCadena() {
        if ("gemini".equalsIgnoreCase(preferido) && gemini.disponible())
            return List.of(gemini, local);
        return List.of(local);
    }
}



