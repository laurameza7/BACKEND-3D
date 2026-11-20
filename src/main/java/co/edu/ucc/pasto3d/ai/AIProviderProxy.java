package co.edu.ucc.pasto3d.ai;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * PROXY pattern (protection proxy): it stands in front of the real provider (Gemini) and CONTROLS ACCESS.
 * It only lets a maximum number of requests per minute through (the free API has a quota).
 * When the limit is exceeded it rejects the request without calling Gemini and the facade answers with the local AI.
 * The rest of the system does not notice: the proxy has the same interface as the real provider.
 */
public class AIProviderProxy implements AIProvider {

    /** The request was not sent because the quota of the minute ran out. */
    public static class QuotaExceededException extends RuntimeException {
        public QuotaExceededException(String m) { super(m); }
    }

    private final AIProvider real;
    private final int limitPerMinute;
    private final Deque<Long> calls = new ArrayDeque<>();

    public AIProviderProxy(AIProvider real, int limitPerMinute) {
        this.real = real;
        this.limitPerMinute = limitPerMinute;
    }

    @Override public String name() { return real.name(); }
    @Override public boolean available() { return real.available(); }

    @Override
    public String answer(String question, CampusContext context) {
        return answer(question, context, null);
    }

    @Override
    public String answer(String question, CampusContext context, String hints) {
        if (!real.available()) throw new IllegalStateException("The provider " + real.name() + " is not configured");
        reserveSlot();
        return real.answer(question, context, hints);
    }

    private synchronized void reserveSlot() {
        long now = System.currentTimeMillis();
        while (!calls.isEmpty() && now - calls.peekFirst() > 60_000) calls.pollFirst();
        if (calls.size() >= limitPerMinute) {
            AssistantMetrics.instance().recordQuotaRejection();
            throw new QuotaExceededException("Quota of " + limitPerMinute + " questions per minute exhausted");
        }
        calls.addLast(now);
    }
}
