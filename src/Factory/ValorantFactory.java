package Factory;

import java.util.List;

public class ValorantFactory implements JuegoFactory{
    public List<String> getRolesDelJuego(){
        return List.of("DUELISTA","INICIADOR","CENTINELA","CONTROLADOR","SUPPORT");
    }
    public List<String> getRangosDelJuego(){
        return List.of("HIERRO","BRONCE","PLATA","ORO","DIAMANTE","PLATINO","INMORTAL","RADIANT");
    }
}
