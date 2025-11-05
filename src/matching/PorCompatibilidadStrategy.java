package matching;

import java.util.ArrayList;
import java.util.List;
import model.Jugador;
import model.Equipo;
import model.Rol;

/**
 * Estrategia de emparejamiento por compatibilidad/historial
 * Empareja jugadores con historiales similares (win rate, partidas jugadas, etc.)
 */
public class PorCompatibilidadStrategy implements MatchingStrategy {
    private static final double DIFERENCIA_MAXIMA_WINRATE = 15.0;
    private static final int DIFERENCIA_MAXIMA_PARTIDAS = 50;

    @Override
    public Equipo buscarEmparejamiento(Jugador jugador, List<Jugador> jugadoresDisponibles, 
                                      List<Equipo> equiposParciales) {
        System.out.println("[BUSCANDO] Buscando emparejamiento por Compatibilidad/Historial para " + jugador.getNombre());
        
        // Primero intenta unirse a un equipo parcial existente
        for (Equipo equipo : equiposParciales) {
            if (!equipo.estaCompleto() && esCompatible(jugador, equipo)) {
                for (Rol rol : Rol.values()) {
                    if (equipo.getJugador(rol) == null) {
                        equipo.agregarJugador(jugador, rol);
                        System.out.println("[OK] Jugador agregado a equipo existente (historial compatible)");
                        return equipo;
                    }
                }
            }
        }

        // Crear nuevo equipo buscando jugadores compatibles
        Equipo nuevoEquipo = new Equipo("Equipo-" + System.currentTimeMillis());
        nuevoEquipo.agregarJugador(jugador, jugador.getRolPreferido());
        
        // Buscar jugadores con historial similar
        List<Jugador> candidatos = filtrarPorCompatibilidad(jugadoresDisponibles, jugador);
        candidatos.remove(jugador);
        
        int jugadoresNecesarios = Rol.values().length - 1;
        int agregados = 0;
        
        for (Jugador candidato : candidatos) {
            if (agregados >= jugadoresNecesarios) break;
            
            Rol rolDisponible = buscarRolDisponible(nuevoEquipo);
            if (rolDisponible != null && esCompatible(candidato, nuevoEquipo)) {
                nuevoEquipo.agregarJugador(candidato, rolDisponible);
                agregados++;
            }
        }
        
        if (nuevoEquipo.cantidadJugadores() > 1) {
            System.out.println("[OK] Equipo nuevo creado con jugadores de historial compatible");
            return nuevoEquipo;
        }
        
        System.out.println("[INFO] No se encontro emparejamiento por compatibilidad");
        return null;
    }

    private boolean esCompatible(Jugador jugador, Equipo equipo) {
        // Verificar compatibilidad con el equipo promedio
        double winRatePromedio = equipo.getJugadoresPorRol().values().stream()
            .mapToDouble(Jugador::getWinRate)
            .average()
            .orElse(0.0);
        
        int partidasPromedio = (int) equipo.getJugadoresPorRol().values().stream()
            .mapToInt(Jugador::getPartidasJugadas)
            .average()
            .orElse(0.0);
        
        double diferenciaWinRate = Math.abs(jugador.getWinRate() - winRatePromedio);
        int diferenciaPartidas = Math.abs(jugador.getPartidasJugadas() - partidasPromedio);
        
        return diferenciaWinRate <= DIFERENCIA_MAXIMA_WINRATE && 
               diferenciaPartidas <= DIFERENCIA_MAXIMA_PARTIDAS;
    }

    private List<Jugador> filtrarPorCompatibilidad(List<Jugador> jugadores, Jugador referencia) {
        List<Jugador> compatibles = new ArrayList<>();
        double winRateRef = referencia.getWinRate();
        int partidasRef = referencia.getPartidasJugadas();
        
        for (Jugador j : jugadores) {
            double diferenciaWinRate = Math.abs(j.getWinRate() - winRateRef);
            int diferenciaPartidas = Math.abs(j.getPartidasJugadas() - partidasRef);
            
            if (diferenciaWinRate <= DIFERENCIA_MAXIMA_WINRATE && 
                diferenciaPartidas <= DIFERENCIA_MAXIMA_PARTIDAS) {
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
        return "Por Compatibilidad/Historial";
    }
}

