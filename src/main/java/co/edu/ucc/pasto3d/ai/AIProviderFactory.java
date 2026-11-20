package co.edu.ucc.pasto3d.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Chooses which AI families (ABSTRACT FACTORY) to use from the configuration (AI_PROVIDER)
 * and their availability, in order of preference: if the first one fails, the next one answers.
 */
@Component
public class AIProviderFactory {

    private final GeminiFactory gemini;
    private final LocalFactory local;
    private final String preferred;

    public AIProviderFactory(GeminiFactory gemini, LocalFactory local,
                              @Value("${ai.provider:gemini}") String preferred) {
        this.gemini = gemini;
        this.local = local;
        this.preferred = preferred;
    }

    public List<AIFactory> createChain() {
        if ("gemini".equalsIgnoreCase(preferred) && gemini.available())
            return List.of(gemini, local);
        return List.of(local);
    }
}
