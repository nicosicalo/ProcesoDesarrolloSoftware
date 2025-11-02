package Models;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import Enums.ScrimStatus;

public class Scrim {

    private Long id;
    private ScrimStatus status;
    private int cupoMaximo;
    private Set<Long> jugadores = new HashSet<>();
    private LocalDateTime fechaHora;

    public Scrim(int cupoMaximo, LocalDateTime fechaHora) {
        this.cupoMaximo = cupoMaximo;
        this.fechaHora = fechaHora;
        this.status = ScrimStatus.BUSCANDO;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ScrimStatus getStatus() { return status; }
    public void setStatus(ScrimStatus status) { this.status = status; }

    public int getCupoMaximo() { return cupoMaximo; }
    public Set<Long> getJugadores() { return jugadores; }
    public LocalDateTime getFechaHora() { return fechaHora; }
}
