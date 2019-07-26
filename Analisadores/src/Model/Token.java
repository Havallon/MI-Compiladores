package Model;

import Model.util.Constants;

public class Token {

    private Constants tipo;
    private String lexema;
    private int linha;
    
    public Token(Constants tipo, String lexema, int linha) {
        this.lexema = lexema;
        this.tipo = tipo;
        this.linha = linha;
    }
    
    public void setTipo(Constants tipo) {
        this.tipo = tipo;
    }

    public Constants getTipo() {
        return this.tipo;
    }

    public String getLexema() {
        return this.lexema;
    }

    public int getLinha() {
        return this.linha;
    }

    @Override
    public String toString() {
        if (tipo == Constants.ERRO_SINT)
            return ("Linha: " + linha + " - " + lexema);
        else
            return (this.linha + " " + this.lexema + " " + this.tipo.getValor());
    }
}
