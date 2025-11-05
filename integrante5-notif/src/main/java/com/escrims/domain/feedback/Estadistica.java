package com.escrims.domain.feedback;

import java.util.UUID;

public record Estadistica(
        UUID scrimId,
        UUID usuarioId,
        boolean mvp,
        int kills,
        int assists,
        String observaciones
) { }
