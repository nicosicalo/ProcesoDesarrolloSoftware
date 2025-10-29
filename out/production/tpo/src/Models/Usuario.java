package Models;

import java.util.UUID;

public abstract class Usuario {
    protected final String id = UUID.randomUUID().toString();
    protected String username;
    protected String email;
    protected boolean verified = false;
    protected Perfil perfil = new Perfil();

    public String getId() { return id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }
    public Perfil getPerfil() { return perfil; }
    public void setPerfil(Perfil perfil) { this.perfil = perfil; }

    public abstract String userType();

    @Override
    public String toString() {
        return "Usuario{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", verified=" + verified +
                ", perfil=" + perfil +
                ", type=" + userType() +
                '}';
    }
}
