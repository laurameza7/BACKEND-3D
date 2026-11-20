package co.edu.ucc.pasto3d.ai;

/** Refined abstraction of the BRIDGE: students with an account, whom Coopi calls by their name. */
public class StudentProfile extends AssistantProfile {

    private final String name;

    public StudentProfile(AIProvider implementation, String name) {
        super(implementation);
        this.name = name == null ? "" : name.split(" ")[0];
    }

    @Override public String type() { return "student"; }

    @Override
    protected String hints() {
        return "The person asking is " + name + ", a registered STUDENT of the university. Call them by their name "
             + "and get straight to the point (they already know the campus in general).";
    }
}
