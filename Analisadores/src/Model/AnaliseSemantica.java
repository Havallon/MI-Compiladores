
package Model;

import Model.Semantico.Constante;
import java.util.ArrayList;
import java.util.Collections;


public class AnaliseSemantica {
    
    private final Automatos automatos;
    
    public AnaliseSemantica(){
    
        this.automatos = new Automatos();
    }
    
    public ArrayList<Constante> verificarConstantesIguais(ArrayList<Constante> lista){
        ArrayList<Constante> iguais = new ArrayList<>();
        ArrayList<Constante> aux = (ArrayList<Constante>) lista.clone();
        while(!aux.isEmpty()){
            Constante c = aux.remove(aux.size()-1);
            int ocorrencias = Collections.frequency(aux, c);
            if (ocorrencias > 0){
                iguais.add(c);
            }
        }
        return iguais;
    }
    
    public ArrayList<Constante> verificarConstanteVazias(ArrayList<Constante> lista){
        ArrayList<Constante> vazios = new ArrayList<>();
        for (Constante c : lista){
            if (c.getTipo().equals("vazio")){
                vazios.add(c);
            }
        }
        return vazios;
    }
    
    public ArrayList<Constante>  verificarTipoConstante(ArrayList<Constante> lista){
        ArrayList<Constante> tipos = new ArrayList<>();
        for (Constante c : lista){
            switch (c.getTipo()){
                case "inteiro":
                    try{
                        int i = Integer.parseInt(c.getValor());
                    }catch(Exception ex){
                        tipos.add(c);
                    }
                    break;
                case "real":
                    try{
                        float i = Float.parseFloat(c.getValor());
                    }catch(Exception ex){
                        tipos.add(c);
                    }
                    break;
                case "boleano":
                    if (!automatos.isTipoBoleano(c.getValor())){
                        tipos.add(c);
                    }
                    break;
                case "texto":
                    if (!c.getValor().matches("\"(.)*\"")){
                        tipos.add(c);
                    }
                    break;
            }
        }
        return tipos;
    }
}
