
package Model;

import Model.Semantico.Comando;
import Model.Semantico.Condicao;
import Model.Semantico.Constante;
import Model.Semantico.Metodo;
import Model.Semantico.Variavel;
import Model.util.Constants;
import java.util.ArrayList;


public class AnaliseSintatica {
    
    private ArrayList<Token> tokens;
    private final Automatos automatos;
    private ArrayList<Token> erros;
    private final Primeiros primeiros;
    private Token atual;
    private ArrayList<Constante> listaConstantes;
    private ArrayList<Metodo> listaMetodos;
    
    //Variaveis para analise semantica das constantes
    private String tipoConstante;
    private String nomeConstante;
    private String valorConstante;
    
    //Variaveis para analise semantica dos metodos
    private ArrayList<Variavel> paramMetodos;
    private ArrayList<Variavel> varMetodos;
    private Metodo metodo;
    
    //variaveis para analise semantica de variaveis
    private String tipoVar;
    private String nomeVar;
    private boolean vetorVar;
    private boolean matrizVar;
    private String indice;
    private String tipoIndice;
    private String indiceM;
    private String tipoIndiceM;
    //Variaveis para analise Semantica de comandos
    private Comando cmd;
    
    public AnaliseSintatica(){
        primeiros = new Primeiros();
        automatos = new Automatos();
    }
    
    public ArrayList<Constante> getConstantes(){
        return this.listaConstantes;
    }
    
    public ArrayList<Metodo> getMetodos(){
        return this.listaMetodos;
    }
    
