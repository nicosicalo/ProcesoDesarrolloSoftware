package Factory;

import java.util.List;

public class LolFactory implements JuegoFactory{
    public List<String> getRolesDelJuego(){
        return List.of("TOP","ADC","JUNGLA","MID","SUPPORT");
    }
    public List<String> getRangosDelJuego(){
        return List.of("HIERRO","BRONCE","PLATA","ORO","DIAMANTE");
    }
}
