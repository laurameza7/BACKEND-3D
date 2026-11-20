package co.edu.ucc.pasto3d.ai;

import co.edu.ucc.pasto3d.dto.Dtos.*;

import static co.edu.ucc.pasto3d.ai.TextUtil.floorName;

/**
 * BUILDER pattern: assembles step by step the instructions + knowledge text
 * that is sent to the language model.
 */
public class PromptContextBuilder {
    private final StringBuilder sb = new StringBuilder();

    public PromptContextBuilder instructions() {
        sb.append("""
            You are "Coopi", the virtual assistant of Universidad Cooperativa de Colombia, Pasto campus (Nariño).
            ALWAYS answer in Spanish, in a friendly, clear and short way (at most 6 sentences or a short list).
            Write plain text: do NOT use Markdown (no asterisks, # or bold). For lists start each line with "• ".
            Use ONLY the campus information below. If something is missing, say so honestly and
            suggest calling the switchboard 602 7370660 (Admisiones, ext. 2312).
            If the block or floor of an office or laboratory is not given, do NOT invent its location:
            give its phone extension instead. The campus has only two buildings: Bloque A and Bloque B.
            When the answer refers to a physical place, mention the exact block name (for example "Bloque A")
            so the 3D map can highlight it. Do not invent prices, dates or people's names.
            """).append('\n');
        return this;
    }

    public PromptContextBuilder buildings(CampusContext c) {
        sb.append("## Campus buildings\n");
        for (BuildingDTO e : c.buildings())
            sb.append("- ").append(e.name()).append(": ").append(e.description())
              .append(" (").append(e.floors()).append(" floors")
              .append(e.basements() > 0 ? ", " + e.basements() + " basements" : "").append(")\n");
        return this;
    }

    public PromptContextBuilder places(CampusContext c) {
        sb.append("\n## Offices, laboratories and services\n");
        for (PlaceDTO l : c.places()) {
            sb.append("- ").append(l.name()).append(" — ").append(l.building()).append(", ").append(floorName(l.floor()));
            if (l.description() != null) sb.append(". ").append(l.description());
            if (l.schedule() != null) sb.append(" Schedule: ").append(l.schedule()).append('.');
            if (l.phone() != null) sb.append(" Phone: ").append(l.phone()).append('.');
            if (l.email() != null) sb.append(" Email: ").append(l.email()).append('.');
            sb.append('\n');
        }
        return this;
    }

    public PromptContextBuilder programs(CampusContext c) {
        sb.append("\n## Academic programs\n");
        for (ProgramDTO p : c.programs())
            sb.append("- ").append(p.name()).append(" (").append(p.level()).append(", ").append(p.modality())
              .append(p.durationSemesters() != null ? ", " + p.durationSemesters() + " semesters" : "")
              .append(", degree: ").append(p.degreeTitle()).append(", ").append(p.faculty())
              .append(p.building() != null ? ", classes in " + p.building() : "")
              .append(p.place() != null ? ", its faculty is in " + p.location() + " (" + p.place() + ")" : "").append("). ")
              .append(p.description()).append('\n');
        return this;
    }

    public PromptContextBuilder faqs(CampusContext c) {
        sb.append("\n## Frequently asked questions\n");
        for (FaqDTO f : c.questions())
            sb.append("Q: ").append(f.question()).append("\nA: ").append(f.answer()).append('\n');
        return this;
    }

    public String build() {
        return sb.toString();
    }
}


