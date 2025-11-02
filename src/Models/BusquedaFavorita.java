package Models;

import Service.FiltrosBusqueda;
import java.util.UUID;

public class BusquedaFavorita {
    private final UUID id = UUID.randomUUID();
    private final UUID usuarioId;
    private final String nombre;
    private final FiltrosBusqueda filtros; // Contiene los criterios de búsqueda

    public BusquedaFavorita(UUID usuarioId, String nombre, FiltrosBusqueda filtros) {
        this.usuarioId = usuarioId;
        this.nombre = nombre;
        this.filtros = filtros;
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public String getNombre() {
        return nombre;
    }

    public FiltrosBusqueda getFiltros() {
        return filtros;
    }
}
