package ScrimsLifecycle.scheduler;

import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

import Service.ScrimLifecycleService;

/**
 * Scheduler simple con java.util.Timer.
 * Llama al servicio cada N milisegundos.
 */
public class ScrimSchedulerService {

    private final ScrimLifecycleService scrimService;
    private final Timer timer = new Timer(true); // daemon

    public ScrimSchedulerService(ScrimLifecycleService scrimService) {
        this.scrimService = scrimService;
    }

    public void start(long periodMillis) {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                scrimService.tick(LocalDateTime.now());
            }
        }, 0, periodMillis);
    }
}
