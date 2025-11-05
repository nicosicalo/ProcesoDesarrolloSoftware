package ScrimsLifecycle.state;

import ScrimsLifecycle.context.ScrimContext;

/**
 * Estado: FINALIZADO
 * - No acepta nada.
 */
public class FinalizadoState implements ScrimState {

    @Override
    public void postular(ScrimContext ctx, String userId) {
        throw new IllegalStateException("Scrim finalizado.");
    }

    @Override
    public void confirmar(ScrimContext ctx, String userId) { }

    @Override
    public void iniciar(ScrimContext ctx) { }

    @Override
    public void finalizar(ScrimContext ctx) { }

    @Override
    public void cancelar(ScrimContext ctx) { }
}
