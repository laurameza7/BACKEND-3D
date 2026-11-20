package co.edu.ucc.pasto3d.ai;

/** ABSTRACT FACTORY product: leaves the answer ready for the chat (plain text). */
public interface AnswerFormatter {
    String format(String answer);
}
