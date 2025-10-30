package Infraestructura;

import Factory.CsgoFactory;
import Factory.LolFactory;
import Factory.ValorantFactory;
import Models.Juego;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class JuegoRepository {

    // Instancia única (Singleton)
    private static final JuegoRepository INSTANCE = new JuegoRepository();

    private final Map<String, Juego> juegosById = new HashMap<>();


    private JuegoRepository() {

        Juego valorant = new Juego("1", "valorant",new ValorantFactory());
        Juego lol = new Juego("2", "lol",  new LolFactory());
        Juego csgo = new Juego("3", "csgo",  new CsgoFactory());

        // Los guardamos en el mapa
        juegosById.put(valorant.getId(), valorant);
        juegosById.put(lol.getId(), lol);
        juegosById.put(csgo.getId(), csgo);
    }

    /**
     * Obtiene la instancia única del repositorio de juegos.
     * @return La instancia de JuegoRepository.
     */
    public static JuegoRepository getInstance() {
        return INSTANCE;
    }

    /**
     * Busca un juego por su ID de texto (ej: "valorant").
     * @param stringId El ID de texto del juego.
     * @return Un Optional conteniendo el Juego si se encuentra.
     */
    public Optional<Juego> findByStringId(String stringId) {
        if (stringId == null) return Optional.empty();
        return Optional.ofNullable(juegosById.get(stringId.toLowerCase()));
    }

    /**

     * @return Una Collection<Juego> con todos los juegos.
     */
    public Collection<Juego> findAll() {

        return juegosById.values();
    }
    public Map<String, Juego> getJuegosDisponibles() {
        return juegosById;
    }

}