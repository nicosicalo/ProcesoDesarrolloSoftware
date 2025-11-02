package Service;

import Domain.Events.DomainEventBus;
import Domain.Events.ScrimCreadoEvent;
import Domain.ScrimBuilder;
import Infraestructura.RepositorioDeScrims;
import Infraestructura.UsuarioRepository;
import Models.GameProfile;
import Models.Postulacion;
import Models.Scrim;
import Models.Usuario;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ScrimAppService {
    private final RepositorioDeScrims scrimRepo;
    private final UsuarioRepository usuarioRepo;
    private final DomainEventBus eventBus;

    public ScrimAppService(RepositorioDeScrims scrimRepo, UsuarioRepository usuarioRepo, DomainEventBus eventBus) {
        this.scrimRepo = scrimRepo;
        this.usuarioRepo = usuarioRepo;
        this.eventBus = eventBus;
    }

    public Scrim crearScrim(ScrimCreationDTO dto) {
        
        Scrim scrim = new ScrimBuilder(dto.organizadorId())
                .conJuego(dto.juegoId(), dto.formato())
                .conRegion(dto.regionId())
                .conRangos(dto.rangoMin(), dto.rangoMax())
                .conCupos(dto.cupos())
                .conFecha(dto.fechaHora(), dto.duracionEstimadaMin())
                .build();
        
        scrimRepo.save(scrim);
        
        // Publicar evento (Patrón Observer)
        eventBus.publish(new ScrimCreadoEvent(scrim.getId(), scrim.getJuegoId()));
        
        return scrim;
    }

    public List<Scrim> buscarScrims(FiltrosBusqueda filtros) {
        return scrimRepo.findByFiltros(filtros);
    }
    
    public Optional<Postulacion> postularse(UUID scrimId, UUID usuarioId, String rolDeseado) {
        Optional<Scrim> scrimOpt = scrimRepo.findById(scrimId);
        Optional<Usuario> userOpt = usuarioRepo.findByAnyId(usuarioId.toString());

        if (scrimOpt.isEmpty() || userOpt.isEmpty()) return Optional.empty();


        Scrim scrim = scrimOpt.get();
        Usuario usuario = userOpt.get();
        
        GameProfile gp = usuario.getPerfil().getPerfilPorJuego(scrim.getJuegoId());
        
        if (gp == null) {
            System.out.println("❌ No puedes postularte, no tienes perfil de juego configurado para " + scrim.getJuegoId());
            return Optional.empty();
        }
        


        Postulacion p = new Postulacion(usuarioId, scrimId, rolDeseado);
 

        return Optional.of(p);
    }
}