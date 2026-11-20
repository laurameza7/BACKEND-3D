package co.edu.ucc.pasto3d.seguridad;

import co.edu.ucc.pasto3d.modelo.Usuario;

/** Creador concreto: sesión de un usuario registrado (con su id, nombre, rol y correo). */
public class CreadorSesionUsuario extends CreadorSesion {

    private final Usuario usuario;

    public CreadorSesionUsuario(Usuario usuario) { this.usuario = usuario; }

    @Override
    protected Sesion crearSesion() { return new Sesion(usuario.getId(), usuario.getNombre(), usuario.getRol()); }

    @Override
    protected String correo() { return usuario.getCorreo(); }
}
