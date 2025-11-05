package Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Map;

public record ScrimCreationDTO(
        UUID organizadorId,
        String juegoId,
        String formato,
        String regionId,
        int rangoMin,
        int rangoMax,
        int latenciaMaxMs, 
        String modalidad,
        int cupos,
        LocalDateTime fechaHora,
        int duracionEstimadaMin,
        Map<String, Integer> rolesRequeridos
) { }