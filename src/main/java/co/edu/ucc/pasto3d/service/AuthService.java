package co.edu.ucc.pasto3d.service;

import co.edu.ucc.pasto3d.dto.Dtos.*;
import co.edu.ucc.pasto3d.model.AppUser;
import co.edu.ucc.pasto3d.repository.AppUserRepository;
import co.edu.ucc.pasto3d.security.GuestSessionCreator;
import co.edu.ucc.pasto3d.security.UserSessionCreator;
import co.edu.ucc.pasto3d.security.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.regex.Pattern;

/**
 * Registration, login and guests.
 * The "fingerprint" that arrives is SHA-256(password) computed in the browser (64 hex characters).
 * Here it is hashed again with BCrypt (random salt, cost 10) and only that is stored.
 */
@Service
public class AuthService {

    private static final Pattern FINGERPRINT = Pattern.compile("^[a-f0-9]{64}$");
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final AppUserRepository users;
    private final TokenService tokens;
    private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder(10);

    public AuthService(AppUserRepository users, TokenService tokens) {
        this.users = users;
        this.tokens = tokens;
    }

    @Transactional
    public SessionDTO register(RegisterDTO r) {
        String email = r.email().trim().toLowerCase();
        if (!EMAIL.matcher(email).matches()) throw error(HttpStatus.BAD_REQUEST, "El correo no es válido.");
        validateFingerprint(r.fingerprint());
        if (users.existsByEmailIgnoreCase(email)) throw error(HttpStatus.CONFLICT, "Ya existe una cuenta con ese correo.");
        AppUser u = users.save(new AppUser(r.name().trim(), email, bcrypt.encode(r.fingerprint()), "STUDENT"));
        return sessionOf(u);
    }

    @Transactional
    public SessionDTO login(LoginDTO i) {
        validateFingerprint(i.fingerprint());
        AppUser u = users.findByEmailIgnoreCase(i.email().trim())
                .filter(x -> bcrypt.matches(i.fingerprint(), x.getPasswordHash()))
                .orElseThrow(() -> error(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos."));
        u.registerLogin();
        return sessionOf(u);
    }

    public SessionDTO guest() {
        return new GuestSessionCreator().start(tokens);          // FACTORY METHOD
    }

    private SessionDTO sessionOf(AppUser u) {
        return new UserSessionCreator(u).start(tokens);          // FACTORY METHOD
    }

    private void validateFingerprint(String fingerprint) {
        if (fingerprint == null || !FINGERPRINT.matcher(fingerprint).matches())
            throw error(HttpStatus.BAD_REQUEST, "La contraseña debe llegar cifrada desde la aplicación.");
    }

    private static ResponseStatusException error(HttpStatus status, String message) {
        return new ResponseStatusException(status, message);
    }
}
