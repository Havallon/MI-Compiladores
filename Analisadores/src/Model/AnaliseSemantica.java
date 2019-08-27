
package Model;

import Model.Semantico.Constante;
import Model.Semantico.ErroSemantico;
import Model.Semantico.Metodo;
import Model.Semantico.Variavel;
import java.util.ArrayList;
import java.util.Collections;


public class AnaliseSemantica {
    
    private final Automatos automatos;
    private final ArrayList<ErroSemantico> erros;
    
    public AnaliseSemantica(){
        this.erros = new ArrayList<>();
        this.automatos = new Automatos();
    }
    
    public void reset(){
        erros.clear();
    }
    
    public ArrayList<ErroSemantico> getErros(){
        return this.erros;
    }
    
    public void verificarMetodo(ArrayList<Metodo> lista, ArrayList<Constante> ctes){
        verificarMetodoPrincipal(lista);
        verificarMetodoParam(lista, ctes);
        verificarVar(lista, ctes);
    }
    
    private void verificarVar(ArrayList<Metodo> lista, ArrayList<Constante> ctes){
        ArrayList<String> citados = new ArrayList<>();
        for (Metodo m : lista){
            for (Variavel v : m.getVariaveis()){
                if (v.getTipo().equals("vazio")){
                    erros.add(new ErroSemantico(v.getLinha(), "Linha: " + v.getLinha() + " - A variavel '" + v.getNome() + "' não pode ser do tipo vazio"));
                }
                
                for (Constante c : ctes){
                    if (v.getNome().equals(c.getNome())){
                        erros.add(new ErroSemantico(v.getLinha(), "Linha: " + v.getLinha() + " - Nome da variavel '" + v.getNome() + "' já foi declarada como constante"));
                    }
                }
                
                if (Collections.frequency(m.getParametros(), v) > 1){
                    if (citados.contains(v.getNome())){
                        erros.add(new ErroSemantico(v.getLinha(),"Linha: " + v.getLinha() + " - O nome do parametro '" + v.getNome() + "' ja esta em uso"));
                    } else{
                        citados.add(v.getNome());
                    }
                }
                
                for (Variavel param : m.getParametros()){
                    if (v.getNome().equals(param.getNome())){
                        erros.add(new ErroSemantico(v.getLinha(), "Linha: " + v.getLinha() + " - A variavel '" + v.getNome() + "' já foi declarada nos parametros do metodo"));
                    }
                }
            }
        }
    }
    
    private void verificarMetodoParam(ArrayList<Metodo> lista, ArrayList<Constante> ctes){
        ArrayList<String> citados = new ArrayList<>();
        
        for (Metodo m : lista){
            for (Variavel v : m.getParametros()){
                if (v.getTipo().equals("vazio")){
                    erros.add(new ErroSemantico(v.getLinha(), "Linha: " + v.getLinha() + " - O parametro '" + v.getNome() + "' não pode ser do tipo vazio"));
                }
                
                for (Constante c : ctes){
                    if (v.getNome().equals(c.getNome())){
                        erros.add(new ErroSemantico(v.getLinha(), "Linha: " + v.getLinha() + " - Nome do parametro '" + v.getNome() + "' já foi declarado como constante"));
                    }
                }
                if (Collections.frequency(m.getParametros(), v) > 1){
                    if (citados.contains(v.getNome())){
                        erros.add(new ErroSemantico(v.getLinha(),"Linha: " + v.getLinha() + " - O nome do parametro '" + v.getNome() + "' ja esta em uso"));
                    } else{
                        citados.add(v.getNome());
                    }
                }
            }
        }
    }
    
    private void verificarMetodoPrincipal(ArrayList<Metodo> lista){
        int quantidade = 0;
        for (Metodo m : lista){
            if (m.getNome().equals("principal")){
                quantidade++;
                if (!m.getRetorno().equals("vazio")){
                    erros.add(new ErroSemantico(m.getLinha(), "Linha: " + m.getLinha() + " - O retorno do método principal tem que ser vazio"));
                }
                if (!m.getParametros().isEmpty()){
                    erros.add(new ErroSemantico(m.getLinha(), "Linha: " + m.getLinha() + " - O método principal não pode ter parametros"));
                }
            }
            if (quantidade > 1){
                erros.add(new ErroSemantico(m.getLinha(),"Linha: " + m.getLinha() + " - O método principal já foi declarado"));
            }
        }
        if (quantidade == 0){
            erros.add(new ErroSemantico(0,"Está faltando o método principal"));
        }
        
    }
    
    public void verificarConstantes(ArrayList<Constante> lista){
        verificarConstantesIguais(lista);
        verificarConstanteVazias(lista);
        verificarTipoConstante(lista);
    }
    
    private void verificarConstantesIguais(ArrayList<Constante> lista){
        ArrayList<Constante> aux = (ArrayList<Constante>) lista.clone();
        while(!aux.isEmpty()){
            Constante c = aux.remove(aux.size()-1);
            int ocorrencias = Collections.frequency(aux, c);
            if (ocorrencias > 0){
                erros.add(new ErroSemantico(c.getLinha(),"Linha: " + c.getLinha() + " - O nome da constante '" + c.getNome() + "' ja esta em uso"));
            }
        }
    }
    
    private void verificarConstanteVazias(ArrayList<Constante> lista){
        for (Constante c : lista){
            if (c.getTipo().equals("vazio")){
                erros.add(new ErroSemantico(c.getLinha(),"Linha: " + c.getLinha() + " - Nao e permitido declarar constantes com do tipo 'vazio'"));
            }
        }
    }
    
    private void verificarTipoConstante(ArrayList<Constante> lista){
        ArrayList<Constante> tipos = new ArrayList<>();
        for (Constante c : lista){
            switch (c.getTipo()){
                case "inteiro":
                    try{
                        int i = Integer.parseInt(c.getValor());
                    }catch(Exception ex){
                        erros.add(new ErroSemantico(c.getLinha(),"Linha: " + c.getLinha() + " - Constante '" + c.getNome() + "' esta recebendo um valor incompativel com sua tipagem"));
                    }
                    break;
                case "real":
                    try{
                        float i = Float.parseFloat(c.getValor());
                    }catch(Exception ex){
                        erros.add(new ErroSemantico(c.getLinha(),"Linha: " + c.getLinha() + " - Constante '" + c.getNome() + "' esta recebendo um valor incompativel com sua tipagem"));
                    }
                    break;
                case "boleano":
                    if (!automatos.isTipoBoleano(c.getValor())){
                        erros.add(new ErroSemantico(c.getLinha(),"Linha: " + c.getLinha() + " - Constante '" + c.getNome() + "' esta recebendo um valor incompativel com sua tipagem"));
                    }
                    break;
                case "texto":
                    if (!c.getValor().matches("\"(.)*\"")){
                        erros.add(new ErroSemantico(c.getLinha(),"Linha: " + c.getLinha() + " - Constante '" + c.getNome() + "' esta recebendo um valor incompativel com sua tipagem"));
                    }
                    break;
            }
        }
    }
}
