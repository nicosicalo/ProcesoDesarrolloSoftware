package Infraestructura;
import Models.Postulacion;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PostulacionRepository {
    private final Map<UUID, Postulacion> byId = new ConcurrentHashMap<>();
 
    public void save(Postulacion p) {
        byId.put(p.getId(), p);
        System.out.println("DB_SIM: Postulación " + p.getId() + " guardada en el sistema.");
    }

    public List<Postulacion> findByUsuarioId(UUID usuarioId) {
        return byId.values().stream()
                .filter(p -> p.getUsuarioId().equals(usuarioId))
                .collect(Collectors.toList());
    }
    
    public Optional<Postulacion> findById(UUID id) {
        return Optional.ofNullable(byId.get(id));
    }
    public List<Postulacion> findByScrimId(UUID scrimId) {
        return byId.values().stream()
                .filter(p -> p.getScrimId().equals(scrimId))
                .collect(Collectors.toList());
    }
}
