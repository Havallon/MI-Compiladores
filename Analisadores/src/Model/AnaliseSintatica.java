
package Model;

import Model.util.Constants;
import java.util.ArrayList;


public class AnaliseSintatica {
    
    private ArrayList<Token> tokens;
    private final Automatos automatos;
    private ArrayList<Token> erros;
    private final Primeiros primeiros;
    private Token atual;
    
    
    public AnaliseSintatica(){
        primeiros = new Primeiros();
        automatos = new Automatos();
    }
    
    public ArrayList<Token> start(ArrayList<Token> tokens){
        this.tokens = tokens;
        tokens.add(new Token(Constants.FIM_PROGRAMA, "$", 0));
        this.erros = new ArrayList<Token>();
        //Verificando se o escopo do programa foi iniciado
        programa();
        if (atual.getTipo() != Constants.FIM_PROGRAMA){
            erros.add(new Token(Constants.ERRO_SINT,"Simbolos fora do escopo do programa" , atual.getLinha()));
        }
        return erros;
    }
    
    private void programa(){
        atual = proximoToken();
        //Verificando se o token atual é o inicio do programa
        if (atual.getLexema().equals("programa")){ 
            atual = proximoToken();
            //Verificando se há o { logo em seguida 
            if (atual.getLexema().equals("{")){
                atual = proximoToken();
                //verificando se há declaração de constantes
                if (primeiros.constantes(atual.getLexema())){
                    blocoConstantes();
                }
                escopoPrograma();
                if (atual.getLexema().equals("}")){//Verificando se o programa foi finalizado
                    atual = proximoToken();
                }
                else{
                   erro("Faltando o delimitador '}' para finializar o bloco do programa");
                }
            }else{
                erro("Faltando o delimitador '{' para inicar o bloco do programa");
            }
        } else {
            erro("programa não inicializado");
        }
    }
    
    private void escopoPrograma(){
        if (primeiros.metodo(atual.getLexema())){
            metodo();
            escopoPrograma();
        } else {
            if (!atual.getLexema().equals("}")){
                if (atual.getLexema().equals("constantes"))
                    erro("Constantes só podem ser declaradas antes dos metodos");
                else
                    erro("Simbolo: " + atual.getLexema() + " fora do escopo de metodo");
            }
        }
    }
    
    private void metodo(){
        atual = proximoToken();
        if (atual.getTipo() == Constants.IDENTIFICADOR || atual.getLexema().equals("principal")){
            atual = proximoToken();
            if (atual.getLexema().equals("(")){
                atual = proximoToken();
                listaParametros(true);
                if (atual.getLexema().equals(")")){
                    atual = proximoToken();
                    if (atual.getLexema().equals(":")){
                        atual = proximoToken();
                        if (automatos.isTipo(atual.getLexema())){
                            atual = proximoToken();
                            if (atual.getLexema().equals("{")){
                                atual = proximoToken();
                                declaracaoVariaveis();
                                escopoMetodo();
                                if (atual.getLexema().equals("}")){
                                    atual = proximoToken();
                                }else{
                                    erro("Esta faltado } para finalizar o bloco do metodo");
                                }
                            }else {
                                erro("Esta faltado { para iniciar o bloco do metodo");
                            }
                        }else{
                            erro("Esta faltando declarar o tipo do retorno");
                        }
                    } else {
                        erro("Esta faltando : para declarar o tipo de retorno do metodo");
                    }
                }else {
                   erro("Esta faltando o ) para finalizar adeclaracao dos parametros do metodo"); 
                }
            } else {
                erro("Esta faltando o ( para declarar os parametros do metodo");
            }
        } else {
            erro("Faltando o nome do metodo");
        }
    }
    
    private void escopoMetodo(){
        comandos();
    }
    
    private void comandos(){
        if (primeiros.comandos(atual.getLexema())){
            if (atual.getLexema().equals("leia")){
                atual = proximoToken();
                leia();
            }
            comandos();
        }
    }
    
    private void leia(){
        if (atual.getLexema().equals("(")){
            atual = proximoToken();
            conteudoLeia();
            if (atual.getLexema().equals(")")){
                atual = proximoToken();
                if (atual.getLexema().equals(";")){
                    atual = proximoToken();
                }else{
                    erro("Esta faltando o ';' para finalizar o comando de leia");
                }
            } else {
                erro("Esta faltando o ')' para finalizar o comando de leia");
            }
        }else{
            erro("Esta faltando o '(' apos o comando de leia");
        }
    }
    
    private void conteudoLeia(){
        if(atual.getTipo() == Constants.IDENTIFICADOR){
            atual = proximoToken();
            vetor();
            lerMais();
        }else{
            erro("Esta faltando uma variavel para o parametro do metodo leia");
        }
    }
    
    private void lerMais(){
        if (atual.getLexema().equals(",")){
            atual = proximoToken();
            conteudoLeia();
        }
    }
    
    private void matriz(){
        if (atual.getLexema().equals("[")){
            atual = proximoToken();
            opI2();
            opIndice();
            if (atual.getLexema().equals("]")){
                atual = proximoToken();
            } else {
                erro("Esta faltando ']' na utilizacao da matriz");
            }
        }
    }
    
    private void vetor(){
        if (atual.getLexema().equals("[")){
            atual = proximoToken();
            opI2();
            opIndice();
            if (atual.getLexema().equals("]")){
                atual = proximoToken();
                matriz();
            }else{
                erro("Esta faltando ']' na utilizacao do vetor");
            }
        }
    }
    
