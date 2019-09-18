package Model.Semantico;

import java.util.ArrayList;

public class Comando {
    private String tipo;
    private String id;
    private boolean vetor;
    private boolean matriz;
    private ArrayList<Comando> param;
    private ArrayList<String> op;
    private ArrayList<Condicao> conds;
    private String indice;
    private String tipoIndice;
    private String indiceM;
    private String tipoIndiceM;
    
    private int Linha;
    
    public Comando() {
        param = new ArrayList<>();
        op = new ArrayList<>();
        conds = new ArrayList<>();
    }

    public String getIndice() {
        return indice;
    }

    public void setIndice(String indice) {
        this.indice = indice;
    }

    public String getTipoIndice() {
        return tipoIndice;
    }

    public void setTipoIndice(String tipoIndice) {
        this.tipoIndice = tipoIndice;
    }

    public String getIndiceM() {
        return indiceM;
    }

    public void setIndiceM(String indiceM) {
        this.indiceM = indiceM;
    }

    public String getTipoIndiceM() {
        return tipoIndiceM;
    }

    public void setTipoIndiceM(String tipoIndiceM) {
        this.tipoIndiceM = tipoIndiceM;
    }
    
    
    
    public ArrayList<Condicao> getConds() {
        return conds;
    }
    
    public ArrayList<String> getOp() {
        return op;
    }

    public ArrayList<Comando> getParam() {
        return param;
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
