import model.*;
import matching.*;
import command.*;
import system.ListaEsperaManager;
import integration.MatchmakingIntegrationService;
import integration.UsuarioPersistenceService;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("===============================================================");
        System.out.println("     SISTEMA DE EMPAREJAMIENTO DE VIDEOJUEGO");
        System.out.println("     Integrante 4 - Patrones: Strategy y Command");
        System.out.println("===============================================================\n");

        // Intentar cargar usuarios desde módulos integrados
        List<Jugador> jugadores = cargarJugadoresDesdeModulos();
        
        // Si no se pudieron cargar, usar datos simulados
        if (jugadores.isEmpty()) {
            System.out.println("[INFO] No se encontraron módulos integrados, usando datos simulados...\n");
            jugadores = crearJugadoresSimulados();
        }
        
        // Inicializar componentes del sistema
        MatchingContext matchingContext = new MatchingContext();
        CommandInvoker commandInvoker = new CommandInvoker();
        ListaEsperaManager listaEsperaManager = new ListaEsperaManager();
        
        // Inicializar servicio de integración
        MatchmakingIntegrationService integrationService = inicializarIntegracion();

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
        
        // 3. Integrar con módulos de scrims y ciclo de vida
        if (partidaFinal != null && integrationService != null) {
            integrarConModulos(partidaFinal, integrationService);
        }
        
        // 4. Mostrar estado final con partida 5 vs 5
        mostrarEstadoFinal(matchingContext, listaEsperaManager, partidaFinal);
        
        System.out.println("\n[FIN] Simulacion completada exitosamente!");
    }
    
    // Variable estática para mantener una referencia al repositorio compartido
    private static Object repositorioCompartido = null;
    
    /**
     * Intenta cargar jugadores desde los módulos integrados
     * IMPORTANTE: Para que los usuarios creados en main sean usados aquí,
     * ambos módulos deben compartir la misma instancia del repositorio.
     * Ver INSTRUCCIONES_INTEGRACION.md para más detalles.
     */
    private static List<Jugador> cargarJugadoresDesdeModulos() {
        List<Jugador> jugadores = new ArrayList<>();
        
        try {
            // Intentar cargar UsuarioRepository desde el módulo main
            Class<?> repoClass = Class.forName("Infraestructura.UsuarioRepository");
            Constructor<?> repoConstructor = repoClass.getConstructor();
            Object usuarioRepo = repositorioCompartido != null ? repositorioCompartido : repoConstructor.newInstance();
            
            // Guardar referencia para uso futuro
            if (repositorioCompartido == null) {
                repositorioCompartido = usuarioRepo;
            }
            
            System.out.println("[INFO] UsuarioRepository encontrado, buscando usuarios...\n");
            
            // PRIMERO: Intentar cargar usuarios desde archivo guardado (si fueron creados en main)
            System.out.println("[INFO] Intentando cargar usuarios guardados desde archivo...");
            List<String> usuarioIds = UsuarioPersistenceService.cargarUsuarios(usuarioRepo);
            
            if (!usuarioIds.isEmpty()) {
                System.out.println("[OK] ✅ Usuarios cargados desde archivo guardado!");
                System.out.println("[INFO] Estos usuarios fueron creados previamente en el módulo main");
                System.out.println("[INFO] Serán usados para el matchmaking\n");
            } else {
                // SEGUNDO: Intentar obtener usuarios del repositorio actual (si están en memoria)
                System.out.println("[INFO] No se encontraron usuarios guardados, buscando en repositorio actual...");
                usuarioIds = obtenerUsuariosDelRepositorio(usuarioRepo);
                
                if (!usuarioIds.isEmpty()) {
                    System.out.println("[OK] ✅ Usuarios encontrados en el repositorio!");
                    System.out.println("[INFO] Estos usuarios serán usados para el matchmaking\n");
                } else {
                    // TERCERO: Si no hay usuarios, crear algunos de prueba
                    System.out.println("[INFO] No se encontraron usuarios en el repositorio");
                    System.out.println("[INFO] Creando usuarios de prueba para esta simulación...\n");
                    usuarioIds = crearUsuariosDePrueba(usuarioRepo);
                }
            }
            
            if (!usuarioIds.isEmpty()) {
                System.out.println("[INFO] Cargando " + usuarioIds.size() + " jugadores desde usuarios...\n");
                
                // Usar el servicio de integración para convertir usuarios a jugadores
                MatchmakingIntegrationService service = new MatchmakingIntegrationService(
                    usuarioRepo, null, new ConcurrentHashMap<>());
                
                jugadores = service.obtenerJugadoresPorIds(usuarioIds, null);
                
                if (!jugadores.isEmpty()) {
                    System.out.println("[OK] " + jugadores.size() + " jugadores cargados desde módulo de usuarios\n");
                    return jugadores;
                } else {
                    System.out.println("[ADVERTENCIA] No se pudieron convertir los usuarios a jugadores");
                    System.out.println("[INFO] Asegúrate de que los usuarios tengan perfiles de juego configurados");
                    System.out.println("[INFO] Los perfiles deben tener: rango, roles, y región\n");
                }
            }
            
        } catch (ClassNotFoundException e) {
            // Módulo no disponible, continuar con datos simulados
            System.out.println("[INFO] Módulo de usuarios no encontrado en el classpath");
            System.out.println("[INFO] Usando datos simulados como fallback\n");
        } catch (Exception e) {
            System.err.println("[ERROR] Error al intentar cargar módulos: " + e.getMessage());
            e.printStackTrace();
        }
        
        return jugadores;
    }
    
    /**
     * Permite establecer un repositorio compartido desde fuera
     * Útil para inyectar el repositorio del módulo main
     */
    public static void setRepositorioCompartido(Object repo) {
        repositorioCompartido = repo;
    }
    
    /**
     * Intenta obtener todos los usuarios del repositorio usando reflexión
     * Como no hay método findAll(), intentamos acceder al Map interno
     */
    private static List<String> obtenerUsuariosDelRepositorio(Object usuarioRepo) {
        List<String> ids = new ArrayList<>();
        
        try {
            // Intentar acceder al Map byId usando reflexión
            java.lang.reflect.Field byIdField = usuarioRepo.getClass().getDeclaredField("byId");
            byIdField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, Object> byId = (Map<String, Object>) byIdField.get(usuarioRepo);
            
            if (byId != null && !byId.isEmpty()) {
                ids.addAll(byId.keySet());
                System.out.println("[INFO] Encontrados " + ids.size() + " usuarios en el repositorio");
            }
        } catch (NoSuchFieldException e) {
            // Si no se puede acceder al campo, intentar otros métodos
            System.out.println("[INFO] No se puede acceder directamente al repositorio, intentando otros métodos...");
        } catch (Exception e) {
            System.out.println("[INFO] No se pudieron obtener usuarios del repositorio: " + e.getMessage());
        }
        
        return ids;
    }
    
    /**
     * Crea usuarios de prueba en el repositorio si está vacío
     * Retorna una lista de IDs de usuarios creados
     */
    private static List<String> crearUsuariosDePrueba(Object usuarioRepo) {
        List<String> ids = new ArrayList<>();
        
        try {
            // Intentar crear usuarios usando UserFactory
            Class<?> userFactoryClass = Class.forName("Factory.UserFactory");
            Method createClassicUser = userFactoryClass.getMethod("createClassicUser", 
                String.class, String.class, String.class);
            
            // Crear algunos usuarios de prueba
            String[][] usuariosPrueba = {
                {"testuser1", "test1@example.com", "hash1"},
                {"testuser2", "test2@example.com", "hash2"},
                {"testuser3", "test3@example.com", "hash3"},
                {"testuser4", "test4@example.com", "hash4"},
                {"testuser5", "test5@example.com", "hash5"},
                {"testuser6", "test6@example.com", "hash6"},
                {"testuser7", "test7@example.com", "hash7"},
                {"testuser8", "test8@example.com", "hash8"},
                {"testuser9", "test9@example.com", "hash9"},
                {"testuser10", "test10@example.com", "hash10"}
            };
            
            Method saveMethod = usuarioRepo.getClass().getMethod("save", Object.class);
            
            for (String[] datos : usuariosPrueba) {
                Object usuario = createClassicUser.invoke(null, datos[0], datos[1], datos[2]);
                saveMethod.invoke(usuarioRepo, usuario);
                
                // Obtener el ID del usuario
                Method getId = usuario.getClass().getMethod("getId");
                String id = (String) getId.invoke(usuario);
                ids.add(id);
            }
            
            System.out.println("[INFO] " + ids.size() + " usuarios de prueba creados");
            
        } catch (Exception e) {
            // Si no se pueden crear usuarios de prueba, continuar sin ellos
            System.out.println("[INFO] No se pudieron crear usuarios de prueba: " + e.getMessage());
        }
        
        return ids;
    }
    
    /**
     * Inicializa el servicio de integración con los módulos
     */
    private static MatchmakingIntegrationService inicializarIntegracion() {
        try {
            Object usuarioRepo = null;
            Object scrimRepo = null;
            Map<UUID, Object> scrimContexts = new ConcurrentHashMap<>();
            
            // Intentar cargar UsuarioRepository
            try {
                Class<?> repoClass = Class.forName("Infraestructura.UsuarioRepository");
                Constructor<?> repoConstructor = repoClass.getConstructor();
                usuarioRepo = repoConstructor.newInstance();
            } catch (ClassNotFoundException e) {
                // No disponible
            }
            
            // Intentar cargar RepositorioDeScrims
            try {
                Class<?> scrimRepoClass = Class.forName("Infraestructura.RepositorioDeScrims");
                Constructor<?> scrimRepoConstructor = scrimRepoClass.getConstructor();
                scrimRepo = scrimRepoConstructor.newInstance();
            } catch (ClassNotFoundException e) {
                // No disponible
            }
            
            // Intentar cargar ScrimLifecycleService para obtener contexts
            try {
                Class.forName("Service.ScrimLifecycleService");
                // El servicio mantiene los contexts internamente
                // Por ahora, dejamos el mapa vacío
            } catch (ClassNotFoundException e) {
                // No disponible
            }
            
            if (usuarioRepo != null || scrimRepo != null) {
                return new MatchmakingIntegrationService(usuarioRepo, scrimRepo, scrimContexts);
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Error al inicializar integración: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Integra la partida con los módulos de scrims y ciclo de vida
     */
    private static void integrarConModulos(Partida partida, MatchmakingIntegrationService integrationService) {
        System.out.println("\n===============================================================");
        System.out.println("     INTEGRACION CON MODULOS DE SCRIMS");
        System.out.println("===============================================================\n");
        
        try {
            System.out.println("[INFO] Partida creada: " + partida.getId());
            System.out.println("[INFO] Estado inicial: " + partida.getEstado());
            
            // Intentar obtener scrims disponibles
            try {
                // Intentar obtener una scrim de ejemplo usando reflexión
                Object scrimRepo = obtenerScrimRepository();
                if (scrimRepo != null) {
                    // Intentar obtener todas las scrims (si hay un método findAll)
                    try {
                        Method findAll = scrimRepo.getClass().getMethod("findAll");
                        @SuppressWarnings("unchecked")
                        List<Object> scrims = (List<Object>) findAll.invoke(scrimRepo);
                        
                        if (scrims != null && !scrims.isEmpty()) {
                            Object primeraScrim = scrims.get(0);
                            Method getId = primeraScrim.getClass().getMethod("getId");
                            Object scrimId = getId.invoke(primeraScrim);
                            
                            if (scrimId instanceof UUID) {
                                UUID uuid = (UUID) scrimId;
                                System.out.println("[INFO] Scrim encontrada: " + uuid);
                                
                                // Vincular partida con scrim
                                integrationService.vincularPartidaConScrim(partida, uuid);
                                
                                // Actualizar estado de partida según scrim
                                integrationService.actualizarEstadoPartidaDesdeScrim(partida, uuid);
                                
                                String estadoScrim = integrationService.obtenerEstadoScrim(uuid);
                                System.out.println("[INFO] Estado de scrim: " + estadoScrim);
                                System.out.println("[INFO] Estado de partida actualizado: " + partida.getEstado());
                            }
                        } else {
                            System.out.println("[INFO] No hay scrims disponibles en el repositorio");
                            System.out.println("[INFO] Crea una scrim en el módulo integrante-2 para usar esta funcionalidad");
                        }
                    } catch (NoSuchMethodException e) {
                        System.out.println("[INFO] Repositorio de scrims encontrado, pero no hay método findAll()");
                        System.out.println("[INFO] Para usar scrims, proporciona el UUID de una scrim creada");
                    }
                } else {
                    System.out.println("[INFO] Módulo de scrims no disponible en el classpath");
                }
            } catch (Exception e) {
                System.out.println("[INFO] No se pudo acceder al módulo de scrims: " + e.getMessage());
            }
            
            System.out.println("[OK] Integración con módulos configurada");
            
        } catch (Exception e) {
            System.err.println("[ERROR] Error en integración: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Intenta obtener el repositorio de scrims usando reflexión
     */
    private static Object obtenerScrimRepository() {
        try {
            Class<?> scrimRepoClass = Class.forName("Infraestructura.RepositorioDeScrims");
            Constructor<?> scrimRepoConstructor = scrimRepoClass.getConstructor();
            return scrimRepoConstructor.newInstance();
        } catch (Exception e) {
            return null;
        }
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