    public ArrayList<Token> start(ArrayList<Token> tokens){
        this.listaConstantes = new ArrayList<>();
        this.listaMetodos = new ArrayList<>();
        indice = "";
        indiceM = "";
        tipoIndice = "";
        tipoIndiceM = "";
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
        metodo = new Metodo();
        metodo.setLinha(atual.getLinha());
        this.paramMetodos = new ArrayList<>();
        this.varMetodos = new ArrayList<>();
        atual = proximoToken();
        if (atual.getTipo() == Constants.IDENTIFICADOR || atual.getLexema().equals("principal")){
            metodo.setNome(atual.getLexema());
            atual = proximoToken();
            if (atual.getLexema().equals("(")){
                atual = proximoToken();
                listaParametros(true);
                if (atual.getLexema().equals(")")){
                    atual = proximoToken();
                    if (atual.getLexema().equals(":")){
                        atual = proximoToken();
                        if (automatos.isTipo(atual.getLexema())){
                            metodo.setRetorno(atual.getLexema());
                            atual = proximoToken();
                            if (atual.getLexema().equals("{")){
                                atual = proximoToken();
                                declaracaoVariaveis();
                                escopoMetodo();
                                if (atual.getLexema().equals("}")){
                                    atual = proximoToken();
                                    metodo.setParametros(paramMetodos);
                                    metodo.setVariaveis(varMetodos);
                                    listaMetodos.add(metodo);
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
    
    private void incrementador(){
        if (atual.getLexema().equals("++") || atual.getLexema().equals("--")){
            cmd.setTipo("incremento");
            cmd.setLinha(atual.getLinha());
            cmd.setVetor(vetorVar);
            cmd.setMatriz(matrizVar);
            cmd.setIndice(indice);
            cmd.setIndiceM(indiceM);
            cmd.setTipoIndice(tipoIndice);
            cmd.setTipoIndiceM(tipoIndiceM);
            indice = "";
            indiceM = "";
            tipoIndice = "";
            tipoIndiceM = "";
            metodo.getComandos().add(cmd);
            matrizVar = false;
            vetorVar = false;
            atual = proximoToken();
            if (atual.getLexema().equals(";")){
                    atual = proximoToken();
            }else{
                erro("Faltando o ';' para finalizar a operacao");
            }
        } else{
            erro("Faltando o incrementado na variavel");
        }
    }
    
    private void comandos(){
        if (primeiros.comandos(atual)){
            cmd = new Comando();
            if (atual.getLexema().equals("leia")){
                atual = proximoToken();
                leia();
            }
            else if (atual.getLexema().equals("se")){
                atual = proximoToken();
                se();
            }
            else if (atual.getLexema().equals("enquanto")){
                atual = proximoToken();
                enquanto();
            }
            else if (atual.getLexema().equals("escreva")){
                atual = proximoToken();
                escreva();
            }
            else if (atual.getLexema().equals("resultado")){
                atual = proximoToken();
                cmd.setTipo("resultado");
                metodo.getComandos().add(cmd);
                resultado();
            }
            else if (atual.getTipo() == Constants.IDENTIFICADOR){
                cmd.setId(atual.getLexema());
                atual = proximoToken();
                qlComando();
            }
            comandos();
        }
    }
    
    private void resultado(){
        cmd.setId("resultado");
        cmd.setTipo("resultado");
        cmd.setLinha(atual.getLinha());
        if (atual.getLexema().equals(";")){
            Comando cmd2 = new Comando();
            cmd2.setId("vazio");
            cmd2.setTipo("vazio");
            cmd.getParam().add(cmd2);
            atual = proximoToken();
        }else{
            verificaCaso();
            if (atual.getLexema().equals(";")){
                atual = proximoToken();
            }else{
                erro("Faltando ';'");
            }
        }
    }
    
    private void escreva(){
        if (atual.getLexema().equals("(")){
            atual = proximoToken();
            paramEscrita();
            if (atual.getLexema().equals(")")){
                atual = proximoToken();
                if (atual.getLexema().equals(";")){
                    atual = proximoToken();
                }else{
                    erro("Faltando o ';'");
                }
            }else{
                erro("Esta faltando ')' para finalizar o escreva");
            }
        }else{
            erro("Está faltando '(' para iniciar o escreva");
        }
    }
    
    private void paramEscrita(){
        verificaCaso();
        maisParamEscrita();
    }
    
    private void maisParamEscrita(){
        if (atual.getLexema().equals(",")){
            atual = proximoToken();
            paramEscrita();
        }
    }
    
    private void enquanto(){
        cmd.setLinha(atual.getLinha());
        condSe();
        if (atual.getLexema().equals("{")){
            atual = proximoToken();
            conteudoLaco();
            if (atual.getLexema().equals("}")){
                atual = proximoToken();
            } else {
                erro("Está faltando '}' para terminar o bloco do enquanto");
            }
        } else{
            erro("Está faltando '{' para iniciar o bloco do enquanto");
        }
        cmd.setTipo("se");
        cmd.setId("se");
        metodo.getComandos().add(cmd);        
    }
    
    private void conteudoLaco(){
        if (primeiros.comandos(atual)){
            comandos();
            conteudoLaco();
        }
    }
    
    private void operacaoRelacional(){
        tipoTermo();
        operacaoRelacional2();
    }
    
    private void operacaoRelacional2(){
        if (atual.getTipo() == Constants.OPERADOR_RELACIONAL){
            atual = proximoToken();
            tipoTermo();
        }
    }
    
    private void qlComando(){
        if (atual.getLexema().equals("(")){
            atual = proximoToken();
            cmd.setTipo("chamada");
            chamadaDeMetodos(cmd);
            if (atual.getLexema().equals(";")){
                atual = proximoToken();
            } else{
                erro("Faltando ';'");
            }
        }else{
            vetor();
            qlComando2();
        }
    }
    
    private void chamadaDeMetodos(Comando comando){
        if (comando != null){
            comando.getParam().clear();
        }
        var(true, comando);
        if (comando != null){
            if (comando.getTipo().equals("chamada")){
                comando.setLinha(atual.getLinha());
                metodo.getComandos().add(comando);
                
            }
        }
        if (atual.getLexema().equals(")")){
            atual = proximoToken();

        } else{
            erro("Esta faltando o ')' da chamada de metodo");
        }
    }
    
    private void var(boolean blank, Comando comando){
        if (atual.getTipo() == Constants.NUMERO || atual.getTipo() == Constants.CADEIA_CARACTERES
                || automatos.isTipoBoleano(atual.getLexema())){
            Comando cmd2 = new Comando();
            cmd2.setId(atual.getLexema());
            cmd2.setTipo("imediato");
            comando.getParam().add(cmd2);
            atual = proximoToken();
            maisVariavel(comando);
        } else if (atual.getTipo() == Constants.IDENTIFICADOR){
            Comando cmd2 = new Comando();
            cmd2.setId(atual.getLexema());
            atual = proximoToken();
            if (atual.getLexema().equals("(")){
                cmd2.setTipo("met");
                atual = proximoToken();
                var(true, comando);
                if (atual.getLexema().equals(")")){
                    atual = proximoToken();
                    maisVariavel(comando); 
                } else{
                    erro("Faltando ')' na chamada de metodo");
                }
            } else{
                cmd2.setTipo("var");
                vetor();
                maisVariavel(comando);
            }
            cmd2.setVetor(vetorVar);
            cmd2.setMatriz(matrizVar);
            cmd2.setIndice(indice);
            cmd2.setIndiceM(indiceM);
            cmd2.setTipoIndice(tipoIndice);
            cmd2.setTipoIndiceM(tipoIndiceM);
            indice = "";
            indiceM = "";
            tipoIndice = "";
            tipoIndiceM = "";
            comando.getParam().add(cmd2);
            vetorVar = false;
            matrizVar = false;
            
        } else {
            if (!blank){
                erro("Ta faltando mais parametros");
            }
        }
    }
    
    private void maisVariavel(Comando comando){
        if (atual.getLexema().equals(",")){
            atual = proximoToken();
            var(false,comando);
        }
    }
    
    private void qlComando2(){
        if (atual.getLexema().equals("=")){
            atual = proximoToken();
            atribuicaoDeVariavel();
        }else{
            incrementador();
        }
    }
    
    private void atribuicaoDeVariavel(){
        cmd.setVetor(vetorVar);
        cmd.setMatriz(matrizVar);
        cmd.setIndice(indice);
            cmd.setIndiceM(indiceM);
            cmd.setTipoIndice(tipoIndice);
            cmd.setTipoIndiceM(tipoIndiceM);
            indice = "";
            indiceM = "";
            tipoIndice = "";
            tipoIndiceM = "";
        vetorVar = false;
        vetorVar = false;
        cmd.setLinha(atual.getLinha());
        verificaCaso();
        cmd.setTipo("atribuicao");
        metodo.getComandos().add(cmd);
        if (atual.getLexema().equals(";")){
            atual = proximoToken();
        }else{
            erro("Esta faltando ';' no finao do comando");
        }
    }
    
    private void verificaCaso(){
        String auxiliar = "";
        if (atual.getTipo() == Constants.IDENTIFICADOR){
            auxiliar = atual.getLexema();
            atual = proximoToken();
            
            if (atual.getLexema().equals("(")){
                atual = proximoToken();
                Comando cmd2 = new Comando();
                cmd2.setTipo("chamada2");
                chamadaDeMetodos(cmd2);
                cmd2.setId(auxiliar);
                cmd2.setTipo("chamada");
                cmd.getParam().add(cmd2);
                if (atual.getTipo() == Constants.OPERADOR_ARITMETICO){
                    colocarToken(atual);
                    atual = colocarToken();
                    expressao();
                }
                return;
            }
            Comando cmd2 = new Comando();
            vetor();
            if (automatos.isIncrementador(atual.getLexema())){
                cmd2.setId(auxiliar);
                cmd2.setTipo("inc2");
                cmd2.setVetor(vetorVar);
                cmd2.setMatriz(matrizVar);
                cmd2.setIndice(indice);
                cmd2.setIndiceM(indiceM);
                cmd2.setTipoIndice(tipoIndice);
                cmd2.setTipoIndiceM(tipoIndiceM);
                cmd.getParam().add(cmd2);
                indice = "";
                indiceM = "";
                tipoIndice = "";
                tipoIndiceM = "";
                vetorVar = false;
                matrizVar = false;
                atual = proximoToken();
            }else if (atual.getTipo() == Constants.OPERADOR_ARITMETICO){
                cmd2.setId(auxiliar);
                cmd2.setTipo("id");
                cmd2.setVetor(vetorVar);
                cmd2.setMatriz(matrizVar);
                cmd2.setIndice(indice);
                cmd2.setIndiceM(indiceM);
                cmd2.setTipoIndice(tipoIndice);
                cmd2.setTipoIndiceM(tipoIndiceM);
                cmd.getParam().add(cmd2);
                indice = "";
                indiceM = "";
                tipoIndice = "";
                tipoIndiceM = "";
                vetorVar = false;
                matrizVar = false;
                colocarToken(atual);
                atual = colocarToken();
                expressao();
            } else{
                cmd2.setId(auxiliar);
                cmd2.setTipo("id");
                cmd2.setVetor(vetorVar);
                cmd2.setMatriz(matrizVar);
                cmd2.setIndice(indice);
                cmd2.setIndiceM(indiceM);
                cmd2.setTipoIndice(tipoIndice);
                cmd2.setTipoIndiceM(tipoIndiceM);
                indice = "";
                indiceM = "";
                tipoIndice = "";
                tipoIndiceM = "";
                vetorVar = false;
                matrizVar = false;
                cmd.getParam().add(cmd2);
            }
        }else if (automatos.isIncrementador(atual.getLexema())){
            atual = proximoToken();
            if (atual.getTipo() == Constants.IDENTIFICADOR){
                Comando cmd2 =  new Comando();
                cmd2.setId(atual.getLexema());
                cmd2.setTipo("inc");
                atual = proximoToken();
                vetor();
                cmd2.setVetor(vetorVar);
                cmd2.setMatriz(matrizVar);
                cmd2.setIndice(indice);
                cmd2.setIndiceM(indiceM);
                cmd2.setTipoIndice(tipoIndice);
                cmd2.setTipoIndiceM(tipoIndiceM);
                indice = "";
                indiceM = "";
                tipoIndice = "";
                tipoIndiceM = "";
                vetorVar = false;
                matrizVar = false;
                cmd.getParam().add(cmd2);
            }else{
                erro("Apos o incremento precisa de uma variavel");
            }
        } else if (automatos.isTipoBoleano(atual.getLexema())){
            Comando cmd2 = new Comando();
            cmd2.setId(atual.getLexema());
            cmd2.setTipo("boleano");
            cmd.getParam().add(cmd2);
            atual = proximoToken();
        } else if (atual.getTipo() == Constants.NUMERO || atual.getTipo() == Constants.CADEIA_CARACTERES){
            expressao();
        } else if (atual.getLexema().equals("(")){
            atual = proximoToken();
            verificaCaso();
            if (atual.getLexema().equals(")")){
                atual = proximoToken();
                if (atual.getTipo() == Constants.OPERADOR_ARITMETICO){
                    colocarToken(atual);
                    atual = colocarToken();
                    expressao();
                }
                return;
            } else{
                erro("esta falando o ')'");
            }
        }
        else if (atual.getLexema().equals("!")){
            atual = proximoToken();
            if (atual.getTipo() == Constants.IDENTIFICADOR){
                Comando cmd2 = new Comando();
                cmd2.setId(atual.getLexema());
                cmd2.setTipo("negar");
                atual = proximoToken();
                vetor();
                cmd2.setVetor(vetorVar);
                cmd2.setMatriz(matrizVar);
                cmd2.setIndice(indice);
                cmd2.setIndiceM(indiceM);
                cmd2.setTipoIndice(tipoIndice);
                cmd2.setTipoIndiceM(tipoIndiceM);
                indice = "";
                indiceM = "";
                tipoIndice = "";
                tipoIndiceM = "";
                vetorVar = false;
                matrizVar = false;
                cmd.getParam().add(cmd2);
            }else if (automatos.isTipoBoleano(atual.getLexema())){
                atual = proximoToken();
            } else{
                erro("Apos o '!' tem que vim um variavel ou um tipo booleano");
            }
        }
        else{
            erro("Comando invalido ou incompleto");
        }
    }
    
    private void expressao(){
        multExp();
        E2();
    }
    
    private void E2(){
        if (atual.getLexema().equals("+") || atual.getLexema().equals("-")){
            exp2();
            E2();
        }
    }
    
    private void exp2(){
        if (atual.getLexema().equals("+") || atual.getLexema().equals("-")){
            cmd.getOp().add(atual.getLexema());
            atual = proximoToken();
            multExp();
        }
    }
    
    private void multExp(){
        negate();
        M2();    
    }
    
    private void M2(){
        if (atual.getLexema().equals("*") || atual.getLexema().equals("/")){
            mul2();
            M2();
        }
    }
    
    private void mul2(){
        if (atual.getLexema().equals("*") || atual.getLexema().equals("/")){
            cmd.getOp().add(atual.getLexema());
            atual = proximoToken();
            negate();
        }
    }
    
    private void negate(){
        if (atual.getLexema().equals("-")){
            atual = proximoToken();
        }
        value();
    }
    
    private void value(){
        if (atual.getTipo() == Constants.IDENTIFICADOR){
            String aux = atual.getLexema();
            atual = proximoToken();
            if (atual.getLexema().equals("(")){
                atual = proximoToken();
                Comando cmd2 = new Comando();
                cmd2.setTipo("chamada2");
                chamadaDeMetodos(cmd2);
                cmd2.setId(aux);
                cmd2.setTipo("chamada");
                cmd.getParam().add(cmd2);
            }else{
                vetor();
                Comando cmd2 = new Comando();
                cmd2.setId(aux);
                cmd2.setTipo("id");
                cmd2.setVetor(vetorVar);
                cmd2.setMatriz(matrizVar);
                cmd2.setIndice(indice);
                cmd2.setIndiceM(indiceM);
                cmd2.setTipoIndice(tipoIndice);
                cmd2.setTipoIndiceM(tipoIndiceM);
                indice = "";
                indiceM = "";
                tipoIndice = "";
                tipoIndiceM = "";
                vetorVar = false;
                matrizVar = false;
                cmd.getParam().add(cmd2);
                
            }
        } else if (atual.getTipo() == Constants.NUMERO || atual.getTipo() == Constants.CADEIA_CARACTERES){
            Comando cmd2 = new Comando();
            cmd2.setId(atual.getLexema());
            if (atual.getLexema().matches("\"(.)*\"")){
                cmd2.setTipo("texto");
            }else {
                if (atual.getLexema().contains(".")){
                    cmd2.setTipo("real");
                }else{
                    cmd2.setTipo("inteiro");
                }
            }
            cmd.getParam().add(cmd2);
            atual = proximoToken();
        } else if (atual.getLexema().equals("(")){
            atual = proximoToken();
            expressao();
            if (atual.getLexema().equals(")")){
                atual = proximoToken();
            } else{
                erro("Está faltando ')'");
            }
        }else if (atual.getTipo() == Constants.EXP){
            atual = proximoToken();
        }else {
            erro("Erro no valor");
        }
    }
    
 
    private void se(){
        cmd.setLinha(atual.getLinha());
        condSe();
        if (atual.getLexema().equals("entao")){
            atual = proximoToken();
            if (atual.getLexema().equals("{")){
                atual = proximoToken();
                blocoSe();
                if (atual.getLexema().equals("}")){
                    atual = proximoToken();
                    senao();
                }else{
                    erro("Esta faltando o '}' para finalizar o bloco se");
                }
            }else {
                erro("Esta faltando o '{' para iniciar o bloco do se");
            }
        }else{
            erro("Esta faltando o 'entao' para iniciar o bloco do se");
        }
        cmd.setTipo("se");
        cmd.setId("se");
        metodo.getComandos().add(cmd);
    }
    
    private void senao(){
        if (atual.getLexema().equals("senao")){
            atual = proximoToken();
            condSenao();
            if (atual.getLexema().equals("{")){
                atual = proximoToken();
                blocoSe();
                if (atual.getLexema().equals("}")){
                    atual = proximoToken();
                    senao();
                }else{
                    erro("Faltando o } para finalizar o bloco do senao");
                }
            } else {
                erro("Faltando o { para iniciar o bloco do senao");
            }
        }
    }
    
    private void condSenao(){
        if (atual.getLexema().equals("se")){
            atual = proximoToken();
            condSe();
            if (atual.getLexema().equals("entao")){
                atual = proximoToken();
            }else{
                erro("Faltando o 'entao' no bloco do senao");
            }
        }
    }
    
    private void blocoSe(){
        if (primeiros.comandos(atual)){
            comandos();
            blocoSe();
        }
    }
    
    private void condSe(){
        if (atual.getLexema().equals("(")){
            atual = proximoToken();
            cond();
            maisCond();
            if (atual.getLexema().equals(")")){
                atual = proximoToken();
            }else{
                erro("Esta faltando o ')' para terminar a condicao do se" );
            }
        } else{
            erro("Esta faltando o '(' para iniciar a condicao do se");
        }
    }
    
    private void maisCond(){
        if (atual.getTipo() == Constants.OPERADOR_LOGICO){
            atual = proximoToken();
            cond();
            maisCond();
        }
    }
    
    private void cond(){
        cmd.getConds().add(new Condicao());
        termo();
        tipoCond();
    }
    
    private void negar(){
        if (atual.getLexema().equals("!")){
            atual = proximoToken();
        }
    }
    
    private void tipoCond(){
        if (atual.getTipo() == Constants.OPERADOR_RELACIONAL){
            cmd.getConds().get(cmd.getConds().size()-1).setOp(atual.getLexema());
            atual = proximoToken();
            termo();
        }
    }
    
    private void termo(){
        tipoTermo();
        //op();
    }
    
    private void op(){
        if (atual.getTipo() == Constants.OPERADOR_ARITMETICO){
            atual = proximoToken();
            tipoTermo();
            op();
        }
    }
    
    private void tipoTermo(){
        if (atual.getTipo() == Constants.NUMERO || atual.getTipo() == Constants.CADEIA_CARACTERES
            || automatos.isTipoBoleano(atual.getLexema())){
            
            Condicao condicao = cmd.getConds().get(cmd.getConds().size()-1);
            
            if (!condicao.isAux()){
                condicao.setTermoA(atual.getLexema());
                if (atual.getTipo() == Constants.NUMERO){
                    if (atual.getLexema().contains(".")){
                        condicao.setTipoA("real");
                    } else{
                        condicao.setTipoA("inteiro");
                    }
                } else if (atual.getTipo() == Constants.CADEIA_CARACTERES){
                    condicao.setTipoA("texto");
                } else {
                    condicao.setTipoA("boleano");
                }
                condicao.setAux(true);
            } else {
                condicao.setTermoB(atual.getLexema());
                if (atual.getTipo() == Constants.NUMERO){
                    if (atual.getLexema().contains(".")){
                        condicao.setTipoB("real");
                    } else{
                        condicao.setTipoB("inteiro");
                    }
                } else if (atual.getTipo() == Constants.CADEIA_CARACTERES){
                    condicao.setTipoB("texto");
                } else {
                    condicao.setTipoB("boleano");
                }
            }
            atual = proximoToken();
        }else{
            negar();
            if (atual.getTipo() == Constants.IDENTIFICADOR){
                atual = proximoToken();
                vetor();
            } else{
                erro("Esta falando a condicao");
            }
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
            opI2(false);
            //opIndice();
            if (atual.getLexema().equals("]")){
                atual = proximoToken();
                matrizVar = true;
            } else {
                erro("Esta faltando ']' na utilizacao da matriz");
            }
        }
    }
    
    private void vetor(){
        if (atual.getLexema().equals("[")){
            atual = proximoToken();
            opI2(true);
            //opIndice();
            if (atual.getLexema().equals("]")){
                atual = proximoToken();
                vetorVar = true;
                matriz();
            }else{
                erro("Esta faltando ']' na utilizacao do vetor");
            }
        }
    }
    
    private void opIndice(){
        if (atual.getTipo() == Constants.OPERADOR_ARITMETICO){
            atual = proximoToken();
            opI2(false);
            opIndice();
        }
    }
    
    private void opI2(boolean isVetor){
        if (atual.getTipo() == Constants.IDENTIFICADOR || atual.getTipo() == Constants.NUMERO){
            if (isVetor){
                if (atual.getTipo() == Constants.IDENTIFICADOR){
                    indice = atual.getLexema();
                    tipoIndice = "var";
                }else{
                    indice = atual.getLexema();
                    if (indice.contains(".")){
                        tipoIndice = "real";
                    }else{
                        tipoIndice = "inteiro";
                    }
                }
            } else {
                if (atual.getTipo() == Constants.IDENTIFICADOR){
                    indiceM = atual.getLexema();
                    tipoIndiceM = "var";
                }else{
                    indiceM = atual.getLexema();
                    if (indiceM.contains(".")){
                        tipoIndiceM = "real";
                    }else{
                        tipoIndiceM = "inteiro";
                    }
                }
            }
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
            tipoVar = atual.getLexema();
            atual = proximoToken();
            complementoV();
            maisVariaveis();
        } else {
            erro("Está faltando a tipagem da variavel");
        }
    }
    
    private void complementoV(){
        if (atual.getTipo() == Constants.IDENTIFICADOR){
            nomeVar = atual.getLexema();
            int linhaV = atual.getLinha();
            atual = proximoToken();
            vetor();
            varMetodos.add(new Variavel(nomeVar, tipoVar, linhaV,vetorVar,matrizVar));
            vetorVar = false;
            matrizVar = false;
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
        String tipagem;
        String nomeParam;
        if (automatos.isTipo(atual.getLexema())){
            tipagem = atual.getLexema();
            atual = proximoToken();
            if (atual.getTipo() == Constants.IDENTIFICADOR){
                nomeParam = atual.getLexema();
                paramMetodos.add(new Variavel(nomeParam, tipagem, atual.getLinha(),false,false));
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
            tipoConstante = atual.getLexema();
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
            nomeConstante = atual.getLexema();
            atual = proximoToken();
            if (atual.getLexema().equals("=")){
                atual = proximoToken();
                if (atual.getTipo() == Constants.NUMERO || atual.getTipo() == Constants.CADEIA_CARACTERES || automatos.isTipoBoleano(atual.getLexema())){
                    valorConstante = atual.getLexema();
                    listaConstantes.add(new Constante(tipoConstante, nomeConstante, valorConstante,atual.getLinha()));
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
    
    private Token verToken(){
        if (!tokens.isEmpty())
            return tokens.get(0);
        else
            return new Token(Constants.NULO,"SEM LEXEMAS",atual.getLinha());
    }
    
    private void colocarToken(Token t){
        tokens.add(0,t);
    }
    
    private Token colocarToken(){
        Token t = new Token(Constants.EXP, "a", atual.getLinha());
        //tokens.add(0, t);
        return t;
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
