package co.edu.ucc.pasto3d.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * INTERCEPTOR pattern (Spring MVC variant of Chain of Responsibility):
 * before reaching the controller it checks the "Authorization: Bearer ..." token.
 * If it is valid it leaves the Session in the request; otherwise it answers 401.
 */
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    public static final String ATTRIBUTE = "session";
    private final TokenService tokens;

    public AuthenticationInterceptor(TokenService tokens) { this.tokens = tokens; }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) return true;   // CORS preflight
        String header = req.getHeader("Authorization");
        String token = header != null && header.startsWith("Bearer ") ? header.substring(7) : null;
        var session = tokens.validate(token);
        if (session.isPresent()) {
            req.setAttribute(ATTRIBUTE, session.get());
            return true;
        }
        res.setStatus(HttpStatus.UNAUTHORIZED.value());
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write("{\"error\":\"Debes iniciar sesión o entrar como invitado.\"}");
        return false;
    }
}
