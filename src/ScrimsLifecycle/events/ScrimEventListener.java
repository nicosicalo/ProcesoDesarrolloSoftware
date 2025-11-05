package ScrimsLifecycle.events;

//Interfaz muy simple para que OTROS módulos (por ejemplo el de notificaciones del integrante 5 puedan "escuchar" cambios de estado del scrim.

public interface ScrimEventListener {

    //Se dispara cuando el scrim cambia de estado.
    void onScrimEvent(ScrimStateChangedEvent event);
}
