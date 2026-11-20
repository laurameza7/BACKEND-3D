package co.edu.ucc.pasto3d.ai;

/**
 * ABSTRACT FACTORY pattern: each AI family creates products that belong together:
 * its provider (already protected and decorated) and the formatter that matches its answers.
 * This way the local provider is never mixed with the Gemini formatter, for example.
 */
public interface AIFactory {
    String family();
    boolean available();
    AIProvider createProvider();
    AnswerFormatter createFormatter();
}
