package Model.util;

public enum Constants {
    PALAVRA_RESERVADA("Palavra Reservada"), IDENTIFICADOR("Identificador"),
    NUMERO("Numero"), COMENTARIOL("Comentario de Linha"), COMENTARIOB("Comentario de Bloco"),
    DELIMITADOR("Delimitador"), OPERADOR_ARITMETICO("Operador aritmetico"),
    OPERADOR_RELACIONAL("Operador relacional"), OPERADOR_LOGICO("Operador logico"),
    CADEIA_CARACTERES("Cadeia de Caracteres"), ERRORB("Comentario de bloco mal formado"),
    ESPACO("Espaço"), ERRORI("Identificador mal formado"), NULO("nulo"),
    ERRORN("Numero mal formado"), ERRORC("Cadeia de caracteres mal formada"),
    ERRORS("Simbolo invalido"), ERRORL("Operador Logico mal formado"),
    ERRO_SINT("Erro Sintatico");

    private final String valor;

    Constants(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return this.valor;
    }

}
