package co.edu.ucc.pasto3d.security;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

/** Unit tests of the signed token (without starting Spring). */
class TokenServiceTests {

    private final TokenService tokens = new TokenService("test-secret");

    @Test
    void issuedTokenIsValidAndKeepsTheData() {
        String t = tokens.issue(new Session(7, "Laura Meza", "STUDENT"));
        Session s = tokens.validate(t).orElseThrow();
        assertThat(s.userId()).isEqualTo(7);
        assertThat(s.name()).isEqualTo("Laura Meza");
        assertThat(s.role()).isEqualTo("STUDENT");
        assertThat(s.isGuest()).isFalse();
    }

    @Test
    void guestHasNoId() {
        String t = tokens.issue(new Session(null, "Invitado", "GUEST"));
        Session s = tokens.validate(t).orElseThrow();
        assertThat(s.userId()).isNull();
        assertThat(s.isGuest()).isTrue();
    }

    @Test
    void expiredTokenIsRejected() {
        String t = tokens.issue(new Session(1, "Juan", "STUDENT"), -10);
        assertThat(tokens.validate(t)).isEmpty();
    }

    @Test
    void tokenSignedWithAnotherSecretIsRejected() {
        String foreign = new TokenService("another-secret").issue(new Session(1, "Manuel", "STUDENT"));
        assertThat(tokens.validate(foreign)).isEmpty();
    }

    @Test
    void changingTheContentInvalidatesTheSignature() {
        String t = tokens.issue(new Session(2, "Juan", "STUDENT"));
        String fake = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("1|Juan|ADMIN|9999999999".getBytes(StandardCharsets.UTF_8));
        assertThat(tokens.validate(fake + t.substring(t.indexOf('.')))).isEmpty();
    }

    @Test
    void malformedTokensAreRejectedWithoutExceptions() {
        assertThat(tokens.validate(null)).isEmpty();
        assertThat(tokens.validate("")).isEmpty();
        assertThat(tokens.validate("nodot")).isEmpty();
        assertThat(tokens.validate(".")).isEmpty();
        assertThat(tokens.validate("abc.%%%")).isEmpty();
        assertThat(tokens.validate("a.b.c")).isEmpty();
    }

    @Test
    void verticalBarInNameDoesNotBreakTheToken() {
        String t = tokens.issue(new Session(3, "Ana|ADMIN", "STUDENT"));
        Session s = tokens.validate(t).orElseThrow();
        assertThat(s.role()).isEqualTo("STUDENT");
        assertThat(s.name()).isEqualTo("Ana ADMIN");
    }
}
