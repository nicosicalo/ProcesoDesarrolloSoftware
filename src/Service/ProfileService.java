package Service;

import Models.GameProfile;
import Models.Usuario;
import Enums.Region;

import Infraestructura.UsuarioRepository;
import java.util.Optional;
import java.util.Set;

import java.util.Optional;
import java.util.Set;


public class ProfileService {
    private final UsuarioRepository repo;

    public ProfileService(UsuarioRepository repo) {
        this.repo = repo;
    }

    /**
     * Actualiza la información general del perfil del usuario (no específica del juego).
     * @param userId El ID del usuario.
     * @param region La región/servidor general.
     * @param disponibilidadHoraria La franja horaria.
     * @return El Usuario actualizado.
     */
    public Optional<Usuario> updateGeneralProfile(
            String userId,
            Region region,
            String disponibilidadHoraria
    ) {
        Optional<Usuario> optionalUser = repo.findByAnyId(userId);
        if (optionalUser.isEmpty()) {
            return Optional.empty();
        }

        Usuario user = optionalUser.get();
        user.getPerfil().setRegion(region);
        user.getPerfil().setDisponibilidadHoraria(disponibilidadHoraria);

        repo.save(user);
        return Optional.of(user);
    }

    /**
     * Crea o actualiza el perfil de un juego específico (rango y roles).
     * @param userId El ID del usuario.
     * @param juegoId El ID del juego (ej: "valorant", "lol").
     * @param rango El rango (como String, ej: "RADIANT").
     * @param roles Los roles (como Set<String>, ej: ["DUELISTA", "SUPPORT"]).
     * @return El Usuario actualizado.
     */
    public Optional<Usuario> updateGameProfile(
            String userId,
            String juegoId,
            String rango,
            Set<String> roles
    ) {
        Optional<Usuario> optionalUser = repo.findByAnyId(userId);
        if (optionalUser.isEmpty()) {
            return Optional.empty();
        }

        Usuario user = optionalUser.get();


        GameProfile gameProfile = user.getPerfil().getPerfilPorJuego(juegoId);
        if (gameProfile == null) {
            gameProfile = new GameProfile();
        }


        gameProfile.setRango(rango);
        gameProfile.setRoles(roles);


        user.getPerfil().getPerfilesDeJuego().put(juegoId, gameProfile);

        repo.save(user);
        return Optional.of(user);
    }

    /**
     * Establece el juego principal del usuario.
     * @param userId El ID del usuario.
     * @param juegoId El ID del juego a marcar como principal.
     * @return El Usuario actualizado, o Optional.empty() si el usuario no tiene
     * configurado ese perfil de juego.
     */
    public Optional<Usuario> setJuegoPrincipal(String userId, String juegoId) {
        Optional<Usuario> optionalUser = repo.findByAnyId(userId);
        if (optionalUser.isEmpty()) {
            return Optional.empty();
        }

        Usuario user = optionalUser.get();


        if (!user.getPerfil().getPerfilesDeJuego().containsKey(juegoId)) {
            return Optional.empty();
        }

        user.getPerfil().setJuegoPrincipalId(juegoId);
        repo.save(user);
        return Optional.of(user);
    }
}