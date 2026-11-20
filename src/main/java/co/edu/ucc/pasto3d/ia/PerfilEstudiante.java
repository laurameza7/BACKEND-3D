package co.edu.ucc.pasto3d.ia;

/** Abstracción refinada del BRIDGE: estudiantes con cuenta, a quienes Coopi trata por su nombre. */
public class PerfilEstudiante extends PerfilAsistente {

    private final String nombre;

    public PerfilEstudiante(ProveedorIA implementacion, String nombre) {
        super(implementacion);
        this.nombre = nombre == null ? "" : nombre.split(" ")[0];
    }

    @Override public String tipo() { return "estudiante"; }

    @Override
    protected String indicaciones() {
        return "Quien pregunta es " + nombre + ", ESTUDIANTE registrado de la universidad. Llámalo por su nombre "
             + "y ve directo al punto (ya conoce el campus en general).";
    }
}
