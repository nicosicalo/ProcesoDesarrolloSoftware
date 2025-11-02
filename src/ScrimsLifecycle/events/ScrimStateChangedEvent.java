package ScrimsLifecycle.events;

// Contiene solo: id del scrim + nombre del nuevo estado.Esto lo publica el ScrimContext cada vez que hace setState(...)

public class ScrimStateChangedEvent {

    private Long scrimId;
    private String nuevoEstado;

    //IMPORTANTE: este constructor es el que te pedía Eclipse. En tu ScrimContext estabas haciendo:
     //    new ScrimStateChangedEvent(scrim.getId(), newStatus.name())
     // Por eso TIENE QUE existir exactamente este constructor.
    
    public ScrimStateChangedEvent(Long scrimId, String nuevoEstado) {
        this.scrimId = scrimId;
        this.nuevoEstado = nuevoEstado;
    }

    public Long getScrimId() {
        return scrimId;
    }

    public String getNuevoEstado() {
        return nuevoEstado;
    }
}
