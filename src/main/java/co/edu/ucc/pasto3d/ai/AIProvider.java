package co.edu.ucc.pasto3d.ai;

/**
 * STRATEGY pattern: each AI provider is an interchangeable strategy
 * to answer questions about the campus.
 * It is also the "implementor" of the BRIDGE pattern (see {@link AssistantProfile}).
 */
public interface AIProvider {
    String name();
    boolean available();
    String answer(String question, CampusContext context);

    /**
     * Answers with extra hints about who is asking (added by the BRIDGE profile).
     * They are ignored by default; Gemini uses them in its instructions.
     */
    default String answer(String question, CampusContext context, String hints) {
        return answer(question, context);
    }
}
