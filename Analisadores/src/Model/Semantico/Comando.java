package Model.Semantico;

public class Comando {
    private String tipo;
    private String id;
    private int Linha;
    
    public Comando() {
    }

    public int getLinha() {
        return Linha;
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
