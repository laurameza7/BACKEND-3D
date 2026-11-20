package co.edu.ucc.pasto3d.ai;

import java.util.ArrayList;
import java.util.List;

/**
 * PROTOTYPE pattern: the template with all the campus knowledge (buildings, places,
 * programs, frequently asked questions) is expensive to build. It is built ONCE with the BUILDER
 * and, for every question, it is CLONED and the copy gets the data of that question
 * (profile of the person asking, date). The original prototype is never modified.
 */
public class PromptTemplate implements Cloneable {

    private List<String> sections;

    public PromptTemplate(String base) {
        this.sections = new ArrayList<>(List.of(base));
    }

    /** Clones the prototype (deep copy of the list of sections). */
    public PromptTemplate copy() {
        try {
            PromptTemplate copy = (PromptTemplate) super.clone();
            copy.sections = new ArrayList<>(this.sections);
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException(e);
        }
    }

    public PromptTemplate add(String section) {
        if (section != null && !section.isBlank()) sections.add(section);
        return this;
    }

    public int size() { return sections.size(); }

    public String text() { return String.join("\n\n", sections); }
}
