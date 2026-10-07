package co.edu.ucc.pasto3d.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "programa")
public class Programa {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nombre;
    private String facultad;
    private String nivel;
    private String modalidad;
    @Column(name = "duracion_semestres") private Integer duracionSemestres;
    private String titulo;
    private String descripcion;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "edificio_id")
    private Edificio edificio;

    public Integer getId() { return id; }
    public String getNombre() { return nombre; }
    public String getFacultad() { return facultad; }
    public String getNivel() { return nivel; }
    public String getModalidad() { return modalidad; }
    public Integer getDuracionSemestres() { return duracionSemestres; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public Edificio getEdificio() { return edificio; }
}
