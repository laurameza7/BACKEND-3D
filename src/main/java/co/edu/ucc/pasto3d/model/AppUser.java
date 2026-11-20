package co.edu.ucc.pasto3d.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Registered user. It does not store the password: it stores BCrypt( SHA-256(password) ),
 * where the SHA-256 is computed in the browser before anything is sent.
 */
@Entity
@Table(name = "app_user")
public class AppUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String email;
    @Column(name = "password_hash") private String passwordHash;
    private String role;
    @Column(name = "created_at") private LocalDateTime createdAt;
    @Column(name = "last_login") private LocalDateTime lastLogin;

    protected AppUser() { }

    public AppUser(String name, String email, String passwordHash, String role) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.createdAt = LocalDateTime.now();
    }

    public void registerLogin() { this.lastLogin = LocalDateTime.now(); }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getRole() { return role; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastLogin() { return lastLogin; }
}
