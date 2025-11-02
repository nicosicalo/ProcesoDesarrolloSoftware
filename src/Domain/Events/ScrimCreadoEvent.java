package Domain.Events;

import java.util.UUID;

public record ScrimCreadoEvent(UUID scrimId, String juegoId) implements DomainEvent {
}