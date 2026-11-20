package co.edu.ucc.pasto3d.controller;

import co.edu.ucc.pasto3d.dto.Dtos.*;
import co.edu.ucc.pasto3d.security.AuthenticationInterceptor;
import co.edu.ucc.pasto3d.security.Session;
import co.edu.ucc.pasto3d.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService auth;

    public AuthController(AuthService auth) { this.auth = auth; }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public SessionDTO register(@Valid @RequestBody RegisterDTO body) { return auth.register(body); }

    @PostMapping("/login")
    public SessionDTO login(@Valid @RequestBody LoginDTO body) { return auth.login(body); }

    @PostMapping("/guest")
    public SessionDTO guest() { return auth.guest(); }

    /** Returns who owns the token (route protected by the interceptor). */
    @GetMapping("/me")
    public Map<String, Object> me(@RequestAttribute(AuthenticationInterceptor.ATTRIBUTE) Session s) {
        return Map.of("name", s.name(), "role", s.role(), "guest", s.isGuest());
    }
}
