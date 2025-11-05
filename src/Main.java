import Controller.AuthController;
import Controller.ProfileController;
import Infraestructura.UsuarioRepository;
import ScrimsAuth.AuthService;
import ScrimsAuth.SessionManager;
import ScrimsLifecycle.scheduler.ScrimSchedulerService;
import Service.*;
import Infraestructura.JuegoRepository;
import Infraestructura.PostulacionRepository;
import Infraestructura.RepositorioDeScrims;
import java.util.Scanner;
import Domain.Events.DomainEventBus;
import Infraestructura.BusquedaFavoritaRepository;
import Controller.ScrimController;


public class Main {

    private final Scanner sc = new Scanner(System.in);

    // --- Repositorios ---
    private final UsuarioRepository repo = new UsuarioRepository();
    private final JuegoRepository juegoRepository = JuegoRepository.getInstance();
    // repo 2
    private final RepositorioDeScrims scrimRepo = new RepositorioDeScrims();
    private final BusquedaFavoritaRepository busquedaRepo = new BusquedaFavoritaRepository();
    private final PostulacionRepository postulacionRepo = new PostulacionRepository(); // ¡Nueva Instancia!
    // --- Servicios ---
    private final EmailService emailService = new EmailService();
    private final AuthService authService = new AuthService(repo, emailService);
    private final SessionManager sessions = SessionManager.getInstance();
    private final ProfileService profileService = new ProfileService(repo);
    private final ScrimLifecycleService lifecycleService = new ScrimLifecycleService();
    private final ScrimSchedulerService schedulerService = new ScrimSchedulerService(lifecycleService);
    // *** Event Bus y Servicios de Scrims (Integrante 2) ***
    private final DomainEventBus eventBus = DomainEventBus.getInstance();
    private final ScrimAppService scrimAppService;

    // --- Controladores ---
    private final AuthController authController = new AuthController(sc, authService, sessions);
    private final ProfileController profileController;
    // 2
    private final ScrimController scrimController;

    private String currentToken = null;

    public static void main(String[] args) {

        new Main().run();
    }


    public Main() {

        this.scrimAppService = new ScrimAppService(
                scrimRepo,
                repo,
                eventBus,
                postulacionRepo,
                juegoRepository,
                busquedaRepo,
                lifecycleService
        );

        this.profileController = new ProfileController(
                sc,
                sessions,
                profileService,
                juegoRepository
        );
        this.scrimController = new ScrimController(sc, sessions, scrimAppService, busquedaRepo, juegoRepository);
        eventBus.subscribe(new BusquedaFavoritaSubscriber(busquedaRepo, scrimRepo));
        System.out.println("[SISTEMA] Iniciando el scheduler de ciclo de vida de Scrims (cada 15 seg)...");
        schedulerService.start(15000); // 15000 ms = 15 segundos
    }




    public void run() {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n--- eScrims - Auth & Profile & Scrims ---"); 
            System.out.println("1) Registrar \n2) Registrar (OAuth simulado)\n3) Verificar email\n4) Login\n5) Editar perfil\n6) Mostrar mi usuario\n7) Logout");
            System.out.println("8) Gestión de Scrims (Crear/Buscar/Postular/Ver)");
            System.out.println("9) Confirmar asistencia a Scrim");
            System.out.println("0) Salir");
            System.out.print("> ");


            if (!sc.hasNextLine()) break;
            String opt = sc.nextLine().trim();

            switch (opt) {
                case "1": authController.registerClassic(); break;
                case "2": authController.registerOAuth(); break;
                case "3": authController.verifyEmail(); break;
                case "4":

                    currentToken = authController.login();
                    break;
                case "5": profileController.editarPerfil(currentToken); break;
                case "6": profileController.mostrarUsuario(currentToken); break;
                case "7":
                    authController.logout(currentToken);
                    currentToken = null;
                    break;
                case "8": scrimController.gestionarScrims(currentToken); break;
                case "9": confirmarAsistencia(currentToken); break;
                case "0": exit = true; break;

                default: System.out.println("Opción inválida"); break;
            }
        }
        System.out.println("Gracias por jugar con nosotros");
    }
    private void confirmarAsistencia(String currentToken) {
        if (currentToken == null) {
            System.out.println("Debes estar logueado para confirmar.");
            return;
        }

        // Obtenemos el ID de usuario (String) de la sesión
        String usuarioId = sessions.getUser(currentToken)
                .map(Models.Usuario::getId)
                .orElse(null);
        if (usuarioId == null) {
            System.out.println("Sesión inválida.");
            return;
        }

        System.out.print("Ingresa el ID del Scrim al que quieres confirmar asistencia (UUID): ");
        String scrimIdStr = sc.nextLine().trim();

        try {
            java.util.UUID scrimId = java.util.UUID.fromString(scrimIdStr);

            // Llamamos al servicio de ciclo de vida
            lifecycleService.confirmar(scrimId, usuarioId);

            System.out.println("¡Asistencia confirmada para el Scrim " + scrimId + "!");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: ID de Scrim inválido.");
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

}