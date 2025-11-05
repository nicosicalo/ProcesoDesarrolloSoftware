package command;

import java.util.Stack;

/**
 * Invocador de comandos que mantiene un historial para poder deshacer (Patron Command)
 */
public class CommandInvoker {
    private Stack<Command> historial;
    private Stack<Command> rehacerPila;

    public CommandInvoker() {
        this.historial = new Stack<>();
        this.rehacerPila = new Stack<>();
    }

    /**
     * Ejecuta un comando y lo guarda en el historial
     */
    public boolean ejecutarComando(Command comando) {
        boolean exito = comando.ejecutar();
        if (exito) {
            historial.push(comando);
            rehacerPila.clear(); // Limpiar pila de rehacer cuando se ejecuta un nuevo comando
            System.out.println("[CMD] Comando ejecutado: " + comando.getDescripcion());
        }
        return exito;
    }

    /**
     * Deshace el ultimo comando ejecutado
     */
    public void deshacer() {
        if (historial.isEmpty()) {
            System.out.println("[INFO] No hay comandos para deshacer");
            return;
        }
        
        Command comando = historial.pop();
        comando.deshacer();
        rehacerPila.push(comando);
        System.out.println("[UNDO] Comando deshecho: " + comando.getDescripcion());
    }

    /**
     * Rehace el ultimo comando deshecho
     */
    public void rehacer() {
        if (rehacerPila.isEmpty()) {
            System.out.println("[INFO] No hay comandos para rehacer");
            return;
        }
        
        Command comando = rehacerPila.pop();
        comando.ejecutar();
        historial.push(comando);
        System.out.println("[REDO] Comando rehecho: " + comando.getDescripcion());
    }

    /**
     * Muestra el historial de comandos
     */
    public void mostrarHistorial() {
        if (historial.isEmpty()) {
            System.out.println("[HISTORIAL] Historial vacio");
            return;
        }
        
        System.out.println("\n[HISTORIAL] Historial de comandos:");
        int i = 1;
        for (Command cmd : historial) {
            System.out.println("   " + i++ + ". " + cmd.getDescripcion());
        }
    }
}

