package Infraestructura;

import Models.Scrim;
import Service.FiltrosBusqueda;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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
                .collect(Collectors.toList());
    }

    public List<Scrim> findAll() {
        return byId.values().stream().toList();
    }
}