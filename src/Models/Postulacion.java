package Models;

import java.util.UUID;

public class Postulacion {
    private final UUID id = UUID.randomUUID();
    private UUID usuarioId;
    private UUID scrimId;
    private String rolDeseado; 
    private String estado; 

    public Postulacion(UUID usuarioId, UUID scrimId, String rolDeseado) {
        this.usuarioId = usuarioId;
        this.scrimId = scrimId;
        this.rolDeseado = rolDeseado;
        this.estado = "PENDIENTE";
    }

    // Getters y Setters
    public UUID getId() { return id; }
    public UUID getUsuarioId() { return usuarioId; }
    public UUID getScrimId() { return scrimId; }
    public String getRolDeseado() { return rolDeseado; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}