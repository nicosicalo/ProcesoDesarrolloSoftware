package ScrimsLifecycle.test;

import java.time.LocalDateTime;

import Enums.ScrimStatus;
import Service.ScrimLifecycleService;
import ScrimsLifecycle.context.ScrimContext;
import ScrimsLifecycle.scheduler.ScrimSchedulerService;

public class DemoScrimState {

    public static void main(String[] args) throws InterruptedException {

        ScrimLifecycleService service = new ScrimLifecycleService();

        // crear scrim que empieza en 20 seg
        Long scrimId = service.crearScrim(2, LocalDateTime.now().plusSeconds(20));

        // engancho un listener para ver cambios
        ScrimContext ctx = service.getContext(scrimId);
        ctx.addListener(e ->
            System.out.println("-> cambio de estado: " + e.getNuevoEstado())
        );

        // arrancar scheduler cada 5 seg
        ScrimSchedulerService scheduler = new ScrimSchedulerService(service);
        scheduler.start(5000);

        // simular jugadores
        service.postular(scrimId, 10L);
        service.postular(scrimId, 11L); // debería pasar a LOBBY_ARMADO
        service.confirmar(scrimId, 10L);
        service.confirmar(scrimId, 11L); // debería pasar a CONFIRMADO

        // ahora solo esperamos que el scheduler lo pase a EN_JUEGO
        Thread.sleep(30000); // 30 seg para mirar la consola
    }
}
