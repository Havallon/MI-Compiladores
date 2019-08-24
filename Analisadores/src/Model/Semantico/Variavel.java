package Model.Semantico;

public class Variavel {
    private final String nome;
    private final String tipo;
    private final int linha;
    
    public Variavel(String nome, String tipo, int linha) {
        this.nome = nome;
        this.tipo = tipo;
        this.linha = linha;
    }

    public String getNome() {
        return nome;
    }

    public String getTipo() {
        return tipo;
    }
    
    public int getLinha(){
        return linha;
    }
    
    @Override
    public boolean equals(Object obj){
        if (obj instanceof Variavel){
            Variavel aux = (Variavel) obj;
            return (aux.getNome().equals(this.nome));
        } else return false;
    }
    
}
