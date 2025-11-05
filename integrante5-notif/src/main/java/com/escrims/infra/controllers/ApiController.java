package com.escrims.infra.controllers;

import com.escrims.domain.calendar.ICalAdapter;
import com.escrims.domain.events.DomainEventBus;
import com.escrims.domain.events.ScrimEvent;
import com.escrims.domain.events.ScrimEventType;
import com.escrims.domain.feedback.Estadistica;
import com.escrims.domain.feedback.Rating;
import com.escrims.domain.moderation.BadWordsHandler;
import com.escrims.domain.moderation.HumanModeratorHandler;
import com.escrims.domain.moderation.LowStarsEscalationHandler;
import com.escrims.infra.services.CooldownService;
import com.escrims.infra.services.NotificationService;
import com.escrims.infra.services.ReminderService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final DomainEventBus bus;
    private final NotificationService notificationService;
    private final ReminderService reminderService;
    private final CooldownService cooldownService;

    public ApiController(
            DomainEventBus bus,
            NotificationService notificationService,
            ReminderService reminderService,
            CooldownService cooldownService) {
        this.bus = bus;
        this.notificationService = notificationService;
        this.reminderService = reminderService;
        this.cooldownService = cooldownService;
        this.bus.subscribe(this.notificationService);
    }

    @PostMapping("/events/scrim")
    public ResponseEntity<?> publishEvent(@RequestBody Map<String, String> body) {
        UUID scrimId = UUID.fromString(body.get("scrimId"));
        ScrimEventType type = ScrimEventType.valueOf(body.get("type"));
        String juego = body.getOrDefault("juego", "Valorant");
        String region = body.getOrDefault("region", "LATAM");
        LocalDateTime at = LocalDateTime.parse(body.getOrDefault("at", LocalDateTime.now().toString()));

        bus.publish(new ScrimEvent(scrimId, type, at, juego, region));
        return ResponseEntity.ok(Map.of("status", "published"));
    }

    @PostMapping("/estadisticas")
    public ResponseEntity<?> submitStats(@RequestBody Estadistica e) {
        // Persistencia omitida en la demo
        return ResponseEntity.ok(Map.of("saved", true));
    }

    @PostMapping("/feedback")
    public ResponseEntity<?> submitRating(@RequestBody Rating r) {
        var bad = new BadWordsHandler();
        var low = new LowStarsEscalationHandler();
        var human = new HumanModeratorHandler();
        bad.linkWith(low).linkWith(human);
        var res = bad.handle(r);
        return ResponseEntity.ok(Map.of(
                "moderation", res.status().toString(),
                "reason", res.reason()));
    }

    @PostMapping("/reportes/noshow/{userId}")
    public ResponseEntity<?> registerNoShow(@PathVariable("userId") UUID userId) {
        cooldownService.registerNoShow(userId);
        boolean inCooldown = cooldownService.isInCooldown(userId);
        long remaining = cooldownService.remaining(userId).toSeconds();
        return ResponseEntity.ok(Map.of("cooldown", inCooldown, "remainingSeconds", remaining));
    }

    @GetMapping(value = "/ical/{scrimId}.ics", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> getIcs(@PathVariable("scrimId") UUID scrimId,
            @RequestParam String juego,
            @RequestParam String region,
            @RequestParam String startIso,
            @RequestParam(defaultValue = "90") int durMin) {
        var bytes = ICalAdapter.scrimToIcs(
                scrimId,
                juego,
                region,
                LocalDateTime.parse(startIso).atZone(ZoneId.systemDefault()),
                durMin);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"scrim-" + scrimId + ".ics\"")
                .body(bytes);
    }

    @PostMapping("/reminders")
    public ResponseEntity<?> scheduleReminder(@RequestBody Map<String, String> body) {
        UUID scrimId = UUID.fromString(body.get("scrimId"));
        String juego = body.getOrDefault("juego", "Valorant");
        String region = body.getOrDefault("region", "LATAM");
        LocalDateTime at = LocalDateTime.parse(body.get("at"));
        reminderService.scheduleReminder(scrimId, juego, region, at);
        return ResponseEntity.ok(Map.of("scheduled", true));
    }
}
