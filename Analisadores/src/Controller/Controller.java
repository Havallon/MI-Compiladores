
package Controller;

import Model.AnaliseLexica;
import Model.Token;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;



public class Controller {
    
    private AnaliseLexica lexica;
    
    public Controller(){
        lexica = new AnaliseLexica();
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
            lexica.analisar(arq);
            ArrayList<Token> tokens = lexica.getTokens();
            //Escrevendo no arquivo
            FileWriter escrita = new FileWriter("./teste/"+a[1]+".saida");
            PrintWriter gravar = new PrintWriter(escrita);
            for(Token token : tokens){
                gravar.println(token.toString());
            }
            if (!lexica.getErro()){
                gravar.println("");
                gravar.println("");
                gravar.println("CODIGO FONTE SEM ERRO LEXICO");
            }else{
                tokens = lexica.getTokensInvalidos();
                gravar.println("");
                gravar.println("");
                gravar.println("ERROS LEXICOS");
                for(Token token : tokens){
                gravar.println(token.toString());
                }
            }
            gravar.close();
            escrita.close();
            System.out.println("Arquivo: " + a[1] + ".txt foi analisado");
            arq.close();
        }
        
    }
}
