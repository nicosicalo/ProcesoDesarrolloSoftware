import java.util.List;

public class Equipo {
    private final List<Jugador> jugadores;
    private String estado = "Encontrado";

    public Equipo(List<Jugador> jugadores) {
        this.jugadores = jugadores;
    }

    // Método receptor de la acción de asignar rol
    public void asignarRol(Jugador jugador, String nuevoRol) {
        jugador.setRol(nuevoRol);
        System.out.println("⚙️ EQUIPO_ACTION: Rol asignado -> " + jugador.id + " ahora es " + nuevoRol);
    }

    // Método receptor de la acción de intercambiar roles
    public void intercambiarRoles(Jugador j1, Jugador j2) {
        String rol1 = j1.getRol();
        String rol2 = j2.getRol();
        j1.setRol(rol2);
        j2.setRol(rol1);
        System.out.println("↩️ EQUIPO_ACTION: Roles intercambiados entre " + j1.id + " y " + j2.id);
        System.out.println("   -> " + j1.id + " es " + j1.getRol() + ", " + j2.id + " es " + j2.getRol());
    }
    
    // Método para simular la lista de espera/suplentes
    public void agregarSuplente(Jugador suplente) {
        System.out.println("⏳ EQUIPO_ACTION: Jugador " + suplente.id + " añadido como suplente.");
        // Lógica real de suplentes (ej. mover a un equipo si es necesario)
    }

    // ... más métodos ...
}