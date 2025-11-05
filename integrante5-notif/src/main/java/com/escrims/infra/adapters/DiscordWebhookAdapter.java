package com.escrims.infra.adapters;

import com.escrims.domain.notifications.Notificacion;
import com.escrims.domain.notifications.Notifier;

public class DiscordWebhookAdapter implements Notifier {
    private final boolean dryRun;
    public DiscordWebhookAdapter(boolean dryRun){ this.dryRun = dryRun; }
    @Override
    public void send(Notificacion n) {
        System.out.println("[Discord:"+ (dryRun?"DRY":"LIVE") +"] " + n.titulo() + " :: " + n.cuerpo());
    }
}
