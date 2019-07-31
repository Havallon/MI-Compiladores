
package Model;

import java.util.ArrayList;

public class Primeiros {
    
    public Primeiros(){
    }
    
    public boolean constantes(String lexema){
        if (lexema.equals("constantes"))
            return true;
        else
            return false;
    }
    
    public boolean metodo(String lexema){
        if (lexema.equals("metodo"))
            return true;
        else
            return false;
    }
    
    public boolean comandos(String lexema){
        ArrayList<String> comandos = new ArrayList<>();
        comandos.add("leia");
        
        return comandos.contains(lexema);
    }
}
