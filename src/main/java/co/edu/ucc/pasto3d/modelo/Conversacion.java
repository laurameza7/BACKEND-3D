package co.edu.ucc.pasto3d.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "conversacion")
public class Conversacion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String pregunta;
    private String respuesta;
    private String proveedor;
    @Column(name = "usuario_id") private Integer usuarioId;
    private String accion;
    @Column(name = "creado_en") private LocalDateTime creadoEn;

    protected Conversacion() { }

    public Conversacion(String pregunta, String respuesta, String proveedor) {
        this(pregunta, respuesta, proveedor, null, null);
    }

    public Conversacion(String pregunta, String respuesta, String proveedor, Integer usuarioId, String accion) {
        this.pregunta = pregunta;
        this.respuesta = respuesta;
        this.proveedor = proveedor;
        this.usuarioId = usuarioId;
        this.accion = accion;
        this.creadoEn = LocalDateTime.now();
    }

    public Integer getId() { return id; }
    public String getPregunta() { return pregunta; }
    public String getRespuesta() { return respuesta; }
    public String getProveedor() { return proveedor; }
    public Integer getUsuarioId() { return usuarioId; }
    public String getAccion() { return accion; }
    public LocalDateTime getCreadoEn() { return creadoEn; }
}


