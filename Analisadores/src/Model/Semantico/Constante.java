
package Model.Semantico;

public class Constante {
    
    private final String tipo;
    private final String nome;
    private final String valor;
    private final int linha;
    
    public Constante(String tipo, String nome, String valor, int linha){
        this.tipo = tipo;
        this.nome = nome;
        this.valor = valor;
        this.linha = linha;
    }
    
    public int getLinha(){
        return linha;
    }
    
    public String getTipo() {
        return tipo;
    }

    public String getNome() {
        return nome;
    }

    public String getValor() {
        return valor;
    }
    
    @Override
    public boolean equals(Object obj){
        if (obj instanceof Constante){
            Constante aux = (Constante) obj;
            return (aux.getNome().equals(this.nome));
        } else return false;
    }
    
    @Override
    public String toString(){
        return "Linha: " + linha + " -> " + tipo + " " + nome + " = " + valor;
    }
}
