package ScrimsLifecycle.state;

import Enums.ScrimStatus;
import ScrimsLifecycle.context.ScrimContext;

/**
 * Estado: EN_JUEGO
 */
public class EnJuegoState implements ScrimState {

    @Override
    public void postular(ScrimContext ctx, String userId) {
        throw new IllegalStateException("Scrim en juego.");
    }

    @Override
    public void confirmar(ScrimContext ctx, String userId) {
        // no hace nada
    }

    @Override
    public void iniciar(ScrimContext ctx) {
        // ya está
    }

    @Override
    public void finalizar(ScrimContext ctx) {
        ctx.setState(new FinalizadoState(), ScrimStatus.FINALIZADO);
    }

    @Override
    public void cancelar(ScrimContext ctx) {
        // en juego ya no se cancela
    }
}
