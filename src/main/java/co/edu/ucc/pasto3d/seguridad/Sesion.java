package co.edu.ucc.pasto3d.seguridad;

/** Quién está haciendo la petición. usuarioId es null para los invitados. */
public record Sesion(Integer usuarioId, String nombre, String rol) {
    public boolean esInvitado() { return usuarioId == null; }
}
