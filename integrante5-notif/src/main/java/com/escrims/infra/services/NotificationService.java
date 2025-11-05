package com.escrims.infra.services;

import com.escrims.domain.events.DomainEvent;
import com.escrims.domain.events.ScrimEvent;
import com.escrims.domain.notifications.Canal;
import com.escrims.domain.notifications.Notificacion;
import com.escrims.domain.notifications.Notifier;
import com.escrims.infra.factory.NotifierFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@Service
public class NotificationService implements Consumer<DomainEvent> {

    private final Notifier push;
    private final Notifier email;
    private final Notifier chat;

    public NotificationService(NotifierFactory notifierFactory) {
        this.push = notifierFactory.createPush();
        this.email = notifierFactory.createEmail();
        this.chat = notifierFactory.createChat();
    }

    @Override
    public void accept(DomainEvent e) {
        if (e instanceof ScrimEvent se){
            var title = "Scrim " + se.type();
            var body = "Juego: %s | Región: %s | Hora: %s".formatted(se.juego(), se.region(), se.at());
            var n1 = new Notificacion(UUID.randomUUID(), Canal.PUSH, se.type().name(), title, body, Map.of("scrimId", se.scrimId()));
            var n2 = new Notificacion(UUID.randomUUID(), Canal.EMAIL, se.type().name(), title, body, Map.of("scrimId", se.scrimId()));
            var n3 = new Notificacion(UUID.randomUUID(), Canal.CHAT, se.type().name(), title, body, Map.of("scrimId", se.scrimId()));
            push.send(n1);
            email.send(n2);
            chat.send(n3);
        }
    }
}
