package ScrimsLifecycle.test;

import java.time.LocalDateTime;
import java.util.UUID;

import Service.ScrimLifecycleService;
import ScrimsLifecycle.context.ScrimContext;
import ScrimsLifecycle.scheduler.ScrimSchedulerService;

public class DemoScrimState {

    public static void main(String[] args) throws InterruptedException {

        ScrimLifecycleService service = new ScrimLifecycleService();

        // crear scrim que empieza en 20 seg
        UUID scrimId = service.crearScrim(2, LocalDateTime.now().plusSeconds(20));

        // engancho un listener para ver cambios
        ScrimContext ctx = service.getContext(scrimId);
        ctx.addListener(e -> System.out.println("-> cambio de estado: " + e.getNuevoEstado()));

        // arrancar scheduler cada 5 seg
        ScrimSchedulerService scheduler = new ScrimSchedulerService(service);
        scheduler.start(5000);

        // simular jugadores (UUID)
        UUID user1 = UUID.randomUUID();
        UUID user2 = UUID.randomUUID();

        service.postular(scrimId, user1);
        service.postular(scrimId, user2); // debería pasar a LOBBY_ARMADO
        service.confirmar(scrimId, user1);
        service.confirmar(scrimId, user2); // debería pasar a CONFIRMADO

        // ahora solo esperamos que el scheduler lo pase a EN_JUEGO
        Thread.sleep(30000); // 30 seg para mirar la consola
    }
}
