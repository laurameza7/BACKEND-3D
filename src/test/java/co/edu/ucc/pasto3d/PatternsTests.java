package co.edu.ucc.pasto3d;

import co.edu.ucc.pasto3d.ai.*;
import co.edu.ucc.pasto3d.security.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.*;

/** Unit tests of the design patterns applied in the backend. */
class PatternsTests {

    /** Fake provider that counts how many times it is called and which hints it receives. */
    static class FakeProvider implements AIProvider {
        int calls = 0;
        final List<String> hints = new ArrayList<>();
        public String name() { return "fake"; }
        public boolean available() { return true; }
        public String answer(String p, CampusContext c) { return answer(p, c, null); }
        public String answer(String p, CampusContext c, String hint) { calls++; hints.add(hint); return "**Answer** to " + p; }
    }

    static final CampusContext EMPTY = new CampusContext(List.of(), List.of(), List.of(), List.of());

    @Test
    void singletonAlwaysReturnsTheSameInstance() {
        assertThat(AssistantMetrics.instance()).isSameAs(AssistantMetrics.instance());
        long before = AssistantMetrics.instance().questionsFrom("test-singleton");
        AssistantMetrics.instance().register("test-singleton", 10);
        assertThat(AssistantMetrics.instance().questionsFrom("test-singleton")).isEqualTo(before + 1);
    }

    @Test
    void prototypeClonesWithoutChangingTheOriginal() {
        PromptTemplate prototype = new PromptTemplate("campus knowledge");
        PromptTemplate copy = prototype.copy().add("visitor profile");
        assertThat(copy).isNotSameAs(prototype);
        assertThat(copy.size()).isEqualTo(2);
        assertThat(prototype.size()).isEqualTo(1);
        assertThat(copy.text()).contains("campus knowledge").contains("visitor profile");
    }

    @Test
    void builderAssemblesThePromptStepByStep() {
        String prompt = new PromptContextBuilder().instructions().buildings(EMPTY).programs(EMPTY).build();
        assertThat(prompt).contains("Coopi").contains("## Campus buildings").contains("## Academic programs");
    }

    @Test
    void abstractFactoryCreatesProductsOfTheSameFamily() {
        AIFactory local = new LocalFactory(new LocalAIProvider());
        assertThat(local.createProvider().name()).isEqualTo("local");
        assertThat(local.createFormatter()).isInstanceOf(PlainTextFormatter.class);
        AIFactory gemini = new GeminiFactory(new GeminiAIProvider("", "gemini-3.5-flash"), 30);
        assertThat(gemini.available()).isFalse();                         // no API key
        assertThat(gemini.createFormatter()).isInstanceOf(MarkdownFormatter.class);
        assertThat(gemini.createFormatter().format("**Hola**\n# Título\n- uno")).isEqualTo("Hola\nTítulo\n• uno");
    }

    @Test
    void factoryMethodCreatesTheRightSessionType() {
        TokenService tokens = new TokenService("test-secret");
        var guest = new GuestSessionCreator().start(tokens);
        assertThat(guest.role()).isEqualTo("GUEST");
        assertThat(guest.id()).isNull();
        assertThat(tokens.validate(guest.token())).get().extracting(Session::isGuest).isEqualTo(true);
    }

    @Test
    void cacheDecoratorAvoidsRepeatedCalls() {
        FakeProvider real = new FakeProvider();
        AIProvider withCache = new TimedAIProvider(new CachedAIProvider(real, new ConcurrentHashMap<>()));
        String a = withCache.answer("¿Dónde queda la biblioteca?", EMPTY);
        String b = withCache.answer("donde queda la BIBLIOTECA", EMPTY);
        assertThat(a).isEqualTo(b);
        assertThat(real.calls).isEqualTo(1);
        assertThat(withCache.name()).isEqualTo("fake");
    }

    @Test
    void proxyRejectsWhenQuotaIsExhausted() {
        FakeProvider real = new FakeProvider();
        AIProviderProxy proxy = new AIProviderProxy(real, 2);
        proxy.answer("one", EMPTY);
        proxy.answer("two", EMPTY);
        assertThatThrownBy(() -> proxy.answer("three", EMPTY)).isInstanceOf(AIProviderProxy.QuotaExceededException.class);
        assertThat(real.calls).isEqualTo(2);                            // the third one never reached the real provider
    }

    @Test
    void bridgeCombinesAnyProfileWithAnyProvider() {
        FakeProvider real = new FakeProvider();
        AssistantProfile visitor = AssistantProfile.forSession(new Session(null, "Invitado", "GUEST"), real);
        AssistantProfile student = AssistantProfile.forSession(new Session(7, "Laura Meza", "STUDENT"), real);
        assertThat(visitor.type()).isEqualTo("visitor");
        assertThat(student.type()).isEqualTo("student");
        visitor.answer("hola", EMPTY);
        student.answer("hola", EMPTY);
        assertThat(real.hints.get(0)).contains("VISITOR");
        assertThat(real.hints.get(1)).contains("Laura").contains("STUDENT");
        // the same profile also works with another provider (the local AI)
        assertThat(new VisitorProfile(new LocalAIProvider()).type()).isEqualTo("visitor");
    }

    @Test
    void adapterTranslatesGeminiToTheSystemInterface() {
        AIProvider adapted = new GeminiAIProvider("", "gemini-3.5-flash");
        assertThat(adapted.name()).isEqualTo("gemini");
        assertThat(adapted.available()).isFalse();
    }
}
