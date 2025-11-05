package Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import Models.Scrim;
import ScrimsLifecycle.context.ScrimContext;

/** Servicio de aplicación para manejar scrims. */
public class ScrimLifecycleService {

    // “BD” en memoria, indexada por ID de scrim (UUID)
    private final Map<UUID, ScrimContext> scrims = new HashMap<>();

    /** Crea un scrim básico compatible con Models.Scrim */
    public UUID crearScrim(int cupoMaximo, LocalDateTime fechaHora) {
        Scrim scrim = new Scrim();            // constructor vacío
        scrim.setCupos(cupoMaximo);
        scrim.setFechaHora(fechaHora);
        scrim.setEstado("Buscando jugadores");  // estado inicial (String)

        ScrimContext ctx = new ScrimContext(scrim);
        scrims.put(scrim.getId(), ctx);       // id lo genera Scrim (UUID)
        return scrim.getId();
    }

    public void postular(UUID scrimId, UUID userId) {
        ScrimContext ctx = scrims.get(scrimId);
        if (ctx == null) throw new IllegalArgumentException("Scrim no existe");
        ctx.postular(userId);                 // el contexto debe aceptar UUID
    }

    public void confirmar(UUID scrimId, UUID userId) {
        ScrimContext ctx = scrims.get(scrimId);
        if (ctx == null) throw new IllegalArgumentException("Scrim no existe");
        ctx.confirmar(userId);
    }

    public void cancelar(UUID scrimId) {
        ScrimContext ctx = scrims.get(scrimId);
        if (ctx == null) throw new IllegalArgumentException("Scrim no existe");
        ctx.cancelar();
    }

    public void finalizar(UUID scrimId) {
        ScrimContext ctx = scrims.get(scrimId);
        if (ctx == null) throw new IllegalArgumentException("Scrim no existe");
        ctx.finalizar();
    }

    /** Lo llama el scheduler. */
    public void tick(LocalDateTime now) {
        for (ScrimContext ctx : scrims.values()) {
            String estado = ctx.getScrim().getEstado();
            if (!isCerrado(estado)) {
                ctx.onTimeReached(now);
            }
        }
    }

    public ScrimContext getContext(UUID scrimId) {
        return scrims.get(scrimId);
    }

    // Finalizado/Cancelado (tolerante a mayúsculas/minúsculas/acentos)
    private boolean isCerrado(String estado) {
        if (estado == null) return false;
        String e = estado.trim().toLowerCase();
        return e.contains("finalizado") || e.contains("cancelado");
    }
}
