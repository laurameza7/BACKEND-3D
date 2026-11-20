package co.edu.ucc.pasto3d.security;

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
 * Issues and validates session tokens signed with HMAC-SHA256 (same principle as a JWT):
 * content = id|name|role|expiration, signature = HMAC(secret, content).
 * The server does not store sessions: if the signature does not match or it expired, the token is rejected.
 */
@Component
public class TokenService {

    private static final long DURATION_SECONDS = 12 * 60 * 60;   // 12 hours
    private final byte[] secret;

    public TokenService(@Value("${app.secret:ucc-pasto3d-development-secret-change-in-production}") String secret) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
    }

    public String issue(Session s) {
        return issue(s, DURATION_SECONDS);
    }

    /** Lets tokens be issued with another duration (tests use a negative one to simulate an expired token). */
    String issue(Session s, long durationSeconds) {
        long expires = Instant.now().getEpochSecond() + durationSeconds;
        String content = (s.userId() == null ? "" : s.userId()) + "|" + clean(s.name()) + "|" + s.role() + "|" + expires;
        String c64 = b64(content.getBytes(StandardCharsets.UTF_8));
        return c64 + "." + b64(sign(c64));
    }

    public Optional<Session> validate(String token) {
        if (token == null || !token.contains(".")) return Optional.empty();
        String[] parts = token.split("\\.", 2);
        byte[] expected = sign(parts[0]);
        byte[] received;
        try { received = Base64.getUrlDecoder().decode(parts[1]); }
        catch (IllegalArgumentException e) { return Optional.empty(); }
        if (!MessageDigest.isEqual(expected, received)) return Optional.empty();

        try {
            String[] c = new String(Base64.getUrlDecoder().decode(parts[0]), StandardCharsets.UTF_8).split("\\|", -1);
            if (c.length != 4 || Long.parseLong(c[3]) < Instant.now().getEpochSecond()) return Optional.empty();
            Integer id = c[0].isEmpty() ? null : Integer.valueOf(c[0]);
            return Optional.of(new Session(id, c[1], c[2]));
        } catch (IllegalArgumentException e) {            // malformed content (includes NumberFormatException)
            return Optional.empty();
        }
    }

    private byte[] sign(String text) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));
            return mac.doFinal(text.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("The token could not be signed", e);
        }
    }

    private static String b64(byte[] b) { return Base64.getUrlEncoder().withoutPadding().encodeToString(b); }
    private static String clean(String s) { return s == null ? "" : s.replace("|", " "); }
}
