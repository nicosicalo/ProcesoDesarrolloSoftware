package Controller;

import Models.Usuario;
import ScrimsAuth.SessionManager;
import Service.FiltrosBusqueda;
import Service.ScrimAppService;
import Service.ScrimCreationDTO;
import Models.Postulacion;
import Models.Scrim;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;
import Infraestructura.BusquedaFavoritaRepository;
import java.util.Set;
import java.util.stream.Collectors;
import Models.Juego;
import Infraestructura.JuegoRepository;
import Enums.Region;
import Models.BusquedaFavorita;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class ScrimController {
    private final Scanner sc;
    private final SessionManager sessionManager;
    private final ScrimAppService scrimAppService;
    private final BusquedaFavoritaRepository busquedaRepo;
    private final JuegoRepository juegoRepository;
    private static final List<String> FORMATOS_VALIDOS = List.of("1V1", "3V3", "5V5");
    private static final List<String> MODALIDADES_VALIDAS = List.of("RANKED-LIKE", "CASUAL", "PRACTICA");
    private FiltrosBusqueda ultimosFiltros = null;

    public ScrimController(Scanner sc, SessionManager sessionManager, ScrimAppService scrimAppService,
            BusquedaFavoritaRepository busquedaRepo, JuegoRepository juegoRepository) {
        this.sc = sc;
        this.sessionManager = sessionManager;
        this.scrimAppService = scrimAppService;
        this.busquedaRepo = busquedaRepo;
        this.juegoRepository = juegoRepository;
    }

    private Optional<Usuario> validarSesion(String currentToken) {
        if (currentToken == null) {
            System.out.println("No estás logueado.");
            return Optional.empty();
        }
        Optional<Usuario> uo = sessionManager.getUser(currentToken);
        if (uo.isEmpty()) {
            System.out.println("Sesión no válida. Por favor, vuelve a iniciar sesión.");
            return Optional.empty();
        }
        return uo;
    }

    public void gestionarScrims(String currentToken) {
        Optional<Usuario> uo = validarSesion(currentToken);
        if (uo.isEmpty())
            return;
        boolean exit = false;
        while (!exit) {
            System.out.println("\n--- GESTIÓN DE SCRIMS ---");
            System.out.println("1) Crear nuevo Scrim (Organizador)");
            System.out.println("2) Buscar Scrims disponibles (Filtros)");
            System.out.println("3) Postularme a un Scrim");
            System.out.println("4) Ver mis Scrims activos (Postulado/Creado)");
            System.out.println("5) Ver historial de mis Postulaciones");
            System.out.println("6) Ver Postulantes a Scrim (Organizador)"); 
            System.out.println("0) Volver al menú principal");
            System.out.print("> ");

            String opt = sc.nextLine().trim();
            switch (opt) {
                case "1":
                    crearScrim(currentToken);
                    break;
                case "2":
                    buscarScrims(currentToken);
                    break;
                case "3":
                    postularseAScrim(currentToken);
                    break;
                case "4":
                    verMisScrims(currentToken);
                    break;
                case "5":
                    verMisPostulaciones(currentToken);
                    break;
                case "6":
                    verPostulacionesPorScrim(currentToken);
                    break; 
                case "0":
                    exit = true;
                    break;
                default:
                    System.out.println("Opción inválida.");
                    break;
            }
        }
    }

    public void crearScrim(String currentToken) {
        Optional<Usuario> uo = validarSesion(currentToken);
        if (uo.isEmpty())
            return;
        Usuario user = uo.get();

        System.out.println("\n--- Creación de Scrim ---");

        // --- 1. SELECCIÓN DEL JUEGO ---
        Juego juegoSeleccionado = elegirJuegoCreacion();
        if (juegoSeleccionado == null) {
            System.out.println("Creación cancelada.");
            return;
        }
        String juegoId = juegoSeleccionado.getNombre();
        // --- 2. FORMATO ---
        String formato = null;
        while (formato == null) {
            System.out.print("Formato (Opciones: " + FORMATOS_VALIDOS + "): ");
            String input = sc.nextLine().trim().toUpperCase();
            if (FORMATOS_VALIDOS.contains(input)) {
                formato = input;
            } else {
                System.out.println("Formato inválido. Debe ser uno de: " + FORMATOS_VALIDOS);
            }
        }

        // --- 3. SELECCIÓN DE REGIÓN (Usando Enum) ---
        String regionId = elegirRegion();
        if (regionId == null) {
            System.out.println("Creación cancelada.");
            return;
        }
        String modalidad = elegirModalidad(); // <--- RECOLECCIÓN NUEVA
        if (modalidad == null) {
            System.out.println("Modalidad inválida. Creación cancelada.");
            return;
        }

        // --- 4. RESTO DE LAS OPCIONES ---
        System.out.print("Rango Mínimo (ej: 1000): ");
        int rangoMin = Integer.parseInt(sc.nextLine());
        System.out.print("Rango Máximo (ej: 2500): ");
        int rangoMax = Integer.parseInt(sc.nextLine());
        System.out.print("Cupos Totales (ej: 10): ");
        int cupos = Integer.parseInt(sc.nextLine());
        System.out.print("Duración estimada (min): ");
        int duracion = Integer.parseInt(sc.nextLine());
        System.out.print("Latencia máxima (ms): ");
        int latenciaMaxMs = Integer.parseInt(sc.nextLine());

        Map<String, Integer> rolesRequeridos = new HashMap<>();
        List<String> rolesDisponibles = obtenerRolesDisponiblesParaJuego(juegoSeleccionado);
        System.out.println("\n--- Roles Requeridos (Opcional) ---");
        if (rolesDisponibles.isEmpty()) {
            System.out.println("No hay roles espec\u00edficos definidos para este juego.");
        } else {
            System.out.println("Roles disponibles: " + rolesDisponibles);
            System.out.print("\u00bfDesea especificar la cantidad de jugadores por Rol? (S/N): ");
            if (sc.nextLine().trim().toUpperCase().equals("S")) {
                int totalRoles = 0;
                int cuposRestantes = cupos;
                
                System.out.println("Ingrese la cantidad de cupos para cada rol. Total cupos: " + cupos);
                
                for (String rol : rolesDisponibles) {
                    System.out.print("Cupos para " + rol + " (m\u00e1x " + cuposRestantes + ", 0 para ignorar): ");
                    try {
                        String input = sc.nextLine().trim();
                        if (input.isEmpty()) continue;
                        int cantidad = Integer.parseInt(input);

                        if (cantidad > cuposRestantes) {
                            System.out.println("❌ Error: La cantidad excede los cupos restantes. Ignorando rol.");
                            continue;
                        }
                        if (cantidad > 0) {
                            rolesRequeridos.put(rol, cantidad);
                            cuposRestantes -= cantidad;
                            totalRoles += cantidad;
                        }
                        if (cuposRestantes == 0) break;
                    } catch (NumberFormatException e) {
                        System.out.println("Entrada inv\u00e1lida. Ignorando rol.");
                    }
                }
                
                if (totalRoles > 0 && totalRoles != cupos) {
                    System.out.println("\u26a0\ufe0f Advertencia: Solo se han asignado roles a " + totalRoles + " de " + cupos + " cupos totales. Los restantes ser\u00e1n flexibles.");
                } else if (totalRoles == 0 && cupos > 0) {
                    System.out.println("No se especific\u00f3 ning\u00fan rol. Todos los cupos ser\u00e1n flexibles.");
                    rolesRequeridos = new HashMap<>();
                }
            }
        }

        try {
            ScrimCreationDTO dto = new ScrimCreationDTO(
                    UUID.fromString(user.getId()),
                    juegoSeleccionado.getId(),
                    formato,
                    regionId,
                    rangoMin,
                    rangoMax,
                    latenciaMaxMs,
                    modalidad,
                    cupos,
                    LocalDateTime.now().plusMinutes(30),
                    duracion,
                    rolesRequeridos);
            scrimAppService.crearScrim(dto);
            System.out.println("✅ Scrim creado con éxito. Alerta disparada al sistema de notificaciones.");
        } catch (Exception e) {
            System.out.println("❌ Error al crear Scrim: " + e.getMessage());
        }
    }

    private String elegirModalidad() {
        while (true) {
            System.out.print("Modalidad (Opciones: " + MODALIDADES_VALIDAS + "): ");
            String input = sc.nextLine().trim().toUpperCase();

            if (MODALIDADES_VALIDAS.contains(input)) {
                return input;
            } else {
                System.out.println("Modalidad inválida. Debe ser uno de: " + MODALIDADES_VALIDAS + ".");
            }
        }
    }

    private String elegirRegion() {
        while (true) {
            System.out.println("\n--- REGIONES DISPONIBLES ---");
            System.out.println("Regiones: " + java.util.Arrays.toString(Region.values()));
            System.out.println("Escribe el nombre de la región (o 0 para cancelar): ");
            String input = sc.nextLine().trim().toUpperCase();

            if (input.equals("0"))
                return null;

            try {
                // Validamos la entrada contra el Enum Region
                Region reg = Region.valueOf(input);
                return reg.name(); // Devolvemos el String del nombre del Enum
            } catch (IllegalArgumentException e) {
                System.out.println("Región inválida. Por favor, elige una de las opciones.");
            }
        }
    }

    private Juego elegirJuegoCreacion() {
        Map<String, Juego> juegosDisponibles = juegoRepository.getJuegosDisponibles();
        while (true) {
            System.out.println("\n--- JUEGOS DISPONIBLES ---");
            int i = 1;
            List<Juego> listaJuegos = juegosDisponibles.values().stream().toList();
            for (Juego j : listaJuegos) {
                System.out.printf("%d) %s\n", i++, j.getNombre());
            }
            System.out.println("0) Cancelar creación");
            System.out.print("Juego a seleccionar (Número o Nombre): ");
            String input = sc.nextLine().trim();
            if (input.equals("0"))
                return null;

            Optional<Juego> juegoOpt = juegosDisponibles.values().stream()
                    .filter(j -> j.getNombre().equalsIgnoreCase(input))
                    .findFirst();
            if (juegoOpt.isEmpty()) {
                try {
                    int num = Integer.parseInt(input);
                    if (num > 0 && num <= listaJuegos.size()) {
                        juegoOpt = Optional.of(listaJuegos.get(num - 1));
                    }
                } catch (NumberFormatException e) {
                }
            }
            if (juegoOpt.isPresent()) {
                return juegoOpt.get();
            } else {
                System.out.println("Selección inválida. Intenta de nuevo.");
            }
        }
    }
    private List<String> obtenerRolesDisponiblesParaJuego(Juego juego) {
        if (juego != null) {
            return juego.getJuegoFactory().getRolesDelJuego();
        } else {
            return List.of();
        }
    }

    public void buscarScrims(String currentToken) {
        Optional<Usuario> uo = validarSesion(currentToken);
        if (uo.isEmpty()) {
            System.out.println("\n--- Búsqueda de Scrims (Filtros amplios) ---");
            System.out.println("Inicia sesión para guardar búsquedas favoritas y usar filtros predefinidos.");
        }

        Optional<FiltrosBusqueda> filtrosOpt = Optional.empty();

        if (uo.isPresent()) {
            filtrosOpt = elegirBusquedaFavorita(uo.get());
        }

        FiltrosBusqueda filtros = filtrosOpt.orElseGet(this::ingresarNuevosFiltros);

        if (filtros == null) {
            System.out.println("Búsqueda cancelada.");
            return;
        }

        // Ejecutar Búsqueda
        var resultados = scrimAppService.buscarScrims(filtros);
        // LÓGICA DE GUARDADO (Automático para nuevos filtros)
        if (uo.isPresent() && filtrosOpt.isEmpty()) { // Solo si ingresó filtros nuevos
            preguntarYGuardarBusqueda(uo.get(), filtros);
        }
        if (resultados.isEmpty()) {
            System.out.println("No se encontraron scrims con los filtros aplicados.");
        } else {
            System.out.println("Resultados encontrados (" + resultados.size() + "):");
            resultados.forEach(s -> System.out
                    .println("- ID: " + s.getId() + ", Juego: " + s.getJuegoId() + ", Formato: " + s.getFormato()
                            + ", Cupos: " + s.getCupos() + ", Rango: " + s.getRangoMin() + "-" + s.getRangoMax()));
        }

    }

    private void preguntarYGuardarBusqueda(Usuario user, FiltrosBusqueda filtros) {
        System.out.print("\nDesea guardar estos filtros como búsqueda favorita para recibir alertas? (S/N): ");
        String saveOpt = sc.nextLine().trim().toUpperCase();

        if (saveOpt.equals("S")) {
            System.out.print("Nombre para esta búsqueda: ");
            String nombre = sc.nextLine().trim();

            if (!nombre.isEmpty()) {
                try {
                    scrimAppService.guardarBusquedaFavorita(UUID.fromString(user.getId()), nombre, filtros);
                    System.out.println("Búsqueda '" + nombre + "' guardada con éxito. Recibirás alertas.");
                } catch (Exception e) {
                    System.out.println("Error al guardar la búsqueda favorita: " + e.getMessage());
                }
            } else {
                System.out.println("Guardado cancelado. El nombre no puede estar vacío.");
            }
        }
    }

    // <--- NUEVO MÉTODO: Permite ingresar nuevos filtros
    private FiltrosBusqueda ingresarNuevosFiltros() {
        System.out.println("\n--- Ingreso de Nuevos Filtros ---");
        // 1. Juego ID
        System.out.print("Juego ID a buscar (Deje en blanco para buscar todos): ");
        String juegoInput = sc.nextLine().trim();
        String juegoBuscado = juegoInput.isEmpty() ? null : juegoInput;

        // 2. Formato
        String formatoBuscado = null;
        while (formatoBuscado == null) {
            System.out
                    .print("Formato a buscar (Opciones: " + FORMATOS_VALIDOS + ". Deje en blanco para buscar todos): ");
            String formatoInput = sc.nextLine().trim().toUpperCase();
            if (formatoInput.isEmpty()) {
                formatoBuscado = null; // Permite buscar todos (null)
                break; // <--- CORRECCIÓN CLAVE: Sale del bucle si se deja en blanco
            } else if (FORMATOS_VALIDOS.contains(formatoInput)) {
                formatoBuscado = formatoInput;
            } else {
                System.out.println("Formato inválido. Debe ser uno de: " + FORMATOS_VALIDOS + " o dejar en blanco.");
            }
        }
        // 3. Región
        System.out.print("Región a buscar (Deje en blanco para buscar todos): ");
        String regionInput = sc.nextLine().trim().toUpperCase();
        String regionBuscado = regionInput.isEmpty() ? null : regionInput;

        //fecha
        LocalDateTime fechaHoraBuscada = ingresarFechaHoraBusqueda();
        // 4 & 5. Rangos y Latencia (Usamos valores por defecto si no se ingresa nada)
        int rangoMin = 0;
        int rangoMax = 10000;
        int latenciaMaxMs = 999;
        try {
            System.out.print("Rango Mínimo (0 para ignorar): ");
            String minStr = sc.nextLine().trim();
            if (!minStr.isEmpty())
                rangoMin = Integer.parseInt(minStr);
            System.out.print("Rango Máximo (10000 para ignorar): ");
            String maxStr = sc.nextLine().trim();
            if (!maxStr.isEmpty())
                rangoMax = Integer.parseInt(maxStr);
            System.out.print("Latencia Máxima (ms - 999 para ignorar): ");
            String latStr = sc.nextLine().trim();
            if (!latStr.isEmpty())
                latenciaMaxMs = Integer.parseInt(latStr);
        } catch (NumberFormatException e) {
            System.out.println("Valores numéricos inválidos. Usando filtros por defecto (0-10000, 999ms).");
        }
        return new FiltrosBusqueda(
                juegoBuscado,
                formatoBuscado,
                regionBuscado,
                rangoMin,
                rangoMax,
                latenciaMaxMs,
                fechaHoraBuscada
                );
    }

    private LocalDateTime ingresarFechaHoraBusqueda() {
        System.out.print("¿Desea buscar Scrims que comiencen DESPUÉS de una fecha/hora específica? (S/N): ");
        String opt = sc.nextLine().trim().toUpperCase();
        
        if (!opt.equals("S")) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        while (true) {
            System.out.print("Fecha/Hora Mínima (Formato YYYY-MM-DD HH:MM): ");
            String input = sc.nextLine().trim();
            try {

                return LocalDateTime.parse(input, formatter);
            } catch (Exception e) {
                System.out.println("Formato de fecha/hora inválido. Use YYYY-MM-DD HH:MM. Intente de nuevo.");
            }
        }
    }

    private Optional<FiltrosBusqueda> elegirBusquedaFavorita(Usuario user) {
        List<BusquedaFavorita> favoritas = scrimAppService
                .findBusquedasFavoritasByUsuarioId(UUID.fromString(user.getId()));

        if (favoritas.isEmpty()) {
            System.out.println("\nNo tienes búsquedas favoritas guardadas. Ingresa una nueva.");
            return Optional.empty();
        }

        while (true) {
            System.out.println("\n--- SELECCIÓN DE BÚSQUEDA ---");
            int i = 1;
            for (BusquedaFavorita fav : favoritas) {
                System.out.printf("%d) %s (Juego: %s, Rango: %d-%d)\n",
                        i++,
                        fav.getNombre(),
                        fav.getFiltros().juegoId() != null ? fav.getFiltros().juegoId() : "Todos",
                        fav.getFiltros().rangoMin(),
                        fav.getFiltros().rangoMax());
            }
            System.out.println("0) Ingresar nuevos filtros manualmente");
            System.out.print("Elige un número de búsqueda o 0: ");
            String input = sc.nextLine().trim();

            if (input.equals("0"))
                return Optional.empty();

            try {
                int num = Integer.parseInt(input);
                if (num > 0 && num <= favoritas.size()) {
                    System.out.println("✅ Usando filtros de: " + favoritas.get(num - 1).getNombre());
                    return Optional.of(favoritas.get(num - 1).getFiltros());
                }
            } catch (NumberFormatException e) {
                // Ignore
            }
            System.out.println("Opción inválida. Intenta de nuevo.");
        }
    }

    public void postularseAScrim(String currentToken) {
        Optional<Usuario> uo = validarSesion(currentToken);
        if (uo.isEmpty())
            return;
        Usuario user = uo.get();

        System.out.println("\n--- Postularse a Scrim ---");
        System.out.print("ID del Scrim al que deseas postularte (UUID): ");
        String scrimIdStr = sc.nextLine().trim();

        UUID scrimId;
        try {
            scrimId = UUID.fromString(scrimIdStr);
        } catch (IllegalArgumentException e) {
            System.out.println("ID de Scrim inválido. Asegúrate de ingresar un UUID correcto.");
            return;
        }

        // 1. OBTENER SCRIM PARA SABER EL JUEGO
        Optional<Scrim> scrimOpt = scrimAppService.findById(scrimId);
        if (scrimOpt.isEmpty()) {
            System.out.println("Scrim no encontrado. Postulación cancelada.");
            return;
        }
        Scrim scrim = scrimOpt.get();

        // 2. MUESTRA LOS ROLES ESPECÍFICOS DEL JUEGO (Lo que solicitaste)
        List<String> rolesDisponibles = obtenerRolesDisponiblesParaScrim(scrim);

        if (rolesDisponibles.isEmpty()) {
            System.out.println("No hay roles definidos para este juego. Puedes dejar el rol en blanco.");
        } else {
            // Muestra la lista específica de la Factory
            System.out.println("Roles disponibles: " + rolesDisponibles);
        }

        System.out.print("Rol deseado (Deje en blanco si es flexible): ");
        String rolDeseadoInput = sc.nextLine().trim();
        String rolDeseado = rolDeseadoInput.isEmpty() ? null : rolDeseadoInput.toUpperCase();

        // 3. DELEGAR AL SERVICIO
        Optional<Postulacion> postulado = scrimAppService.postularse(
                scrimId,
                UUID.fromString(user.getId()),
                rolDeseado);

        if (postulado.isPresent()) {
            System.out.println("Postulación exitosa. ID de Postulación: " + postulado.get().getId());
            System.out.println("El organizador revisará tu solicitud.");
        } else {
            System.out.println(
                    "Fallo en la postulación. Verifica que el Scrim exista, que tengas perfil de juego configurado, y que el rol sea válido.");
        }
    }

    private List<String> obtenerRolesDisponiblesParaScrim(Scrim scrim) {
        // Usa el repositorio inyectado en el constructor (juegoRepository)
        Optional<Juego> juegoOpt = juegoRepository.findByStringId(scrim.getJuegoId());

        if (juegoOpt.isPresent()) {
            // Devuelve la lista REAL de la Factory (Patrón Factory)
            return juegoOpt.get().getJuegoFactory().getRolesDelJuego();
        } else {
            // Defensa contra datos corruptos
            return List.of();
        }
    }

    public void verMisPostulaciones(String currentToken) {
        Optional<Usuario> uo = validarSesion(currentToken);
        if (uo.isEmpty())
            return;
        Usuario user = uo.get();
        UUID usuarioId = UUID.fromString(user.getId());
        List<Postulacion> misPostulaciones = scrimAppService.findPostulacionesByUsuarioId(usuarioId);
        System.out.println("\n--- Mis Postulaciones (" + misPostulaciones.size() + ") ---");
        if (misPostulaciones.isEmpty()) {
            System.out.println("Aún no tienes postulaciones registradas.");
            return;
        }
        misPostulaciones.forEach(p -> System.out.println("- ID: " + p.getId() +
                ", Scrim ID: " + p.getScrimId() +
                ", Rol: " + (p.getRolDeseado() != null ? p.getRolDeseado() : "Cualquiera") +
                ", Estado: " + p.getEstado()));
    }

    public void verPostulacionesPorScrim(String currentToken) {
        Optional<Usuario> uo = validarSesion(currentToken);
        if (uo.isEmpty())
            return;
        Usuario user = uo.get();

        System.out.println("\n--- Postulantes por Scrim (Organizador) ---");
        System.out.print("ID del Scrim que organizas: ");
        String scrimIdStr = sc.nextLine().trim();

        UUID scrimId;
        try {
            scrimId = UUID.fromString(scrimIdStr);
        } catch (IllegalArgumentException e) {
            System.out.println("ID de Scrim inválido.");
            return;
        }

        // 1. Validar que el usuario sea el organizador (o al menos que el Scrim exista)
        Optional<Scrim> scrimOpt = scrimAppService.findById(scrimId);
        if (scrimOpt.isEmpty()) {
            System.out.println("Scrim no encontrado.");
            return;
        }

        Scrim scrim = scrimOpt.get();
        if (!scrim.getOrganizadorId().equals(UUID.fromString(user.getId()))) {
            System.out.println("Acceso denegado: Solo el organizador puede ver los postulantes.");
            return;
        }

        // 2. Obtener la lista de postulaciones
        List<Postulacion> postulaciones = scrimAppService.findApplicantsForScrim(scrimId);

        System.out.println("\nPostulaciones para Scrim ID: " + scrimId + " (" + postulaciones.size() + " en total)");

        if (postulaciones.isEmpty()) {
            System.out.println("No hay postulantes registrados todavía.");
            return;
        }

        postulaciones.forEach(p -> System.out.println("- Jugador ID: " + p.getUsuarioId() +
                ", Rol Deseado: " + (p.getRolDeseado() != null ? p.getRolDeseado() : "Flexible") +
                ", Estado: " + p.getEstado()));
    }

    // MÉTODO DE VISIBILIDAD DE SCRIMS

    public void verMisScrims(String currentToken) {
        Optional<Usuario> uo = validarSesion(currentToken);
        if (uo.isEmpty())
            return;
        Usuario user = uo.get();

        UUID usuarioId = UUID.fromString(user.getId());

        // 1. Obtener los IDs de los Scrims a los que el usuario se postuló (Visibilidad
        // por Postulación)
        List<Postulacion> misPostulaciones = scrimAppService.findPostulacionesByUsuarioId(usuarioId);
        Set<UUID> scrimIds = misPostulaciones.stream()
                .map(Postulacion::getScrimId)
                .collect(Collectors.toSet());

        // 2. Obtener los IDs de los Scrims que el usuario creó (Visibilidad por
        // Organización)
        List<Scrim> scrimsOrganizados = scrimAppService.findScrimsOrganizadosPor(usuarioId);
        scrimsOrganizados.stream()
                .map(Scrim::getId)
                .forEach(scrimIds::add); // Agregar todos los IDs a la colección (Set asegura unicidad)

        if (scrimIds.isEmpty()) {
            System.out.println("\n No estás postulado ni has creado ningún Scrim activo.");
            return;
        }

        // 3. Obtener los objetos Scrim completos
        List<Scrim> misScrims = scrimAppService.findScrimsByIds(scrimIds);

        System.out.println("\n--- Mis Scrims Activos (" + misScrims.size() + ") ---");

        misScrims.forEach(s -> {
            String rol = misPostulaciones.stream()
                    .filter(p -> p.getScrimId().equals(s.getId()))
                    .map(p -> p.getRolDeseado() != null ? p.getRolDeseado() : "Cualquiera")
                    .findFirst().orElse("-");

            String tipo = s.getOrganizadorId().equals(usuarioId) ? "ORGANIZADOR" : "PARTICIPANTE";

            System.out.println("-> ID: " + s.getId() +
                    ", Juego: " + s.getJuegoId() +
                    ", Formato: " + s.getFormato() +
                    ", Cupos: " + s.getCupos() +
                    ", ROL: " + rol +
                    " (" + tipo + ") " +
                    ", ESTADO: " + s.getStatus());
        });
    }
}