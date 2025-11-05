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
        if (uo.isEmpty()) return;

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
                case "1" -> crearScrim(currentToken);
                case "2" -> buscarScrims(currentToken);
                case "3" -> postularseAScrim(currentToken);
                case "4" -> verMisScrims(currentToken);
                case "5" -> verMisPostulaciones(currentToken);
                case "6" -> verPostulacionesPorScrim(currentToken);
                case "0" -> exit = true;
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    public void crearScrim(String currentToken) {
        Optional<Usuario> uo = validarSesion(currentToken);
        if (uo.isEmpty()) return;
        Usuario user = uo.get();

        System.out.println("\n--- Creación de Scrim ---");

        // 1) Juego
        Juego juegoSeleccionado = elegirJuegoCreacion();
        if (juegoSeleccionado == null) {
            System.out.println("Creación cancelada.");
            return;
        }

        // 2) Formato
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

        // 3) Región + Modalidad
        String regionId = elegirRegion();
        if (regionId == null) {
            System.out.println("Creación cancelada.");
            return;
        }
        String modalidad = elegirModalidad();
        if (modalidad == null) {
            System.out.println("Modalidad inválida. Creación cancelada.");
            return;
        }

        // 4) Restricciones y datos
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

        // 5) Roles requeridos (opcional)
        Map<String, Integer> rolesRequeridos = new HashMap<>();
        List<String> rolesDisponibles = obtenerRolesDisponiblesParaJuego(juegoSeleccionado);

        System.out.println("\n--- Roles Requeridos (Opcional) ---");
        if (rolesDisponibles.isEmpty()) {
            System.out.println("No hay roles específicos definidos para este juego.");
        } else {
            System.out.println("Roles disponibles: " + rolesDisponibles);
            System.out.print("¿Desea especificar la cantidad de jugadores por rol? (S/N): ");
            boolean porRol = sc.nextLine().trim().equalsIgnoreCase("S");

            if (porRol) {
                int totalRoles = 0;
                int cuposRestantes = cupos;
                System.out.println("Ingrese la cantidad de cupos para cada rol. Total de cupos: " + cupos);

                for (String rol : rolesDisponibles) {
                    while (true) {
                        System.out.print("Cupos para \"" + rol + "\" (restantes " + cuposRestantes + ", 0 para ignorar): ");
                        String input = sc.nextLine().trim();
                        if (input.isEmpty()) break;

                        try {
                            int cantidad = Integer.parseInt(input);
                            if (cantidad < 0) {
                                System.out.println("Debe ser un número >= 0.");
                                continue;
                            }
                            if (cantidad > cuposRestantes) {
                                System.out.println("La cantidad excede los cupos restantes.");
                                continue;
                            }
                            if (cantidad > 0) {
                                rolesRequeridos.put(rol, cantidad);
                                cuposRestantes -= cantidad;
                                totalRoles += cantidad;
                            }
                            break;
                        } catch (NumberFormatException e) {
                            System.out.println("Número inválido. Intente de nuevo.");
                        }
                    }
                    if (cuposRestantes == 0) break;
                }

                if (totalRoles > 0 && totalRoles != cupos) {
                    System.out.println("⚠️ Advertencia: la suma por rol (" + totalRoles + ") no coincide con los cupos totales (" + cupos + ").");
                    System.out.print("¿Continuar de todos modos? (S/N): ");
                    if (!sc.nextLine().trim().equalsIgnoreCase("S")) {
                        System.out.println("Creación cancelada por el usuario.");
                        return;
                    }
                } else if (totalRoles == 0) {
                    System.out.println("No se especificó ningún rol. Todos los cupos serán flexibles.");
                    rolesRequeridos.clear();
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
                    rolesRequeridos
            );

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
            if (MODALIDADES_VALIDAS.contains(input)) return input;
            System.out.println("Modalidad inválida. Debe ser una de: " + MODALIDADES_VALIDAS + ".");
        }
    }

    private String elegirRegion() {
        while (true) {
            System.out.println("\n--- REGIONES DISPONIBLES ---");
            System.out.println("Regiones: " + java.util.Arrays.toString(Region.values()));
            System.out.print("Escribe el nombre de la región (o 0 para cancelar): ");
            String input = sc.nextLine().trim().toUpperCase();

            if (input.equals("0")) return null;
            try {
                Region reg = Region.valueOf(input);
                return reg.name();
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
            for (Juego j : listaJuegos) System.out.printf("%d) %s%n", i++, j.getNombre());

            System.out.println("0) Cancelar creación");
            System.out.print("Juego a seleccionar (Número o Nombre): ");
            String input = sc.nextLine().trim();
            if (input.equals("0")) return null;

            Optional<Juego> juegoOpt = juegosDisponibles.values().stream()
                    .filter(j -> j.getNombre().equalsIgnoreCase(input))
                    .findFirst();
            if (juegoOpt.isEmpty()) {
                try {
                    int num = Integer.parseInt(input);
                    if (num > 0 && num <= listaJuegos.size()) juegoOpt = Optional.of(listaJuegos.get(num - 1));
                } catch (NumberFormatException ignore) {}
            }
            if (juegoOpt.isPresent()) return juegoOpt.get();
            System.out.println("Selección inválida. Intenta de nuevo.");
        }
    }

    private List<String> obtenerRolesDisponiblesParaJuego(Juego juego) {
        if (juego != null) return juego.getJuegoFactory().getRolesDelJuego();
        return List.of();
    }

    public void buscarScrims(String currentToken) {
        Optional<Usuario> uo = validarSesion(currentToken);
        if (uo.isEmpty()) {
            System.out.println("\n--- Búsqueda de Scrims (Filtros amplios) ---");
            System.out.println("Inicia sesión para guardar búsquedas favoritas y usar filtros predefinidos.");
        }

        Optional<FiltrosBusqueda> filtrosOpt = Optional.empty();
        if (uo.isPresent()) filtrosOpt = elegirBusquedaFavorita(uo.get());

        FiltrosBusqueda filtros = filtrosOpt.orElseGet(this::ingresarNuevosFiltros);
        if (filtros == null) {
            System.out.println("Búsqueda cancelada.");
            return;
        }

        var resultados = scrimAppService.buscarScrims(filtros);

        if (uo.isPresent() && filtrosOpt.isEmpty()) {
            preguntarYGuardarBusqueda(uo.get(), filtros);
        }

        if (resultados.isEmpty()) {
            System.out.println("No se encontraron scrims con los filtros aplicados.");
        } else {
            System.out.println("Resultados encontrados (" + resultados.size() + "):");
            resultados.forEach(s -> System.out.println(
                    "- ID: " + s.getId() +
                    ", Juego: " + s.getJuegoId() +
                    ", Formato: " + s.getFormato() +
                    ", Cupos: " + s.getCupos() +
                    ", Rango: " + s.getRangoMin() + "-" + s.getRangoMax()
            ));
        }
    }

    private void preguntarYGuardarBusqueda(Usuario user, FiltrosBusqueda filtros) {
        System.out.print("\n¿Desea guardar estos filtros como búsqueda favorita para recibir alertas? (S/N): ");
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

    private FiltrosBusqueda ingresarNuevosFiltros() {
        System.out.println("\n--- Ingreso de Nuevos Filtros ---");

        System.out.print("Juego ID a buscar (Deje en blanco para todos): ");
        String juegoBuscado = sc.nextLine().trim();
        if (juegoBuscado.isEmpty()) juegoBuscado = null;

        String formatoBuscado = null;
        while (formatoBuscado == null) {
            System.out.print("Formato a buscar (Opciones: " + FORMATOS_VALIDOS + ". Deje en blanco para todos): ");
            String formatoInput = sc.nextLine().trim().toUpperCase();
            if (formatoInput.isEmpty()) {
                formatoBuscado = null;
                break;
            } else if (FORMATOS_VALIDOS.contains(formatoInput)) {
                formatoBuscado = formatoInput;
            } else {
                System.out.println("Formato inválido. Use uno de: " + FORMATOS_VALIDOS + " o deje en blanco.");
            }
        }

        System.out.print("Región a buscar (Deje en blanco para todas): ");
        String regionInput = sc.nextLine().trim().toUpperCase();
        String regionBuscado = regionInput.isEmpty() ? null : regionInput;

        LocalDateTime fechaHoraBuscada = ingresarFechaHoraBusqueda();

        int rangoMin = 0, rangoMax = 10000, latenciaMaxMs = 999;
        try {
            System.out.print("Rango Mínimo (0 para ignorar): ");
            String minStr = sc.nextLine().trim();
            if (!minStr.isEmpty()) rangoMin = Integer.parseInt(minStr);

            System.out.print("Rango Máximo (10000 para ignorar): ");
            String maxStr = sc.nextLine().trim();
            if (!maxStr.isEmpty()) rangoMax = Integer.parseInt(maxStr);

            System.out.print("Latencia Máxima (ms - 999 para ignorar): ");
            String latStr = sc.nextLine().trim();
            if (!latStr.isEmpty()) latenciaMaxMs = Integer.parseInt(latStr);
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
        if (!opt.equals("S")) return null;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        while (true) {
            System.out.print("Fecha/Hora Mínima (Formato YYYY-MM-DD HH:MM): ");
            String input = sc.nextLine().trim();
            try {
                return LocalDateTime.parse(input, formatter);
            } catch (Exception e) {
                System.out.println("Formato inválido. Use YYYY-MM-DD HH:MM. Intente de nuevo.");
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
                System.out.printf("%d) %s (Juego: %s, Rango: %d-%d)%n",
                        i++,
                        fav.getNombre(),
                        fav.getFiltros().juegoId() != null ? fav.getFiltros().juegoId() : "Todos",
                        fav.getFiltros().rangoMin(),
                        fav.getFiltros().rangoMax());
            }
            System.out.println("0) Ingresar nuevos filtros manualmente");
            System.out.print("Elige un número de búsqueda o 0: ");
            String input = sc.nextLine().trim();

            if (input.equals("0")) return Optional.empty();

            try {
                int num = Integer.parseInt(input);
                if (num > 0 && num <= favoritas.size()) {
                    System.out.println("✅ Usando filtros de: " + favoritas.get(num - 1).getNombre());
                    return Optional.of(favoritas.get(num - 1).getFiltros());
                }
            } catch (NumberFormatException ignore) {}
            System.out.println("Opción inválida. Intenta de nuevo.");
        }
    }

    public void postularseAScrim(String currentToken) {
        Optional<Usuario> uo = validarSesion(currentToken);
        if (uo.isEmpty()) return;
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

        Optional<Scrim> scrimOpt = scrimAppService.findById(scrimId);
        if (scrimOpt.isEmpty()) {
            System.out.println("Scrim no encontrado. Postulación cancelada.");
            return;
        }
        Scrim scrim = scrimOpt.get();

        List<String> rolesDisponibles = obtenerRolesDisponiblesParaScrim(scrim);
        if (rolesDisponibles.isEmpty()) {
            System.out.println("No hay roles definidos para este juego. Puedes dejar el rol en blanco.");
        } else {
            System.out.println("Roles disponibles: " + rolesDisponibles);
        }

        System.out.print("Rol deseado (Deje en blanco si es flexible): ");
        String rolDeseadoInput = sc.nextLine().trim();
        String rolDeseado = rolDeseadoInput.isEmpty() ? null : rolDeseadoInput.toUpperCase();

        Optional<Postulacion> postulado = scrimAppService.postularse(
                scrimId,
                UUID.fromString(user.getId()),
                rolDeseado
        );

        if (postulado.isPresent()) {
            System.out.println("Postulación exitosa. ID de Postulación: " + postulado.get().getId());
            System.out.println("El organizador revisará tu solicitud.");
        } else {
            System.out.println("Fallo en la postulación. Verifica que el Scrim exista, que tengas perfil de juego configurado, y que el rol sea válido.");
        }
    }

    private List<String> obtenerRolesDisponiblesParaScrim(Scrim scrim) {
        Optional<Juego> juegoOpt = juegoRepository.findByStringId(scrim.getJuegoId());
        if (juegoOpt.isPresent()) return juegoOpt.get().getJuegoFactory().getRolesDelJuego();
        return List.of();
    }

    public void verMisPostulaciones(String currentToken) {
        Optional<Usuario> uo = validarSesion(currentToken);
        if (uo.isEmpty()) return;

        Usuario user = uo.get();
        UUID usuarioId = UUID.fromString(user.getId());
        List<Postulacion> misPostulaciones = scrimAppService.findPostulacionesByUsuarioId(usuarioId);

        System.out.println("\n--- Mis Postulaciones (" + misPostulaciones.size() + ") ---");
        if (misPostulaciones.isEmpty()) {
            System.out.println("Aún no tienes postulaciones registradas.");
            return;
        }

        misPostulaciones.forEach(p -> System.out.println(
                "- ID: " + p.getId() +
                ", Scrim ID: " + p.getScrimId() +
                ", Rol: " + (p.getRolDeseado() != null ? p.getRolDeseado() : "Cualquiera") +
                ", Estado: " + p.getEstado()
        ));
    }

    public void verPostulacionesPorScrim(String currentToken) {
        Optional<Usuario> uo = validarSesion(currentToken);
        if (uo.isEmpty()) return;
        Usuario user = uo.get();

        System.out.println("\n--- Postulantes por Scrim (Organizador) ---");
        System.out.print("ID del Scrim que organizas (UUID): ");
        String scrimIdStr = sc.nextLine().trim();

        UUID scrimId;
        try {
            scrimId = UUID.fromString(scrimIdStr);
        } catch (IllegalArgumentException e) {
            System.out.println("ID de Scrim inválido.");
            return;
        }

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

        List<Postulacion> postulaciones = scrimAppService.findApplicantsForScrim(scrimId);

        System.out.println("\nPostulaciones para Scrim ID: " + scrimId + " (" + postulaciones.size() + " en total)");
        if (postulaciones.isEmpty()) {
            System.out.println("No hay postulantes registrados todavía.");
            return;
        }

        postulaciones.forEach(p -> System.out.println(
                "- Jugador ID: " + p.getUsuarioId() +
                ", Rol Deseado: " + (p.getRolDeseado() != null ? p.getRolDeseado() : "Flexible") +
                ", Estado: " + p.getEstado()
        ));
    }

    public void verMisScrims(String currentToken) {
        Optional<Usuario> uo = validarSesion(currentToken);
        if (uo.isEmpty()) return;

        Usuario user = uo.get();
        UUID usuarioId = UUID.fromString(user.getId());

        List<Postulacion> misPostulaciones = scrimAppService.findPostulacionesByUsuarioId(usuarioId);
        Set<UUID> scrimIds = misPostulaciones.stream()
                .map(Postulacion::getScrimId)
                .collect(Collectors.toSet());

        List<Scrim> scrimsOrganizados = scrimAppService.findScrimsOrganizadosPor(usuarioId);
        scrimsOrganizados.stream().map(Scrim::getId).forEach(scrimIds::add);

        if (scrimIds.isEmpty()) {
            System.out.println("\nNo estás postulado ni has creado ningún Scrim activo.");
            return;
        }

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
                    ", ESTADO: " + s.getEstado());
        });
    }
}
