package co.edu.ucc.pasto3d.ia;

import co.edu.ucc.pasto3d.seguridad.Sesion;

/**
 * Patrón BRIDGE: separa dos dimensiones que cambian por su cuenta.
 *  - Abstracción: el PERFIL de quien pregunta (visitante o estudiante) decide el enfoque de la respuesta.
 *  - Implementación: el PROVEEDOR de IA (Gemini o local) que genera el texto.
 * Cualquier perfil funciona con cualquier proveedor (2 × 2 combinaciones) sin crear una clase por cada una.
 */
public abstract class PerfilAsistente {

    protected final ProveedorIA implementacion;   // el "puente"

    protected PerfilAsistente(ProveedorIA implementacion) { this.implementacion = implementacion; }

    public String responder(String pregunta, ContextoCampus contexto) {
        return implementacion.responder(pregunta, contexto, indicaciones());
    }

    /** Lo que cambia según el perfil. */
    protected abstract String indicaciones();

    public abstract String tipo();

    /** Elige el perfil según la sesión (invitado o registrado). */
    public static PerfilAsistente para(Sesion sesion, ProveedorIA proveedor) {
        if (sesion == null || sesion.esInvitado()) return new PerfilVisitante(proveedor);
        return new PerfilEstudiante(proveedor, sesion.nombre());
    }
}
