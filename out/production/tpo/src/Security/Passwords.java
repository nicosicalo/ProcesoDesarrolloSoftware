package Security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;


//hasheo de contraseña utilizando la bilioteca criptocgrafica de java PBKDF2(Password-Based Key Derivation Function 2) con HMAC-SHA512.

public class Passwords {
    private static final SecureRandom RAND = new SecureRandom();
    private static final int ITER = 65536;
    private static final int KEY_LEN = 256;

    public static String hashPassword(char[] password) {
        byte[] salt = new byte[16];
        RAND.nextBytes(salt);
        byte[] hash = pbkdf2(password, salt, ITER, KEY_LEN);
        // formateo: iter:salt:hash (base64)
        return ITER + ":" + Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verifyPassword(char[] password, String stored) {
        try {
            String[] parts = stored.split(":");
            int iterations = Integer.parseInt(parts[0]);
            byte[] salt = Base64.getDecoder().decode(parts[1]);
            byte[] hash = Base64.getDecoder().decode(parts[2]);
            byte[] test = pbkdf2(password, salt, iterations, hash.length * 8);
            return Arrays.equals(hash, test);
        } catch (Exception e) {
            return false;
        }
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLen) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLen);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            return skf.generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

