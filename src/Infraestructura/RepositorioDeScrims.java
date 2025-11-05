package Infraestructura;

import Models.Scrim;
import Service.FiltrosBusqueda;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

// Repositorio en memoria para la entidad Scrim
public class RepositorioDeScrims {
    private final Map<UUID, Scrim> byId = new ConcurrentHashMap<>();

    public void save(Scrim scrim) {
        byId.put(scrim.getId(), scrim);
    }

    public Optional<Scrim> findById(UUID id) {
        return Optional.ofNullable(byId.get(id));
    }

    // Lógica de búsqueda con filtros 
    public List<Scrim> findByFiltros(FiltrosBusqueda filtros) {
        return byId.values().stream()
                .filter(s -> filtros.juegoId() == null || s.getJuegoId().equalsIgnoreCase(filtros.juegoId()))
                .filter(s -> filtros.formato() == null || s.getFormato().equalsIgnoreCase(filtros.formato()))
                .filter(s -> filtros.regionId() == null || s.getRegionId().equalsIgnoreCase(filtros.regionId()))
                .filter(s -> s.getRangoMin() >= filtros.rangoMin() && s.getRangoMax() <= filtros.rangoMax())
                .filter(s -> s.getLatenciaMaxMs() <= filtros.latenciaMaxMs())
                .filter(s -> filtros.fechaHora() == null || s.getFechaHora().isAfter(filtros.fechaHora()))
                .collect(Collectors.toList());
    }

    public List<Scrim> findAll() {
        return byId.values().stream().toList();
    }
    public List<Scrim> findByIds(Set<UUID> ids) {
        return ids.stream()
                // 1. Mapea cada ID al objeto Scrim que tiene en el Map interno
                .map(byId::get) 
                // 2. Filtra IDs que no existen (devuelven null)
                .filter(scrim -> scrim != null) 
                // 3. Recolecta los resultados en una lista
                .collect(Collectors.toList());
    }
    public List<Scrim> findByOrganizadorId(UUID organizadorId) {
        return byId.values().stream()
                .filter(s -> s.getOrganizadorId().equals(organizadorId))
                .collect(Collectors.toList());
    }
}