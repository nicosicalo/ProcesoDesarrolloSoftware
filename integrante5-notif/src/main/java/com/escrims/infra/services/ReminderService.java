package com.escrims.infra.services;

import com.escrims.domain.events.DomainEventBus;
import com.escrims.domain.events.ScrimEvent;
import com.escrims.domain.events.ScrimEventType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ReminderService {
    private final DomainEventBus bus;
    public ReminderService(DomainEventBus bus){ this.bus = bus; }

    public void scheduleReminder(UUID scrimId, String juego, String region, LocalDateTime at){
        bus.publish(new ScrimEvent(scrimId, ScrimEventType.REMINDER, at, juego, region));
    }
}
