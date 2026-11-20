package co.edu.ucc.pasto3d.ai;

/** Formatter of the Local family: its answers are already plain text, only spaces are cleaned. */
public class PlainTextFormatter implements AnswerFormatter {
    @Override
    public String format(String t) {
        return t == null ? "" : t.replaceAll("[ \\t]+\\n", "\n").trim();
    }
}
