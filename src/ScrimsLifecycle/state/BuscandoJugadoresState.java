package ScrimsLifecycle.state;

import java.time.LocalDateTime;

import Enums.ScrimStatus;
import Models.Scrim;
import ScrimsLifecycle.context.ScrimContext;

/**
 * Estado: BUSCANDO
 * - Permite postular jugadores.
 * - Cuando se llena el cupo -> pasa a LOBBY_ARMADO.
 * - Se puede cancelar.
 */
public class BuscandoJugadoresState implements ScrimState {

    @Override
    public void postular(ScrimContext ctx, Long userId) {
        Scrim scrim = ctx.getScrim();

        // si ya estaba, no lo agrego de nuevo
        if (scrim.getJugadores().contains(userId)) {
            return;
        }

        // lo agrego al conjunto
        scrim.getJugadores().add(userId);

        // ¿se llenó?
        if (scrim.getJugadores().size() >= scrim.getCupoMaximo()) {
            // pasamos a Lobby
            ctx.setState(new LobbyArmadoState(), ScrimStatus.LOBBY_ARMADO);
        }
    }

    @Override
    public void confirmar(ScrimContext ctx, Long userId) {
        throw new IllegalStateException("No se puede confirmar mientras está BUSCANDO.");
    }

    @Override
    public void iniciar(ScrimContext ctx) {
        throw new IllegalStateException("No se puede iniciar: el scrim todavía está BUSCANDO.");
    }

    @Override
    public void finalizar(ScrimContext ctx) {
        throw new IllegalStateException("No se puede finalizar un scrim que está BUSCANDO.");
    }

    @Override
    public void cancelar(ScrimContext ctx) {
        ctx.setState(new CanceladoState(), ScrimStatus.CANCELADO);
    }

    @Override
    public void onTimeReached(ScrimContext ctx, LocalDateTime now) {
        // Podrías cancelar por timeout acá.
    }
}
