package Controller;

import Models.Usuario;
import ScrimsAuth.SessionManager;
import Service.FiltrosBusqueda;
import Service.ScrimAppService;
import Service.ScrimCreationDTO;

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
        System.out.print("Juego ID a buscar (Deje en blanco para buscar todos): ");
        String juegoInput = sc.nextLine().trim();
        String juegoBuscado = sc.nextLine().trim();
        FiltrosBusqueda filtros = new FiltrosBusqueda(
                juegoBuscado, 
                null,
                "NA", 
                1000,
                3000,
                100,
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
}