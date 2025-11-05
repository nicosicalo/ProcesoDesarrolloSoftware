package matching;

import java.util.ArrayList;
import java.util.List;
import model.Jugador;
import model.Equipo;
import model.Rol;

/**
 * Estrategia de emparejamiento por rango/MMR
 * Empareja jugadores con rangos similares (diferencia maxima de 200 puntos)
 */
public class PorRangoMMRStrategy implements MatchingStrategy {
    private static final int DIFERENCIA_MAXIMA_MMR = 200;

    @Override
    public Equipo buscarEmparejamiento(Jugador jugador, List<Jugador> jugadoresDisponibles, 
                                      List<Equipo> equiposParciales) {
        System.out.println("[BUSCANDO] Buscando emparejamiento por Rango/MMR para " + jugador.getNombre());
        
        // Primero intenta unirse a un equipo parcial existente
        for (Equipo equipo : equiposParciales) {
            if (!equipo.estaCompleto() && esCompatibleMMR(jugador, equipo)) {
                // Buscar un rol disponible en el equipo
                for (Rol rol : Rol.values()) {
                    if (equipo.getJugador(rol) == null) {
                        equipo.agregarJugador(jugador, rol);
                        System.out.println("[OK] Jugador agregado a equipo existente");
                        return equipo;
                    }
                }
            }
        }

        // Si no hay equipo disponible, crear uno nuevo y buscar jugadores compatibles
        Equipo nuevoEquipo = new Equipo("Equipo-" + System.currentTimeMillis());
        nuevoEquipo.agregarJugador(jugador, jugador.getRolPreferido());
        
        // Buscar otros jugadores con MMR similar
        List<Jugador> candidatos = filtrarPorMMR(jugadoresDisponibles, jugador);
        candidatos.remove(jugador); // Remover el jugador actual
        
        int jugadoresNecesarios = Rol.values().length - 1;
        int agregados = 0;
        
        for (Jugador candidato : candidatos) {
            if (agregados >= jugadoresNecesarios) break;
            
            Rol rolDisponible = buscarRolDisponible(nuevoEquipo);
            if (rolDisponible != null && esCompatibleMMR(candidato, nuevoEquipo)) {
                nuevoEquipo.agregarJugador(candidato, rolDisponible);
                agregados++;
            }
        }
        
        if (nuevoEquipo.cantidadJugadores() > 1) {
            System.out.println("[OK] Equipo nuevo creado con jugadores de MMR similar");
            return nuevoEquipo;
        }
        
        System.out.println("[INFO] No se encontro emparejamiento por MMR");
        return null;
    }

    private boolean esCompatibleMMR(Jugador jugador, Equipo equipo) {
        int diferencia = Math.abs(jugador.getPuntosMMR() - equipo.getPuntuacionPromedio());
        return diferencia <= DIFERENCIA_MAXIMA_MMR;
    }

    private List<Jugador> filtrarPorMMR(List<Jugador> jugadores, Jugador referencia) {
        List<Jugador> compatibles = new ArrayList<>();
        for (Jugador j : jugadores) {
            if (Math.abs(j.getPuntosMMR() - referencia.getPuntosMMR()) <= DIFERENCIA_MAXIMA_MMR) {
                compatibles.add(j);
            }
        }
        return compatibles;
    }

    private Rol buscarRolDisponible(Equipo equipo) {
        for (Rol rol : Rol.values()) {
            if (equipo.getJugador(rol) == null) {
                return rol;
            }
        }
        return null;
    }

    @Override
    public String getNombre() {
        return "Por Rango/MMR";
    }
}

