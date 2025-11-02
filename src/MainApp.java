import java.util.Arrays;
import java.util.List;

// App.java

import java.util.Arrays;
import java.util.List;

// 1. Clases del Paquete 'modelos'
import modelos.Jugador;
import modelos.Equipo;

// 2. Clases del Paquete 'emparejamiento' (Patrón Strategy)
import emparejamiento.SistemaEmparejamiento;
import emparejamiento.EstrategiaEmparejamiento; // Aunque se use indirectamente, es útil
import emparejamiento.EstrategiaPorMMR;
import emparejamiento.EstrategiaPorLatencia;

// 3. Clases del Paquete 'comandos' (Patrón Command)
import comandos.ComandoRol;
import comandos.AsignarRolComando;
import comandos.SwapRolComando;
import comandos.GestorComandos;



public class MainApp {
    public static void main(String[] args) {
        GestorComandos gestor = new GestorComandos();
        gestor.simularConexionDB();

        // 1. SIMULACIÓN DE DATOS (Lista de Espera)
        List<Jugador> listaEspera = Arrays.asList(
            new Jugador("Player1", 1600, 30, "SinRol"),
            new Jugador("Player2", 1550, 45, "SinRol"),
            new Jugador("Player3", 1650, 20, "SinRol"),
            new Jugador("Player4", 1610, 50, "SinRol"),
            new Jugador("Player5", 1580, 25, "SinRol"),
            new Jugador("Player6", 2500, 10, "SinRol") // Jugador de alto MMR, no será emparejado con MMR
        );

        // 2. EMPAREJAMIENTO (Patrón Strategy)
        SistemaEmparejamiento sistema = new SistemaEmparejamiento(listaEspera);
        
        // Intenta emparejar con la estrategia por defecto (MMR)
        Equipo equipoEncontrado = sistema.intentarEmparejar();

        if (equipoEncontrado != null) {
            System.out.println("\n>> SALIDA: Equipo Encontrado! Jugadores: " + equipoEncontrado.jugadores);

            // 3. GESTIÓN DE ROLES (Patrón Command)
            Jugador j1 = equipoEncontrado.jugadores.get(0);
            Jugador j2 = equipoEncontrado.jugadores.get(1);

            // Asignación de rol inicial (ConcreteCommand 1)
            ComandoRol asignarTank = new AsignarRolComando(equipoEncontrado, j1, "Tank");
            gestor.ejecutarComando(asignarTank);

            // Intercambio de roles (ConcreteCommand 2)
            ComandoRol swapRoles = new SwapRolComando(equipoEncontrado, j1, j2);
            gestor.ejecutarComando(swapRoles);

            // 4. LISTA DE SUPLENTES (simulación)
            Jugador suplente = listaEspera.get(5);
            equipoEncontrado.agregarSuplente(suplente);

        } else {
            // Cambio dinámico de estrategia
            System.out.println("\n⚠️ No se encontró equipo. Cambiando estrategia a Latencia...");
            sistema.setEstrategia(new EstrategiaPorLatencia());
            equipoEncontrado = sistema.intentarEmparejar();
            
            // ... continuar lógica de roles para el nuevo equipo si se encuentra ...
        }
    }
}