    private void opIndice(){
        if (atual.getTipo() == Constants.OPERADOR_ARITMETICO){
            atual = proximoToken();
            opI2();
            opIndice();
        }
    }
    
    private void opI2(){
        if (atual.getTipo() == Constants.IDENTIFICADOR || atual.getTipo() == Constants.NUMERO){
            atual = proximoToken();
        }else{
            erro("Está faltando o indice do vetor");
        }
    }
    
    private void declaracaoVariaveis(){
        if (atual.getLexema().equals("variaveis")){
            atual = proximoToken();
            if (atual.getLexema().equals("{")){
                atual = proximoToken();
                varV();
                if (atual.getLexema().equals("}")){
                    atual = proximoToken();
                }else {
                    erro("Esta faltando o } para finalizar o bloco das variaveiss");
                }
            }else{
                erro("Esta faltando o { para iniciar o bloco das variaveis");
            }
        }
    }
    
    private void varV(){
        if (automatos.isTipo(atual.getLexema())){
            atual = proximoToken();
            complementoV();
            maisVariaveis();
        } else {
            erro("Está faltando a tipagem da variavel");
        }
    }
    
    private void complementoV(){
        if (atual.getTipo() == Constants.IDENTIFICADOR){
            atual = proximoToken();
            vetor();
            variavelMesmoTipo();
        } else {
            erro("Esta faltando a variavel a ser declarada");
        }
    }
    
    private void variavelMesmoTipo(){
        if (atual.getLexema().equals(";")){
            atual = proximoToken();
        }
        else if (atual.getLexema().equals(",")){
            atual = proximoToken();
            complementoV();
        }
        else{
            erro("Esta faltando o ';' para finalizar a declaração das variaveis");
        }
    }
    
    private void maisVariaveis(){
        if (automatos.isTipo(atual.getLexema())){
            varV();
        }
    }
    
    private void listaParametros(boolean blank){
        if (automatos.isTipo(atual.getLexema())){
            atual = proximoToken();
            if (atual.getTipo() == Constants.IDENTIFICADOR){
                atual = proximoToken();
                maisParametros();
            } else {
                erro("Esta faltando o nome da variavel do parametro");
            }
        } else {
            if (atual.getTipo() != Constants.DELIMITADOR){
                erro(atual.getLexema() + " não é um tipo de variavel");
            }
            else if (!blank){
                erro("Apos a ',' é necessario adicionar outra variavel nos parametros");
            }
        }
    }
    
    private void maisParametros(){
        if (atual.getLexema().equals(",")){
            atual = proximoToken();
            listaParametros(false);
        }
    }
    
    private void blocoConstantes(){
        if (atual.getLexema().equals("constantes")){
            atual = proximoToken();
            if (atual.getLexema().equals("{")){
               atual = proximoToken();
               estruturaConstante();
               if (atual.getLexema().equals("}")){
                   atual = proximoToken();
               }else{
                   erro("Faltando o delimitador '}' para finalizar o bloco de constantes");
               }
            }else{
                erro("Faltando o delimitador '{' para inciar o bloco de constantes");
            }
        }
    }
    
   
    private void estruturaConstante(){
        if (automatos.isTipo(atual.getLexema())){
            atual = proximoToken();
            constantes();
            if (atual.getLexema().equals(";")){
                atual = proximoToken();
                estruturaConstante();
            } else {
                erro("Faltando ';' para finalizar a constante declarada");
            }
        } else {
            if (atual.getTipo() != Constants.DELIMITADOR){
                erro("Faltando a tipagem da constante");
            }
        }
    } 
    
    //Declaração da constante
    private void constantes(){
        if (atual.getTipo() == Constants.IDENTIFICADOR){
            atual = proximoToken();
            if (atual.getLexema().equals("=")){
                atual = proximoToken();
                if (atual.getTipo() == Constants.NUMERO || atual.getTipo() == Constants.CADEIA_CARACTERES || automatos.isTipoBoleano(atual.getLexema())){
                    atual = proximoToken();
                    multiplasConstantes();
                }else {
                    erro("Faltando o valor da constante");
                }
            } else {
                erro("Faltando a atriuição '=' da constante");
            }
        } else {
            erro("Faltando o indentificador da constante");
        }
    }
    
    //Não terminal de multiplas consantes
    private void multiplasConstantes(){
        if (atual.getLexema().equals(",")){
            atual = proximoToken();
            constantes();
        }
    }
    
    //Metodo para adicionar um erro sintatico na lista de erros
    private void erro(String texto){
        erros.add(new Token(Constants.ERRO_SINT,texto, atual.getLinha()));
        atual = proximaLinha();
        System.out.println(texto);
    }
    
    private Token proximaLinha(){
        if (!tokens.isEmpty()){
            Token aux = tokens.remove(0);
            while(aux.getLinha() == atual.getLinha() && !tokens.isEmpty()){
                aux = tokens.remove(0);
            }
            return aux;
        } else{
            if (atual != null)
                return new Token(Constants.NULO,"SEM LEXEMAS",atual.getLinha());
            else
                return atual = new Token(Constants.NULO, "arquivo vazio", 0);
        }
    }
    
    //Metodo para pegar o proximo token
    private Token proximoToken(){
        if (!tokens.isEmpty())
            return tokens.remove(0);
        else{
            if (atual != null)
                return new Token(Constants.NULO,"SEM LEXEMAS",atual.getLinha());
            else
                return atual = new Token(Constants.NULO, "arquivo vazio", 0);
        }
    }
}
