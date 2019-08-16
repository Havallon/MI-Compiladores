package Model;

import Model.util.Constants;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class AnaliseLexica {

    private final Automatos automatos;
    private ArrayList<Token> tokens;
    private ArrayList<Token> tokensInvalidos;
    private Constants tokenAnterior;
    private int linhaComentarioBloco; //Linha que inicia o comentario em bloco
    private boolean bloco; //Flag para saber se o comentario e de bloco
    private boolean numero; //Flag para verificar numero mal formado
    private boolean numeroNegativo; //flag para verificar numero negativo
    private boolean erro; //flag de erros na analise lexica
    private boolean cadeia; //flag para detectar erro na cadeia de caracteres
    private boolean erroC; //erro na cadeia de carecteres
    
    public AnaliseLexica() {
        automatos = new Automatos();
        bloco = false;
        numero = false;
        numeroNegativo = false;
        cadeia = false;
        erro = false;
        erroC = false;
    }

    //Método para fazer a analise lexica no arquivo
    public void analisar(FileReader arq) throws IOException {
        erro = false;
        BufferedReader lerArq = new BufferedReader(arq);

        //listas de tokens identificados
        tokens = new ArrayList<>();
        tokensInvalidos = new ArrayList<>();
        int linhas = 1; //contador de linhas

        //Variavel auxiliar para identificar um token
        String token = "";

        //lendo o arquivo todo, linha por linha.
        String linha = lerArq.readLine();
        while (linha != null) {
            // listando todos os caracteres da linhas
            char[] aux = linha.toCharArray();

            //Varrendo todos caractes, tentando identificar o token
            for (int i = 0; i < aux.length; i++) {
                if ((int)aux[i] > 2000){
                    i++;
                }
                token = token + aux[i];
                switch (automatos.verificarToken(token, linhas).getTipo()) {
                    /*
                    Caso a entrada seja do tipo identificador
                    o automato vai falhar, e assim caracterizando um identificador
                    quando o proximo caracter for um espaço, delimitador,
                    operadores aritmeticos, relacionais ou logicos.
                     */
                    case IDENTIFICADOR:
                        if (i + 1 < aux.length) {
                            if (((Character) aux[i + 1]).toString().matches(automatos.getEspaco() + "|" + automatos.getDelimitador() + "|" + automatos.getOperadorA()
                            +"|"+automatos.getOperadorL()+"|"+automatos.getOperadorR())) {
                                token = adicionarToken(token, linhas);
                            } else if (!((Character) aux[i + 1]).toString().matches(automatos.getLetra() + "|" + automatos.getDigito() + "|_")) {
                                Constants auxiliar = Constants.ERRORI;
                                token = token + aux[i + 1];
                                i = i + 1;
                                Token t = new Token(auxiliar, token, linhas);
                                token = "";
                                tokensInvalidos.add(t);
                                erro = true;
                            }
                        } else {
                            token = adicionarToken(token, linhas);
                        }
                        break;

                    //caso o caracter seja um delimitador
                    case DELIMITADOR:
                        token = adicionarToken(token, linhas);
                        break;
                    //Se for um espaço, ignora ele.
                    case ESPACO:
                        token = "";
                        break;
                        
                    //Automato de numero
                    case NUMERO:
                        numero = true; //habilita a flag de numero
                        //verificando quando o automato de numero identifica o numero
                        if (i + 1 < aux.length) {
                            if (((Character) aux[i + 1]).toString().matches(automatos.getEspaco()+"|"+automatos.getDelimitador()+"|"+automatos.getOperadorA()
                            +"|"+automatos.getOperadorL()+"|"+automatos.getOperadorR())) {
                                if (aux[i+1] != '.'){
                                    if (numeroNegativo){
                                        Constants auxiliar = Constants.NUMERO;
                                        token = "-" + token;
                                        Token t = new Token(auxiliar, token, linhas);
                                        numero = false;
                                        token = "";
                                        tokens.add(t);
                                        tokenAnterior = auxiliar;
                                        numeroNegativo = false;
                                    }else{
                                    token = adicionarToken(token, linhas);
                                    numero = false;
                                    }
                                }
                            }
                        } else {
                            //verificando se o numero é negativo
                            if (numeroNegativo) {
                                Constants auxiliar = Constants.NUMERO;
                                token = "-" + token;
                                Token t = new Token(auxiliar, token, linhas);
                                numero = false;
                                token = "";
                                tokens.add(t);
                                tokenAnterior = auxiliar;
                                numeroNegativo = false;
                            } else {
                                token = adicionarToken(token, linhas);
                            }
                            numero = false; //numero formado
                        }
                        break;
                    case OPERADOR_ARITMETICO:
                        //Caso seja um +, veriicar se vai ter outro + em seguida
                        if (token.equals("+")) {
                            if (i + 1 < aux.length) {
                                if (aux[i + 1] == '+') {
                                    token = token + aux[i + 1];
                                    token = adicionarToken(token, linhas);
                                    i = i + 1;
                                } else {
                                    token = adicionarToken(token, linhas);
                                }
                            } else {
                                token = adicionarToken(token, linhas);
                            }
                        } //Caso seja um /, verificar se vai ter outro / em seguida
                        //Ou então se vai ter um * em seguida, classificando
                        //como comentario.
                        else if (token.equals("/")) {
                            if (i + 1 < aux.length) {
                                if (aux[i + 1] == '/') {

                                } else if (aux[i + 1] == '*') {
                                    linhaComentarioBloco = linhas;
                                    bloco = true;
                                    break;
                                } else {
                                    token = adicionarToken(token, linhas);
                                }
                            } else {
                                token = adicionarToken(token, linhas);
                            }
                        } //Caso seja um -, verificar se vai ter outro - em seguida
                        else if (token.equals("-")) {
                            if (i + 1 < aux.length) {
                                if (aux[i + 1] == '-') {
                                    token = token + aux[i + 1];
                                    token = adicionarToken(token, linhas);
                                    i = i + 1;
                                }
                                /*
                                Verificando se o lexema '-' é uma operação aritmetica
                                olhando os tokens anteriores.
                                */
                                else if (tokenAnterior == Constants.IDENTIFICADOR
                                        || tokenAnterior == Constants.NUMERO || tokenAnterior == Constants.DELIMITADOR) {
                                    token = adicionarToken(token, linhas);
                                }
                                //Caso contrario, é um possivel numero negativo
                                else {
                                    if (!((Character)aux[i+1]).toString().matches(automatos.getIdentificador())){
                                        numeroNegativo = true;
                                        token = "";
                                    }else{
                                        token = adicionarToken(token, linhas);
                                    }
                                }
                            } else {
                                token = adicionarToken(token, linhas);
                            }
                        } else {
                            token = adicionarToken(token, linhas);
                        }

                        break;
                        
                    //Automato Operador LOGICO
                    case OPERADOR_LOGICO:
                        /*
                        Caso o automato reconheça o caracter '!', verifica se o
                        o proximo caracter é um '=', para classifica-lo como
                        operador relacional.
                        */
                        if (token.equals("!")) {
                            if (i + 1 < aux.length) {
                                if (aux[i + 1] == '=') {
                                    token = token + aux[i + 1];
                                    i++;
                                    token = adicionarToken(token, linhas);
                                } else {
                                    token = adicionarToken(token, linhas);
                                }
                            } else {
                                token = adicionarToken(token, linhas);
                            }
                        } else {
                            if (token.equals("&")){
                                if (i+1 < aux.length){
                                    if (aux[i+1] == '&'){
                                        token = token + aux[i+1];
                                        i++;
                                        token = adicionarToken(token, linhas);
                                    }
                                    else{
                                        Constants auxiliar = Constants.ERRORL;
                                        Token t = new Token(auxiliar, token, linhas);
                                        token = "";
                                        tokensInvalidos.add(t);
                                        tokenAnterior = auxiliar;
                                        erro = true;
                                    }
                                }else{
                                    Constants auxiliar = Constants.ERRORL;
                                    Token t = new Token(auxiliar, token, linhas);
                                    token = "";
                                    tokensInvalidos.add(t);
                                    tokenAnterior = auxiliar;
                                    erro = true;
                                }
                            } else if (token.equals("|")){
                                if (i+1 < aux.length){
                                    if (aux[i+1] == '|'){
                                        token = token + aux[i+1];
                                        i++;
                                        token = adicionarToken(token, linhas);
                                    }
                                    else{
                                        Constants auxiliar = Constants.ERRORL;
                                        Token t = new Token(auxiliar, token, linhas);
                                        token = "";
                                        tokensInvalidos.add(t);
                                        tokenAnterior = auxiliar;
                                        erro = true;
                                    }
                                }else{
                                    Constants auxiliar = Constants.ERRORL;
                                    Token t = new Token(auxiliar, token, linhas);
                                    token = "";
                                    tokensInvalidos.add(t);
                                    tokenAnterior = auxiliar;
                                    erro = true;
                                }
                            }
                            else{
                                token = adicionarToken(token, linhas);
                            }
                        }
                        break;
                    case OPERADOR_RELACIONAL:
                        /*
                        Verificado se o lexema é '=', se for verificar se o
                        proximo tbm é '=' para formar o operador '==', caso
                        contrario o operador é só '='
                        */
                        if (token.equals("=")) {
                            if (i + 1 < aux.length) {
                                if (aux[i + 1] == '=') {
                                    token = token + aux[i + 1];
                                    i++;
                                    token = adicionarToken(token, linhas);
                                } else {
                                    token = adicionarToken(token, linhas);
                                }
                            } else {
                                token = adicionarToken(token, linhas);
                            }
                         /*
                        Verificado se o lexema é '<', se for verificar se o
                        proximo tbm é '=' para formar o operador '<=', caso
                        contrario o operador é só '<'
                        */
                        } else if (token.equals("<")) {
                            if (i + 1 < aux.length) {
                                if (aux[i + 1] == '=') {
                                    token = token + aux[i + 1];
                                    i++;
                                    token = adicionarToken(token, linhas);
                                } else {
                                    token = adicionarToken(token, linhas);
                                }
                            } else {
                                token = adicionarToken(token, linhas);
                            }
                        }
                          /*
                        Verificado se o lexema é '>', se for verificar se o
                        proximo tbm é '=' para formar o operador '>=', caso
                        contrario o operador é só '>'
                        */
                        else if (token.equals(">")) {
                            if (i + 1 < aux.length) {
                                if (aux[i + 1] == '=') {
                                    token = token + aux[i + 1];
                                    i++;
                                    token = adicionarToken(token, linhas);
                                } else {
                                    token = adicionarToken(token, linhas);
                                }
                            } else {
                                token = adicionarToken(token, linhas);
                            }
                        } else {
                            token = adicionarToken(token, linhas);
                        }
                        break;
                    /*
                        Após o primeiro '"' detectado ativa a flag da cadeia.
                        quando o automato encontrar o segundo " veriica se
                        o caracter anterior é um \, indicando a aspas na string
                        Caso não for, verifica se o proximo carecater então é um
                        delimitador, finzalizando assim o automato da cadeia.
                        Caso o proximo nao seja um delimitador, ou um fim de linha.
                        Aponta erro na cadeia de caracter.
                    */
                    case CADEIA_CARACTERES:
                        if (cadeia){
                            if(aux[i] == '"'){
                                if (aux[i-1] != '\\'){
                                    
                                    if (i + 1 < aux.length) {
                                        if (((Character) aux[i + 1]).toString().matches(automatos.getEspaco() + "|" + automatos.getDelimitador() + "|" + automatos.getOperadorA())) {
                                            token = adicionarToken(token, linhas);
                                            cadeia = false;
                                            break;
                                        } else {
                                            Constants auxiliar = Constants.ERRORC;
                                            for (int j =i+1; j < aux.length; j++){
                                                token = token + aux[j];
                                                i = j;
                                            }
                                            Token t = new Token(auxiliar, token, linhas);
                                            token = "";
                                            tokensInvalidos.add(t);
                                            erro = true;
                                            erroC = true;
                                            cadeia = false;
                                            break;
                                        }
                                    }
                                    
                                    else{
                                        token = adicionarToken(token, linhas);
                                        cadeia = false;
                                        break;
                                    }
                                }
                            }
                        }
                        cadeia = true;
                        break;
                    default:
                        if (!token.matches(automatos.getComentarioL())){
                            if (!token.matches("\\/\\*(.)*")){
                                if(!numero){
                                    token = token.trim();
                                    Constants auxiliar = Constants.ERRORS;
                                    Token t = new Token(auxiliar, token, linhas);
                                    token = "";
                                    tokensInvalidos.add(t);
                                    tokenAnterior = auxiliar;
                                    erro = true;
                                }
                            }
                        }
                        if (token.matches(automatos.getComentarioB())){
                            token = adicionarToken(token, linhaComentarioBloco);
                            bloco = false;
                        }
                        break;
                }
            }

            //Verificando se o comentario é de linha/bloco
            if (token.matches(automatos.getComentarioL())) {
                token = adicionarToken(token, linhas);
            } else if (token.matches(automatos.getComentarioB())) {
                token = adicionarToken(token, linhaComentarioBloco);
                bloco = false;
            } else {
                if (!token.isEmpty()) {
                    token = token + ' ';
                }
            }

            //Verificando se há um numero mal formado
            if (numero) {
                token = token.trim();
                if (numeroNegativo){
                    numeroNegativo = false;
                    token = "-"+token;
                }
                Constants auxiliar = Constants.ERRORN;
                Token t = new Token(auxiliar, token, linhas);
                numero = false;
                token = "";
                tokensInvalidos.add(t);
                tokenAnterior = auxiliar;
                erro = true;
            }
            //verificando se há cadeias de carecters mal formada
            if (cadeia && !erroC){
                token = token.trim();
                Constants auxiliar = Constants.ERRORC;
                Token t = new Token(auxiliar, token, linhas);
                token = "";
                tokensInvalidos.add(t);
                tokenAnterior = auxiliar;
                erro = true;
                cadeia = false;
            }
            if (numeroNegativo){
                token = '-'+token;
                token = adicionarToken(token, linhas);
                numeroNegativo = false;
            }
            linha = lerArq.readLine();
            erroC = false;
            tokenAnterior = Constants.NULO;
            linhas++;
        }

        //Verificando se um comentario de bloco foi aberto
        if (bloco) {
            Constants aux = Constants.ERRORB;
            Token t = new Token(aux, token, linhaComentarioBloco);
            tokensInvalidos.add(t);
            erro = true;
        }

        lerArq.close();

        //Verificando se os tokens são palavras reservadas
        for (Token t : tokens) {
            automatos.verificarPalavraReservada(t);
        }

    }
    
    public ArrayList<Token> getTokens(){
        return tokens;
    }
    
    public ArrayList<Token> getTokensInvalidos(){
        return tokensInvalidos;
    }
    
    //Método para adicionar o token a lista de tokens
    private String adicionarToken(String token, int linha) {
        token = token.trim();
        Token t = automatos.verificarToken(token, linha);
        tokenAnterior = t.getTipo();
        if (t.getTipo() == Constants.COMENTARIOB || t.getTipo() == Constants.COMENTARIOL)
            return "";
        tokens.add(t);
        return "";
    }
    
    public boolean getErro(){
        return this.erro;
    }
    
}
