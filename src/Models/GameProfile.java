package Models;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class GameProfile {
    private int id;

    private String rango;

    private Set<String> roles = new HashSet<>();
    public GameProfile() { }
    public GameProfile(String rango, Set<String> roles) {
        this.rango = rango;
        this.roles = roles;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRango() {
        return rango;
    }

    public void setRango(String rango) {
        this.rango = rango;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }



    @Override
    public String toString() {
        return "GameProfile{" +
                "rango='" + rango + '\'' +
                ", roles=" + roles +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameProfile that = (GameProfile) o;


        return Objects.equals(rango, that.rango) &&
                Objects.equals(roles, that.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rango, roles);
    }
}

