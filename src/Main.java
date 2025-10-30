import Controller.AuthController;
import Controller.ProfileController;
import Infraestructura.UsuarioRepository;
import ScrimsAuth.AuthService;
import ScrimsAuth.SessionManager;
import Service.EmailService;
import Service.ProfileService;
import Factory.CsgoFactory;
import Factory.LolFactory;
import Factory.ValorantFactory;
import Infraestructura.JuegoRepository;
import Infraestructura.UsuarioRepository;
import Models.Juego;
import java.util.Scanner;


public class Main {

    private final Scanner sc = new Scanner(System.in);

    // --- Repositorios ---
    private final UsuarioRepository repo = new UsuarioRepository();
    private final JuegoRepository juegoRepository = JuegoRepository.getInstance();

    // --- Servicios ---
    private final EmailService emailService = new EmailService();
    private final AuthService authService = new AuthService(repo, emailService);
    private final SessionManager sessions = SessionManager.getInstance();
    private final ProfileService profileService = new ProfileService(repo);

    // --- Controladores ---
    private final AuthController authController = new AuthController(sc, authService, sessions);
    private final ProfileController profileController;

    private String currentToken = null;

    public static void main(String[] args) {

        new Main().run();
    }


    public Main() {




        this.profileController = new ProfileController(
                sc,
                sessions,
                profileService,
                juegoRepository
        );
    }




    public void run() {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n--- eScrims - Auth & Profile");
            System.out.println("1) Registrar \n2) Registrar (OAuth simulado)\n3) Verificar email\n4) Login\n5) Editar perfil (debe estar logueado)\n6) Mostrar mi usuario\n7) Logout\n0) Salir");
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
                case "0": exit = true; break;
                default: System.out.println("Opción inválida"); break;
            }
        }
        System.out.println("👋 ¡Hasta pronto!");
    }
}