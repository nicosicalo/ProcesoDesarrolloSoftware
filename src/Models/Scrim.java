package Models;

import Enums.ScrimStatus;

import java.time.LocalDateTime;

import java.util.*;

// Asumimos que la transición de estados la manejará ScrimContext (Integrante 3)
public class Scrim {

    // --- Campos de tu Branch 2 (Búsqueda/Creación) ---
    private final UUID id = UUID.randomUUID();
    private String juegoId;
    private String formato;
    private String regionId;
    private int rangoMin;
    private int rangoMax;
    private int latenciaMaxMs;
    private LocalDateTime fechaHora; // <-- Este campo estaba en ambas
    private int duracionEstimadaMin;
    private int cupos; // <-- Tu branch lo llama 'cupos', la 3 'cupoMaximo'. Dejamos 'cupos'.
    private UUID organizadorId;
    private String modalidad;
    private Map<String, Integer> rolesRequeridos = new HashMap<>();

    // --- Campos fusionados de la Branch 3 (Ciclo de Vida) ---
    private ScrimStatus status; // <-- Reemplaza tu 'private String estado;'
    private Set<String> jugadores = new HashSet<>(); // <-- Añadido. Se usa String para el UUID del Usuario.


    // Constructor vacío (necesario para el ScrimBuilder)
    public Scrim() {}

    // --- Getters y Setters ---

    public UUID getId() { return id; }

    public String getJuegoId() { return juegoId; }
    public void setJuegoId(String juegoId) { this.juegoId = juegoId; }

    public String getFormato() { return formato; }
    public void setFormato(String formato) { this.formato = formato; }

    public String getRegionId() { return regionId; }
    public void setRegionId(String regionId) { this.regionId = regionId; }

    public int getRangoMin() { return rangoMin; }
    public void setRangoMin(int rangoMin) { this.rangoMin = rangoMin; }

    public int getRangoMax() { return rangoMax; }
    public void setRangoMax(int rangoMax) { this.rangoMax = rangoMax; }

    public int getLatenciaMaxMs() { return latenciaMaxMs; }
    public void setLatenciaMaxMs(int latenciaMaxMs) { this.latenciaMaxMs = latenciaMaxMs; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public int getDuracionEstimadaMin() { return duracionEstimadaMin; }
    public void setDuracionEstimadaMin(int duracionEstimadaMin) { this.duracionEstimadaMin = duracionEstimadaMin; }

    public int getCupos() { return cupos; }
    public void setCupos(int cupos) { this.cupos = cupos; }

    public UUID getOrganizadorId() { return organizadorId; }
    public void setOrganizadorId(UUID organizadorId) { this.organizadorId = organizadorId; }

    public String getModalidad() { return modalidad; }
    public void setModalidad(String modalidad) { this.modalidad = modalidad; }

    public Map<String, Integer> getRolesRequeridos() { return rolesRequeridos; }
    public void setRolesRequeridos(Map<String, Integer> rolesRequeridos) { this.rolesRequeridos = rolesRequeridos; }

    // --- Getters/Setters del Ciclo de Vida (fusionados) ---

    public ScrimStatus getStatus() { return status; }
    public void setStatus(ScrimStatus status) { this.status = status; }

    public Set<String> getJugadores() { return jugadores; }
    public void setJugadores(Set<String> jugadores) { this.jugadores = jugadores; }
}
