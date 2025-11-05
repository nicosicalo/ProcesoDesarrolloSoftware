package ScrimsLifecycle.events;

// Contiene solo: id del scrim + nombre del nuevo estado.Esto lo publica el ScrimContext cada vez que hace setState(...)

import java.util.UUID;

public class ScrimStateChangedEvent {

    private UUID scrimId;
    private String nuevoEstado;

    //IMPORTANTE: este constructor es el que te pedía Eclipse. En tu ScrimContext estabas haciendo:
     //    new ScrimStateChangedEvent(scrim.getId(), newStatus.name())
     // Por eso TIENE QUE existir exactamente este constructor.
    
    public ScrimStateChangedEvent(UUID scrimId, String nuevoEstado) {
        this.scrimId = scrimId;
        this.nuevoEstado = nuevoEstado;
    }

    public UUID getScrimId() {
        return scrimId;
    }

    public String getNuevoEstado() {
        return nuevoEstado;
    }
}
