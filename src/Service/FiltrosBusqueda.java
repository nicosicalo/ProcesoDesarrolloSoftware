package Service;

import java.time.LocalDateTime;

public record FiltrosBusqueda(
        String juegoId,
        String formato,
        String regionId,
        int rangoMin,
        int rangoMax,
        int latenciaMaxMs,
        LocalDateTime fechaHora
) {

    public FiltrosBusqueda() {
        this(null, null, null, 0, 10000, 999, null);
    }
}