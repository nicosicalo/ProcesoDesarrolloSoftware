package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase que representa un equipo de jugadores con sus roles asignados
 */
public class Equipo {
    private String id;
    private Map<Rol, Jugador> jugadoresPorRol;
    private List<Jugador> suplentes;
    private int puntuacionPromedio;

    public Equipo(String id) {
        this.id = id;
        this.jugadoresPorRol = new HashMap<>();
        this.suplentes = new ArrayList<>();
        this.puntuacionPromedio = 0;
    }

    public void agregarJugador(Jugador jugador, Rol rol) {
        jugadoresPorRol.put(rol, jugador);
        actualizarPuntuacionPromedio();
    }

    public void agregarSuplente(Jugador jugador) {
        if (!suplentes.contains(jugador)) {
            suplentes.add(jugador);
        }
    }

    public void removerJugador(Rol rol) {
        jugadoresPorRol.remove(rol);
        actualizarPuntuacionPromedio();
    }

    public Jugador getJugador(Rol rol) {
        return jugadoresPorRol.get(rol);
    }

    public Map<Rol, Jugador> getJugadoresPorRol() {
        return new HashMap<>(jugadoresPorRol);
    }

    public List<Jugador> getSuplentes() {
        return new ArrayList<>(suplentes);
    }

    public int getPuntuacionPromedio() {
        return puntuacionPromedio;
    }

    public String getId() {
        return id;
    }

    private void actualizarPuntuacionPromedio() {
        if (jugadoresPorRol.isEmpty()) {
            puntuacionPromedio = 0;
            return;
        }
        
        int total = jugadoresPorRol.values().stream()
            .mapToInt(Jugador::getPuntosMMR)
            .sum();
        puntuacionPromedio = total / jugadoresPorRol.size();
    }

    public boolean estaCompleto() {
        return jugadoresPorRol.size() == Rol.values().length;
    }

    public int cantidadJugadores() {
        return jugadoresPorRol.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Equipo ").append(id).append(" (MMR Promedio: ").append(puntuacionPromedio).append(")\n");
        jugadoresPorRol.forEach((rol, jugador) -> 
            sb.append("  ").append(rol.getNombre()).append(": ").append(jugador.getNombre()).append("\n")
        );
        if (!suplentes.isEmpty()) {
            sb.append("  Suplentes: ");
            suplentes.forEach(j -> sb.append(j.getNombre()).append(", "));
            sb.append("\n");
        }
        return sb.toString();
    }
}

