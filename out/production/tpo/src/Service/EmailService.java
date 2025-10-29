package Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

//revisar despues
public class EmailService {
    // token -> email
    private final Map<String, String> tokens = new ConcurrentHashMap<>();

    // simula enviar un email con token de verificación
    public String sendVerificationEmail(String email) {
        String token = UUID.randomUUID().toString();
        tokens.put(token, email.toLowerCase());
        // en app real: enviar email con link que contiene token
        System.out.println("[EmailService] Simulando envío de verificación a " + email + " - token: " + token);
        return token;
    }

    public boolean validateToken(String token, String email) {
        String stored = tokens.get(token);
        if (stored != null && stored.equals(email.toLowerCase())) {
            tokens.remove(token);
            return true;
        }
        return false;
    }

}
