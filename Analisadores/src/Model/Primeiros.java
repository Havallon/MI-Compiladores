
package Model;

import Model.util.Constants;
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
    
    public boolean comandos(Token token){
        ArrayList<String> comandos = new ArrayList<>();
        comandos.add("leia");
        comandos.add("se");
        comandos.add("enquanto");
        comandos.add("escreva");
        comandos.add("resultado");
        return (comandos.contains(token.getLexema()) || token.getTipo() == Constants.IDENTIFICADOR);
    }
}
