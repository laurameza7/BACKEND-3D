package co.edu.ucc.pasto3d.ai;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

/**
 * DECORATOR: if the same question (with the same profile) was asked less than 30 minutes ago,
 * it returns the stored answer without calling the AI again: faster and it saves quota.
 */
public class CachedAIProvider extends AIProviderDecorator {

    public record CacheEntry(String answer, Instant created) { }

    static final Duration VALIDITY = Duration.ofMinutes(30);
    private static final int MAX_ENTRIES = 300;
    private final Map<String, CacheEntry> cache;

    public CachedAIProvider(AIProvider wrapped, Map<String, CacheEntry> cache) {
        super(wrapped);
        this.cache = cache;
    }

    @Override
    public String answer(String question, CampusContext context, String hints) {
        String password = TextUtil.normalize(question) + "|" + (hints == null ? "" : hints);
        CacheEntry e = cache.get(password);
        if (e != null && e.created().plus(VALIDITY).isAfter(Instant.now())) {
            AssistantMetrics.instance().recordCacheHit();
            return e.answer();
        }
        String r = wrapped.answer(question, context, hints);
        if (cache.size() >= MAX_ENTRIES) cache.clear();
        cache.put(password, new CacheEntry(r, Instant.now()));
        return r;
    }
}
