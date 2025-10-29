package Models;

public class ClassicUser extends Usuario {
    private String passwordHash; // PBKDF2 hashed

    public ClassicUser() {}

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    @Override
    public String userType() { return "CLASSIC"; }
}


