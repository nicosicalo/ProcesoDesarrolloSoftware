package Service;

import java.time.LocalDateTime;
import java.util.UUID;

public record ScrimCreationDTO(
        UUID organizadorId,
        String juegoId,
        String formato,
        String regionId,
        int rangoMin,
        int rangoMax,
        int cupos,
        LocalDateTime fechaHora,
        int duracionEstimadaMin
) { }