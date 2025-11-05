package Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import Enums.ScrimStatus;
import Models.Scrim;
import ScrimsLifecycle.context.ScrimContext;

/**
 * Servicio de aplicación para manejar scrims.
 */
public class ScrimLifecycleService {

    // simula una "BD" en memoria. CAMBIO: La clave es UUID, no Long.
    private Map<UUID, ScrimContext> scrims = new HashMap<>();

    // ELIMINADO: private long seq = 1L; (El Scrim ahora genera su propio UUID)

    /**
     * Registra un Scrim (creado por ScrimAppService) en el sistema de ciclo de vida.
     * Este método REEMPLAZA al antiguo 'crearScrim' de la branch 3.
     */
    public void registrarNuevoScrim(Scrim scrim) {
        ScrimContext ctx = new ScrimContext(scrim);
        scrims.put(scrim.getId(), ctx);
        System.out.println("[LIFECYCLE] Scrim " + scrim.getId() + " registrado. Estado inicial: " + scrim.getStatus());
    }

    /**
     * CAMBIO: Los parámetros ahora son UUID y String (para el ID de Usuario)
     */
    public void postular(UUID scrimId, String userId) {
        ScrimContext ctx = scrims.get(scrimId);
        if (ctx == null) throw new IllegalArgumentException("Scrim no existe");
        ctx.postular(Long.valueOf(userId));
    }

    /**
     * CAMBIO: Los parámetros ahora son UUID y String
     */
    public void confirmar(UUID scrimId, String userId) {
        ScrimContext ctx = scrims.get(scrimId);
        if (ctx == null) throw new IllegalArgumentException("Scrim no existe");
        ctx.confirmar(Long.valueOf(userId));
    }

    /**
     * CAMBIO: El parámetro ahora es UUID
     */
    public void cancelar(UUID scrimId) {
        ScrimContext ctx = scrims.get(scrimId);
        if (ctx == null) throw new IllegalArgumentException("Scrim no existe");
        ctx.cancelar();
    }

    /**
     * CAMBIO: El parámetro ahora es UUID
     */
    public void finalizar(UUID scrimId) {
        ScrimContext ctx = scrims.get(scrimId);
        if (ctx == null) throw new IllegalArgumentException("Scrim no existe");
        ctx.finalizar();
    }

    /**
     * Lo llama el scheduler. (Esta lógica no necesita cambios)
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

    /**
     * CAMBIO: El parámetro ahora es UUID
     */
    public ScrimContext getContext(Long scrimId) {
        return scrims.get(scrimId);
    }
}