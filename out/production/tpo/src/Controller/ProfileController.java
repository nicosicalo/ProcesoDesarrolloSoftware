package Controller;
import Enums.Rango;
import Enums.Region;
import Enums.Rol;
import Models.Usuario;
import ScrimsAuth.SessionManager;
import Service.ProfileService;

import java.util.HashSet;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
public class ProfileController {
    private final Scanner sc;
    private final SessionManager sessionManager;
    private final ProfileService profileService;

    public ProfileController(Scanner sc, SessionManager sessionManager, ProfileService profileService) {
        this.sc = sc;
        this.sessionManager = sessionManager;
        this.profileService = profileService;
    }

    /**
     * Permite al usuario editar su perfil.
     * @param currentToken El token de la sesión actual.
     */
    public void editarPerfil(String currentToken) {
        if (currentToken == null) {
            System.out.println("⚠️ No estás logueado.");
            return;
        }

        Optional<Usuario> uo = sessionManager.getUser(currentToken);
        if (uo.isEmpty()) {
            System.out.println("⚠️ Sesión no válida. Por favor, vuelve a iniciar sesión.");
            return;
        }

        Usuario user = uo.get();

        System.out.println("\n--- Edición de Perfil de " + user.getUsername() + " ---");
        System.out.print("Juego principal: "); String juego = sc.nextLine();

        // Manejo de Rango con validación básica de Enum
        Rango r = null;
        while (r == null) {
            try {
                System.out.println("Rango (HIERRO, BRONCE, PLATA, ORO, PLATINO, DIAMANTE, ASCENDENTE, INMORTAL, RADIANTE): ");
                r = Rango.valueOf(sc.nextLine().trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("❌ Rango inválido. Intenta de nuevo.");
            }
        }

        // Manejo de Región con validación básica de Enum
        Region reg = null;
        while (reg == null) {
            try {
                System.out.println("Region (LAS, LAN, NA, EUW, EUNE, BR): ");
                reg = Region.valueOf(sc.nextLine().trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("❌ Región inválida. Intenta de nuevo.");
            }
        }

        // Manejo de Roles
        Set<Rol> roles = new HashSet<>();
        while (roles.isEmpty()) {
            System.out.println("Roles preferidos (separados por coma: DUELIST, SUPPORT, JUNGLA, etc.): ");
            String rolesIn = sc.nextLine().trim();
            for (String s : rolesIn.split(",")) {
                if (s.isBlank()) continue;
                try {
                    roles.add(Rol.valueOf(s.trim().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    System.out.println("❌ Rol '" + s.trim().toUpperCase() + "' inválido. Por favor, corrige la lista.");
                    roles.clear(); // Limpiar y pedir de nuevo si hay error
                    break;
                }
            }
        }

        System.out.print("Disponibilidad (ej 18:00-22:00): "); String disp = sc.nextLine();

        // Delegar la actualización a la capa Service
        Optional<Usuario> updatedUser = profileService.updateProfile(
                user.getId(), // Usamos el ID para la actualización
                juego, r, roles, reg, disp
        );

        if (updatedUser.isPresent()) {
            System.out.println("✅ Perfil actualizado con éxito.");
            System.out.println(updatedUser.get());
        } else {
            // Este caso solo debería ocurrir si el usuario desaparece mientras edita
            System.out.println("❌ Error al guardar el perfil. Usuario no encontrado.");
        }
    }

    /**
     * Muestra la información del usuario logueado.
     * @param currentToken El token de la sesión actual.
     */
    public void mostrarUsuario(String currentToken) {
        if (currentToken == null) {
            System.out.println("⚠️ No estás logueado.");
            return;
        }

        sessionManager.getUser(currentToken).ifPresentOrElse(
                u -> System.out.println(u),
                () -> System.out.println("⚠️ Sesión inválida")
        );
    }
}
