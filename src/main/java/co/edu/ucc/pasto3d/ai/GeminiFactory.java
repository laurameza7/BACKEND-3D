package co.edu.ucc.pasto3d.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * "gemini" family of the ABSTRACT FACTORY.
 * Builds: Timing( Cache( Quota proxy( Gemini adapter ) ) ).
 */
@Component
public class GeminiFactory implements AIFactory {

    private final GeminiAIProvider gemini;
    private final AIProviderProxy proxy;                         // reused: it keeps the quota window
    private final Map<String, CachedAIProvider.CacheEntry> cache = new ConcurrentHashMap<>();

    public GeminiFactory(GeminiAIProvider gemini,
                         @Value("${ai.gemini.limit-per-minute:30}") int limitPerMinute) {
        this.gemini = gemini;
        this.proxy = new AIProviderProxy(gemini, limitPerMinute);
    }

    @Override public String family() { return "gemini"; }
    @Override public boolean available() { return gemini.available(); }

    @Override
    public AIProvider createProvider() {
        return new TimedAIProvider(new CachedAIProvider(proxy, cache));
    }

    @Override
    public AnswerFormatter createFormatter() { return new MarkdownFormatter(); }
}
