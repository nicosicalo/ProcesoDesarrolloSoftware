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
import Enums.Region; // <--- Importación necesaria para Region enum

public class ScrimController {
    private final Scanner sc;
    private final SessionManager sessionManager;
    private final ScrimAppService scrimAppService;
    private final BusquedaFavoritaRepository busquedaRepo;
    private final JuegoRepository juegoRepository;

    public ScrimController(Scanner sc, SessionManager sessionManager, ScrimAppService scrimAppService, BusquedaFavoritaRepository busquedaRepo, JuegoRepository juegoRepository) {
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

    // ======================================================
    // 🚀 FACHADA DE MENÚ (GESTIÓN DE SCRIMS)
    // ======================================================
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
            System.out.println("6) Ver Postulantes a Scrim (Organizador)"); // <--- ¡NUEVA OPCIÓN!
            System.out.println("0) Volver al menú principal");
            System.out.print("> ");

            String opt = sc.nextLine().trim();
            switch (opt) {
                case "1": crearScrim(currentToken); break;
                case "2": buscarScrims(); break;
                case "3": postularseAScrim(currentToken); break;
                case "4": verMisScrims(currentToken); break;
                case "5": verMisPostulaciones(currentToken); break;
                case "6": verPostulacionesPorScrim(currentToken); break; // <--- NUEVO CASE
                case "0": exit = true; break;
                default: System.out.println("Opción inválida."); break;
            }
        }
    }


    public void crearScrim(String currentToken) {
        Optional<Usuario> uo = validarSesion(currentToken);
        if (uo.isEmpty()) return;
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
        System.out.print("Formato (ej: 5v5): "); String formato = sc.nextLine();
        
        // --- 3. SELECCIÓN DE REGIÓN (Usando Enum) ---
        String regionId = elegirRegion();
        if (regionId == null) {
            System.out.println("Creación cancelada.");
            return;
        }

        // --- 4. RESTO DE LAS OPCIONES ---
        System.out.print("Rango Mínimo (ej: 1000): "); int rangoMin = Integer.parseInt(sc.nextLine());
        System.out.print("Rango Máximo (ej: 2500): "); int rangoMax = Integer.parseInt(sc.nextLine());
        System.out.print("Cupos Totales (ej: 10): "); int cupos = Integer.parseInt(sc.nextLine());
        System.out.print("Duración estimada (min): "); int duracion = Integer.parseInt(sc.nextLine());

        try {
            ScrimCreationDTO dto = new ScrimCreationDTO(
                    UUID.fromString(user.getId()), juegoSeleccionado.getId(), formato, regionId, rangoMin, rangoMax, cupos, LocalDateTime.now().plusMinutes(30), duracion
            );
            scrimAppService.crearScrim(dto);
            System.out.println("Scrim creado con éxito. Alerta disparada al sistema de notificaciones.");
        } catch (Exception e) {
            System.out.println("Error al crear Scrim: " + e.getMessage());
        }
    }

    private String elegirRegion() {
        while (true) {
            System.out.println("\n--- REGIONES DISPONIBLES ---");
            System.out.println("Regiones: " + java.util.Arrays.toString(Region.values()));
            System.out.println("Escribe el nombre de la región (o 0 para cancelar): ");
            String input = sc.nextLine().trim().toUpperCase();

            if (input.equals("0")) return null;

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
            if (input.equals("0")) return null;

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

    public void buscarScrims() {
        System.out.println("\n--- Búsqueda de Scrims (Filtros amplios) ---");
        // 1. Juego ID
        System.out.print("Juego ID a buscar (Deje en blanco para buscar todos): ");
        String juegoInput = sc.nextLine().trim();
        String juegoBuscado = juegoInput.isEmpty() ? null : juegoInput;
        // 2. Formato
        System.out.print("Formato a buscar (Deje en blanco para buscar todos): ");
        String formatoInput = sc.nextLine().trim();
        String formatoBuscado = formatoInput.isEmpty() ? null : formatoInput;
        // 3. Región
        System.out.print("Región a buscar (Deje en blanco para buscar todos): ");
        String regionInput = sc.nextLine().trim().toUpperCase();
        String regionBuscado = regionInput.isEmpty() ? null : regionInput;
        // 4 & 5. Rangos y Latencia (Usamos valores por defecto si no se ingresa nada)
        int rangoMin = 0;
        int rangoMax = 10000;
        int latenciaMaxMs = 999;
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
        FiltrosBusqueda filtros = new FiltrosBusqueda(
                juegoBuscado,
                formatoBuscado,
                regionBuscado,
                rangoMin,
                rangoMax,
                latenciaMaxMs,
                null
        );
        var resultados = scrimAppService.buscarScrims(filtros);
        if (resultados.isEmpty()) {
            System.out.println("No se encontraron scrims con los filtros aplicados.");
        } else {
            System.out.println("Resultados encontrados (" + resultados.size() + "):");
            resultados.forEach(s -> System.out.println("- ID: " + s.getId() + ", Juego: " + s.getJuegoId() + ", Formato: " + s.getFormato() + ", Cupos: " + s.getCupos() + ", Rango: " + s.getRangoMin() + "-" + s.getRangoMax()));
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
        if (uo.isEmpty()) return;
        Usuario user = uo.get();
        UUID usuarioId = UUID.fromString(user.getId());
        List<Postulacion> misPostulaciones = scrimAppService.findPostulacionesByUsuarioId(usuarioId);
        System.out.println("\n--- Mis Postulaciones (" + misPostulaciones.size() + ") ---");
        if (misPostulaciones.isEmpty()) {
            System.out.println("Aún no tienes postulaciones registradas.");
            return;
        }
        misPostulaciones.forEach(p ->
            System.out.println("- ID: " + p.getId() +
                               ", Scrim ID: " + p.getScrimId() +
                               ", Rol: " + (p.getRolDeseado() != null ? p.getRolDeseado() : "Cualquiera") +
                               ", Estado: " + p.getEstado())
        );
    }
    public void verPostulacionesPorScrim(String currentToken) {
        Optional<Usuario> uo = validarSesion(currentToken);
        if (uo.isEmpty()) return;
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
        
        postulaciones.forEach(p -> 
            System.out.println("- Jugador ID: " + p.getUsuarioId() + 
                               ", Rol Deseado: " + (p.getRolDeseado() != null ? p.getRolDeseado() : "Flexible") +
                               ", Estado: " + p.getEstado())
        );
    }

    // MÉTODO DE VISIBILIDAD DE SCRIMS

    public void verMisScrims(String currentToken) {
        Optional<Usuario> uo = validarSesion(currentToken);
        if (uo.isEmpty()) return;
        Usuario user = uo.get();

        UUID usuarioId = UUID.fromString(user.getId());
        
        // 1. Obtener los IDs de los Scrims a los que el usuario se postuló (Visibilidad por Postulación)
        List<Postulacion> misPostulaciones = scrimAppService.findPostulacionesByUsuarioId(usuarioId);
        Set<UUID> scrimIds = misPostulaciones.stream()
                .map(Postulacion::getScrimId)
                .collect(Collectors.toSet());

        // 2. Obtener los IDs de los Scrims que el usuario creó (Visibilidad por Organización)
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
                               ", ESTADO: " + s.getEstado());
        });
    }
}