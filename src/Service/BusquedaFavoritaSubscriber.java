package Service;

import Domain.Events.DomainEvent;
import Domain.Events.ScrimCreadoEvent;
import Domain.Events.Subscriber;

// Subscriber simulado para Alertas (Patrón Observer)
public class BusquedaFavoritaSubscriber implements Subscriber {
    
    // Simulación de un repositorio/lista de búsquedas favoritas guardadas
    // private final List<FiltrosBusqueda> busquedasGuardadas;

    @Override
    public void onEvent(DomainEvent e) {
        if (e instanceof ScrimCreadoEvent evento) {
            System.out.println("\n[ALERTA DE SISTEMA] Scrim creado: " + evento.scrimId() + " (" + evento.juegoId() + ")");
            
            // Simulación de lógica de alerta:
            // if (busquedasGuardadas.stream().anyMatch(f -> f.juegoId().equals(evento.juegoId()))) {
            //     System.out.println("🔔 ¡COINCIDENCIA! Tu búsqueda favorita tiene un nuevo resultado.");
            // }
        }
    }
}