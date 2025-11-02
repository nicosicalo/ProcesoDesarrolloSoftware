package Service;

import Domain.Events.DomainEvent;
import Domain.Events.ScrimCreadoEvent;
import Domain.Events.Subscriber;
import Infraestructura.BusquedaFavoritaRepository; 
import Models.BusquedaFavorita;
import java.util.List;
// Subscriber simulado para Alertas (Patrón Observer)
public class BusquedaFavoritaSubscriber implements Subscriber {
    private final BusquedaFavoritaRepository busquedaRepo;
    public BusquedaFavoritaSubscriber(BusquedaFavoritaRepository busquedaRepo) {
        this.busquedaRepo = busquedaRepo;
    }
    // Simulación de un repositorio/lista de búsquedas favoritas guardadas
    // private final List<FiltrosBusqueda> busquedasGuardadas;

    @Override
    public void onEvent(DomainEvent e) {
        if (e instanceof ScrimCreadoEvent evento) {
            System.out.println("\n[OBSERVER] Scrim creado: " + evento.scrimId() + " (" + evento.juegoId() + ")");
            // 1. Obtener todas las búsquedas favoritas guardadas
            List<BusquedaFavorita> favoritas = busquedaRepo.findAll();

            // 2. Iterar sobre ellas y buscar coincidencias (Lógica de Alerta)
            favoritas.stream()
                .filter(fav -> matches(evento, fav.getFiltros()))
                .forEach(fav -> {
                    System.out.println("ALERTA ENCONTRADA para usuario " + fav.getUsuarioId());
                    System.out.println("   -> Coincide con su búsqueda: '" + fav.getNombre() + "'");
                    // Aquí iría la lógica de Notificación (Integración con Integrante 5: Abstract Factory/Adapter)
                });
        }
    }
    
    /**
     * Compara un Scrim recién creado (evento) con los criterios de una búsqueda favorita.
     * Esta es una simplificación de la lógica de búsqueda.
     */
    private boolean matches(ScrimCreadoEvent scrim, FiltrosBusqueda filtros) {
        // Coincidencia de Juego
        if (filtros.juegoId() != null && !filtros.juegoId().equalsIgnoreCase(scrim.juegoId())) {
            return false;
        }
        
        // **Falta: Lógica de Rango, Formato, Región, Latencia**
        // Para simular que siempre hay alguna coincidencia por ahora:
        return true; 
    }
}