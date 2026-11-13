package co.edu.ucc.pasto3d.controlador;

import co.edu.ucc.pasto3d.dto.Dtos.*;
import co.edu.ucc.pasto3d.seguridad.AutenticacionInterceptor;
import co.edu.ucc.pasto3d.seguridad.Sesion;
import co.edu.ucc.pasto3d.servicio.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService auth;

    public AuthController(AuthService auth) { this.auth = auth; }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public SesionDTO registro(@Valid @RequestBody RegistroDTO cuerpo) { return auth.registrar(cuerpo); }

    @PostMapping("/ingresar")
    public SesionDTO ingresar(@Valid @RequestBody IngresoDTO cuerpo) { return auth.ingresar(cuerpo); }

    @PostMapping("/invitado")
    public SesionDTO invitado() { return auth.invitado(); }

    /** Devuelve quién es el dueño del token (ruta protegida por el interceptor). */
    @GetMapping("/yo")
    public Map<String, Object> yo(@RequestAttribute(AutenticacionInterceptor.ATRIBUTO) Sesion s) {
        return Map.of("nombre", s.nombre(), "rol", s.rol(), "invitado", s.esInvitado());
    }
}
