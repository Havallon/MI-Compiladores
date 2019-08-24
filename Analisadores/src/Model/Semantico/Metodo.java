package Model.Semantico;

import java.util.ArrayList;

public class Metodo {
    private final String nome;
    private final String retorno;
    private final int linha;
    private final ArrayList<Variavel> parametros;
    
    public Metodo(String nome, String retorno, int linha, ArrayList<Variavel> parametros){
        this.nome = nome;
        this.retorno = retorno;
        this.linha = linha;
        this.parametros = (ArrayList<Variavel>) parametros.clone();
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
