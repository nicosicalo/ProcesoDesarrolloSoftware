package ScrimsLifecycle.state;

import java.util.HashSet;
import java.util.Set;

import Enums.ScrimStatus;
import Models.Scrim;
import ScrimsLifecycle.context.ScrimContext;

/**
 * Estado: LOBBY_ARMADO
 * - Ya está el cupo completo.
 * - Cada jugador debe CONFIRMAR.
 * - Si confirman todos -> pasa a CONFIRMADO.
 */
public class LobbyArmadoState implements ScrimState {

    // en este ejemplo lo guardamos aquí
    // (después se puede mover a una entidad aparte)
    private Set<String> jugadoresConfirmados = new HashSet<>();

    @Override
    public void postular(ScrimContext ctx, String userId) {
        throw new IllegalStateException("El lobby ya está armado. No se aceptan más jugadores.");
    }

    @Override
    public void confirmar(ScrimContext ctx, String userId) {
        Scrim scrim = ctx.getScrim();

        if (!scrim.getJugadores().contains(userId)) {
            throw new IllegalStateException("Solo los jugadores del lobby pueden confirmar.");
        }

        jugadoresConfirmados.add(userId);

        // ¿confirmaron todos?
        if (jugadoresConfirmados.size() == scrim.getJugadores().size()) {
            ctx.setState(new ConfirmadoState(), ScrimStatus.CONFIRMADO);
        }
    }

    @Override
    public void iniciar(ScrimContext ctx) {
        throw new IllegalStateException("No se puede iniciar: faltan confirmaciones.");
    }

    @Override
    public void finalizar(ScrimContext ctx) {
        throw new IllegalStateException("No se puede finalizar desde Lobby.");
    }

    @Override
    public void cancelar(ScrimContext ctx) {
        ctx.setState(new CanceladoState(), ScrimStatus.CANCELADO);
    }
}
