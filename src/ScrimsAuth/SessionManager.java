package ScrimsAuth;

import Models.Usuario;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager{
    private static final SessionManager INSTANCE = new SessionManager();
    private final Map<String, Usuario> sessions = new ConcurrentHashMap<>();

    private SessionManager() {}

    public static SessionManager getInstance() { return INSTANCE; }

    public String createSession(Usuario u) {
        String token = UUID.randomUUID().toString();
        sessions.put(token, u);
        return token;
    }

    public Optional<Usuario> getUser(String token) {
        return Optional.ofNullable(sessions.get(token));
    }

    public void invalidate(String token) {
        sessions.remove(token);
    }
}
