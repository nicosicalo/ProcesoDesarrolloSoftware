import model.*;
import matching.*;
import command.*;
import system.ListaEsperaManager;
import java.util.List;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("===============================================================");
        System.out.println("     SISTEMA DE EMPAREJAMIENTO DE VIDEOJUEGO");
        System.out.println("     Integrante 4 - Patrones: Strategy y Command");
        System.out.println("===============================================================\n");

        // Crear datos simulados
        List<Jugador> jugadores = crearJugadoresSimulados();
        
        // Inicializar componentes del sistema
        MatchingContext matchingContext = new MatchingContext();
        CommandInvoker commandInvoker = new CommandInvoker();
        ListaEsperaManager listaEsperaManager = new ListaEsperaManager();

        // SIMULACION AUTOMATICA
        System.out.println("\n[INICIO] Iniciando simulacion de emparejamiento...\n");
        
        // DEMOSTRACION: Mostrar estrategia actual y sus mensajes
        System.out.println("===============================================================");
        System.out.println("     DEMOSTRACION PATRON STRATEGY");
        System.out.println("===============================================================\n");
        String nombreEstrategia = matchingContext.getEstrategiaActual().getNombre();
        System.out.println("[INFO] Estrategia configurada: " + nombreEstrategia);
        
        // Mostrar descripción según la estrategia
        String descripcion = "";
        if (nombreEstrategia.contains("Latencia")) {
            descripcion = "Zona geografica y Latencia";
        } else if (nombreEstrategia.contains("MMR") || nombreEstrategia.contains("Rango")) {
            descripcion = "Puntuacion MMR (rango de habilidad)";
        } else if (nombreEstrategia.contains("Compatibilidad")) {
            descripcion = "Historial y compatibilidad (win rate, partidas jugadas)";
        }
        System.out.println("[INFO] Esta estrategia empareja jugadores por: " + descripcion + "\n");
        
        // Demostrar busqueda de emparejamiento con algunos jugadores para ver los mensajes
        System.out.println("[DEMOSTRACION] Simulando busqueda de emparejamiento con la estrategia actual...\n");
        for (int i = 0; i < 3 && i < jugadores.size(); i++) {
            Jugador jugador = jugadores.get(i);
            System.out.println("[GAME] " + jugador.getNombre() + " busca partida...");
            System.out.println("   MMR: " + jugador.getPuntosMMR() + 
                             " | Zona: " + jugador.getZona() + 
                             " | Latencia: " + jugador.getLatencia() + "ms");
            
            Partida partidaDemo = matchingContext.buscarEmparejamiento(jugador);
            
            if (partidaDemo != null) {
                System.out.println("\n[MATCH] PARTIDA ENCONTRADA!");
                System.out.println(partidaDemo.toString());
            }
            
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        System.out.println("\n[INFO] Demostracion completada. Continuando con formacion de equipos 5 vs 5...\n");
        
        // 1. Formar 2 equipos completos (5 vs 5) con los 10 jugadores
        Partida partidaFinal = formarEquiposCompletos(jugadores, matchingContext);
        
        // 2. Detectar y resolver conflictos de roles duplicados usando comandos
        if (partidaFinal != null) {
            simularGestionRoles(partidaFinal, commandInvoker, jugadores);
        }
        
        // 3. Mostrar estado final con partida 5 vs 5
        mostrarEstadoFinal(matchingContext, listaEsperaManager, partidaFinal);
        
        System.out.println("\n[FIN] Simulacion completada exitosamente!");
    }

    private static List<Jugador> crearJugadoresSimulados() {
        System.out.println("[INFO] Cargando datos simulados de jugadores...\n");
        
        List<Jugador> jugadores = List.of(
            new Jugador("J1", "ProGamer99", 2500, 150, "NA-EAST", 45, Rol.DPS, Rol.ASESINO, 85, 65),
            new Jugador("J2", "ElitePlayer", 2400, 200, "NA-EAST", 50, Rol.TANQUE, Rol.SOPORTE, 120, 80),
            new Jugador("J3", "MasterMage", 2550, 180, "NA-EAST", 48, Rol.MAGE, Rol.SOPORTE, 100, 80),
            new Jugador("J4", "SupportKing", 2450, 220, "NA-WEST", 120, Rol.SOPORTE, Rol.MAGE, 130, 90),
            new Jugador("J5", "NinjaAssassin", 2600, 140, "NA-WEST", 110, Rol.ASESINO, Rol.DPS, 90, 50),
            new Jugador("J6", "TankWarrior", 2300, 100, "EU-WEST", 85, Rol.TANQUE, Rol.DPS, 55, 45),
            new Jugador("J7", "MagicWizard", 2500, 160, "EU-WEST", 90, Rol.MAGE, Rol.ASESINO, 95, 65),
            new Jugador("J8", "SniperPro", 2400, 190, "SA-BRAZIL", 150, Rol.DPS, Rol.ASESINO, 110, 80),
            new Jugador("J9", "HealerAura", 2350, 210, "SA-BRAZIL", 145, Rol.SOPORTE, Rol.DPS, 115, 95),
            new Jugador("J10", "ShadowKiller", 2480, 170, "ASIA-JAPAN", 200, Rol.ASESINO, Rol.DPS, 100, 70)
        );

        System.out.println("[OK] " + jugadores.size() + " jugadores cargados exitosamente\n");
        return jugadores;
    }

    /**
     * Forma 2 equipos completos de 5 jugadores cada uno (5 vs 5)
     * Garantiza que cada equipo tenga los 5 roles diferentes
     */
    private static Partida formarEquiposCompletos(List<Jugador> jugadores, MatchingContext context) {
        System.out.println("===============================================================");
        System.out.println("     FORMANDO EQUIPOS COMPLETOS (5 vs 5)");
        System.out.println("===============================================================\n");

        if (jugadores.size() < 10) {
            System.out.println("[ERROR] Se necesitan al menos 10 jugadores para formar 2 equipos");
            return null;
        }

        // Crear Equipo 1 con los primeros 5 jugadores
        Equipo equipo1 = new Equipo("Equipo-A");
        System.out.println("[FORMANDO] Creando Equipo 1...");
        
        // Asignar roles estratégicamente para el Equipo 1
        equipo1.agregarJugador(jugadores.get(0), Rol.DPS);      // ProGamer99
        equipo1.agregarJugador(jugadores.get(1), Rol.TANQUE);   // ElitePlayer
        equipo1.agregarJugador(jugadores.get(2), Rol.MAGE);      // MasterMage
        equipo1.agregarJugador(jugadores.get(3), Rol.SOPORTE);  // SupportKing
        equipo1.agregarJugador(jugadores.get(4), Rol.ASESINO);   // NinjaAssassin
        
        System.out.println("[OK] Equipo 1 formado con 5 jugadores");
        System.out.println(equipo1.toString());

        // Crear Equipo 2 con los últimos 5 jugadores
        Equipo equipo2 = new Equipo("Equipo-B");
        System.out.println("\n[FORMANDO] Creando Equipo 2...");
        
        // INTENCIONALMENTE crear un conflicto: dos jugadores con el mismo rol
        // Luego los comandos resolverán este conflicto asignando el segundo rol
        equipo2.agregarJugador(jugadores.get(5), Rol.TANQUE);    // TankWarrior
        equipo2.agregarJugador(jugadores.get(6), Rol.MAGE);      // MagicWizard
        equipo2.agregarJugador(jugadores.get(7), Rol.DPS);       // SniperPro (DPS preferido)
        equipo2.agregarJugador(jugadores.get(8), Rol.DPS);       // HealerAura - CONFLICTO: mismo rol que SniperPro
                                                                  // (HealerAura reemplaza a SniperPro como DPS)
                                                                  // SniperPro quedará sin rol asignado
        equipo2.agregarJugador(jugadores.get(9), Rol.ASESINO);   // ShadowKiller
        
        System.out.println("[OK] Equipo 2 formado con 5 jugadores");
        System.out.println("[ADVERTENCIA] Se detectó un conflicto: HealerAura y SniperPro intentaron tener el mismo rol DPS");
        System.out.println("   Nota: Como ambos tienen segundo rol, se asignará el segundo rol a uno de ellos");
        System.out.println(equipo2.toString());

        // Crear la partida final (5 vs 5)
        Partida partidaFinal = new Partida("PARTIDA-FINAL", equipo1, equipo2);
        
        // Agregar la partida al contexto
        context.agregarPartida(partidaFinal);
        
        System.out.println("\n===============================================================");
        System.out.println("     ¡PARTIDA 5 vs 5 CREADA!");
        System.out.println("===============================================================\n");
        System.out.println(partidaFinal.toString());
        
        return partidaFinal;
    }

    /**
     * Detecta roles duplicados en los equipos y los resuelve usando comandos
     */
    private static void simularGestionRoles(Partida partida, CommandInvoker invoker, List<Jugador> todosLosJugadores) {
        System.out.println("\n===============================================================");
        System.out.println("     GESTION DE ROLES: DETECCION Y RESOLUCION DE CONFLICTOS");
        System.out.println("===============================================================\n");

        Equipo equipo1 = partida.getEquipo1();
        Equipo equipo2 = partida.getEquipo2();

        // Lista de jugadores esperados en cada equipo (primeros 5 y últimos 5)
        List<Jugador> jugadoresEquipo1 = todosLosJugadores.subList(0, 5);
        List<Jugador> jugadoresEquipo2 = todosLosJugadores.subList(5, 10);

        // Verificar Equipo 1
        System.out.println("[VERIFICANDO] Revisando conflictos de roles en " + equipo1.getId() + "...");
        resolverRolesDuplicados(equipo1, invoker, jugadoresEquipo1);

        // Verificar Equipo 2
        System.out.println("\n[VERIFICANDO] Revisando conflictos de roles en " + equipo2.getId() + "...");
        resolverRolesDuplicados(equipo2, invoker, jugadoresEquipo2);

        // Mostrar historial de comandos ejecutados
        System.out.println("\n[INFO] Historial de comandos ejecutados para resolver conflictos:");
        invoker.mostrarHistorial();
    }

    /**
     * Detecta si hay roles duplicados en un equipo y los resuelve asignando un rol diferente
     * Un conflicto ocurre cuando se intenta asignar dos jugadores al mismo rol.
     * Como el HashMap solo guarda uno, el segundo reemplaza al primero, dejando un rol faltante.
     * @param jugadoresEsperados Lista de jugadores que deberían estar en este equipo
     */
    private static void resolverRolesDuplicados(Equipo equipo, CommandInvoker invoker, List<Jugador> jugadoresEsperados) {
        var jugadoresPorRol = equipo.getJugadoresPorRol();
        
        // Verificar si hay conflictos: si hay menos de 5 roles únicos, hay duplicados
        if (jugadoresPorRol.size() < Rol.values().length) {
            System.out.println("[CONFLICTO DETECTADO] Hay roles duplicados en el equipo!");
            System.out.println("   Roles actuales: " + jugadoresPorRol.size() + " de " + Rol.values().length);
            
            // Encontrar roles faltantes
            List<Rol> rolesDisponibles = new java.util.ArrayList<>();
            for (Rol rol : Rol.values()) {
                if (!jugadoresPorRol.containsKey(rol)) {
                    rolesDisponibles.add(rol);
                    System.out.println("   Rol faltante: " + rol.getNombre());
                }
            }
            
            System.out.println("\n[ESTADO] Estado actual del equipo:");
            System.out.println(equipo.toString());
            
            // Si hay roles disponibles, asignarlos a jugadores que los necesitan
            if (!rolesDisponibles.isEmpty()) {
                Rol rolDisponible = rolesDisponibles.get(0);
                System.out.println("\n[ACCION] Resolviendo conflicto: asignando rol " + rolDisponible.getNombre() + 
                                 " a un jugador que tenía rol duplicado...");
                
                // Obtener todos los jugadores del equipo
                List<Jugador> todosLosJugadores = new java.util.ArrayList<>(jugadoresPorRol.values());
                
                // Estrategia 1: Buscar jugador en el equipo cuyo rol secundario coincide con el rol disponible
                // PRIORIDAD: Asignar el rol faltante al jugador que está en el equipo pero tiene rol duplicado
                for (Jugador jugador : todosLosJugadores) {
                    if (jugador.getRolSecundario() != null && jugador.getRolSecundario() == rolDisponible) {
                        Rol rolActual = encontrarRolDelJugador(equipo, jugador);
                        if (rolActual != null && rolActual != rolDisponible) {
                            // Este jugador tiene un rol diferente pero su segundo rol está disponible
                            // Asignarle su segundo rol (el que falta)
                            AsignarRolCommand cmd = new AsignarRolCommand(equipo, jugador, rolDisponible);
                            invoker.ejecutarComando(cmd);
                            System.out.println("[OK] Rol " + rolDisponible.getNombre() + 
                                             " asignado a " + jugador.getNombre() + 
                                             " (su segunda opcion de rol, resuelto conflicto)");
                            System.out.println("\n[ESTADO] Equipo actualizado:");
                            System.out.println(equipo.toString());
                            
                            // Después de asignar el rol faltante, buscar el jugador que quedó fuera
                            // y asignarle el rol que tenía el jugador anterior (para completar 5vs5)
                            resolverJugadorFaltante(equipo, invoker, jugadoresEsperados, rolActual, todosLosJugadores);
                            return;
                        }
                    }
                }
                
                // Estrategia 2: Buscar jugador cuyo rol preferido coincide con el rol disponible
                for (Jugador jugador : todosLosJugadores) {
                    if (jugador.getRolPreferido() == rolDisponible) {
                        Rol rolActual = encontrarRolDelJugador(equipo, jugador);
                        if (rolActual != null && rolActual != rolDisponible) {
                            // Este jugador tiene un rol diferente a su preferido
                            // Asignarle su rol preferido que está disponible
                            AsignarRolCommand cmd = new AsignarRolCommand(equipo, jugador, rolDisponible);
                            invoker.ejecutarComando(cmd);
                            // Verificar si es su segundo rol o preferido
                            if (jugador.getRolSecundario() == rolDisponible) {
                                System.out.println("[OK] Rol " + rolDisponible.getNombre() + 
                                                 " asignado a " + jugador.getNombre() + 
                                                 " (su segunda opcion de rol, resuelto conflicto)");
                            } else {
                                System.out.println("[OK] Rol " + rolDisponible.getNombre() + 
                                                 " asignado a " + jugador.getNombre() + 
                                                 " (su rol preferido, resuelto conflicto)");
                            }
                            System.out.println("\n[ESTADO] Equipo actualizado:");
                            System.out.println(equipo.toString());
                            
                            // Después de asignar el rol faltante, buscar el jugador que quedó fuera
                            resolverJugadorFaltante(equipo, invoker, jugadoresEsperados, rolActual, todosLosJugadores);
                            return;
                        }
                    }
                }
                
                // Estrategia 3: Si no hay coincidencia con rol preferido o secundario, buscar jugador
                // que tenga un rol que no es su preferido y cuyo rol preferido está disponible
                for (Jugador jugador : todosLosJugadores) {
                    Rol rolActual = encontrarRolDelJugador(equipo, jugador);
                    // Si el jugador tiene un rol que no es su preferido y hay un rol disponible
                    // que podría ser mejor, lo asignamos
                    if (rolActual != null && jugador.getRolPreferido() != rolActual) {
                        // Si el rol preferido no está disponible, intentar con el segundo rol PRIMERO
                        if (jugador.getRolSecundario() != null && rolesDisponibles.contains(jugador.getRolSecundario())) {
                            AsignarRolCommand cmd = new AsignarRolCommand(equipo, jugador, jugador.getRolSecundario());
                            invoker.ejecutarComando(cmd);
                            System.out.println("[OK] Rol " + jugador.getRolSecundario().getNombre() + 
                                             " asignado a " + jugador.getNombre() + 
                                             " (su segunda opcion de rol, resuelto conflicto de " + rolActual.getNombre() + ")");
                            System.out.println("\n[ESTADO] Equipo actualizado:");
                            System.out.println(equipo.toString());
                            
                            // Después de asignar el rol faltante, buscar el jugador que quedó fuera
                            resolverJugadorFaltante(equipo, invoker, jugadoresEsperados, rolActual, todosLosJugadores);
                            return;
                        }
                        // Si el segundo rol no está disponible, intentar con el preferido
                        else if (rolesDisponibles.contains(jugador.getRolPreferido())) {
                            AsignarRolCommand cmd = new AsignarRolCommand(equipo, jugador, jugador.getRolPreferido());
                            invoker.ejecutarComando(cmd);
                            System.out.println("[OK] Rol " + jugador.getRolPreferido().getNombre() + 
                                             " asignado a " + jugador.getNombre() + 
                                             " (su rol preferido, resuelto conflicto de " + rolActual.getNombre() + ")");
                            System.out.println("\n[ESTADO] Equipo actualizado:");
                            System.out.println(equipo.toString());
                            
                            // Después de asignar el rol faltante, buscar el jugador que quedó fuera
                            resolverJugadorFaltante(equipo, invoker, jugadoresEsperados, rolActual, todosLosJugadores);
                            return;
                        }
                    }
                }
                
                // Estrategia 4: Buscar jugadores que deberían estar en el equipo pero no lo están
                // (fueron reemplazados por otro jugador con el mismo rol)
                List<Jugador> jugadoresFaltantes = new java.util.ArrayList<>();
                for (Jugador jugadorEsperado : jugadoresEsperados) {
                    if (!todosLosJugadores.contains(jugadorEsperado)) {
                        jugadoresFaltantes.add(jugadorEsperado);
                    }
                }
                
                // Si hay jugadores faltantes, asignarles el rol que estaba duplicado (para completar 5vs5)
                if (!jugadoresFaltantes.isEmpty()) {
                    Jugador jugadorFaltante = jugadoresFaltantes.get(0);
                    
                    // El rol que falta es el que estaba duplicado (ej: DPS)
                    // Buscar qué rol está duplicado (el que tiene el jugador que está en el equipo)
                    Rol rolDuplicado = null;
                    for (Jugador j : todosLosJugadores) {
                        Rol rolJ = encontrarRolDelJugador(equipo, j);
                        if (rolJ != null) {
                            // Verificar si este rol está ocupado por más de un jugador esperado
                            for (Jugador esperado : jugadoresEsperados) {
                                if (esperado == jugadorFaltante || esperado == j) {
                                    // Verificar si ambos esperaban este rol
                                    if ((esperado.getRolPreferido() == rolJ || esperado.getRolSecundario() == rolJ) &&
                                        (jugadorFaltante.getRolPreferido() == rolJ || jugadorFaltante.getRolSecundario() == rolJ)) {
                                        rolDuplicado = rolJ;
                                        break;
                                    }
                                }
                            }
                            if (rolDuplicado != null) break;
                        }
                    }
                    
                    // Si encontramos el rol duplicado, asignarlo al jugador faltante
                    if (rolDuplicado != null && 
                        (jugadorFaltante.getRolPreferido() == rolDuplicado || jugadorFaltante.getRolSecundario() == rolDuplicado)) {
                        AsignarRolCommand cmd = new AsignarRolCommand(equipo, jugadorFaltante, rolDuplicado);
                        invoker.ejecutarComando(cmd);
                        if (jugadorFaltante.getRolSecundario() == rolDuplicado) {
                            System.out.println("[OK] Jugador " + jugadorFaltante.getNombre() + 
                                             " agregado al equipo con rol " + rolDuplicado.getNombre() + 
                                             " (su segunda opcion de rol, estaba fuera del equipo por conflicto)");
                        } else {
                            System.out.println("[OK] Jugador " + jugadorFaltante.getNombre() + 
                                             " agregado al equipo con rol " + rolDuplicado.getNombre() + 
                                             " (estaba fuera del equipo por conflicto)");
                        }
                        System.out.println("\n[ESTADO] Equipo actualizado:");
                        System.out.println(equipo.toString());
                        return;
                    }
                    
                    // Si no encontramos el rol duplicado, usar el primer rol disponible
                    if (!rolesDisponibles.isEmpty()) {
                        Rol nuevoRol = rolesDisponibles.get(0);
                        if (jugadorFaltante.getRolSecundario() != null && rolesDisponibles.contains(jugadorFaltante.getRolSecundario())) {
                            nuevoRol = jugadorFaltante.getRolSecundario();
                        }
                        
                        AsignarRolCommand cmd = new AsignarRolCommand(equipo, jugadorFaltante, nuevoRol);
                        invoker.ejecutarComando(cmd);
                        if (jugadorFaltante.getRolSecundario() == nuevoRol) {
                            System.out.println("[OK] Jugador " + jugadorFaltante.getNombre() + 
                                             " agregado al equipo con rol " + nuevoRol.getNombre() + 
                                             " (su segunda opcion de rol, estaba fuera del equipo por conflicto)");
                        } else {
                            System.out.println("[OK] Jugador " + jugadorFaltante.getNombre() + 
                                             " agregado al equipo con rol " + nuevoRol.getNombre() + 
                                             " (estaba fuera del equipo por conflicto)");
                        }
                        System.out.println("\n[ESTADO] Equipo actualizado:");
                        System.out.println(equipo.toString());
                        return;
                    }
                }
                
                // Estrategia 5: Asignar el rol disponible al primer jugador que tenga un rol conflictivo
                // (por ejemplo, si hay un DPS cuando debería haber otro rol)
                if (!todosLosJugadores.isEmpty() && !rolesDisponibles.isEmpty()) {
                    Jugador jugador = todosLosJugadores.get(0);
                    Rol nuevoRol = rolesDisponibles.get(0);
                    
                    // Si el jugador tiene segundo rol disponible, usarlo
                    if (jugador.getRolSecundario() != null && rolesDisponibles.contains(jugador.getRolSecundario())) {
                        nuevoRol = jugador.getRolSecundario();
                        AsignarRolCommand cmd = new AsignarRolCommand(equipo, jugador, nuevoRol);
                        invoker.ejecutarComando(cmd);
                        System.out.println("[OK] Rol " + nuevoRol.getNombre() + 
                                         " asignado a " + jugador.getNombre() + 
                                         " (su segunda opcion de rol, resuelto conflicto)");
                    } else {
                        AsignarRolCommand cmd = new AsignarRolCommand(equipo, jugador, nuevoRol);
                        invoker.ejecutarComando(cmd);
                        System.out.println("[OK] Rol " + nuevoRol.getNombre() + 
                                         " asignado a " + jugador.getNombre() + 
                                         " (resuelto conflicto: faltaba este rol en el equipo)");
                    }
                    
                    // Después de asignar el rol faltante, buscar el jugador que quedó fuera
                    resolverJugadorFaltante(equipo, invoker, jugadoresEsperados, null, todosLosJugadores);
                    System.out.println("\n[ESTADO] Equipo actualizado:");
                System.out.println(equipo.toString());
                }
            } else {
                System.out.println("[OK] No hay roles disponibles para resolver el conflicto");
            }
        } else {
            System.out.println("[OK] No hay conflictos de roles en este equipo (todos los roles están asignados correctamente)");
        }
    }

    /**
     * Encuentra el rol actual de un jugador en un equipo
     */
    private static Rol encontrarRolDelJugador(Equipo equipo, Jugador jugador) {
        var jugadoresPorRol = equipo.getJugadoresPorRol();
        for (var entry : jugadoresPorRol.entrySet()) {
            if (entry.getValue().equals(jugador)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Resuelve el jugador faltante después de asignar un rol a otro jugador
     * Cuando hay conflicto, un jugador queda fuera y necesita el rol duplicado
     */
    private static void resolverJugadorFaltante(Equipo equipo, CommandInvoker invoker, 
                                               List<Jugador> jugadoresEsperados, Rol rolDuplicado,
                                               List<Jugador> jugadoresActuales) {
        // Buscar jugadores que deberían estar en el equipo pero no lo están
        List<Jugador> jugadoresFaltantes = new java.util.ArrayList<>();
        for (Jugador jugadorEsperado : jugadoresEsperados) {
            if (!jugadoresActuales.contains(jugadorEsperado)) {
                jugadoresFaltantes.add(jugadorEsperado);
            }
        }
        
        if (!jugadoresFaltantes.isEmpty()) {
            Jugador jugadorFaltante = jugadoresFaltantes.get(0);
            
            // Si sabemos el rol duplicado, asignarlo al jugador faltante
            if (rolDuplicado != null && 
                (jugadorFaltante.getRolPreferido() == rolDuplicado || jugadorFaltante.getRolSecundario() == rolDuplicado)) {
                AsignarRolCommand cmd = new AsignarRolCommand(equipo, jugadorFaltante, rolDuplicado);
                invoker.ejecutarComando(cmd);
                if (jugadorFaltante.getRolSecundario() == rolDuplicado) {
                    System.out.println("[OK] Jugador " + jugadorFaltante.getNombre() + 
                                     " agregado al equipo con rol " + rolDuplicado.getNombre() + 
                                     " (su segunda opcion de rol, estaba fuera del equipo por conflicto)");
                } else {
                    System.out.println("[OK] Jugador " + jugadorFaltante.getNombre() + 
                                     " agregado al equipo con rol " + rolDuplicado.getNombre() + 
                                     " (estaba fuera del equipo por conflicto)");
                }
                System.out.println("\n[ESTADO] Equipo actualizado:");
                System.out.println(equipo.toString());
            }
        }
    }

    private static void mostrarEstadoFinal(MatchingContext context, ListaEsperaManager listaEspera, Partida partidaFinal) {
        System.out.println("\n===============================================================");
        System.out.println("     ESTADO FINAL DEL SISTEMA");
        System.out.println("===============================================================\n");

        System.out.println("[ESTRATEGIA] Estrategia actual: " + context.getEstrategiaActual().getNombre());
        
        // Mostrar la partida final 5 vs 5 de forma destacada
        if (partidaFinal != null) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("     PARTIDA FINAL: 5 vs 5");
            System.out.println("=".repeat(60) + "\n");
            System.out.println(partidaFinal.toString());
            System.out.println("\n" + "=".repeat(60));
        }
        
        List<Equipo> equipos = context.getEquiposParciales();
        if (!equipos.isEmpty()) {
        System.out.println("\n[EQUIPOS] Equipos parciales (" + equipos.size() + "):");
        for (Equipo e : equipos) {
            System.out.println(e.toString());
            }
        }

        List<Partida> partidas = context.getPartidasEmparejadas();
        if (partidas.size() > 1) {
            System.out.println("\n[PARTIDAS] Otras partidas encontradas (" + (partidas.size() - 1) + "):");
        for (Partida p : partidas) {
                if (p != partidaFinal) {
            System.out.println(p.toString() + "\n");
                }
            }
        }

        listaEspera.mostrarEstado();
    }
}
