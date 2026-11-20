package co.edu.ucc.pasto3d.ai;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * SINGLETON pattern: a single instance in the whole application keeps Coopi's metrics
 * (questions per provider, times, answers from the cache, quota rejections).
 * Private constructor + static "holder": the instance is created only once, lazily
 * and thread-safely. It is shown in GET /api/health.
 */
public final class AssistantMetrics {

    private final Map<String, AtomicLong> questions = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> milliseconds = new ConcurrentHashMap<>();
    private final AtomicLong fromCache = new AtomicLong();
    private final AtomicLong rejectedByQuota = new AtomicLong();

    private AssistantMetrics() { }

    private static final class Holder {
        private static final AssistantMetrics INSTANCE = new AssistantMetrics();
    }

    public static AssistantMetrics instance() { return Holder.INSTANCE; }

    public void register(String provider, long ms) {
        questions.computeIfAbsent(provider, k -> new AtomicLong()).incrementAndGet();
        milliseconds.computeIfAbsent(provider, k -> new AtomicLong()).addAndGet(ms);
    }
    public void recordCacheHit() { fromCache.incrementAndGet(); }
    public void recordQuotaRejection() { rejectedByQuota.incrementAndGet(); }

    public long questionsFrom(String provider) {
        AtomicLong n = questions.get(provider);
        return n == null ? 0 : n.get();
    }

    public Map<String, Object> summary() {
        Map<String, Object> r = new LinkedHashMap<>();
        questions.forEach((p, n) -> r.put(p, Map.of(
                "answers", n.get(),
                "averageMs", n.get() == 0 ? 0 : milliseconds.get(p).get() / n.get())));
        r.put("fromCache", fromCache.get());
        r.put("rejectedByQuota", rejectedByQuota.get());
        return r;
    }
}
