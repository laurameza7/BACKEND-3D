package co.edu.ucc.pasto3d.ai;

import org.springframework.stereotype.Component;

/** "local" family of the ABSTRACT FACTORY: answers from the database, without internet. */
@Component
public class LocalFactory implements AIFactory {

    private final LocalAIProvider local;

    public LocalFactory(LocalAIProvider local) { this.local = local; }

    @Override public String family() { return "local"; }
    @Override public boolean available() { return true; }
    @Override public AIProvider createProvider() { return new TimedAIProvider(local); }
    @Override public AnswerFormatter createFormatter() { return new PlainTextFormatter(); }
}
