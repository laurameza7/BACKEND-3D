package co.edu.ucc.pasto3d.ia;

/**
 * Patrón DECORATOR (base): envuelve un ProveedorIA y le agrega comportamiento
 * sin modificar su clase. Los decoradores se pueden apilar.
 */
public abstract class ProveedorIADecorador implements ProveedorIA {

    protected final ProveedorIA envuelto;

    protected ProveedorIADecorador(ProveedorIA envuelto) { this.envuelto = envuelto; }

    @Override public String nombre() { return envuelto.nombre(); }
    @Override public boolean disponible() { return envuelto.disponible(); }

    @Override
    public String responder(String pregunta, ContextoCampus contexto) {
        return responder(pregunta, contexto, null);
    }
}
