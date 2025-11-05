package matching;

import java.util.List;
import model.Jugador;
import model.Equipo;

/**
 * Interfaz del patron Strategy para diferentes estrategias de emparejamiento
 */
public interface MatchingStrategy {
    /**
     * Busca un equipo compatible para el jugador segun la estrategia
     * @param jugador Jugador que busca emparejamiento
     * @param jugadoresDisponibles Lista de jugadores disponibles
     * @param equiposParciales Lista de equipos parcialmente formados
     * @return Equipo encontrado o null si no hay match
     */
    Equipo buscarEmparejamiento(Jugador jugador, List<Jugador> jugadoresDisponibles, 
                                List<Equipo> equiposParciales);
    
    /**
     * Obtiene el nombre de la estrategia
     */
    String getNombre();
}

