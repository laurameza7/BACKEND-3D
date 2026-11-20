package co.edu.ucc.pasto3d.seguridad;

import co.edu.ucc.pasto3d.dto.Dtos.SesionDTO;

/**
 * Patrón FACTORY METHOD: el proceso para iniciar una sesión es siempre el mismo
 * (crear la sesión → firmar el token → armar la respuesta), pero QUÉ sesión se crea
 * lo decide cada subclase en su método fábrica {@link #crearSesion()}:
 * {@link CreadorSesionUsuario} para cuentas registradas y {@link CreadorSesionInvitado} para invitados.
 */
public abstract class CreadorSesion {

    /** Método fábrica: cada subclase crea su propio tipo de sesión. */
    protected abstract Sesion crearSesion();

    /** Datos extra que solo tienen algunos tipos de sesión. */
    protected String correo() { return null; }

    /** Operación común que usa el producto creado por la subclase. */
    public final SesionDTO iniciar(TokenServicio tokens) {
        Sesion s = crearSesion();
        return new SesionDTO(tokens.emitir(s), s.usuarioId(), s.nombre(), correo(), s.rol());
    }
}
