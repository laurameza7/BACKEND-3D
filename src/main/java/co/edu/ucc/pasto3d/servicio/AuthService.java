package co.edu.ucc.pasto3d.servicio;

import co.edu.ucc.pasto3d.dto.Dtos.*;
import co.edu.ucc.pasto3d.modelo.Usuario;
import co.edu.ucc.pasto3d.repositorio.UsuarioRepositorio;
import co.edu.ucc.pasto3d.seguridad.Sesion;
import co.edu.ucc.pasto3d.seguridad.TokenServicio;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.regex.Pattern;

/**
 * Registro, ingreso e invitados.
 * La "huella" que llega es SHA-256(contraseña) calculado en el navegador (64 caracteres hex).
 * Aquí se vuelve a cifrar con BCrypt (sal aleatoria, costo 10) y solo eso se guarda.
 */
@Service
public class AuthService {

    private static final Pattern HUELLA = Pattern.compile("^[a-f0-9]{64}$");
    private static final Pattern CORREO = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final UsuarioRepositorio usuarios;
    private final TokenServicio tokens;
    private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder(10);

    public AuthService(UsuarioRepositorio usuarios, TokenServicio tokens) {
        this.usuarios = usuarios;
        this.tokens = tokens;
    }

    @Transactional
    public SesionDTO registrar(RegistroDTO r) {
        String correo = r.correo().trim().toLowerCase();
        if (!CORREO.matcher(correo).matches()) throw error(HttpStatus.BAD_REQUEST, "El correo no es válido.");
        validarHuella(r.huella());
        if (usuarios.existsByCorreoIgnoreCase(correo)) throw error(HttpStatus.CONFLICT, "Ya existe una cuenta con ese correo.");
        Usuario u = usuarios.save(new Usuario(r.nombre().trim(), correo, bcrypt.encode(r.huella()), "ESTUDIANTE"));
        return sesionDe(u);
    }

    @Transactional
    public SesionDTO ingresar(IngresoDTO i) {
        validarHuella(i.huella());
        Usuario u = usuarios.findByCorreoIgnoreCase(i.correo().trim())
                .filter(x -> bcrypt.matches(i.huella(), x.getHashClave()))
                .orElseThrow(() -> error(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos."));
        u.registrarIngreso();
        return sesionDe(u);
    }

    public SesionDTO invitado() {
        Sesion s = new Sesion(null, "Invitado", "INVITADO");
        return new SesionDTO(tokens.emitir(s), null, s.nombre(), null, s.rol());
    }

    private SesionDTO sesionDe(Usuario u) {
        Sesion s = new Sesion(u.getId(), u.getNombre(), u.getRol());
        return new SesionDTO(tokens.emitir(s), u.getId(), u.getNombre(), u.getCorreo(), u.getRol());
    }

    private void validarHuella(String huella) {
        if (huella == null || !HUELLA.matcher(huella).matches())
            throw error(HttpStatus.BAD_REQUEST, "La contraseña debe llegar cifrada desde la aplicación.");
    }

    private static ResponseStatusException error(HttpStatus estado, String mensaje) {
        return new ResponseStatusException(estado, mensaje);
    }
}
