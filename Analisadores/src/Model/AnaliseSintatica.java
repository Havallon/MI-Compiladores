
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
        this.erros = new ArrayList<Token>();
        //Verificando se o escopo do programa foi iniciado
        programa();
        if (!tokens.isEmpty()){
            erros.add(new Token(Constants.ERRO_SINT,"Simbolos fora do escopo do programa" , tokens.get(0).getLinha()));
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
                if (primeiros.Constantes(atual.getLexema())){
                    blocoConstantes();
                }
                if (atual.getLexema().equals("}")){//Verificando se o programa foi finalizado
                    atual = proximoToken();
                }
                else{
                   erros.add(new Token(Constants.ERRO_SINT,"Faltando o delimitador '}' para finializar o bloco do programa" , atual.getLinha()));
                   atual = proximoToken();
                }
            }else{
                erros.add(new Token(Constants.ERRO_SINT,"Faltando o delimitador '{' para inicar o bloco do programa" , atual.getLinha()));
                atual = proximoToken();
            }
        } else {
            erros.add(new Token(Constants.ERRO_SINT,"programa não inicializado" , atual.getLinha()));
            atual = proximoToken();
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
                   erros.add(new Token(Constants.ERRO_SINT,"Faltando o delimitador '}' para finalizar o bloco de constantes" , atual.getLinha()));
                   atual = proximoToken();
               }
            }else{
                erros.add(new Token(Constants.ERRO_SINT,"Faltando o delimitador '{' para inciar o bloco de constantes" , atual.getLinha()));
                atual = proximoToken();
            }
        }
    }
    
    private void estruturaConstante(){
        if (automatos.isTipo(atual.getLexema())){
            atual = proximoToken();
            constantes();
        } else {
            if (atual.getTipo() != Constants.DELIMITADOR){
                erros.add(new Token(Constants.ERRO_SINT,"Faltando a tipagem da constante" , atual.getLinha()));
                atual = proximoToken();
            }
        }
    } 
    
    private void constantes(){
    }
    
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
