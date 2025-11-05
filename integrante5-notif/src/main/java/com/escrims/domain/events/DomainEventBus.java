package com.escrims.domain.events;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DomainEventBus {
    private final List<Consumer<DomainEvent>> subscribers = new ArrayList<>();

    public void subscribe(Consumer<DomainEvent> consumer){
        subscribers.add(consumer);
    }

    public void publish(DomainEvent event){
        for (var s : subscribers){
            try { s.accept(event); } catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }
}
