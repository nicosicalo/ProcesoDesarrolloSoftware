package comandos;

import modelos.Equipo;
import modelos.Jugador;

public class AsignarRolComando implements ComandoRol {
    // 1. Almacenar la solicitud (Receiver y parámetros)
    private final Equipo equipo;
    private final Jugador jugador;
    private final String nuevoRol;

    public AsignarRolComando(Equipo equipo, Jugador jugador, String nuevoRol) {
        this.equipo = equipo;
        this.jugador = jugador;
        this.nuevoRol = nuevoRol;
    }

    @Override
    public void ejecutar() {
        // 2. Implementar la ejecución: Llama al método del Receiver (Equipo)
        this.equipo.asignarRol(jugador, nuevoRol);
    }
}