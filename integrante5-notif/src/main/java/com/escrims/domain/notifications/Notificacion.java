package com.escrims.domain.notifications;

import java.util.Map;
import java.util.UUID;

public record Notificacion(
        UUID id,
        Canal canal,
        String tipo,
        String titulo,
        String cuerpo,
        Map<String,Object> payload
) { }
