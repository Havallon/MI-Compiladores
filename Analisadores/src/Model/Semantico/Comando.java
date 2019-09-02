package Model.Semantico;

public class Comando {
    private String tipo;
    private String id;
    private boolean vetor;
    private boolean matriz;
    
    private int Linha;
    
    public Comando() {
    }

    public int getLinha() {
        return Linha;
    }

    public boolean isVetor() {
        return vetor;
    }

    public void setVetor(boolean vetor) {
        this.vetor = vetor;
    }

    public boolean isMatriz() {
        return matriz;
    }

    public void setMatriz(boolean matriz) {
        this.matriz = matriz;
    }
    
    public void setLinha(int Linha) {
        this.Linha = Linha;
    }
    
    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    
}
