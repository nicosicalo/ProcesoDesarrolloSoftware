package command;

import model.Equipo;
import model.Jugador;
import model.Rol;

/**
 * Comando para intercambiar roles entre dos jugadores (Patron Command)
 */
public class SwapRolesCommand implements Command {
    private Equipo equipo;
    private Jugador jugador1;
    private Jugador jugador2;
    private Rol rol1;
    private Rol rol2;
    private boolean ejecutado = false;

    public SwapRolesCommand(Equipo equipo, Jugador jugador1, Jugador jugador2) {
        this.equipo = equipo;
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
    }

    @Override
    public boolean ejecutar() {
        // Encontrar los roles actuales de cada jugador
        rol1 = null;
        rol2 = null;
        
        for (var entry : equipo.getJugadoresPorRol().entrySet()) {
            if (entry.getValue().equals(jugador1)) {
                rol1 = entry.getKey();
            }
            if (entry.getValue().equals(jugador2)) {
                rol2 = entry.getKey();
            }
        }
        
        if (rol1 == null || rol2 == null) {
            System.out.println("[ERROR] Ambos jugadores deben tener roles asignados para intercambiarlos");
            return false;
        }
        
        // Realizar el intercambio
        equipo.removerJugador(rol1);
        equipo.removerJugador(rol2);
        equipo.agregarJugador(jugador1, rol2);
        equipo.agregarJugador(jugador2, rol1);
        
        ejecutado = true;
        System.out.println("[SWAP] Roles intercambiados:");
        System.out.println("   " + jugador1.getNombre() + " ahora es " + rol2.getNombre());
        System.out.println("   " + jugador2.getNombre() + " ahora es " + rol1.getNombre());
        
        return true;
    }

    @Override
    public void deshacer() {
        if (!ejecutado) return;
        
        // Intercambiar de vuelta
        equipo.removerJugador(rol1);
        equipo.removerJugador(rol2);
        equipo.agregarJugador(jugador1, rol1);
        equipo.agregarJugador(jugador2, rol2);
        
        System.out.println("[UNDO] Intercambio de roles deshecho");
        ejecutado = false;
    }

    @Override
    public String getDescripcion() {
        return "Intercambiar roles entre " + jugador1.getNombre() + " y " + jugador2.getNombre();
    }
}

