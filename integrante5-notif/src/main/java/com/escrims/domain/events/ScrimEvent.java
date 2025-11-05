package com.escrims.domain.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record ScrimEvent(
        UUID scrimId,
        ScrimEventType type,
        LocalDateTime at,
        String juego,
        String region
) implements DomainEvent { }
