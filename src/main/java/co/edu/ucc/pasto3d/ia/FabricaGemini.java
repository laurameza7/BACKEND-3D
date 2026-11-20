package co.edu.ucc.pasto3d.ia;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Familia "gemini" de la ABSTRACT FACTORY.
 * Arma: Medición( Caché( Proxy de cuota( Adaptador de Gemini ) ) ).
 */
@Component
public class FabricaGemini implements FabricaIA {

    private final GeminiProveedorIA gemini;
    private final ProveedorIAProxy proxy;                         // se reutiliza: guarda la ventana de la cuota
    private final Map<String, ProveedorIAConCache.Entrada> cache = new ConcurrentHashMap<>();

    public FabricaGemini(GeminiProveedorIA gemini,
                         @Value("${ia.gemini.limite-por-minuto:30}") int limitePorMinuto) {
        this.gemini = gemini;
        this.proxy = new ProveedorIAProxy(gemini, limitePorMinuto);
    }

    @Override public String familia() { return "gemini"; }
    @Override public boolean disponible() { return gemini.disponible(); }

    @Override
    public ProveedorIA crearProveedor() {
        return new ProveedorIAMedido(new ProveedorIAConCache(proxy, cache));
    }

    @Override
    public FormateadorRespuesta crearFormateador() { return new FormateadorMarkdown(); }
}
