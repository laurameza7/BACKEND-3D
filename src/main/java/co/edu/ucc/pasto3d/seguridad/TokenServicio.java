package co.edu.ucc.pasto3d.seguridad;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

/**
 * Emite y valida tokens de sesión firmados con HMAC-SHA256 (mismo principio que un JWT):
 * contenido = id|nombre|rol|vencimiento, firma = HMAC(secreto, contenido).
 * El servidor no guarda sesiones: si la firma no coincide o ya venció, el token no sirve.
 */
@Component
public class TokenServicio {

    private static final long DURACION_SEGUNDOS = 12 * 60 * 60;   // 12 horas
    private final byte[] secreto;

    public TokenServicio(@Value("${app.secreto:ucc-pasto3d-secreto-de-desarrollo-cambiar-en-produccion}") String secreto) {
        this.secreto = secreto.getBytes(StandardCharsets.UTF_8);
    }

    public String emitir(Sesion s) {
        return emitir(s, DURACION_SEGUNDOS);
    }

    /** Permite emitir tokens con otra duración (las pruebas usan una negativa para simular un token vencido). */
    String emitir(Sesion s, long duracionSegundos) {
        long vence = Instant.now().getEpochSecond() + duracionSegundos;
        String contenido = (s.usuarioId() == null ? "" : s.usuarioId()) + "|" + limpio(s.nombre()) + "|" + s.rol() + "|" + vence;
        String c64 = b64(contenido.getBytes(StandardCharsets.UTF_8));
        return c64 + "." + b64(firmar(c64));
    }

    public Optional<Sesion> validar(String token) {
        if (token == null || !token.contains(".")) return Optional.empty();
        String[] partes = token.split("\\.", 2);
        byte[] esperada = firmar(partes[0]);
        byte[] recibida;
        try { recibida = Base64.getUrlDecoder().decode(partes[1]); }
        catch (IllegalArgumentException e) { return Optional.empty(); }
        if (!MessageDigest.isEqual(esperada, recibida)) return Optional.empty();

        try {
            String[] c = new String(Base64.getUrlDecoder().decode(partes[0]), StandardCharsets.UTF_8).split("\\|", -1);
            if (c.length != 4 || Long.parseLong(c[3]) < Instant.now().getEpochSecond()) return Optional.empty();
            Integer id = c[0].isEmpty() ? null : Integer.valueOf(c[0]);
            return Optional.of(new Sesion(id, c[1], c[2]));
        } catch (IllegalArgumentException e) {            // contenido mal formado (incluye NumberFormatException)
            return Optional.empty();
        }
    }

    private byte[] firmar(String texto) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secreto, "HmacSHA256"));
            return mac.doFinal(texto.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo firmar el token", e);
        }
    }

    private static String b64(byte[] b) { return Base64.getUrlEncoder().withoutPadding().encodeToString(b); }
    private static String limpio(String s) { return s == null ? "" : s.replace("|", " "); }
}
