package Models;

import java.time.LocalDateTime;
import java.util.UUID;

// Asumimos que la transición de estados la manejará ScrimContext (Integrante 3)
public class Scrim {
    private final UUID id = UUID.randomUUID();
    private String juegoId;
    private String formato; 
    private String regionId; 
    private int rangoMin;
    private int rangoMax;
    private int latenciaMaxMs;
    private LocalDateTime fechaHora;
    private int duracionEstimadaMin; 
    private String estado; 
    private int cupos;
    private UUID organizadorId;

    // Getters y Setters (Necesarios para el Builder y persistencia)
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
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public int getCupos() { return cupos; }
    public void setCupos(int cupos) { this.cupos = cupos; }
    public UUID getOrganizadorId() { return organizadorId; }
    public void setOrganizadorId(UUID organizadorId) { this.organizadorId = organizadorId; }
}