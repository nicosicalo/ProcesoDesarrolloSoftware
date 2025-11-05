package matching;

import java.util.ArrayList;
import java.util.List;
import model.Jugador;
import model.Equipo;
import model.Rol;

/**
 * Estrategia de emparejamiento por latencia/cercania
 * Empareja jugadores de la misma zona o con latencia similar
 */
public class PorLatenciaStrategy implements MatchingStrategy {
    private static final int DIFERENCIA_MAXIMA_LATENCIA = 50;

    @Override
    public Equipo buscarEmparejamiento(Jugador jugador, List<Jugador> jugadoresDisponibles, 
                                      List<Equipo> equiposParciales) {
        System.out.println("[BUSCANDO] Buscando emparejamiento por Latencia para " + jugador.getNombre());
        
        // Primero intenta unirse a un equipo parcial existente
        for (Equipo equipo : equiposParciales) {
            if (!equipo.estaCompleto() && esCompatibleLatencia(jugador, equipo)) {
                for (Rol rol : Rol.values()) {
                    if (equipo.getJugador(rol) == null) {
                        equipo.agregarJugador(jugador, rol);
                        System.out.println("[OK] Jugador agregado a equipo existente (misma zona/baja latencia)");
                        return equipo;
                    }
                }
            }
        }

        // Crear nuevo equipo buscando jugadores de la misma zona o latencia similar
        Equipo nuevoEquipo = new Equipo("Equipo-" + System.currentTimeMillis());
        nuevoEquipo.agregarJugador(jugador, jugador.getRolPreferido());
        
        // Priorizar jugadores de la misma zona
        List<Jugador> mismaZona = new ArrayList<>();
        List<Jugador> latenciaSimilar = new ArrayList<>();
        
        for (Jugador candidato : jugadoresDisponibles) {
            if (candidato.equals(jugador)) continue;
            
            if (candidato.getZona().equals(jugador.getZona())) {
                mismaZona.add(candidato);
            } else if (Math.abs(candidato.getLatencia() - jugador.getLatencia()) <= DIFERENCIA_MAXIMA_LATENCIA) {
                latenciaSimilar.add(candidato);
            }
        }
        
        // Primero agregar de la misma zona, luego de latencia similar
        List<Jugador> candidatos = new ArrayList<>(mismaZona);
        candidatos.addAll(latenciaSimilar);
        
        int jugadoresNecesarios = Rol.values().length - 1;
        int agregados = 0;
        
        for (Jugador candidato : candidatos) {
            if (agregados >= jugadoresNecesarios) break;
            
            Rol rolDisponible = buscarRolDisponible(nuevoEquipo);
            if (rolDisponible != null) {
                nuevoEquipo.agregarJugador(candidato, rolDisponible);
                agregados++;
            }
        }
        
        if (nuevoEquipo.cantidadJugadores() > 1) {
            System.out.println("[OK] Equipo nuevo creado con jugadores de zona/latencia compatible");
            return nuevoEquipo;
        }
        
        System.out.println("[INFO] No se encontro emparejamiento por latencia");
        return null;
    }

    private boolean esCompatibleLatencia(Jugador jugador, Equipo equipo) {
        // Verificar si hay jugadores de la misma zona o con latencia similar
        for (Jugador j : equipo.getJugadoresPorRol().values()) {
            if (j.getZona().equals(jugador.getZona()) || 
                Math.abs(j.getLatencia() - jugador.getLatencia()) <= DIFERENCIA_MAXIMA_LATENCIA) {
                return true;
            }
        }
        return false;
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
        return "Por Latencia/Cercania";
    }
}

