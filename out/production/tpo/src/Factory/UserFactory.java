package Factory;


import Models.ClassicUser;
import Models.OAuthUser;
import Models.Usuario;


public class UserFactory {
    public static Usuario createClassicUser(String username, String email, String passwordHash) {
        ClassicUser u = new ClassicUser();
        u.setUsername(username);
        u.setEmail(email);
        u.setPasswordHash(passwordHash);
        u.setVerified(false);
        return u;
    }

    public static Usuario createOAuthUser(String username, String email, String provider) {
        OAuthUser u = new OAuthUser(provider);
        u.setUsername(username);
        u.setEmail(email);
        u.setVerified(true); // asumimos verificado por provider
        return u;
    }
}
