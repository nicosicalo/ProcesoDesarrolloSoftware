package model;

/**
 * Clase que representa a un jugador con sus estadisticas
 */
public class Jugador {
    private String id;
    private String nombre;
    private int puntosMMR; // Puntos de ranking/MMR
    private int partidasJugadas;
    private String zona; // Zona geografica del jugador
    private int latencia; // Latencia en ms
    private Rol rolPreferido;
    private Rol rolSecundario; // Segundo rol que el jugador puede usar
    private int victorias;
    private int derrotas;

    public Jugador(String id, String nombre, int puntosMMR, int partidasJugadas, 
                   String zona, int latencia, Rol rolPreferido, int victorias, int derrotas) {
        this(id, nombre, puntosMMR, partidasJugadas, zona, latencia, rolPreferido, null, victorias, derrotas);
    }

    public Jugador(String id, String nombre, int puntosMMR, int partidasJugadas, 
                   String zona, int latencia, Rol rolPreferido, Rol rolSecundario, int victorias, int derrotas) {
        this.id = id;
        this.nombre = nombre;
        this.puntosMMR = puntosMMR;
        this.partidasJugadas = partidasJugadas;
        this.zona = zona;
        this.latencia = latencia;
        this.rolPreferido = rolPreferido;
        this.rolSecundario = rolSecundario;
        this.victorias = victorias;
        this.derrotas = derrotas;
    }

    // Getters
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public int getPuntosMMR() { return puntosMMR; }
    public int getPartidasJugadas() { return partidasJugadas; }
    public String getZona() { return zona; }
    public int getLatencia() { return latencia; }
    public Rol getRolPreferido() { return rolPreferido; }
    public Rol getRolSecundario() { return rolSecundario; }
    public int getVictorias() { return victorias; }
    public int getDerrotas() { return derrotas; }

    // Calcular win rate
    public double getWinRate() {
        if (partidasJugadas == 0) return 0.0;
        return (victorias * 100.0) / partidasJugadas;
    }

    @Override
    public String toString() {
        return String.format("%s (MMR: %d, Zona: %s, Lat: %dms)", 
                           nombre, puntosMMR, zona, latencia);
    }
}

