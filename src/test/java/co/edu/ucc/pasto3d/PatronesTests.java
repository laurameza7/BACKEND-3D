package co.edu.ucc.pasto3d;

import co.edu.ucc.pasto3d.ia.*;
import co.edu.ucc.pasto3d.seguridad.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.*;

/** Pruebas unitarias de los patrones de diseño aplicados en el backend. */
class PatronesTests {

    /** Proveedor falso que cuenta cuántas veces lo llaman y qué indicaciones recibe. */
    static class ProveedorFalso implements ProveedorIA {
        int llamadas = 0;
        final List<String> indicaciones = new ArrayList<>();
        public String nombre() { return "falso"; }
        public boolean disponible() { return true; }
        public String responder(String p, ContextoCampus c) { return responder(p, c, null); }
        public String responder(String p, ContextoCampus c, String ind) { llamadas++; indicaciones.add(ind); return "**Respuesta** a " + p; }
    }

    static final ContextoCampus VACIO = new ContextoCampus(List.of(), List.of(), List.of(), List.of());

    @Test
    void singletonDevuelveSiempreLaMismaInstancia() {
        assertThat(MetricasAsistente.instancia()).isSameAs(MetricasAsistente.instancia());
        long antes = MetricasAsistente.instancia().preguntasDe("prueba-singleton");
        MetricasAsistente.instancia().registrar("prueba-singleton", 10);
        assertThat(MetricasAsistente.instancia().preguntasDe("prueba-singleton")).isEqualTo(antes + 1);
    }

    @Test
    void prototypeClonaSinModificarElOriginal() {
        PlantillaPrompt prototipo = new PlantillaPrompt("conocimiento del campus");
        PlantillaPrompt copia = prototipo.copiar().agregar("perfil visitante");
        assertThat(copia).isNotSameAs(prototipo);
        assertThat(copia.tamano()).isEqualTo(2);
        assertThat(prototipo.tamano()).isEqualTo(1);
        assertThat(copia.texto()).contains("conocimiento del campus").contains("perfil visitante");
    }

    @Test
    void builderArmaElPromptPorPartes() {
        String prompt = new ContextoPromptBuilder().instrucciones().edificios(VACIO).programas(VACIO).construir();
        assertThat(prompt).contains("Coopi").contains("## Edificios").contains("## Programas");
    }

    @Test
    void abstractFactoryCreaProductosDeLaMismaFamilia() {
        FabricaIA local = new FabricaLocal(new LocalProveedorIA());
        assertThat(local.crearProveedor().nombre()).isEqualTo("local");
        assertThat(local.crearFormateador()).isInstanceOf(FormateadorTextoPlano.class);
        FabricaIA gemini = new FabricaGemini(new GeminiProveedorIA("", "gemini-3.5-flash"), 30);
        assertThat(gemini.disponible()).isFalse();                         // sin API key
        assertThat(gemini.crearFormateador()).isInstanceOf(FormateadorMarkdown.class);
        assertThat(gemini.crearFormateador().formatear("**Hola**\n# Título\n- uno")).isEqualTo("Hola\nTítulo\n• uno");
    }

    @Test
    void factoryMethodCreaElTipoDeSesionCorrecto() {
        TokenServicio tokens = new TokenServicio("secreto-de-prueba");
        var invitado = new CreadorSesionInvitado().iniciar(tokens);
        assertThat(invitado.rol()).isEqualTo("INVITADO");
        assertThat(invitado.id()).isNull();
        assertThat(tokens.validar(invitado.token())).get().extracting(Sesion::esInvitado).isEqualTo(true);
    }

    @Test
    void decoratorConCacheEvitaLlamadasRepetidas() {
        ProveedorFalso real = new ProveedorFalso();
        ProveedorIA conCache = new ProveedorIAMedido(new ProveedorIAConCache(real, new ConcurrentHashMap<>()));
        String a = conCache.responder("¿Dónde queda la biblioteca?", VACIO);
        String b = conCache.responder("donde queda la BIBLIOTECA", VACIO);
        assertThat(a).isEqualTo(b);
        assertThat(real.llamadas).isEqualTo(1);
        assertThat(conCache.nombre()).isEqualTo("falso");
    }

    @Test
    void proxyRechazaCuandoSeAgotaLaCuota() {
        ProveedorFalso real = new ProveedorFalso();
        ProveedorIAProxy proxy = new ProveedorIAProxy(real, 2);
        proxy.responder("uno", VACIO);
        proxy.responder("dos", VACIO);
        assertThatThrownBy(() -> proxy.responder("tres", VACIO)).isInstanceOf(ProveedorIAProxy.CuotaAgotadaException.class);
        assertThat(real.llamadas).isEqualTo(2);                            // la tercera nunca llegó al real
    }

    @Test
    void bridgeCombinaCualquierPerfilConCualquierProveedor() {
        ProveedorFalso real = new ProveedorFalso();
        PerfilAsistente visitante = PerfilAsistente.para(new Sesion(null, "Invitado", "INVITADO"), real);
        PerfilAsistente estudiante = PerfilAsistente.para(new Sesion(7, "Laura Meza", "ESTUDIANTE"), real);
        assertThat(visitante.tipo()).isEqualTo("visitante");
        assertThat(estudiante.tipo()).isEqualTo("estudiante");
        visitante.responder("hola", VACIO);
        estudiante.responder("hola", VACIO);
        assertThat(real.indicaciones.get(0)).contains("VISITANTE");
        assertThat(real.indicaciones.get(1)).contains("Laura").contains("ESTUDIANTE");
        // el mismo perfil funciona también con otro proveedor (la IA local)
        assertThat(new PerfilVisitante(new LocalProveedorIA()).tipo()).isEqualTo("visitante");
    }

    @Test
    void adapterTraduceGeminiALaInterfazDelSistema() {
        ProveedorIA adaptado = new GeminiProveedorIA("", "gemini-3.5-flash");
        assertThat(adaptado.nombre()).isEqualTo("gemini");
        assertThat(adaptado.disponible()).isFalse();
    }
}
