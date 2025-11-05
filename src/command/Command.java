package command;

/**
 * Interfaz del patron Command para operaciones de roles
 */
public interface Command {
    /**
     * Ejecuta el comando
     */
    boolean ejecutar();
    
    /**
     * Deshace el comando
     */
    void deshacer();
    
    /**
     * Obtiene la descripcion del comando
     */
    String getDescripcion();
}

