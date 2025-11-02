public class SwapRolComando implements ComandoRol {
    private final Equipo equipo;
    private final Jugador jugador1;
    private final Jugador jugador2;

    public SwapRolComando(Equipo equipo, Jugador j1, Jugador j2) {
        this.equipo = equipo;
        this.jugador1 = j1;
        this.jugador2 = j2;
    }

    @Override
    public void ejecutar() {
        // La acción se delega al Receiver (Equipo)
        this.equipo.intercambiarRoles(jugador1, jugador2);
    }
}