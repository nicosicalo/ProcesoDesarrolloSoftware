import Controller.AuthController;
import Controller.ProfileController;
import Infraestructura.UsuarioRepository;
import ScrimsAuth.AuthService;
import ScrimsAuth.SessionManager;
import Service.EmailService;
import Service.ProfileService;

import java.util.Scanner;

/**
 * Clase principal que actúa como el Composition Root (inicialización de dependencias)
 * y el bucle principal de la aplicación de consola.
 * Delega las operaciones de negocio y de UI a los Controllers.
 */
public class Main {

    private final Scanner sc = new Scanner(System.in);
    // Infraestructura
    private final UsuarioRepository repo = new UsuarioRepository();
    private final EmailService emailService = new EmailService();

    // Servicios (Capa de Negocio)
    private final AuthService authService = new AuthService(repo, emailService);
    private final SessionManager sessions = SessionManager.getInstance();
    private final ProfileService profileService = new ProfileService(repo);

    // Controladores (Capa de Presentación/Control)
    private final AuthController authController = new AuthController(sc, authService, sessions);
    private final ProfileController profileController = new ProfileController(sc, sessions, profileService);

    // Estado de la aplicación
    private String currentToken = null;

    public static void main(String[] args) {
        // Ejecutar en una instancia no estática
        new Main().run();
    }

    public void run() {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n--- eScrims - Auth & Profile (Consola) ---");
            System.out.println("1) Registrar (Classic)\n2) Registrar (OAuth simulado)\n3) Verificar email\n4) Login\n5) Editar perfil (debe estar logueado)\n6) Mostrar mi usuario\n7) Logout\n0) Salir");
            System.out.print("> ");

            if (!sc.hasNextLine()) break; // Para manejo de EOF
            String opt = sc.nextLine().trim();

            switch (opt) {
                case "1": authController.registerClassic(); break;
                case "2": authController.registerOAuth(); break;
                case "3": authController.verifyEmail(); break;
                case "4":
                    // El login devuelve el nuevo token, el Main es quien mantiene el estado
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
