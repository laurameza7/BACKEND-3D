package co.edu.ucc.pasto3d.security;

import co.edu.ucc.pasto3d.model.AppUser;

/** Concrete creator: session of a registered user (with their id, name, role and email). */
public class UserSessionCreator extends SessionCreator {

    private final AppUser user;

    public UserSessionCreator(AppUser user) { this.user = user; }

    @Override
    protected Session createSession() { return new Session(user.getId(), user.getName(), user.getRole()); }

    @Override
    protected String email() { return user.getEmail(); }
}
