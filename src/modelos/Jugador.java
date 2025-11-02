// Clase Jugador
public class Jugador {
    private final String id;
    private final int mmr;
    private final int latencia;
    private String rol;

    public Jugador(String id, int mmr, int latencia, String rol) {
        this.id = id;
        this.mmr = mmr;
        this.latencia = latencia;
        this.rol = rol;
        // Simulación de "lectura de base de datos"
        System.out.println("DB_SIM: Jugador " + id + " cargado. Rango/MMR: " + mmr);
    }
    // Getters y Setters...
    public int getMmr() { return mmr; }
    public int getLatencia() { return latencia; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    
    @Override
    public String toString() {
        return "Jugador{id='" + id + "', mmr=" + mmr + ", rol='" + rol + "'}";
    }
}