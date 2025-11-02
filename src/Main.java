import Controller.AuthController;
import Controller.ProfileController;
import Infraestructura.UsuarioRepository;
import ScrimsAuth.AuthService;
import ScrimsAuth.SessionManager;
import Service.EmailService;
import Service.ProfileService;

import Infraestructura.JuegoRepository;
import Infraestructura.PostulacionRepository;
import java.util.Scanner;
// *** Nuevas Importaciones para Integrante 2 ***
import Domain.Events.DomainEventBus;
import Service.BusquedaFavoritaSubscriber;
import Service.ScrimAppService;
import Infraestructura.RepositorioDeScrims;
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
            juegoRepository
        );

        this.profileController = new ProfileController(
                sc,
                sessions,
                profileService,
                juegoRepository
        );
        this.scrimController = new ScrimController(sc, sessions, scrimAppService, busquedaRepo, juegoRepository);
        eventBus.subscribe(new BusquedaFavoritaSubscriber(busquedaRepo));
    }




    public void run() {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n--- eScrims - Auth & Profile & Scrims ---"); 
            System.out.println("1) Registrar \n2) Registrar (OAuth simulado)\n3) Verificar email\n4) Login\n5) Editar perfil\n6) Mostrar mi usuario\n7) Logout");
            System.out.println("8) Gestión de Scrims (Crear/Buscar/Postular/Ver)");
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
                case "8": scrimController.gestionarScrims(currentToken); break; // I2
                case "0": exit = true; break;
                default: System.out.println("Opción inválida"); break;
            }
        }
        System.out.println("Gracias por jugar con nosotros");
    }
}