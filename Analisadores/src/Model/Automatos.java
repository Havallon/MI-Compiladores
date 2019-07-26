package Model;

import Model.util.Constants;

public class Automatos {

    private final String letra;
    private final String digito;
    private final String identificador;
    private final String delimitador;
    private final String espaco;
    private final String palavraReservada;
    private final String operadorA; //operador aritmetico
    private final String comentarioL; //comentario de linha
    private final String comentarioB; //Comentairo de bloco
    private final String operadorL; //operador logico
    private final String operadorR; //Operador relacional
    private final String numero;
    private final String cadeia; //Cadeia de caracters;
    private final String tipo;
    private final String tipoBoleano;
    
    //Expressoes Regulares de cada automato.
    public Automatos() {
        this.letra = "[a-zA-Z]";
        this.digito = "\\d";
        this.identificador = letra + "(" + letra + "|" + digito + "|_)*";
        this.delimitador = ";|\\,|\\(|\\)|\\[|\\]|\\{|\\}|\\.";
        char ascii9 = (char) 9;
        char ascii32 = (char) 32;
        this.espaco = ascii9 + "|" + ascii32;
        this.palavraReservada = "programa|constantes|variaveis|metodo|resultado|"
                + "principal|se|entao|senao|enquanto|leia|escreva|vazio|inteiro|"
                + "real|boleano|texto|verdadeiro|falso";
        this.operadorA = "\\+|\\-|\\*|\\/|\\+{2}|\\-{2}";
        this.comentarioL = "\\/{2}(.)*";
        this.comentarioB = "\\/\\*(.)*\\*\\/";
        this.operadorL = "\\!|\\&{1,2}|\\|{1,2}";
        this.operadorR = "\\!\\=|\\={1,2}|\\<\\=?|\\>\\=?";
        this.numero = "\\d\\d*(\\.\\d(\\d)*)?";
        this.cadeia = "\"(.)*";
        this.tipo = "inteiro|real|boleano|texto|vazio";
        this.tipoBoleano = "verdadeiro|falso";
    }

    //Método para veriicar a vericidade do token
    public Token verificarToken(String palavra, int linha) {
        if (palavra.matches(identificador)) {
            return new Token(Constants.IDENTIFICADOR, palavra, linha);
        } else if (palavra.matches(delimitador)) {
            return new Token(Constants.DELIMITADOR, palavra, linha);
        } else if (palavra.matches(espaco)) {
            return new Token(Constants.ESPACO, palavra, linha);
        } else if (palavra.matches(operadorA)) {
            return new Token(Constants.OPERADOR_ARITMETICO, palavra, linha);
        } else if (palavra.matches(comentarioL)) {
            return new Token(Constants.COMENTARIOL, palavra, linha);
        } else if (palavra.matches(comentarioB)) {
            return new Token(Constants.COMENTARIOB, palavra, linha);
        } else if (palavra.matches(operadorL)) {
            return new Token(Constants.OPERADOR_LOGICO, palavra, linha);
        } else if (palavra.matches(operadorR)) {
            return new Token(Constants.OPERADOR_RELACIONAL, palavra, linha);
        } else if (palavra.matches(numero)) {
            return new Token(Constants.NUMERO, palavra, linha);
        } else if (palavra.matches(cadeia)){
            return new Token(Constants.CADEIA_CARACTERES, palavra, linha);
        } else {
            return new Token(Constants.NULO, palavra, linha);
        }
    }
    
    public boolean isTipo(String lexema){
        return lexema.matches(tipo);
    }
    
    public boolean isTipoBoleano(String lexema){
        return lexema.matches(tipoBoleano);
    }
    
    public String getOperadorR() {
        return operadorR;
    }

    public String getNumero() {
        return numero;
    }

    public String getOperadorL() {
        return operadorL;
    }

    public String getComentarioL() {
        return comentarioL;
    }

    public String getComentarioB() {
        return comentarioB;
    }

    //Método para verificar se o token é uma palavra reservada;
    public void verificarPalavraReservada(Token token) {
        if (token.getLexema().matches(palavraReservada)) {
            token.setTipo(Constants.PALAVRA_RESERVADA);
        }
    }

    public String getOperadorA() {
        return operadorA;
    }

    public String getLetra() {
        return letra;
    }

    public String getDigito() {
        return digito;
    }

    public String getIdentificador() {
        return identificador;
    }

    public String getDelimitador() {
        return delimitador;
    }

    public String getEspaco() {
        return espaco;
    }

}
