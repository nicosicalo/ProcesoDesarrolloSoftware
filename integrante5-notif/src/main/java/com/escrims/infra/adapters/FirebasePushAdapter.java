package com.escrims.infra.adapters;

import com.escrims.domain.notifications.Notificacion;
import com.escrims.domain.notifications.Notifier;

public class FirebasePushAdapter implements Notifier {
    private final boolean dryRun;
    public FirebasePushAdapter(boolean dryRun){ this.dryRun = dryRun; }
    @Override
    public void send(Notificacion n) {
        System.out.println("[Push:"+ (dryRun?"DRY":"LIVE") +"] " + n.titulo() + " :: " + n.cuerpo());
    }
}
