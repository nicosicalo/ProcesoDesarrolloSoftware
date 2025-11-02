package Domain.Events;

// Patrón Observer: La interfaz que deben implementar todos los suscriptores.
public interface Subscriber {
    void onEvent(DomainEvent e);
}