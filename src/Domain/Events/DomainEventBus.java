package Domain.Events;

import java.util.ArrayList;
import java.util.List;

// Patrón Observer: Centraliza la publicación de eventos.
public class DomainEventBus {
    private static final DomainEventBus INSTANCE = new DomainEventBus();
    private final List<Subscriber> subscribers = new ArrayList<>();

    private DomainEventBus() {}

    public static DomainEventBus getInstance() {
        return INSTANCE;
    }

    public void subscribe(Subscriber s) {
        subscribers.add(s);
    }

    public void publish(DomainEvent e) {
        for (Subscriber s : subscribers) {
            s.onEvent(e);
        }
    }
}