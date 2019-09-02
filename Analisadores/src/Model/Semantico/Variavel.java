package Model.Semantico;

public class Variavel {
    private final String nome;
    private final String tipo;
    private final int linha;
    private final boolean vetor;
    private final boolean matriz;
    
    public Variavel(String nome, String tipo, int linha, boolean vetor, boolean matriz) {
        this.nome = nome;
        this.tipo = tipo;
        this.linha = linha;
        this.vetor = vetor;
        this.matriz = matriz;
    }

    public boolean isVetor() {
        return vetor;
    }

    public boolean isMatriz() {
        return matriz;
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
