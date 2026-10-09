package co.edu.ucc.pasto3d.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "pregunta_frecuente")
public class PreguntaFrecuente {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String categoria;
    private String pregunta;
    private String respuesta;
    @Column(name = "palabras_clave") private String palabrasClave;

    public Integer getId() { return id; }
    public String getCategoria() { return categoria; }
    public String getPregunta() { return pregunta; }
    public String getRespuesta() { return respuesta; }
    public String getPalabrasClave() { return palabrasClave; }
}


