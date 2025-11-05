package com.escrims.infra.factory;

import com.escrims.domain.notifications.Notifier;
import com.escrims.infra.adapters.DiscordWebhookAdapter;
import com.escrims.infra.adapters.FirebasePushAdapter;
import com.escrims.infra.adapters.SendGridEmailAdapter;

public class ProdNotifierFactory implements NotifierFactory {
    @Override
    public Notifier createPush() { return new FirebasePushAdapter(false); }
    @Override
    public Notifier createEmail() { return new SendGridEmailAdapter(false); }
    @Override
    public Notifier createChat() { return new DiscordWebhookAdapter(false); }
}
