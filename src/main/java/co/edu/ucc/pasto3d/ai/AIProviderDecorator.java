package co.edu.ucc.pasto3d.ai;

/**
 * DECORATOR pattern (base): wraps an AIProvider and adds behavior to it
 * without modifying its class. Decorators can be stacked.
 */
public abstract class AIProviderDecorator implements AIProvider {

    protected final AIProvider wrapped;

    protected AIProviderDecorator(AIProvider wrapped) { this.wrapped = wrapped; }

    @Override public String name() { return wrapped.name(); }
    @Override public boolean available() { return wrapped.available(); }

    @Override
    public String answer(String question, CampusContext context) {
        return answer(question, context, null);
    }
}
