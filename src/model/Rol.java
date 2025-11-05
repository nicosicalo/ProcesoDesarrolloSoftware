package model;

/**
 * Enum que representa los roles disponibles en el juego
 */
public enum Rol {
    TANQUE("Tanque"),
    DPS("DPS"),
    SOPORTE("Soporte"),
    ASESINO("Asesino"),
    MAGE("Mago");

    private final String nombre;

    Rol(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}

