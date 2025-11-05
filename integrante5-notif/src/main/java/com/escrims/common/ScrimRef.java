package com.escrims.common;

import java.time.LocalDateTime;
import java.util.UUID;

public record ScrimRef(UUID id, String juego, String region, LocalDateTime fechaHora) { }
