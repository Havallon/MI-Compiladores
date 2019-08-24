
package Model;

import Model.Semantico.Constante;
import Model.Semantico.Metodo;
import Model.Semantico.Variavel;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;


public class AnaliseSemantica {
    
    private final Automatos automatos;
    
    public AnaliseSemantica(){
    
        this.automatos = new Automatos();
    }
    
    public void verificarMetodo(ArrayList<Metodo> lista, PrintWriter gravar, ArrayList<Constante> ctes){
        verificarMetodoPrincipal(lista, gravar);
        verificarMetodoParam(lista, gravar, ctes);
    }
    
    private void verificarMetodoParam(ArrayList<Metodo> lista, PrintWriter gravar, ArrayList<Constante> ctes){
        ArrayList<String> citados = new ArrayList<>();
        
        for (Metodo m : lista){
            for (Variavel v : m.getParametros()){
                if (v.getTipo().equals("vazio")){
                    gravar.println("Linha: " + v.getLinha() + " - A variavel '" + v.getNome() + "' não pode ser do tipo vazio");
                }
                
                for (Constante c : ctes){
                    if (v.getNome().equals(c.getNome())){
                        gravar.println("Linha: " + v.getLinha() + " - Nome da variavel '" + v.getNome() + "' já foi declarada como constante");
                    }
                }
                if (Collections.frequency(m.getParametros(), v) > 1){
                    if (citados.contains(v.getNome())){
                        gravar.println("Linha: " + v.getLinha() + " - O nome da variavel '" + v.getNome() + "' ja esta em uso");
                    } else{
                        citados.add(v.getNome());
                    }
                }
            }
        }
    }
    
    private void verificarMetodoPrincipal(ArrayList<Metodo> lista, PrintWriter gravar){
        int quantidade = 0;
        for (Metodo m : lista){
            if (m.getNome().equals("principal")){
                quantidade++;
            }
            if (quantidade > 1){
                gravar.println("Linha: " + m.getLinha() + " - O método principal já foi declarado");
            }
        }
        if (quantidade == 0){
            gravar.println("Está faltando o método principal");
        }
        
    }
    
    public void verificarConstantes(ArrayList<Constante> lista, PrintWriter gravar){
        verificarConstantesIguais(lista, gravar);
        verificarConstanteVazias(lista, gravar);
        verificarTipoConstante(lista, gravar);
    }
    
    private void verificarConstantesIguais(ArrayList<Constante> lista, PrintWriter gravar){
        ArrayList<Constante> aux = (ArrayList<Constante>) lista.clone();
        while(!aux.isEmpty()){
            Constante c = aux.remove(aux.size()-1);
            int ocorrencias = Collections.frequency(aux, c);
            if (ocorrencias > 0){
                gravar.println("Linha: " + c.getLinha() + " - O nome da constante '" + c.getNome() + "' ja esta em uso");
            }
        }
    }
    
    private void verificarConstanteVazias(ArrayList<Constante> lista, PrintWriter gravar){
        for (Constante c : lista){
            if (c.getTipo().equals("vazio")){
                gravar.println("Linha: " + c.getLinha() + " - Nao e permitido declarar constantes com do tipo 'vazio'");
            }
        }
    }
    
    private void verificarTipoConstante(ArrayList<Constante> lista, PrintWriter gravar){
        ArrayList<Constante> tipos = new ArrayList<>();
        for (Constante c : lista){
            switch (c.getTipo()){
                case "inteiro":
                    try{
                        int i = Integer.parseInt(c.getValor());
                    }catch(Exception ex){
                        gravar.println("Linha: " + c.getLinha() + " - Constante '" + c.getNome() + "' esta recebendo um valor incompativel com sua tipagem");
                    }
                    break;
                case "real":
                    try{
                        float i = Float.parseFloat(c.getValor());
                    }catch(Exception ex){
                        gravar.println("Linha: " + c.getLinha() + " - Constante '" + c.getNome() + "' esta recebendo um valor incompativel com sua tipagem");
                    }
                    break;
                case "boleano":
                    if (!automatos.isTipoBoleano(c.getValor())){
                        gravar.println("Linha: " + c.getLinha() + " - Constante '" + c.getNome() + "' esta recebendo um valor incompativel com sua tipagem");
                    }
                    break;
                case "texto":
                    if (!c.getValor().matches("\"(.)*\"")){
                        gravar.println("Linha: " + c.getLinha() + " - Constante '" + c.getNome() + "' esta recebendo um valor incompativel com sua tipagem");
                    }
                    break;
            }
        }
    }
}
