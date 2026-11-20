package co.edu.ucc.pasto3d.ai;

import co.edu.ucc.pasto3d.dto.Dtos.*;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.stream.Collectors;

import static co.edu.ucc.pasto3d.ai.TextUtil.floorName;
import static co.edu.ucc.pasto3d.ai.TextUtil.normalize;

/**
 * Fallback strategy: answers by searching matches in the database
 * (programs, places and frequently asked questions). It works without internet
 * or API key, so the application is never left without an answer.
 */
@Component
public class LocalAIProvider implements AIProvider {

    @Override public String name() { return "local"; }

    @Override public boolean available() { return true; }

    @Override
    public String answer(String question, CampusContext c) {
        String q = " " + normalize(question) + " ";

        if (q.matches(".*\\b(hola|buenas|buenos dias|buenas tardes|hey)\\b.*") && q.length() < 25)
            return "¡Hola! Soy Coopi, el asistente del campus Pasto de la UCC. Pregúntame por programas, "
                 + "oficinas, laboratorios, pagos o cómo llegar a un bloque.";

        if (q.matches(".*\\b(programas|carreras|que estudiar|ofrecen|oferta)\\b.*"))
            return "En el campus Pasto se ofrecen estos programas:\n" + c.programs().stream()
                    .map(p -> "• " + p.name() + " (" + p.faculty() + ")")
                    .collect(Collectors.joining("\n"));

        // Is it asking about a specific program?
        for (ProgramDTO p : c.programs()) {
            String name = normalize(p.name());
            String password = name.replace("ingenieria de ", "").replace("ingenieria ", "");
            if (q.contains(" " + name + " ") || q.contains(" " + password + " "))
                return p.name() + ": " + p.description() + " Modalidad " + TextUtil.modalityName(p.modality())
                     + (p.durationSemesters() != null ? ", " + p.durationSemesters() + " semestres" : "")
                     + ". Título: " + p.degreeTitle() + ". Pertenece a la " + p.faculty()
                     + (p.building() != null ? " y sus clases se dictan principalmente en el " + p.building() : "")
                     + ". Para inscribirte entra a www.ucc.edu.co o comunícate con Admisiones (602 7370660 ext. 2312).";
        }

        // Best FAQ according to its keywords
        FaqDTO bestFaq = null;
        int bestScore = 0;
        for (FaqDTO f : c.questions()) {
            int score = score(q, f.question()) + keywordMatches(q, f.keywords());
            if (score > bestScore) { bestScore = score; bestFaq = f; }
        }

        // Best place by name
        PlaceDTO bestPlace = c.places().stream()
                .max(Comparator.comparingInt(l -> score(q, l.name())))
                .filter(l -> score(q, l.name()) >= 1)
                .orElse(null);

        if (bestPlace != null && score(q, bestPlace.name()) >= bestScore) {
            StringBuilder sb = new StringBuilder(bestPlace.name()).append(" queda en el ")
                    .append(bestPlace.building()).append(", ").append(floorName(bestPlace.floor())).append('.');
            if (bestPlace.description() != null)
                sb.append(' ').append(bestPlace.description()).append(bestPlace.description().endsWith(".") ? "" : ".");
            if (bestPlace.schedule() != null) sb.append(" Horario: ").append(bestPlace.schedule()).append('.');
            if (bestPlace.phone() != null) sb.append(" Teléfono: ").append(bestPlace.phone()).append('.');
            return sb.toString();
        }
        if (bestFaq != null) return bestFaq.answer();

        for (BuildingDTO e : c.buildings())
            if (score(q, e.name()) >= 2 || q.contains(" " + normalize(e.name()) + " "))
                return e.name() + ": " + e.description();

        return "No encontré esa información en mi base de conocimiento. Puedes llamar al conmutador "
             + "602 7370660 (Admisiones, ext. 2312).";
    }

    /** Counts the meaningful words (4+ letters) of 'text' that appear in the question. */
    private int score(String normalizedQuestion, String text) {
        int p = 0;
        for (String word : normalize(text).split(" "))
            if (word.length() >= 4 && normalizedQuestion.contains(stem(word))) p++;
        return p;
    }

    /** Every FAQ keyword that appears complete in the question adds 2 points. */
    private int keywordMatches(String normalizedQuestion, String keys) {
        if (keys == null) return 0;
        int p = 0;
        for (String password : keys.split(","))
            if (!password.isBlank() && normalizedQuestion.contains(" " + normalize(password) + " ")) p += 2;
        return p;
    }

    private String stem(String word) {
        return word.length() > 6 ? word.substring(0, word.length() - 2) : word;
    }
}






