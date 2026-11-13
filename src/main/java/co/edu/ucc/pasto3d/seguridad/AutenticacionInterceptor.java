package co.edu.ucc.pasto3d.seguridad;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Patrón INTERCEPTOR (variante de Chain of Responsibility de Spring MVC):
 * antes de llegar al controlador revisa el token "Authorization: Bearer ...".
 * Si es válido deja la Sesion en la petición; si no, responde 401.
 */
@Component
public class AutenticacionInterceptor implements HandlerInterceptor {

    public static final String ATRIBUTO = "sesion";
    private final TokenServicio tokens;

    public AutenticacionInterceptor(TokenServicio tokens) { this.tokens = tokens; }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) return true;   // preflight CORS
        String cabecera = req.getHeader("Authorization");
        String token = cabecera != null && cabecera.startsWith("Bearer ") ? cabecera.substring(7) : null;
        var sesion = tokens.validar(token);
        if (sesion.isPresent()) {
            req.setAttribute(ATRIBUTO, sesion.get());
            return true;
        }
        res.setStatus(HttpStatus.UNAUTHORIZED.value());
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write("{\"error\":\"Debes iniciar sesión o entrar como invitado.\"}");
        return false;
    }
}
