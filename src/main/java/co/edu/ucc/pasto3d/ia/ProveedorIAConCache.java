package co.edu.ucc.pasto3d.ia;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

/**
 * DECORATOR: si la misma pregunta (con el mismo perfil) se hizo hace menos de 30 minutos,
 * devuelve la respuesta guardada sin volver a llamar a la IA: más rápido y ahorra cuota.
 */
public class ProveedorIAConCache extends ProveedorIADecorador {

    public record Entrada(String respuesta, Instant creada) { }

    static final Duration VIGENCIA = Duration.ofMinutes(30);
    private static final int MAXIMO = 300;
    private final Map<String, Entrada> cache;

    public ProveedorIAConCache(ProveedorIA envuelto, Map<String, Entrada> cache) {
        super(envuelto);
        this.cache = cache;
    }

    @Override
    public String responder(String pregunta, ContextoCampus contexto, String indicaciones) {
        String clave = TextoUtil.normalizar(pregunta) + "|" + (indicaciones == null ? "" : indicaciones);
        Entrada e = cache.get(clave);
        if (e != null && e.creada().plus(VIGENCIA).isAfter(Instant.now())) {
            MetricasAsistente.instancia().registrarCache();
            return e.respuesta();
        }
        String r = envuelto.responder(pregunta, contexto, indicaciones);
        if (cache.size() >= MAXIMO) cache.clear();
        cache.put(clave, new Entrada(r, Instant.now()));
        return r;
    }
}
