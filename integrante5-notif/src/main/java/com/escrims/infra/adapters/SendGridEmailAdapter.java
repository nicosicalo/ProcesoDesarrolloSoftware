package com.escrims.infra.adapters;

import com.escrims.domain.notifications.Notificacion;
import com.escrims.domain.notifications.Notifier;

public class SendGridEmailAdapter implements Notifier {
    private final boolean dryRun;
    public SendGridEmailAdapter(boolean dryRun){ this.dryRun = dryRun; }
    @Override
    public void send(Notificacion n) {
        System.out.println("[Email:"+ (dryRun?"DRY":"LIVE") +"] " + n.titulo() + " :: " + n.cuerpo());
    }
}
