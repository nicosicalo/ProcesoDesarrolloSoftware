package Models;

import Enums.Region; // La región puede seguir siendo global
import java.util.Map;
import java.util.Objects;
import java.util.HashMap;

public class Perfil {

    // El ID del juego (ej: "valorant") que el usuario prefiere
    private String juegoPrincipalId;

    // El mapa que contiene TODOS los perfiles del usuario
    // La clave (String) es el ID del juego ("valorant", "lol", etc.)
    private Map<String, GameProfile> perfilesDeJuego = new HashMap<>();

    // Estos son atributos generales del usuario, no dependen del juego
    private Region region;
    private String disponibilidadHoraria;

    public Perfil() { }

    // --- Getters y Setters ---

    public String getJuegoPrincipalId() { return juegoPrincipalId; }
    public void setJuegoPrincipalId(String juegoPrincipalId) { this.juegoPrincipalId = juegoPrincipalId; }

    public Map<String, GameProfile> getPerfilesDeJuego() { return perfilesDeJuego; }
    public void setPerfilesDeJuego(Map<String, GameProfile> perfilesDeJuego) { this.perfilesDeJuego = perfilesDeJuego; }

    public Region getRegion() { return region; }
    public void setRegion(Region region) { this.region = region; }

    public String getDisponibilidadHoraria() { return disponibilidadHoraria; }
    public void setDisponibilidadHoraria(String disponibilidadHoraria) { this.disponibilidadHoraria = disponibilidadHoraria; }

    // --- Métodos de Conveniencia ---

    /**
     * Obtiene el GameProfile del juego marcado como principal.
     * @return El GameProfile principal, o null si no hay juego principal seteado.
     */
    public GameProfile getPerfilPrincipal() {
        if (juegoPrincipalId == null) return null;
        return perfilesDeJuego.get(juegoPrincipalId);
    }

    /**

     * @param juegoId El ID del juego (ej: "lol")
     * @return El GameProfile para ese juego, o null si el usuario no tiene datos cargados.
     */
    public GameProfile getPerfilPorJuego(String juegoId) {
        return perfilesDeJuego.get(juegoId);
    }

    @Override
    public String toString() {
        return "Perfil{" +
                "juegoPrincipalId='" + juegoPrincipalId + '\'' +
                ", region=" + region +
                ", disponibilidadHoraria='" + disponibilidadHoraria + '\'' +
                ", perfilesDeJuego=" + perfilesDeJuego +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Perfil perfil = (Perfil) o;


        return Objects.equals(juegoPrincipalId, perfil.juegoPrincipalId) &&
                Objects.equals(perfilesDeJuego, perfil.perfilesDeJuego) &&
                region == perfil.region && // '==' está bien para Enums
                Objects.equals(disponibilidadHoraria, perfil.disponibilidadHoraria);
    }

    @Override
    public int hashCode() {

        return Objects.hash(juegoPrincipalId, perfilesDeJuego, region, disponibilidadHoraria);
    }
}