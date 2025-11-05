package ScrimsLifecycle.state;

import ScrimsLifecycle.context.ScrimContext;

/**
 * Estado: CANCELADO
 * - "pozo" del que no se vuelve.
 */
public class CanceladoState implements ScrimState {

    @Override
    public void postular(ScrimContext ctx, Long userId) {
        throw new IllegalStateException("Scrim cancelado.");
    }

    @Override
    public void confirmar(ScrimContext ctx, Long userId) {
        throw new IllegalStateException("Scrim cancelado.");
    }

    @Override
    public void iniciar(ScrimContext ctx) {
        throw new IllegalStateException("Scrim cancelado.");
    }

    @Override
    public void finalizar(ScrimContext ctx) {
        // nada
    }

    @Override
    public void cancelar(ScrimContext ctx) {
        // ya está
    }
}
