package co.edu.ucc.pasto3d.ai;

/** DECORATOR: measures how long each answer takes and records it in the metrics (SINGLETON). */
public class TimedAIProvider extends AIProviderDecorator {

    public TimedAIProvider(AIProvider wrapped) { super(wrapped); }

    @Override
    public String answer(String question, CampusContext context, String hints) {
        long start = System.nanoTime();
        String r = wrapped.answer(question, context, hints);
        AssistantMetrics.instance().register(name(), (System.nanoTime() - start) / 1_000_000);
        return r;
    }
}
