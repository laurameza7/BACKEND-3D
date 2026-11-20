package co.edu.ucc.pasto3d.security;

/** Concrete creator: guest session (no account, no history). */
public class GuestSessionCreator extends SessionCreator {
    @Override
    protected Session createSession() { return new Session(null, "Invitado", "GUEST"); }
}
