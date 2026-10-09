package co.edu.ucc.pasto3d.ia;

/**
 * Patrón STRATEGY: cada proveedor de IA es una estrategia intercambiable
 * para responder preguntas sobre el campus.
 */
public interface ProveedorIA {
    String nombre();
    boolean disponible();
    String responder(String pregunta, ContextoCampus contexto);
}




