package co.edu.ucc.pasto3d.ai;

/** Formatter of the Gemini family: the model sometimes answers with Markdown and the chat shows plain text. */
public class MarkdownFormatter implements AnswerFormatter {
    @Override
    public String format(String t) {
        if (t == null) return "";
        return t.replace("**", "")
                .replace("__", "")
                .replaceAll("(?m)^#{1,6}[ \\t]*", "")              // headings
                .replaceAll("(?m)^[ \\t]*[*-][ \\t]+", "• ")         // bullets
                .replaceAll("(?<![\\w*])\\*([^*\\n]+)\\*(?![\\w*])", "$1")   // *italics*
                .replaceAll("`([^`\\n]+)`", "$1")                    // `code`
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
    }
}
