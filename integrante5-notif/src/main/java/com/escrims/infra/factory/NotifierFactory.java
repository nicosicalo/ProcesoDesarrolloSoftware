package com.escrims.infra.factory;

import com.escrims.domain.notifications.Notifier;

public interface NotifierFactory {
    Notifier createPush();
    Notifier createEmail();
    Notifier createChat();
}
