package Models;

public class OAuthUser extends Usuario {
    private String provider;

    public OAuthUser(String provider) { this.provider = provider; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    @Override
    public String userType() { return "OAUTH(" + provider + ")"; }
}