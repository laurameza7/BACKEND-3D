package co.edu.ucc.pasto3d.security;

/** Who is making the request. userId is null for guests. */
public record Session(Integer userId, String name, String role) {
    public boolean isGuest() { return userId == null; }
}
