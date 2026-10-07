package co.edu.ucc.pasto3d.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "edificio")
public class Edificio {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String codigo;
    private String nombre;
    private String descripcion;
    @Column(name = "pos_x") private Double posX;
    @Column(name = "pos_z") private Double posZ;
    private Double ancho;
    private Double profundidad;
    private Integer pisos;
    private String color;
    private String tipo;

    public Integer getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public Double getPosX() { return posX; }
    public Double getPosZ() { return posZ; }
    public Double getAncho() { return ancho; }
    public Double getProfundidad() { return profundidad; }
    public Integer getPisos() { return pisos; }
    public String getColor() { return color; }
    public String getTipo() { return tipo; }
}
