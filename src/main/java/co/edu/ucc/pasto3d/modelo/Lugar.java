package co.edu.ucc.pasto3d.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "lugar")
public class Lugar {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nombre;
    private String tipo;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "edificio_id")
    private Edificio edificio;
    private Integer piso;
    private String descripcion;
    private String horario;
    private String telefono;
    private String correo;

    public Integer getId() { return id; }
    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }
    public Edificio getEdificio() { return edificio; }
    public Integer getPiso() { return piso; }
    public String getDescripcion() { return descripcion; }
    public String getHorario() { return horario; }
    public String getTelefono() { return telefono; }
    public String getCorreo() { return correo; }
}
