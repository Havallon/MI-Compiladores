
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
        System.out.println("Analise Lexica");
        for (String[] a : arqs){
            FileReader arq = new FileReader(a[0]);
            lexica.analisar(arq);
            ArrayList<Token> tokens = lexica.getTokens();
            if (!lexica.getErro()){
                System.out.println("Arquivo: " + a[1] + ".txt foi analisado lexicamente");
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
