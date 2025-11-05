package ScrimsLifecycle.state;

import java.time.LocalDateTime;

import Enums.ScrimStatus;
import ScrimsLifecycle.context.ScrimContext;

/**
 * Estado: CONFIRMADO
 * - Todos confirmaron.
 * - Se puede iniciar MANUAL.
 * - O puede pasar a EN_JUEGO cuando el scheduler diga
 *   "ya llegamos a la hora del scrim".
 */
public class ConfirmadoState implements ScrimState {

    @Override
    public void postular(ScrimContext ctx, String userId) {
        throw new IllegalStateException("Scrim ya confirmado. No se aceptan nuevos jugadores.");
    }

    @Override
    public void confirmar(ScrimContext ctx, String userId) {
        // ya está confirmado -> ignoramos
    }

    @Override
    public void iniciar(ScrimContext ctx) {
        ctx.setState(new EnJuegoState(), ScrimStatus.EN_JUEGO);
    }

    @Override
    public void finalizar(ScrimContext ctx) {
        throw new IllegalStateException("No se puede finalizar sin empezar.");
    }

    @Override
    public void cancelar(ScrimContext ctx) {
        ctx.setState(new CanceladoState(), ScrimStatus.CANCELADO);
    }

    @Override
    public void onTimeReached(ScrimContext ctx, LocalDateTime now) {
        // si la hora del scrim ya llegó -> lo pasamos a EN_JUEGO
        if (!now.isBefore(ctx.getScrim().getFechaHora())) {
            ctx.setState(new EnJuegoState(), ScrimStatus.EN_JUEGO);
        }
    }
}
