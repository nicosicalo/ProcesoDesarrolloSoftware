package Controller;

import Models.Usuario;
import ScrimsAuth.SessionManager;
import Service.FiltrosBusqueda;
import Service.ScrimAppService;
import Service.ScrimCreationDTO;
import Models.Postulacion;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

public class ScrimController {
    private final Scanner sc;
    private final SessionManager sessionManager;
    private final ScrimAppService scrimAppService;

    public ScrimController(Scanner sc, SessionManager sessionManager, ScrimAppService scrimAppService) {
        this.sc = sc;
        this.sessionManager = sessionManager;
        this.scrimAppService = scrimAppService;
    }

    private Optional<Usuario> validarSesion(String currentToken) {
        if (currentToken == null) {
            System.out.println("⚠️ No estás logueado.");
            return Optional.empty();
        }
        Optional<Usuario> uo = sessionManager.getUser(currentToken);
        if (uo.isEmpty()) {
            System.out.println("⚠️ Sesión no válida. Por favor, vuelve a iniciar sesión.");
            return Optional.empty();
        }
        return uo;
    }

    public void crearScrim(String currentToken) {
        Optional<Usuario> uo = validarSesion(currentToken);
        if (uo.isEmpty()) return;
        Usuario user = uo.get();

        System.out.println("\n--- Creación de Scrim ---");
        System.out.print("Juego ID (ej: valorant): "); String juegoId = sc.nextLine();
        System.out.print("Formato (ej: 5v5): "); String formato = sc.nextLine();
        System.out.print("Región (ej: NA): "); String regionId = sc.nextLine();
        System.out.print("Rango Mínimo (ej: 1000): "); int rangoMin = Integer.parseInt(sc.nextLine());
        System.out.print("Rango Máximo (ej: 2500): "); int rangoMax = Integer.parseInt(sc.nextLine());
        System.out.print("Cupos Totales (ej: 10): "); int cupos = Integer.parseInt(sc.nextLine());
        System.out.print("Duración estimada (min): "); int duracion = Integer.parseInt(sc.nextLine());

        try {
            ScrimCreationDTO dto = new ScrimCreationDTO(
                    UUID.fromString(user.getId()), juegoId, formato, regionId, rangoMin, rangoMax, cupos, LocalDateTime.now().plusMinutes(30), duracion
            );

            scrimAppService.crearScrim(dto);
            System.out.println("✅ Scrim creado con éxito. Alerta disparada al sistema de notificaciones.");

        } catch (Exception e) {
            System.out.println("❌ Error al crear Scrim: " + e.getMessage());
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
            System.out.println("⚠️ Valores numéricos inválidos. Usando filtros por defecto (0-10000, 999ms).");
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
            System.out.println("🔍 No se encontraron scrims con los filtros aplicados.");
        } else {
            System.out.println("✅ Resultados encontrados (" + resultados.size() + "):");
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
            System.out.println("❌ ID de Scrim inválido. Asegúrate de ingresar un UUID correcto.");
            return;
        }

        System.out.print("Rol deseado (ej: DUELIST, SUPPORT): ");
        String rolDeseado = sc.nextLine().trim().toUpperCase();

        Optional<Postulacion> postulado = scrimAppService.postularse(
            scrimId,
            UUID.fromString(user.getId()),
            rolDeseado
        );

        if (postulado.isPresent()) {
            System.out.println("✅ Postulación exitosa. ID de Postulación: " + postulado.get().getId());
            System.out.println("El organizador revisará tu solicitud.");
        } else {
            System.out.println("❌ Fallo en la postulación. Verifica que el Scrim exista y que tengas un perfil de juego configurado.");
        }
    }
}