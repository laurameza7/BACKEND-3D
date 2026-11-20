package co.edu.ucc.pasto3d.ia;

/**
 * Patrón ABSTRACT FACTORY: cada familia de IA crea productos que van juntos:
 * su proveedor (ya protegido y decorado) y el formateador que corresponde a sus respuestas.
 * Así nunca se mezcla, por ejemplo, el proveedor local con el formateador de Gemini.
 */
public interface FabricaIA {
    String familia();
    boolean disponible();
    ProveedorIA crearProveedor();
    FormateadorRespuesta crearFormateador();
}
