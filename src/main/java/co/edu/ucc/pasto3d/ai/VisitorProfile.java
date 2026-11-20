package co.edu.ucc.pasto3d.ai;

/** Refined abstraction of the BRIDGE: visitors and applicants who come in as guests. */
public class VisitorProfile extends AssistantProfile {

    public VisitorProfile(AIProvider implementation) { super(implementation); }

    @Override public String type() { return "visitor"; }

    @Override
    protected String hints() {
        return """
            The person asking is a VISITOR or applicant who does not know the campus.
            Explain how to get there (block and floor) and, if they ask about a program, mention that
            enrollment is done at Admisiones (DARC), Bloque B piso 1.""";
    }
}
