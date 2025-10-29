package Models;

import Enums.Rango;
import Enums.Region;
import Enums.Rol;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Perfil {
    private String juegoPrincipal;
    private Rango rangoPorJuego;
    private Set<Rol> rolesPreferidos = new HashSet<>();
    private Region region;
    private String disponibilidadHoraria; // p.ej "18:00-22:00"

    public Perfil() { }

    // getters y setters
    public String getJuegoPrincipal() { return juegoPrincipal; }
    public void setJuegoPrincipal(String juegoPrincipal) { this.juegoPrincipal = juegoPrincipal; }

    public Rango getRangoPorJuego() { return rangoPorJuego; }
    public void setRangoPorJuego(Rango rangoPorJuego) { this.rangoPorJuego = rangoPorJuego; }

    public Set<Rol> getRolesPreferidos() { return rolesPreferidos; }
    public void setRolesPreferidos(Set<Rol> rolesPreferidos) { this.rolesPreferidos = rolesPreferidos; }

    public Region getRegion() { return region; }
    public void setRegion(Region region) { this.region = region; }

    public String getDisponibilidadHoraria() { return disponibilidadHoraria; }
    public void setDisponibilidadHoraria(String disponibilidadHoraria) { this.disponibilidadHoraria = disponibilidadHoraria; }

    @Override
    public String toString() {
        return "Perfil{" +
                "juegoPrincipal='" + juegoPrincipal + '\'' +
                ", rangoPorJuego=" + rangoPorJuego +
                ", rolesPreferidos=" + rolesPreferidos +
                ", region=" + region +
                ", disponibilidadHoraria='" + disponibilidadHoraria + '\'' +
                '}';
    }

    @Override //aca verificamos cuando 2 perfiles son iguales en contenido pero  que son distintos(por el tema de la memoria simulada)
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Perfil)) return false;
        Perfil perfil = (Perfil) o;
        return Objects.equals(juegoPrincipal, perfil.juegoPrincipal) &&
                rangoPorJuego == perfil.rangoPorJuego &&
                Objects.equals(rolesPreferidos, perfil.rolesPreferidos) &&
                region == perfil.region &&
                Objects.equals(disponibilidadHoraria, perfil.disponibilidadHoraria);
    }

    @Override //hashea los onjetos para hacer los mapeos en el hashSet
    public int hashCode() {
        return Objects.hash(juegoPrincipal, rangoPorJuego, rolesPreferidos, region, disponibilidadHoraria);
    }
}