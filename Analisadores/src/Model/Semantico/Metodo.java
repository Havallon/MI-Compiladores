package Model.Semantico;

import java.util.ArrayList;

public class Metodo {
    private  String nome;
    private  String retorno;
    private  int linha;

    private ArrayList<Variavel> parametros;
    private ArrayList<Variavel> variaveis;

    
    
    public Metodo() {
    }
        
    public void setNome(String nome) {
        this.nome = nome;
    }

    public ArrayList<Variavel> getVariaveis() {
        return variaveis;
    }

    public void setVariaveis(ArrayList<Variavel> variaveis) {
        this.variaveis = variaveis;
    }
    
    public void setRetorno(String retorno) {
        this.retorno = retorno;
    }

    public void setLinha(int linha) {
        this.linha = linha;
    }

    public void setParametros(ArrayList<Variavel> parametros) {
        this.parametros = parametros;
    }
    
    public ArrayList<Variavel> getParametros(){
        return parametros;
    }
    
    public int getLinha() {
        return linha;
    }

    public String getNome() {
        return nome;
    }

    public String getRetorno() {
        return retorno;
    }
    
    @Override
    public boolean equals(Object o){
        if (o instanceof Metodo){
            Metodo aux = (Metodo) o;
            return this.nome.equals(aux.getNome());
        }else{
            return false;
        }
    }
    
}
