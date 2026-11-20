package co.edu.ucc.pasto3d.ai;

import co.edu.ucc.pasto3d.security.Session;

/**
 * BRIDGE pattern: separates two dimensions that change independently.
 *  - Abstraction: the PROFILE of the person asking (visitor or student) decides the focus of the answer.
 *  - Implementation: the AI PROVIDER (Gemini or local) that generates the text.
 * Any profile works with any provider (2 × 2 combinations) without a class for each one.
 */
public abstract class AssistantProfile {

    protected final AIProvider implementation;   // the "bridge"

    protected AssistantProfile(AIProvider implementation) { this.implementation = implementation; }

    public String answer(String question, CampusContext context) {
        return implementation.answer(question, context, hints());
    }

    /** What changes depending on the profile. */
    protected abstract String hints();

    public abstract String type();

    /** Chooses the profile from the session (guest or registered). */
    public static AssistantProfile forSession(Session session, AIProvider provider) {
        if (session == null || session.isGuest()) return new VisitorProfile(provider);
        return new StudentProfile(provider, session.name());
    }
}
