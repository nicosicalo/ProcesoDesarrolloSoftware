package matching;

import java.util.ArrayList;
import java.util.List;
import model.Jugador;
import model.Equipo;
import model.Partida;

/**
 * Contexto que permite cambiar dinamicamente la estrategia de emparejamiento (Patron Strategy)
 */
public class MatchingContext {
    private MatchingStrategy estrategia;
    private List<Equipo> equiposParciales;
    private List<Jugador> listaEspera;
    private List<Partida> partidasEmparejadas;
    private int contadorPartidas;

    public MatchingContext() {
        this.equiposParciales = new ArrayList<>();
        this.listaEspera = new ArrayList<>();
        this.partidasEmparejadas = new ArrayList<>();
        this.contadorPartidas = 1;
        // Estrategia por defecto - CAMBIADA A POR MMR para demostracion
        this.estrategia = new PorRangoMMRStrategy();
    }

    /**
     * Cambia la estrategia de emparejamiento dinamicamente
     */
    public void cambiarEstrategia(MatchingStrategy nuevaEstrategia) {
        System.out.println("\n[ESTRATEGIA] Cambiando estrategia de emparejamiento a: " + nuevaEstrategia.getNombre());
        this.estrategia = nuevaEstrategia;
    }

    /**
     * Busca emparejamiento para un jugador usando la estrategia actual
     */
    public Partida buscarEmparejamiento(Jugador jugador) {
        System.out.println("\n[ESTRATEGIA] Estrategia actual: " + estrategia.getNombre());
        
        Equipo equipo = estrategia.buscarEmparejamiento(jugador, listaEspera, equiposParciales);
        
        if (equipo == null) {
            // No se encontro emparejamiento, agregar a lista de espera
            if (!listaEspera.contains(jugador)) {
                listaEspera.add(jugador);
                System.out.println("[ESPERA] Jugador agregado a lista de espera");
            }
            return null;
        }

        // Si el equipo esta completo, buscar oponente
        if (equipo.estaCompleto()) {
            equiposParciales.remove(equipo);
            
            // Buscar equipo oponente completo
            Equipo oponente = buscarEquipoOponente(equipo);
            
            if (oponente != null) {
                equiposParciales.remove(oponente);
                Partida partida = new Partida("PART-" + contadorPartidas++, equipo, oponente);
                partidasEmparejadas.add(partida);
                System.out.println("\n[MATCH] PARTIDA ENCONTRADA!");
                return partida;
            } else {
                // Guardar equipo completo para buscar oponente
                equiposParciales.add(equipo);
                System.out.println("[OK] Equipo completo formado, esperando oponente...");
            }
        } else {
            // Agregar o actualizar equipo parcial
            if (!equiposParciales.contains(equipo)) {
                equiposParciales.add(equipo);
            }
            System.out.println("[ESPERA] Equipo parcial formado, esperando mas jugadores...");
        }
        
        return null;
    }

    private Equipo buscarEquipoOponente(Equipo equipo) {
        // Buscar un equipo completo con puntuacion similar
        int mmrObjetivo = equipo.getPuntuacionPromedio();
        int diferenciaAceptable = 150;
        
        for (Equipo e : equiposParciales) {
            if (e.estaCompleto() && e != equipo) {
                int diferencia = Math.abs(e.getPuntuacionPromedio() - mmrObjetivo);
                if (diferencia <= diferenciaAceptable) {
                    return e;
                }
            }
        }
        return null;
    }

    public List<Equipo> getEquiposParciales() {
        return new ArrayList<>(equiposParciales);
    }

    public List<Jugador> getListaEspera() {
        return new ArrayList<>(listaEspera);
    }

    public List<Partida> getPartidasEmparejadas() {
        return new ArrayList<>(partidasEmparejadas);
    }

    public MatchingStrategy getEstrategiaActual() {
        return estrategia;
    }

    /**
     * Agrega una partida a la lista de partidas emparejadas
     */
    public void agregarPartida(Partida partida) {
        if (partida != null && !partidasEmparejadas.contains(partida)) {
            partidasEmparejadas.add(partida);
        }
    }
}

