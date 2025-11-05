package Domain;

import Models.Scrim;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Map;
// Patrón Builder: Creación segura de Scrim
public class ScrimBuilder {
    private final Scrim scrim = new Scrim();

    public ScrimBuilder(UUID organizadorId) {
        scrim.setOrganizadorId(organizadorId);
        // Estado inicial obligatorio
        scrim.setEstado("BUSCANDO");
    }

    public ScrimBuilder conJuego(String juegoId, String formato) {
        scrim.setJuegoId(juegoId);
        scrim.setFormato(formato);
        return this;
    }

    public ScrimBuilder conRegion(String regionId) {
        scrim.setRegionId(regionId);
        return this;
    }

    public ScrimBuilder conRangos(int min, int max) {
        scrim.setRangoMin(min);
        scrim.setRangoMax(max);
        return this;
    }
    
    public ScrimBuilder conLatencia(int latenciaMaxMs) {
        scrim.setLatenciaMaxMs(latenciaMaxMs);
        return this;
    }
    public ScrimBuilder conModalidad(String modalidad) {
        scrim.setModalidad(modalidad);
        return this;
    }
    public ScrimBuilder conCupos(int cupos) {
        if (cupos <= 0) throw new IllegalArgumentException("Cupos deben ser > 0");
        scrim.setCupos(cupos);
        return this;
    }
    
    public ScrimBuilder conFecha(LocalDateTime fechaHora, int duracionMin) {
        scrim.setFechaHora(fechaHora);
        scrim.setDuracionEstimadaMin(duracionMin);
        return this;
    }

    public ScrimBuilder conRoles(Map<String, Integer> rolesRequeridos) {
        if (rolesRequeridos != null) {
            scrim.setRolesRequeridos(rolesRequeridos);
        }
        return this;
    }

    // Paso final de la construcción con validaciones
    public Scrim build() {
        if (scrim.getJuegoId() == null || scrim.getFormato() == null) {
            throw new IllegalStateException("El Scrim debe tener Juego y Formato definidos.");
        }
        if (scrim.getCupos() == 0) scrim.setCupos(10); // Valor por defecto si no se setea
        return scrim;
    }
}