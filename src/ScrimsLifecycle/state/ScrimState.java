package ScrimsLifecycle.state;

import java.time.LocalDateTime;

import ScrimsLifecycle.context.ScrimContext;

/**
 * Interfaz del PATRÓN STATE.
 * Todas las clases de estado concreto (Buscando, Lobby, Confirmado, etc.)
 * van a implementar ESTO.
 */
public interface ScrimState {

    /**
     * Un jugador quiere entrar al scrim.
     */
    void postular(ScrimContext ctx, String userId);

    /**
     * Un jugador que ya está en el lobby confirma que va a jugar.
     */
    void confirmar(ScrimContext ctx, String userId);

    /**
     * El organizador / sistema intenta iniciar el scrim.
     */
    void iniciar(ScrimContext ctx);

    /**
     * El organizador / sistema intenta finalizar el scrim.
     */
    void finalizar(ScrimContext ctx);

    /**
     * Se cancela el scrim (antes de jugar).
     */
    void cancelar(ScrimContext ctx);

    /**
     * Hook para el SCHEDULER.
     * El scheduler va a llamar a esto cuando llegue la hora.
     * Por defecto no hace nada.
     */
    default void onTimeReached(ScrimContext ctx, LocalDateTime now) {
        // default: no hace nada
    }
}
