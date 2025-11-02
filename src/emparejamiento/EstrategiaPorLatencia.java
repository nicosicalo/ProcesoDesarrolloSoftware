package emparejamiento;

import modelos.Equipo;
import modelos.Jugador;
import java.util.List;
import java.util.stream.Collectors;

public class EstrategiaPorLatencia implements EstrategiaEmparejamiento {

    @Override
    public Equipo buscarEquipo(List<Jugador> listaEspera) {
        System.out.println("🔍 ESTRATEGIA_LATENCIA: Buscando 5 jugadores con Latencia < 40ms...");

        // Simulación: Filtra jugadores con baja latencia (cercanos)
        List<Jugador> equipoJugadores = listaEspera.stream()
            .filter(j -> j.getLatencia() < 40) // Lógica de filtro por Latencia
            .limit(5)
            .collect(Collectors.toList());

        if (equipoJugadores.size() == 5) {
            System.out.println("\n✅ EMPAREJAMIENTO_LATENCIA: ¡Equipo encontrado! Por latencia baja.");
            return new Equipo(equipoJugadores);
        }
        
        System.out.println("❌ EMPAREJAMIENTO_LATENCIA: No se pudo formar equipo. Faltan " + (5 - equipoJugadores.size()) + " jugadores con baja latencia.");
        return null;
    }
}