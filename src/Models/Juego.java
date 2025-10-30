package Models;

import Factory.JuegoFactory;

import java.util.Objects;

public class Juego {
    //uso string id por el tema del mapeo por que las claves string me funcionaban mejor si eran int tiraba mucho error.
    private String id;

    private String nombre;

    private JuegoFactory juegoFactory;
    public Juego(String id, String nombre, JuegoFactory juegoFactory) {
        this.id = id;
        this.nombre = nombre;
        this.juegoFactory = juegoFactory;
    }
    public String getId() {
        return id;
    }

    public JuegoFactory getJuegoFactory() {
        return juegoFactory;
    }

    public void setJuegoFactory(JuegoFactory juegoFactory) {
        this.juegoFactory = juegoFactory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Juego juego)) return false;
        return Objects.equals(getId(), juego.getId()) && Objects.equals(nombre, juego.nombre) && Objects.equals(getJuegoFactory(), juego.getJuegoFactory());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), nombre, getJuegoFactory());
    }

    @Override
    public String toString() {
        return "Juego{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", juegoFactory=" + juegoFactory +
                '}';
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setId(String id) {
        this.id = id;
    }
}
