import java.util.List;
import java.util.stream.Collectors;

public class EstrategiaPorMMR implements EstrategiaEmparejamiento {
    @Override
    public Equipo buscarEquipo(List<Jugador> listaEspera) {
        // Lógica simulada: Agrupa jugadores con MMR similar (ej: diferencia < 100)
        List<Jugador> equipoJugadores = listaEspera.stream()
            .filter(j -> j.getMmr() > 1500 && j.getMmr() < 1700)
            .limit(5)
            .collect(Collectors.toList());

        if (equipoJugadores.size() == 5) {
            System.out.println("\n✅ EMPAREJAMIENTO_MMR: ¡Equipo encontrado! Por rango/MMR similar.");
            return new Equipo(equipoJugadores);
        }
        System.out.println("❌ EMPAREJAMIENTO_MMR: No se pudo formar equipo por MMR. Faltan " + (5 - equipoJugadores.size()) + " jugadores.");
        return null; // O lógica de lista de espera
    }
}