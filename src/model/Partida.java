package model;

/**
 * Clase que representa una partida emparejada
 */
public class Partida {
    private String id;
    private Equipo equipo1;
    private Equipo equipo2;
    private String estado; // "En espera", "En progreso", "Finalizada"

    public Partida(String id, Equipo equipo1, Equipo equipo2) {
        this.id = id;
        this.equipo1 = equipo1;
        this.equipo2 = equipo2;
        this.estado = "En espera";
    }

    public String getId() { return id; }
    public Equipo getEquipo1() { return equipo1; }
    public Equipo getEquipo2() { return equipo2; }
    public String getEstado() { return estado; }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return String.format("Partida %s - Estado: %s\nEquipo 1:\n%s\nEquipo 2:\n%s", 
                           id, estado, equipo1, equipo2);
    }
}

