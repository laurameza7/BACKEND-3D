package co.edu.ucc.pasto3d.ia;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Patrón SINGLETON: una única instancia en toda la aplicación lleva las métricas de Coopi
 * (preguntas por proveedor, tiempos, respuestas desde la caché, cuota rechazada).
 * Constructor privado + "holder" estático: la instancia se crea una sola vez, de forma
 * perezosa y segura entre hilos. Se consulta en GET /api/salud.
 */
public final class MetricasAsistente {

    private final Map<String, AtomicLong> preguntas = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> milisegundos = new ConcurrentHashMap<>();
    private final AtomicLong desdeCache = new AtomicLong();
    private final AtomicLong rechazadasPorCuota = new AtomicLong();

    private MetricasAsistente() { }

    private static final class Contenedor {
        private static final MetricasAsistente INSTANCIA = new MetricasAsistente();
    }

    public static MetricasAsistente instancia() { return Contenedor.INSTANCIA; }

    public void registrar(String proveedor, long ms) {
        preguntas.computeIfAbsent(proveedor, k -> new AtomicLong()).incrementAndGet();
        milisegundos.computeIfAbsent(proveedor, k -> new AtomicLong()).addAndGet(ms);
    }
    public void registrarCache() { desdeCache.incrementAndGet(); }
    public void registrarRechazoCuota() { rechazadasPorCuota.incrementAndGet(); }

    public long preguntasDe(String proveedor) {
        AtomicLong n = preguntas.get(proveedor);
        return n == null ? 0 : n.get();
    }

    public Map<String, Object> resumen() {
        Map<String, Object> r = new LinkedHashMap<>();
        preguntas.forEach((p, n) -> r.put(p, Map.of(
                "respuestas", n.get(),
                "promedioMs", n.get() == 0 ? 0 : milisegundos.get(p).get() / n.get())));
        r.put("desdeCache", desdeCache.get());
        r.put("rechazadasPorCuota", rechazadasPorCuota.get());
        return r;
    }
}
