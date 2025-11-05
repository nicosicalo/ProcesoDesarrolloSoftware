package command;

import model.Equipo;
import model.Jugador;
import model.Rol;

/**
 * Comando para asignar un rol a un jugador en un equipo (Patron Command)
 */
public class AsignarRolCommand implements Command {
    private Equipo equipo;
    private Jugador jugador;
    private Rol rol;
    private Jugador jugadorAnterior; // Jugador que tenia el rol antes

    public AsignarRolCommand(Equipo equipo, Jugador jugador, Rol rol) {
        this.equipo = equipo;
        this.jugador = jugador;
        this.rol = rol;
    }

    @Override
    public boolean ejecutar() {
        // Guardar estado anterior
        jugadorAnterior = equipo.getJugador(rol);
        
        // Si el jugador ya tenia un rol, removerlo primero
        equipo.getJugadoresPorRol().entrySet().stream()
            .filter(entry -> entry.getValue().equals(jugador))
            .findFirst()
            .ifPresent(entry -> equipo.removerJugador(entry.getKey()));
        
        // Asignar nuevo rol
        equipo.agregarJugador(jugador, rol);
        
        System.out.println("[OK] Rol " + rol.getNombre() + " asignado a " + jugador.getNombre());
        
        // Si habia un jugador anterior, agregarlo a suplentes
        if (jugadorAnterior != null) {
            equipo.agregarSuplente(jugadorAnterior);
            System.out.println("[INFO] " + jugadorAnterior.getNombre() + " movido a suplentes");
        }
        
        return true;
    }

    @Override
    public void deshacer() {
        // Remover asignacion actual
        equipo.removerJugador(rol);
        
        // Restaurar estado anterior
        if (jugadorAnterior != null) {
            equipo.agregarJugador(jugadorAnterior, rol);
            equipo.getSuplentes().remove(jugadorAnterior);
        }
        
        // Restaurar rol del jugador actual si tenia uno
        if (jugador != null && jugador.getRolPreferido() != null) {
            equipo.agregarJugador(jugador, jugador.getRolPreferido());
        }
        
        System.out.println("[UNDO] Cambio de rol deshecho");
    }

    @Override
    public String getDescripcion() {
        return "Asignar rol " + rol.getNombre() + " a " + jugador.getNombre();
    }
}

