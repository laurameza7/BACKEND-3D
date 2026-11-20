package co.edu.ucc.pasto3d.ia;

import org.springframework.stereotype.Component;

/** Familia "local" de la ABSTRACT FACTORY: responde con la base de datos, sin internet. */
@Component
public class FabricaLocal implements FabricaIA {

    private final LocalProveedorIA local;

    public FabricaLocal(LocalProveedorIA local) { this.local = local; }

    @Override public String familia() { return "local"; }
    @Override public boolean disponible() { return true; }
    @Override public ProveedorIA crearProveedor() { return new ProveedorIAMedido(local); }
    @Override public FormateadorRespuesta crearFormateador() { return new FormateadorTextoPlano(); }
}
