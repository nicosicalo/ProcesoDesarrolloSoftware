package system;

import java.util.ArrayList;
import java.util.List;
import model.Jugador;

/**
 * Gestor de lista de espera y suplentes
 */
public class ListaEsperaManager {
    private List<Jugador> listaEspera;
    private List<Jugador> suplentesGlobales;

    public ListaEsperaManager() {
        this.listaEspera = new ArrayList<>();
        this.suplentesGlobales = new ArrayList<>();
    }

    /**
     * Agrega un jugador a la lista de espera
     */
    public void agregarAListaEspera(Jugador jugador) {
        if (!listaEspera.contains(jugador)) {
            listaEspera.add(jugador);
            System.out.println("[ESPERA] " + jugador.getNombre() + " agregado a lista de espera");
        }
    }

    /**
     * Remueve un jugador de la lista de espera
     */
    public void removerDeListaEspera(Jugador jugador) {
        if (listaEspera.remove(jugador)) {
            System.out.println("[OK] " + jugador.getNombre() + " removido de lista de espera");
        }
    }

    /**
     * Agrega un jugador a la lista de suplentes globales
     */
    public void agregarSuplente(Jugador jugador) {
        if (!suplentesGlobales.contains(jugador)) {
            suplentesGlobales.add(jugador);
            System.out.println("[SUPLENTE] " + jugador.getNombre() + " agregado a suplentes globales");
        }
    }

    /**
     * Obtiene un suplente disponible
     */
    public Jugador obtenerSuplente() {
        if (suplentesGlobales.isEmpty()) {
            return null;
        }
        return suplentesGlobales.remove(0);
    }

    public List<Jugador> getListaEspera() {
        return new ArrayList<>(listaEspera);
    }

    public List<Jugador> getSuplentesGlobales() {
        return new ArrayList<>(suplentesGlobales);
    }

    /**
     * Muestra el estado actual de la lista de espera y suplentes
     */
    public void mostrarEstado() {
        System.out.println("\n[ESTADO] Estado de Lista de Espera y Suplentes:");
        System.out.println("   Lista de Espera (" + listaEspera.size() + " jugadores):");
        for (Jugador j : listaEspera) {
            System.out.println("      - " + j.toString());
        }
        System.out.println("   Suplentes Globales (" + suplentesGlobales.size() + " jugadores):");
        for (Jugador j : suplentesGlobales) {
            System.out.println("      - " + j.toString());
        }
    }
}

