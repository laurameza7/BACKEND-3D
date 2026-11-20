package co.edu.ucc.pasto3d.security;

import co.edu.ucc.pasto3d.dto.Dtos.SessionDTO;

/**
 * FACTORY METHOD pattern: the process to start a session is always the same
 * (create the session → sign the token → build the response), but WHICH session is created
 * is decided by each subclass in its factory method {@link #createSession()}:
 * {@link UserSessionCreator} for registered accounts and {@link GuestSessionCreator} for guests.
 */
public abstract class SessionCreator {

    /** Factory method: each subclass creates its own type of session. */
    protected abstract Session createSession();

    /** Extra data that only some types of session have. */
    protected String email() { return null; }

    /** Common operation that uses the product created by the subclass. */
    public final SessionDTO start(TokenService tokens) {
        Session s = createSession();
        return new SessionDTO(tokens.issue(s), s.userId(), s.name(), email(), s.role());
    }
}
