package Service;

import Enums.Rango;
import Enums.Region;
import Enums.Rol;
import Infraestructura.UsuarioRepository;
import Models.Usuario;

import java.util.Optional;
import java.util.Set;

/**
 * Servicio encargado de gestionar las operaciones relacionadas con el Perfil de un Usuario,
 * como la edición de sus preferencias de juego.
 */
public class ProfileService {
    private final UsuarioRepository repo;

    public ProfileService(UsuarioRepository repo) {
        this.repo = repo;
    }

    /**
     * Actualiza la información del perfil del usuario.
     * @param userId El ID único del usuario a actualizar.
     * @param juegoPrincipal El juego que el usuario juega más.
     * @param rangoPorJuego El rango actual del usuario en ese juego.
     * @param rolesPreferidos El conjunto de roles que prefiere jugar.
     * @param region La región o servidor principal.
     * @param disponibilidadHoraria Franja horaria para jugar (e.g., "18:00-22:00").
     * @return El Usuario actualizado o Optional.empty() si no se encuentra.
     */
    public Optional<Usuario> updateProfile(
            String userId,
            String juegoPrincipal,
            Rango rangoPorJuego,
            Set<Rol> rolesPreferidos,
            Region region,
            String disponibilidadHoraria
    ) {
        // En un caso real, buscaríamos por ID, pero el repo actual solo tiene findByEmail/Username.
        // Simularemos la búsqueda por un ID o Asumiremos que el repo tendrá un findById
        Optional<Usuario> optionalUser = repo.findByAnyId(userId); // Imaginamos que el repo tiene este método

        if (optionalUser.isEmpty()) {
            // Usuario no encontrado (o sesión inválida)
            return Optional.empty();
        }

        Usuario user = optionalUser.get();

        // Aplicar las actualizaciones al perfil
        user.getPerfil().setJuegoPrincipal(juegoPrincipal);
        user.getPerfil().setRangoPorJuego(rangoPorJuego);
        user.getPerfil().setRolesPreferidos(rolesPreferidos);
        user.getPerfil().setRegion(region);
        user.getPerfil().setDisponibilidadHoraria(disponibilidadHoraria);

        // Guardar el cambio de vuelta al repositorio (si el repositorio usa Mapas, el objeto ya está en memoria,
        // pero en una BDD real se requeriría una operación de save/update).
        repo.save(user);

        return Optional.of(user);
    }
}
