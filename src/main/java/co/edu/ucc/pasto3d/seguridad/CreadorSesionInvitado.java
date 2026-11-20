package co.edu.ucc.pasto3d.seguridad;

/** Creador concreto: sesión de invitado (sin cuenta, sin historial). */
public class CreadorSesionInvitado extends CreadorSesion {
    @Override
    protected Sesion crearSesion() { return new Sesion(null, "Invitado", "INVITADO"); }
}
