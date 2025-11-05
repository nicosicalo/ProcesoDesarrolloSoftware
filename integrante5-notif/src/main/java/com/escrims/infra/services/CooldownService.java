package com.escrims.infra.services;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class CooldownService {
    private final Map<UUID, Integer> strikes = new HashMap<>();
    private final Map<UUID, LocalDateTime> cooldownUntil = new HashMap<>();

    public void registerNoShow(UUID userId){
        int s = strikes.getOrDefault(userId, 0) + 1;
        strikes.put(userId, s);
        if (s >= 3){
            cooldownUntil.put(userId, LocalDateTime.now().plusDays(3));
        }
    }

    public boolean isInCooldown(UUID userId){
        var until = cooldownUntil.get(userId);
        return until != null && until.isAfter(LocalDateTime.now());
    }

    public Duration remaining(UUID userId){
        var until = cooldownUntil.get(userId);
        if (until == null) return Duration.ZERO;
        if (until.isBefore(LocalDateTime.now())) return Duration.ZERO;
        return Duration.between(LocalDateTime.now(), until);
    }
}
