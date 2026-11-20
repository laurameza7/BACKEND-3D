package co.edu.ucc.pasto3d.ia;

/** Abstracción refinada del BRIDGE: visitantes y aspirantes que entran como invitados. */
public class PerfilVisitante extends PerfilAsistente {

    public PerfilVisitante(ProveedorIA implementacion) { super(implementacion); }

    @Override public String tipo() { return "visitante"; }

    @Override
    protected String indicaciones() {
        return """
            Quien pregunta es un VISITANTE o aspirante que no conoce el campus.
            Explica cómo llegar (bloque y piso) y, si pregunta por un programa, menciona que las
            inscripciones se hacen en Admisiones (DARC), Bloque B piso 1.""";
    }
}
