package Controller;


import Factory.JuegoFactory;
import Infraestructura.JuegoRepository;
import Models.Juego;
import Models.Usuario;
import Enums.Region;


import ScrimsAuth.SessionManager;
import Service.ProfileService;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class ProfileController {
    private final Scanner sc;
    private final SessionManager sessionManager;
    private final ProfileService profileService;
    private final JuegoRepository juegoRepository;

    public ProfileController(Scanner sc, SessionManager sessionManager, ProfileService profileService, JuegoRepository juegoRepository) {
        this.sc = sc;
        this.sessionManager = sessionManager;
        this.profileService = profileService;
        this.juegoRepository = juegoRepository;
    }

    /**
     * Menú principal de edición de perfil.
     */
    public void editarPerfil(String currentToken) {
        Optional<Usuario> uo = validarSesion(currentToken);
        if (uo.isEmpty()) return;
        Usuario user = uo.get();

        boolean exit = false;
        while (!exit) {
            System.out.println("\n--- Edición de Perfil de " + user.getUsername() + " ---");
            System.out.println("1) Editar perfil general (Región, Disponibilidad)");
            System.out.println("2) Editar perfil de juego (Rangos, Roles)");
            System.out.println("3) Establecer juego principal");
            System.out.println("0) Volver al menú principal");
            System.out.print("> ");

            String opt = sc.nextLine().trim();
            switch (opt) {
                case "1": editarPerfilGeneral(user); break;
                case "2": editarPerfilDeJuego(user); break;
                case "3": editarJuegoPrincipal(user); break;
                case "0": exit = true; break;
                default: System.out.println("Opción inválida."); break;
            }
        }
    }

    /**
     * Edita los campos generales del perfil.
     */
    private void editarPerfilGeneral(Usuario user) {

        Region reg = null;
        while (reg == null) {
            try {
                System.out.println("Region :" + java.util.Arrays.toString(Region.values()));
                reg = Region.valueOf(sc.nextLine().trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("❌ Región inválida. Intenta de nuevo.");
            }
        }

        System.out.print("Disponibilidad (ej 18:00-22:00): ");
        String disp = sc.nextLine();

        profileService.updateGeneralProfile(user.getId(), reg, disp);
        System.out.println("✅ Perfil general actualizado.");
    }


    private void editarPerfilDeJuego(Usuario user) {

        Juego juegoSeleccionado = elegirJuego();
        if (juegoSeleccionado == null) return;

        String juegoId = juegoSeleccionado.getNombre();
        JuegoFactory factory = juegoSeleccionado.getJuegoFactory();


        List<String> rangosDisponibles = factory.getRangosDelJuego();
        String rango = null;
        while (rango == null) {
            System.out.println("Rangos disponibles para " + juegoSeleccionado.getNombre() + ": " + rangosDisponibles);
            System.out.print("Elige tu rango: ");
            String input = sc.nextLine().trim().toUpperCase();
            if (rangosDisponibles.contains(input)) {
                rango = input;
            } else {
                System.out.println("❌ Rango inválido. Intenta de nuevo.");
            }
        }


        List<String> rolesDisponibles = factory.getRolesDelJuego();
        Set<String> roles = new HashSet<>();
        while (roles.isEmpty()) {
            System.out.println("Roles disponibles para " + juegoSeleccionado.getNombre() + ": " + rolesDisponibles);
            System.out.print("Roles preferidos : ");
            String rolesIn = sc.nextLine().trim().toUpperCase();

            Set<String> rolesInput = Set.of(rolesIn.split(","));
            boolean allValid = true;

            for (String r : rolesInput) {
                String rolTrimmed = r.trim();
                if (rolTrimmed.isEmpty()) continue;

                if (rolesDisponibles.contains(rolTrimmed)) {
                    roles.add(rolTrimmed);
                } else {
                    System.out.println("❌ Rol '" + rolTrimmed + "' es inválido.");
                    allValid = false;
                    roles.clear();
                    break;
                }
            }
            if (!allValid) System.out.println("Por favor, reingresa todos tus roles.");
        }


        profileService.updateGameProfile(user.getId(), juegoId, rango, roles);
        System.out.println("✅ Perfil de " + juegoSeleccionado.getNombre() + " actualizado.");
    }


    private void editarJuegoPrincipal(Usuario user) {
        Set<String> juegosConfiguradosIds = user.getPerfil().getPerfilesDeJuego().keySet();

        if (juegosConfiguradosIds.isEmpty()) {
            System.out.println("⚠️ Aún no has configurado ningún perfil de juego. Edita uno primero (Opción 2).");
            return;
        }


        Map<String, String> nombresJuegos = juegosConfiguradosIds.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        id -> juegoRepository.findByStringId(id).map(Juego::getNombre).orElse(id) // Valor: "Valorant"
                ));

        String juegoId = null;
        while(juegoId == null) {
            System.out.println("¿Qué juego quieres marcar como principal?");
            System.out.println("Juegos configurados: " + nombresJuegos);
            System.out.print("> ");
            String input = sc.nextLine().trim().toLowerCase();

            if (juegosConfiguradosIds.contains(input)) {
                juegoId = input;
            } else {
                System.out.println("❌ No has configurado ese juego. Elige uno de la lista.");
            }
        }

        profileService.setJuegoPrincipal(user.getId(), juegoId);
        System.out.println("✅ " + nombresJuegos.get(juegoId) + " establecido como juego principal.");
    }



    public void mostrarUsuario(String currentToken) {
        validarSesion(currentToken).ifPresentOrElse(
                u -> System.out.println(u),
                () -> System.out.println("⚠️ Sesión inválida")
        );
    }



    /**
     * Valida el token y devuelve el usuario si es válido.
     */
    private Optional<Usuario> validarSesion(String currentToken) {
        if (currentToken == null) {
            System.out.println("⚠️ No estás logueado.");
            return Optional.empty();
        }
        Optional<Usuario> uo = sessionManager.getUser(currentToken);
        if (uo.isEmpty()) {
            System.out.println("⚠️ Sesión no válida. Por favor, vuelve a iniciar sesión.");
            return Optional.empty();
        }
        return uo;
    }

    /**
     * Muestra un menú para elegir un juego del repositorio.
     */
    private Juego elegirJuego() {
        Map<String, Juego> juegosDisponibles = juegoRepository.getJuegosDisponibles();
        while (true) {
            System.out.println("¿Para qué juego deseas editar el perfil?");

            int i = 1;
            for (Juego j : juegosDisponibles.values()) {
                System.out.printf("%d) %s (%s)\n", i++, j.getId(), j.getNombre());
            }
            System.out.println("0) Cancelar");
            System.out.print("> ");
            String input = sc.nextLine().trim().toLowerCase();

            if (input.equals("0")) return null;


            Optional<Juego> juegoOpt = juegoRepository.findByStringId(input);

            if (juegoOpt.isEmpty()) {
                try {
                    int num = Integer.parseInt(input);
                    if (num > 0 && num <= juegosDisponibles.size()) {

                        juegoOpt = Optional.of(juegosDisponibles.values().stream().toList().get(num - 1));
                    }
                } catch (NumberFormatException e) {

                }
            }

            if (juegoOpt.isPresent()) {
                return juegoOpt.get();
            } else {
                System.out.println("❌ Juego inválido. Intenta de nuevo.");
            }
        }
    }
}