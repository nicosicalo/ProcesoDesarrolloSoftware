package Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import Enums.ScrimStatus;
import Models.Scrim;
import ScrimsLifecycle.context.ScrimContext;

/**
 * Servicio de aplicación para manejar scrims.
 */
public class ScrimLifecycleService {

    // simula una "BD" en memoria
    private Map<Long, ScrimContext> scrims = new HashMap<>();
    private long seq = 1L;

    public Long crearScrim(int cupoMaximo, LocalDateTime fechaHora) {
        Scrim scrim = new Scrim(cupoMaximo, fechaHora);
        scrim.setId(seq++);
        ScrimContext ctx = new ScrimContext(scrim);
        scrims.put(scrim.getId(), ctx);
        return scrim.getId();
    }

    public void postular(Long scrimId, Long userId) {
        ScrimContext ctx = scrims.get(scrimId);
        if (ctx == null) throw new IllegalArgumentException("Scrim no existe");
        ctx.postular(userId);
    }

    public void confirmar(Long scrimId, Long userId) {
        ScrimContext ctx = scrims.get(scrimId);
        if (ctx == null) throw new IllegalArgumentException("Scrim no existe");
        ctx.confirmar(userId);
    }

    public void cancelar(Long scrimId) {
        ScrimContext ctx = scrims.get(scrimId);
        if (ctx == null) throw new IllegalArgumentException("Scrim no existe");
        ctx.cancelar();
    }

    public void finalizar(Long scrimId) {
        ScrimContext ctx = scrims.get(scrimId);
        if (ctx == null) throw new IllegalArgumentException("Scrim no existe");
        ctx.finalizar();
    }

    /**
     * Lo llama el scheduler.
     */
    public void tick(LocalDateTime now) {
        for (ScrimContext ctx : scrims.values()) {
            // solo tiene sentido si no está finalizado o cancelado
            if (ctx.getScrim().getStatus() != ScrimStatus.FINALIZADO &&
                ctx.getScrim().getStatus() != ScrimStatus.CANCELADO) {
                ctx.onTimeReached(now);
            }
        }
    }

    public ScrimContext getContext(Long scrimId) {
        return scrims.get(scrimId);
    }
}
