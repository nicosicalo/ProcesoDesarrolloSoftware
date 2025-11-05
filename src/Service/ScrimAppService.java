package Service;

import Domain.Events.DomainEventBus;
import Domain.Events.ScrimCreadoEvent;
import Domain.ScrimBuilder;
import Infraestructura.RepositorioDeScrims;
import Infraestructura.UsuarioRepository;
import Infraestructura.BusquedaFavoritaRepository;
import Infraestructura.JuegoRepository;
import Infraestructura.PostulacionRepository;
import Models.BusquedaFavorita;
import Models.GameProfile;
import Models.Postulacion;
import Models.Scrim;
import Models.Usuario;
import Models.Juego;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class ScrimAppService {
    private final RepositorioDeScrims scrimRepo;
    private final UsuarioRepository usuarioRepo;
    private final DomainEventBus eventBus;
    private final PostulacionRepository postulacionRepo;
    private final JuegoRepository juegoRepo;
    private final BusquedaFavoritaRepository busquedaRepo;

    public ScrimAppService(
        RepositorioDeScrims scrimRepo, 
        UsuarioRepository usuarioRepo, 
        DomainEventBus eventBus,
        PostulacionRepository postulacionRepo,
        JuegoRepository juegoRepo,
        BusquedaFavoritaRepository busquedaRepo
    ) {
        this.scrimRepo = scrimRepo;
        this.usuarioRepo = usuarioRepo;
        this.eventBus = eventBus;
        this.postulacionRepo = postulacionRepo; 
        this.juegoRepo = juegoRepo;
        this.busquedaRepo = busquedaRepo;
    }

    public Scrim crearScrim(ScrimCreationDTO dto) {
        
        Scrim scrim = new ScrimBuilder(dto.organizadorId())
                .conJuego(dto.juegoId(), dto.formato())
                .conRegion(dto.regionId())
                .conRangos(dto.rangoMin(), dto.rangoMax())
                .conLatencia(dto.latenciaMaxMs()) 
                .conModalidad(dto.modalidad())
                .conCupos(dto.cupos())
                .conRoles(dto.rolesRequeridos())
                .conFecha(dto.fechaHora(), dto.duracionEstimadaMin())
                .build();       
        scrimRepo.save(scrim);        
        eventBus.publish(new ScrimCreadoEvent(scrim.getId(), scrim.getJuegoId()));        
        return scrim;
    }

    public List<Scrim> buscarScrims(FiltrosBusqueda filtros) {
        return scrimRepo.findByFiltros(filtros);
    }

    private String getGameProfileKey(String scrimGameId) {
        Optional<Juego> juegoOpt = juegoRepo.findByStringId(scrimGameId); 

        return juegoOpt.map(Juego::getNombre).orElse(scrimGameId);
    }
    
    public List<Postulacion> findPostulacionesByUsuarioId(UUID usuarioId) {
        return postulacionRepo.findByUsuarioId(usuarioId);
    }
    
    public Optional<Postulacion> postularse(UUID scrimId, UUID usuarioId, String rolDeseado) {
        Optional<Scrim> scrimOpt = scrimRepo.findById(scrimId);
        Optional<Usuario> userOpt = usuarioRepo.findByAnyId(usuarioId.toString());
        if (scrimOpt.isEmpty() || userOpt.isEmpty()) return Optional.empty();
        Scrim scrim = scrimOpt.get();
        Usuario usuario = userOpt.get();

        String gameProfileKey = getGameProfileKey(scrim.getJuegoId());
        
        GameProfile gp = usuario.getPerfil().getPerfilPorJuego(gameProfileKey);
        
        // 2. Usar la clave correcta para el mensaje de error (dando mejor feedback al usuario)
        if (gp == null) {
            System.out.println("No puedes postularte, no tienes perfil de juego configurado para " + gameProfileKey);
            return Optional.empty();
        }

        // El resto de la validación del rol usa scrim.getJuegoId() para buscar la Factory,
        // lo cual es correcto ya que el Repositorio de Juegos lo soporta.
        if (rolDeseado != null) {
            Optional<Juego> juegoOpt = juegoRepo.findByStringId(scrim.getJuegoId());
            if (juegoOpt.isPresent()) {
                List<String> rolesValidos = juegoOpt.get().getJuegoFactory().getRolesDelJuego();      
                if (!rolesValidos.contains(rolDeseado)) {
                    System.out.println("Rol '" + rolDeseado + "' es inválido para el juego " + juegoOpt.get().getNombre() + ". Roles válidos: " + rolesValidos);
                    return Optional.empty();
                }
            } else {
                 System.out.println("No se encontró la Factory para el juego " + scrim.getJuegoId() + ". Fallo crítico en validación de rol.");
                 return Optional.empty();
            }
        }

        Postulacion p = new Postulacion(usuarioId, scrimId, rolDeseado);
        postulacionRepo.save(p);
        return Optional.of(p);
    }
    public List<Scrim> findScrimsByIds(Set<UUID> scrimIds) {
        // Delega directamente al repositorio para la búsqueda masiva.
        return scrimRepo.findByIds(scrimIds); 
    }
    public List<Scrim> findScrimsOrganizadosPor(UUID organizadorId) {
        return scrimRepo.findByOrganizadorId(organizadorId);
    }
    public Optional<Scrim> findById(UUID scrimId) {
        return scrimRepo.findById(scrimId);
    }
    public List<Postulacion> findApplicantsForScrim(UUID scrimId) {
        return postulacionRepo.findByScrimId(scrimId);
    }
    public void guardarBusquedaFavorita(UUID usuarioId, String nombre, FiltrosBusqueda filtros) {
        BusquedaFavorita fav = new BusquedaFavorita(usuarioId, nombre, filtros);
        busquedaRepo.save(fav);
    }
    public List<Models.BusquedaFavorita> findBusquedasFavoritasByUsuarioId(UUID userId) {
        return busquedaRepo.findByUsuarioId(userId);
    }
}