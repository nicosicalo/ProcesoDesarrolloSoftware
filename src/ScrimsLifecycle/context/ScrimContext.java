package ScrimsLifecycle.context;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import Enums.ScrimStatus;
import Models.Scrim;
import ScrimsLifecycle.events.ScrimEventListener;
import ScrimsLifecycle.events.ScrimStateChangedEvent;
import ScrimsLifecycle.state.BuscandoJugadoresState;
import ScrimsLifecycle.state.ScrimState;

//CONTEXT del patrón State. Es la clase que usa el REST / Service. No hace lógica propia, DELEGA en el estado actual.

public class ScrimContext {

    private Scrim scrim;                // el modelo de dominio
    private ScrimState state;           // estado ACTUAL del scrim
    private List<ScrimEventListener> listeners = new ArrayList<>(); // observers

    // --------------------- CONSTRUCTOR ---------------------
    public ScrimContext(Scrim scrim) {
        this.scrim = scrim;
        // Estado inicial del enunciado: "Buscando jugadores"
        this.state = new BuscandoJugadoresState();
    }

    public Scrim getScrim() {
        return scrim;
    }

    //Cambia el estado interno del scrim. ACTUALIZA el enum dentro del modelo y PUBLICA un evento para los listeners.

    public void setState(ScrimState newState, ScrimStatus newStatus) {
        this.state = newState;
        this.scrim.setStatus(newStatus);
        System.out.println("[LIFECYCLE] Scrim " + scrim.getId() + " cambió a estado: " + newStatus.name());
        // notificar
        publishEvent(new ScrimStateChangedEvent(scrim.getId(), newStatus.name()));
    }

    //Permite que otros módulos se suscriban.
    public void addListener(ScrimEventListener l) {
        listeners.add(l);
    }

    private void publishEvent(ScrimStateChangedEvent e) {
        for (ScrimEventListener l : listeners) {
            l.onScrimEvent(e);
        }
    }

    // --------------------- DELEGACIONES AL STATE ---------------------

    public void postular(String userId) {
        state.postular(this, userId);
    }

    public void confirmar(String userId) {
        state.confirmar(this, userId);
    }

    public void iniciar() {
        state.iniciar(this);
    }

    public void finalizar() {
        state.finalizar(this);
    }

    public void cancelar() {
        state.cancelar(this);
    }

    //Llamado por el SCHEDULER. Le delega al estado actual la decisión de qué hacer cuando llega la hora.
     
    public void onTimeReached(LocalDateTime now) {
        state.onTimeReached(this, now);
    }
}
