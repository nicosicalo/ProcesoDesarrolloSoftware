package ScrimsAuth;

import Factory.UserFactory;
import Infraestructura.UsuarioRepository;
import Models.ClassicUser;
import Models.Usuario;
import Security.Passwords;
import Service.EmailService;

import java.util.Optional;

public class AuthService {
    private final UsuarioRepository repo;
    private final EmailService emailService;

    public AuthService(UsuarioRepository repo, EmailService emailService) {
        this.repo = repo;
        this.emailService = emailService;
    }

    public Optional<String> registerClassic(String username, String email, char[] password) {
        if (repo.findByEmail(email).isPresent()) return Optional.empty();
        if (repo.findByUsername(username).isPresent()) return Optional.empty();

        String hash = Passwords.hashPassword(password);
        Usuario u = UserFactory.createClassicUser(username, email, hash);
        repo.save(u);
        String token = emailService.sendVerificationEmail(email);
        return Optional.of(token);
    }

    public Optional<Usuario> registerOAuth(String username, String email, String provider) {
        if (repo.findByEmail(email).isPresent()) return Optional.empty();
        Usuario u = UserFactory.createOAuthUser(username, email, provider);
        repo.save(u);
        return Optional.of(u);
    }

    public Optional<Usuario> loginClassic(String email, char[] password) {
        Optional<Usuario> found = repo.findByEmail(email);
        if (found.isEmpty()) return Optional.empty();
        Usuario u = found.get();
        if (!(u instanceof ClassicUser)) return Optional.empty();
        ClassicUser cu = (ClassicUser) u;
        boolean ok = Passwords.verifyPassword(password, cu.getPasswordHash());
        return ok ? Optional.of(u) : Optional.empty();
    }

    public boolean verifyEmail(String token, String email) {
        boolean valid = emailService.validateToken(token, email);
        if (!valid) return false;
        repo.findByEmail(email).ifPresent(u -> u.setVerified(true));
        return true;
    }
}

