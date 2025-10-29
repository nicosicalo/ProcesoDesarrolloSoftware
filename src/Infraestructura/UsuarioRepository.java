package Infraestructura;

import Models.Usuario;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

//esto va a ser persistencia en memoria local, simulada.
//elegimos esto para no tener que tener un backend corriendo y una base de datos conectada para el tema persistencia
public class UsuarioRepository {
    private final Map<String, Usuario> byId = new ConcurrentHashMap<>();
    private final Map<String, Usuario> byEmail = new ConcurrentHashMap<>();
    private final Map<String, Usuario> byUsername = new ConcurrentHashMap<>();

    public void save(Usuario u) {
        byId.put(u.getId(), u); // Nuevo: Mapeo por ID
        byEmail.put(u.getEmail().toLowerCase(), u);
        byUsername.put(u.getUsername().toLowerCase(), u);
    }

    public Optional<Usuario> findByEmail(String email) {
        if (email == null) return Optional.empty();
        return Optional.ofNullable(byEmail.get(email.toLowerCase()));
    }

    public Optional<Usuario> findByUsername(String username) {
        if (username == null) return Optional.empty();
        return Optional.ofNullable(byUsername.get(username.toLowerCase()));
    }

    // Nuevo método para que ProfileService y SessionManager puedan buscar por el ID
    public Optional<Usuario> findByAnyId(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(byId.get(id));
    }
}