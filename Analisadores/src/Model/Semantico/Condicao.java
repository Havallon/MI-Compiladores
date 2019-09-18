
package Model.Semantico;


public class Condicao {
    private String termoA;
    private String tipoA;
    private String termoB;
    private String tipoB;
    private boolean aux;
    private String op;
    
    public Condicao() {
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    
        
    public boolean isAux() {
        return aux;
    }

    public void setAux(boolean aux) {
        this.aux = aux;
    }

    
    
    public String getTipoA() {
        return tipoA;
    }

    public void setTipoA(String tipoA) {
        this.tipoA = tipoA;
    }

    public String getTipoB() {
        return tipoB;
    }

    public void setTipoB(String tipoB) {
        this.tipoB = tipoB;
    }
    
    
    
    public String getTermoA() {
        return termoA;
    }

    public void setTermoA(String termoA) {
        this.termoA = termoA;
    }

    public String getTermoB() {
        return termoB;
    }

    public void setTermoB(String termoB) {
        this.termoB = termoB;
    }
    
    
}
