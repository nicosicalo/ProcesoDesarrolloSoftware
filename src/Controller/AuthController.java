package Controller;

import Models.Usuario;
import ScrimsAuth.AuthService;
import ScrimsAuth.SessionManager;


import java.util.Optional;
import java.util.Scanner;


public class AuthController {
    private final Scanner sc;
    private final AuthService authService;
    private final SessionManager sessionManager;

    public AuthController(Scanner sc, AuthService authService, SessionManager sessionManager) {
        this.sc = sc;
        this.authService = authService;
        this.sessionManager = sessionManager;
    }

    /**
     * Intenta registrar un usuario clásico.
     */
    public void registerClassic() {
        System.out.print("Username: "); String u = sc.nextLine();
        System.out.print("Email: "); String e = sc.nextLine();
        System.out.print("Password: "); char[] p = sc.nextLine().toCharArray();

        try {
            Optional<String> tokenOpt = authService.registerClassic(u, e, p);
            if (tokenOpt.isPresent()) {
                System.out.println("✅ Registrado con éxito. Token de verificación (simulado): " + tokenOpt.get());
                System.out.println("Use la opción 3 para verificar.");
            } else {
                System.out.println("❌ Fallo en registro: El email o username ya existe.");
            }
        } finally {
            // Limpiar la contraseña del array de caracteres por seguridad
            java.util.Arrays.fill(p, ' ');
        }
    }

    /**
     * Simula el registro mediante un proveedor OAuth.
     */
    public void registerOAuth() {
        System.out.print("Provider (STEAM/DISCORD/RIOT): "); String prov = sc.nextLine();
        System.out.print("Username: "); String u = sc.nextLine();
        System.out.print("Email: "); String e = sc.nextLine();

        Optional<Usuario> opt = authService.registerOAuth(u, e, prov);
        if (opt.isPresent()) {
            System.out.println("✅ Registrado vía OAuth. Usuario verificado por proveedor.");
        } else {
            System.out.println("❌ Fallo en registro: El email ya existe.");
        }
    }

    /**
     * Intenta verificar el email de un usuario.
     */
    public void verifyEmail() {
        System.out.print("Email: "); String e = sc.nextLine();
        System.out.print("Token: "); String t = sc.nextLine();
        boolean ok = authService.verifyEmail(t, e);
        System.out.println(ok ? "✅ Email verificado!" : "❌ Token o Email inválido.");
    }

    /**
     * Intenta loguear al usuario y crea una sesión, devolviendo el token.
     * @return El token de sesión si el login es exitoso, o null.
     */
    public String login() {
        System.out.print("Email: "); String e = sc.nextLine();
        System.out.print("Password: "); char[] p = sc.nextLine().toCharArray();

        try {
            Optional<Usuario> u = authService.loginClassic(e, p);
            if (u.isPresent()) {
                String token = sessionManager.createSession(u.get());
                System.out.println("✅ Login correcto. Session token: " + token);
                return token;
            } else {
                System.out.println("❌ Credenciales inválidas.");
                return null;
            }
        } finally {
            java.util.Arrays.fill(p, ' ');
        }
    }

    /**
     * Invalida la sesión actual.
     * @param token El token de sesión a invalidar.
     */
    public void logout(String token) {
        if (token != null) {
            sessionManager.invalidate(token);
        }
        System.out.println("🚪 Logout OK.");
    }
}