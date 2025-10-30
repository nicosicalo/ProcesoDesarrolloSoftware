package Factory;

import java.util.List;

public class CsgoFactory implements JuegoFactory{
    public List<String> getRolesDelJuego(){
        return List.of("FRAGGER","SNIPER","IGL","RIFLER","SUPPORT");
    }
    public List<String> getRangosDelJuego(){
        return List.of("PLATA ELITE ","ORO NOVA ","MASTER GUARDIAN","LEGENDARY EAGLE","SUPREME MASTER CLASS","GLOBAL ELITE");
    }
}
