package emparejamiento;

import modelos.Jugador; // <-- ¡NECESARIO!
import modelos.Equipo;  // <-- ¡NECESARIO!


import java.util.List;

public class SistemaEmparejamiento {
    private EstrategiaEmparejamiento estrategia;
    private final List<Jugador> listaEspera;

    public SistemaEmparejamiento(List<Jugador> listaEspera) {
        // Estrategia por defecto
        this.estrategia = new EstrategiaPorMMR();
        this.listaEspera = listaEspera;
    }

    public void setEstrategia(EstrategiaEmparejamiento nuevaEstrategia) {
        // Permite cambiar la estrategia dinámicamente
        System.out.println("🔄 SISTEMA: Cambiando estrategia de emparejamiento.");
        this.estrategia = nuevaEstrategia;
    }

    public Equipo intentarEmparejar() {
        System.out.println("🔍 SISTEMA: Buscando equipo con " + this.estrategia.getClass().getSimpleName() + "...");
        return this.estrategia.buscarEquipo(this.listaEspera);
    }
}