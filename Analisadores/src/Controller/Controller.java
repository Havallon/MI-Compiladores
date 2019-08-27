
package Controller;

import Model.AnaliseLexica;
import Model.AnaliseSemantica;
import Model.AnaliseSintatica;
import Model.Semantico.ErroSemantico;
import Model.Token;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;



public class Controller {
    
    private AnaliseLexica lexica;
    private AnaliseSintatica parser;
    private AnaliseSemantica semantico;
    
    public Controller(){
        lexica = new AnaliseLexica();
        parser = new AnaliseSintatica();
        semantico = new AnaliseSemantica();
    }
    
    //Metodo inicial
    public void start() throws FileNotFoundException, IOException{
        File f = new File("./teste");
        ArrayList<String[]> arqs = new ArrayList<>();
        //Veriicando se a pasta teste existe, caso contrario cria a mesma
        if (!f.exists()){
            f.mkdir();
        }
        
        //FILTRO PARA LEITURA DE EXTENSÃO SOMENTE .TXT
        FilenameFilter filtro = (File dir, String name) -> {
            String caixaBaixa = name.toLowerCase();
            return (caixaBaixa.endsWith(".txt"));
        };
        
        //Listando todos os arquvios da pasta
        for (String arq : f.list(filtro)){
            String nome = arq.replaceAll(".txt", "");
            String[] aux = {f.getPath()+"/"+arq,nome};
            arqs.add(aux);
        }
        
        //Lendo todos os arquivos
        for (String[] a : arqs){
            FileReader arq = new FileReader(a[0]);
            //Realizando a analise lexica no arquivo
            semantico.reset();
            lexica.analisar(arq);
            ArrayList<Token> tokens = lexica.getTokens();
            /*
            * Se não houver erro lexico, realiza a analise sintatica.
            * Caso contrario, gera um arquivo de saída avisando o erro.
            */
            if (!lexica.getErro()){
                System.out.println("Arquivo: " + a[1] + ".txt foi analisado lexicamente");
                //analise sintatica
                ArrayList<Token> erros = parser.start(tokens);
                
                //Verificando se há erros sintaticos, se sim salva-los em um arq.
                if (!erros.isEmpty()){
                    FileWriter escrita = new FileWriter("./teste/"+a[1]+".saida");
                    PrintWriter gravar = new PrintWriter(escrita);
                    for(Token token : erros){
                        gravar.println(token.toString());
                    }
                    gravar.close();
                    escrita.close();
                    System.out.println("Arquivo: " + a[1] + ".txt contem erro sintatico");
                } else{
                    System.out.println("Arquivo: " + a[1] + ".txt foi analisado sintaticamente");
                    FileWriter escrita = new FileWriter("./teste/"+a[1]+".saida");
                    PrintWriter gravar = new PrintWriter(escrita);
                    
                    semantico.verificarConstantes(parser.getConstantes());
                    semantico.verificarMetodo(parser.getMetodos(), parser.getConstantes());
                    
                    ArrayList<ErroSemantico> erroSemantico = semantico.getErros();
                    
                    if (erroSemantico.isEmpty()){
                        gravar.println("SUCESSO");
                    }else{
                        Collections.sort(erroSemantico);
                        for (ErroSemantico er : erroSemantico){
                            gravar.println(er.getMsg());
                        }
                    }
                    
                    System.out.println("Arquivo: " + a[1] + ".txt foi analisado semanticamente");
                    gravar.close();
                    escrita.close();
                }
            }else{
                FileWriter escrita = new FileWriter("./teste/"+a[1]+".saida");
                PrintWriter gravar = new PrintWriter(escrita);
                tokens = lexica.getTokensInvalidos();
                gravar.println("ERROS LEXICOS");
                for(Token token : tokens){
                    gravar.println(token.toString());
                }
                System.out.println("Arquivo: " + a[1] + ".txt contem erro lexico");
                gravar.close();
                escrita.close();
            }
            
            arq.close();
        }
        
    }
}
