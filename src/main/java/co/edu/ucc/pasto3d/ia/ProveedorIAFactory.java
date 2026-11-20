package co.edu.ucc.pasto3d.ia;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Elige qué familias de IA (ABSTRACT FACTORY) usar según la configuración (IA_PROVEEDOR)
 * y la disponibilidad, en orden de preferencia: si la primera falla, responde la siguiente.
 */
@Component
public class ProveedorIAFactory {

    private final FabricaGemini gemini;
    private final FabricaLocal local;
    private final String preferido;

    public ProveedorIAFactory(FabricaGemini gemini, FabricaLocal local,
                              @Value("${ia.proveedor:gemini}") String preferido) {
        this.gemini = gemini;
        this.local = local;
        this.preferido = preferido;
    }

    public List<FabricaIA> crearCadena() {
        if ("gemini".equalsIgnoreCase(preferido) && gemini.disponible())
            return List.of(gemini, local);
        return List.of(local);
    }
}
