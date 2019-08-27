package Model.Semantico;


public class ErroSemantico implements Comparable {
    
    private final int linha;
    private final String msg;

    public ErroSemantico(int linha, String msg) {
        this.linha = linha;
        this.msg = msg;
    }
    
    public String getMsg(){
        return this.msg;
    }
    
    public int getLinha(){
        return this.linha;
    }
    
    @Override
    public int compareTo(Object t) {
        if (t instanceof ErroSemantico){
            ErroSemantico aux = (ErroSemantico) t;
            if (aux.getLinha() > this.linha){
                return -1;
            } else if (aux.getLinha() < this.linha){
                return 1;
            } else{
                return 0;
            }
        }else{
            return -1;
        }
    }
    
}
