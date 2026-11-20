package co.edu.ucc.pasto3d.seguridad;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

/** Pruebas unitarias del token firmado (sin levantar Spring). */
class TokenServicioTests {

    private final TokenServicio tokens = new TokenServicio("secreto-de-prueba");

    @Test
    void unTokenEmitidoSeValidaYConservaLosDatos() {
        String t = tokens.emitir(new Sesion(7, "Laura Meza", "ESTUDIANTE"));
        Sesion s = tokens.validar(t).orElseThrow();
        assertThat(s.usuarioId()).isEqualTo(7);
        assertThat(s.nombre()).isEqualTo("Laura Meza");
        assertThat(s.rol()).isEqualTo("ESTUDIANTE");
        assertThat(s.esInvitado()).isFalse();
    }

    @Test
    void elInvitadoNoTieneId() {
        String t = tokens.emitir(new Sesion(null, "Invitado", "INVITADO"));
        Sesion s = tokens.validar(t).orElseThrow();
        assertThat(s.usuarioId()).isNull();
        assertThat(s.esInvitado()).isTrue();
    }

    @Test
    void unTokenVencidoNoSirve() {
        String t = tokens.emitir(new Sesion(1, "Juan", "ESTUDIANTE"), -10);
        assertThat(tokens.validar(t)).isEmpty();
    }

    @Test
    void unTokenFirmadoConOtroSecretoNoSirve() {
        String ajeno = new TokenServicio("otro-secreto").emitir(new Sesion(1, "Manuel", "ESTUDIANTE"));
        assertThat(tokens.validar(ajeno)).isEmpty();
    }

    @Test
    void cambiarElContenidoInvalidaLaFirma() {
        String t = tokens.emitir(new Sesion(2, "Juan", "ESTUDIANTE"));
        String falso = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("1|Juan|ADMIN|9999999999".getBytes(StandardCharsets.UTF_8));
        assertThat(tokens.validar(falso + t.substring(t.indexOf('.')))).isEmpty();
    }

    @Test
    void tokensMalFormadosSeRechazanSinExcepciones() {
        assertThat(tokens.validar(null)).isEmpty();
        assertThat(tokens.validar("")).isEmpty();
        assertThat(tokens.validar("sinpunto")).isEmpty();
        assertThat(tokens.validar(".")).isEmpty();
        assertThat(tokens.validar("abc.%%%")).isEmpty();
        assertThat(tokens.validar("a.b.c")).isEmpty();
    }

    @Test
    void laBarraVerticalDelNombreNoRompeElToken() {
        String t = tokens.emitir(new Sesion(3, "Ana|ADMIN", "ESTUDIANTE"));
        Sesion s = tokens.validar(t).orElseThrow();
        assertThat(s.rol()).isEqualTo("ESTUDIANTE");
        assertThat(s.nombre()).isEqualTo("Ana ADMIN");
    }
}
