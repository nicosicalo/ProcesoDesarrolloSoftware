import java.util.Stack;

public class GestorComandos {
    private final Stack<ComandoRol> historial = new Stack<>();

    public void ejecutarComando(ComandoRol comando) {
        comando.ejecutar();
        historial.push(comando); // Para el potencial "Deshacer" (Undo)
    }
    
    // Método para simular una salida
    public void simularConexionDB() {
        // No es una conexión real, solo simula el mensaje
        System.out.println("\n🌐 DB_SIM: Conexión con base de datos establecida y datos de MMR 'leídos'.");
    }
    
    // ... método para deshacer ...
}