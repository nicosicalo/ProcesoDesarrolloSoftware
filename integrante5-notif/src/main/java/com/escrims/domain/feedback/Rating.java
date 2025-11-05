package com.escrims.domain.feedback;

import java.util.UUID;

public record Rating(
        UUID scrimId,
        UUID fromUser,
        UUID toUser,
        int estrellas,
        String comentario
) { }
