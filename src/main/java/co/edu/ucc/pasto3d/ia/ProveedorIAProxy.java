package co.edu.ucc.pasto3d.ia;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Patrón PROXY (de protección): se pone delante del proveedor real (Gemini) y CONTROLA EL ACCESO.
 * Solo deja pasar un número máximo de peticiones por minuto (la API gratuita tiene cuota).
 * Si se supera, rechaza la petición sin llamar a Gemini y la fachada responde con la IA local.
 * El resto del sistema no nota la diferencia: el proxy tiene la misma interfaz que el real.
 */
public class ProveedorIAProxy implements ProveedorIA {

    /** La petición no se envió porque se agotó la cuota del minuto. */
    public static class CuotaAgotadaException extends RuntimeException {
        public CuotaAgotadaException(String m) { super(m); }
    }

    private final ProveedorIA real;
    private final int limitePorMinuto;
    private final Deque<Long> llamadas = new ArrayDeque<>();

    public ProveedorIAProxy(ProveedorIA real, int limitePorMinuto) {
        this.real = real;
        this.limitePorMinuto = limitePorMinuto;
    }

    @Override public String nombre() { return real.nombre(); }
    @Override public boolean disponible() { return real.disponible(); }

    @Override
    public String responder(String pregunta, ContextoCampus contexto) {
        return responder(pregunta, contexto, null);
    }

    @Override
    public String responder(String pregunta, ContextoCampus contexto, String indicaciones) {
        if (!real.disponible()) throw new IllegalStateException("El proveedor " + real.nombre() + " no está configurado");
        reservarCupo();
        return real.responder(pregunta, contexto, indicaciones);
    }

    private synchronized void reservarCupo() {
        long ahora = System.currentTimeMillis();
        while (!llamadas.isEmpty() && ahora - llamadas.peekFirst() > 60_000) llamadas.pollFirst();
        if (llamadas.size() >= limitePorMinuto) {
            MetricasAsistente.instancia().registrarRechazoCuota();
            throw new CuotaAgotadaException("Cuota de " + limitePorMinuto + " preguntas por minuto agotada");
        }
        llamadas.addLast(ahora);
    }
}
