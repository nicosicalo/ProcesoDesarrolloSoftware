package Infraestructura;

import Models.BusquedaFavorita;
import Service.FiltrosBusqueda;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class BusquedaFavoritaRepository {
    private final List<BusquedaFavorita> busquedas = new ArrayList<>();

    public BusquedaFavoritaRepository() {
        // SIMULACIÓN: Precargar algunas búsquedas favoritas
        UUID user1Id = UUID.randomUUID(); // Asumimos un usuario ficticio
        UUID user2Id = UUID.randomUUID(); // Asumimos otro usuario ficticio

        // Búsqueda 1: Usuario 1 busca Valorant, 5v5, rango medio
        busquedas.add(new BusquedaFavorita(
                user1Id,
                "Mi scrim de Valorant",
                new FiltrosBusqueda("valorant", "5v5", "NA", 1500, 2000, 80, null)
        ));

        // Búsqueda 2: Usuario 2 busca cualquier cosa en BR con latencia baja
        busquedas.add(new BusquedaFavorita(
                user2Id,
                "Scrims con buen ping en BR",
                new FiltrosBusqueda(null, null, "BR", 0, 9999, 40, null)
        ));
    }

    /**
     * Devuelve todas las búsquedas guardadas para que el Subscriber las procese.
     */
    public List<BusquedaFavorita> findAll() {
        return busquedas;
    }
    public List<BusquedaFavorita> findByUsuarioId(UUID usuarioId) {
        return busquedas.stream()
                .filter(b -> b.getUsuarioId().equals(usuarioId))
                .collect(Collectors.toList());
    }
    
    // Método para simular el guardado desde el Controller/Service
    public void save(BusquedaFavorita busqueda) {
        this.busquedas.add(busqueda);
    }
}
