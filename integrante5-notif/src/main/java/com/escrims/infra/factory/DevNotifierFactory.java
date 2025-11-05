package com.escrims.infra.factory;

import com.escrims.domain.notifications.Notifier;
import com.escrims.infra.adapters.DiscordWebhookAdapter;
import com.escrims.infra.adapters.FirebasePushAdapter;
import com.escrims.infra.adapters.SendGridEmailAdapter;

public class DevNotifierFactory implements NotifierFactory {
    @Override
    public Notifier createPush() { return new FirebasePushAdapter(true); }
    @Override
    public Notifier createEmail() { return new SendGridEmailAdapter(true); }
    @Override
    public Notifier createChat() { return new DiscordWebhookAdapter(true); }
}
