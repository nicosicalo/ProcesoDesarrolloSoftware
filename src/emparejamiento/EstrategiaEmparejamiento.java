// EstrategiaEmparejamiento.java (Interfaz Strategy)
import java.util.List;

public interface EstrategiaEmparejamiento {
    Equipo buscarEquipo(List<Jugador> listaEspera);
}