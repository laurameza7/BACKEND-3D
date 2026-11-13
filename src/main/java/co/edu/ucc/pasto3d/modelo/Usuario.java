package co.edu.ucc.pasto3d.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Usuario registrado. No guarda la contraseña: guarda BCrypt( SHA-256(contraseña) ),
 * donde el SHA-256 se calcula en el navegador antes de enviar nada.
 */
@Entity
@Table(name = "usuario")
public class Usuario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nombre;
    private String correo;
    @Column(name = "hash_clave") private String hashClave;
    private String rol;
    @Column(name = "creado_en") private LocalDateTime creadoEn;
    @Column(name = "ultimo_ingreso") private LocalDateTime ultimoIngreso;

    protected Usuario() { }

    public Usuario(String nombre, String correo, String hashClave, String rol) {
        this.nombre = nombre;
        this.correo = correo;
        this.hashClave = hashClave;
        this.rol = rol;
        this.creadoEn = LocalDateTime.now();
    }

    public void registrarIngreso() { this.ultimoIngreso = LocalDateTime.now(); }

    public Integer getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
    public String getHashClave() { return hashClave; }
    public String getRol() { return rol; }
    public LocalDateTime getCreadoEn() { return creadoEn; }
    public LocalDateTime getUltimoIngreso() { return ultimoIngreso; }
}
