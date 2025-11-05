package Service;

import Domain.Events.DomainEvent;
import Domain.Events.ScrimCreadoEvent;
import Domain.Events.Subscriber;
import Infraestructura.BusquedaFavoritaRepository; 
import Infraestructura.RepositorioDeScrims;
import Models.BusquedaFavorita;
import java.util.List;
import java.util.Optional;
import Models.Scrim;


public class BusquedaFavoritaSubscriber implements Subscriber {
    private final BusquedaFavoritaRepository busquedaRepo;
    private final RepositorioDeScrims scrimRepo;

    public BusquedaFavoritaSubscriber(BusquedaFavoritaRepository busquedaRepo, RepositorioDeScrims scrimRepo) {
        this.busquedaRepo = busquedaRepo;
        this.scrimRepo = scrimRepo;
    }


    @Override
    public void onEvent(DomainEvent e) {
        if (e instanceof ScrimCreadoEvent evento) {
            System.out.println("\n[OBSERVER] Scrim creado: " + evento.scrimId() + " (" + evento.juegoId() + ")");
            
            Optional<Scrim> scrimOpt = scrimRepo.findById(evento.scrimId()); //

            if (scrimOpt.isEmpty()) {
                System.out.println("[OBSERVER] Scrim no encontrado en repositorio. Alerta omitida.");
                return;
            }
            
            Scrim scrim = scrimOpt.get();

            List<BusquedaFavorita> favoritas = busquedaRepo.findAll();

            favoritas.stream()
                .filter(fav -> matches(scrim, fav.getFiltros())) 
                .forEach(fav -> {
                    System.out.println("ALERTA ENCONTRADA para usuario " + fav.getUsuarioId());
                    System.out.println("   -> Coincide con su búsqueda: '" + fav.getNombre() + "'");
                    //notificacion
                });
        }
    }
    
    private boolean matches(Scrim scrim, FiltrosBusqueda filtros) {

        if (filtros.juegoId() != null && !filtros.juegoId().equalsIgnoreCase(scrim.getJuegoId())) {
            return false;
        }

        if (filtros.formato() != null && !filtros.formato().equalsIgnoreCase(scrim.getFormato())) {
            return false;
        }
        
        if (filtros.regionId() != null && !filtros.regionId().equalsIgnoreCase(scrim.getRegionId())) {
            return false;
        }

        if (!(scrim.getRangoMin() >= filtros.rangoMin() && scrim.getRangoMax() <= filtros.rangoMax())) {
            return false;
        }
        if (scrim.getLatenciaMaxMs() > filtros.latenciaMaxMs()) {
            return false;
        }
        
        if (filtros.fechaHora() != null && !scrim.getFechaHora().isAfter(filtros.fechaHora())) {
            return false;
        }

        return true; 
    }
}