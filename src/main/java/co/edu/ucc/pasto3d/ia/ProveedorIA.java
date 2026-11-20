package co.edu.ucc.pasto3d.ia;

/**
 * Patrón STRATEGY: cada proveedor de IA es una estrategia intercambiable
 * para responder preguntas sobre el campus.
 * También es el "implementador" del patrón BRIDGE (ver {@link PerfilAsistente}).
 */
public interface ProveedorIA {
    String nombre();
    boolean disponible();
    String responder(String pregunta, ContextoCampus contexto);

    /**
     * Responde con indicaciones extra sobre quién pregunta (las agrega el perfil del BRIDGE).
     * Por defecto se ignoran; Gemini las usa en sus instrucciones.
     */
    default String responder(String pregunta, ContextoCampus contexto, String indicaciones) {
        return responder(pregunta, contexto);
    }
}
