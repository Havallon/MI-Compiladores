package Model;

import Model.Semantico.Comando;
import Model.Semantico.Constante;
import Model.Semantico.ErroSemantico;
import Model.Semantico.Metodo;
import Model.Semantico.Variavel;
import java.util.ArrayList;
import java.util.Collections;

public class AnaliseSemantica {

    private final Automatos automatos;
    private final ArrayList<ErroSemantico> erros;

    public AnaliseSemantica() {
        this.erros = new ArrayList<>();
        this.automatos = new Automatos();
    }

    public void reset() {
        erros.clear();
    }

    public ArrayList<ErroSemantico> getErros() {
        return this.erros;
    }

    public void verificarMetodo(ArrayList<Metodo> lista, ArrayList<Constante> ctes) {
        verificarMetodoPrincipal(lista);
        verificarMetodoParam(lista, ctes);
        verificarSobrescrita(lista);
        verificarVar(lista, ctes);
        verificarRetorno(lista);
        verificarComandos(lista, ctes);
    }
    
    private void verificarRetorno(ArrayList<Metodo> lista){
        for (Metodo m : lista){
            boolean exist = false;
            if (!m.getRetorno().equals("vazio")){
                for (Comando cmd : m.getComandos()){
                    if (cmd.getTipo().equals("resultado")){
                        exist = true;
                    }
                }
                if (!exist){
                    erros.add(new ErroSemantico(m.getLinha(), "Linha: " + m.getLinha() + " - Metodo '" + m.getNome() + "' sem retorno"));
                }
            }
        }
    }
    

    private void verificarComandos(ArrayList<Metodo> lista, ArrayList<Constante> ctes) {
        for (Metodo m : lista) {
            for (Comando cmd : m.getComandos()) {
                if (cmd.getTipo().equals("incremento")) {
                    for (Constante c : ctes) {
                        if (cmd.getId().equals(c.getNome())) {
                            erros.add(new ErroSemantico(cmd.getLinha(), "Linha: " + cmd.getLinha() + " - não pode fazer incremento em uma constante"));
                        }
                    }

                    boolean exist = false;
                    boolean vetor = false;
                    boolean matriz = false;
                    for (Variavel p : m.getParametros()) {
                        if (cmd.getId().equals(p.getNome())) {
                            exist = true;
                            vetor = p.isVetor();
                            matriz = p.isMatriz();
                            if (p.getTipo().equals("texto") || p.getTipo().equals("boleano")) {
                                erros.add(new ErroSemantico(cmd.getLinha(), "Linha: " + cmd.getLinha() + " - Só é permitido incremento em variaveis do tipo inteiro ou real"));
                            }
                        }
                    }

                    for (Variavel v : m.getVariaveis()) {
                        if (cmd.getId().equals(v.getNome())) {
                            exist = true;
                            vetor = v.isVetor();
                            matriz = v.isMatriz();
                            if (v.getTipo().equals("texto") || v.getTipo().equals("boleano")) {
                                erros.add(new ErroSemantico(cmd.getLinha(), "Linha: " + cmd.getLinha() + " - Só é permitido incremento em variaveis do tipo inteiro ou real"));
                            }
                        }
                    }

                    if (!exist) {
                        erros.add(new ErroSemantico(cmd.getLinha(), "Linha: " + cmd.getLinha() + " - A variavel '" + cmd.getId() + "' nao foi declarada"));
                    }
                    verificarVetores(vetor, matriz, cmd);
                } 
                else if (cmd.getTipo().equals("chamada")){
                    boolean exist = false;
                    for (Metodo met : lista){
                        if (met.getNome().equals(cmd.getId())){
                            if (met.getParametros().size() == cmd.getParam().size()){
                                exist = true;
                                for (int j = 0; j < met.getParametros().size(); j++){
                                    if (cmd.getParam().get(j).getTipo().equals("imediato")){
                                        switch (met.getParametros().get(j).getTipo()) {
                                            case "inteiro":
                                                try {
                                                    int i = Integer.parseInt(cmd.getParam().get(j).getId());
                                                } catch (Exception ex) {
                                                    erros.add(new ErroSemantico(cmd.getLinha(), "Linha: " + cmd.getLinha() + " - Valor '" + cmd.getParam().get(j).getId() + "' não é do tipo " + met.getParametros().get(j).getTipo()));
                                                }
                                                break;
                                            case "real":
                                                try {
                                                    float i = Float.parseFloat(cmd.getParam().get(j).getId());
                                                } catch (Exception ex) {
                                                    erros.add(new ErroSemantico(cmd.getLinha(), "Linha: " + cmd.getLinha() + " - Valor '" + cmd.getParam().get(j).getId() + "' não é do tipo " + met.getParametros().get(j).getTipo()));
                                                }
                                                break;
                                            case "boleano":
                                                if (!automatos.isTipoBoleano(cmd.getParam().get(j).getId())) {
                                                    erros.add(new ErroSemantico(cmd.getLinha(), "Linha: " + cmd.getLinha() + " - Valor '" + cmd.getParam().get(j).getId() + "' não é do tipo " + met.getParametros().get(j).getTipo()));
                                                }
                                                break;
                                            case "texto":
                                                if (!cmd.getParam().get(j).getId().matches("\"(.)*\"")) {
                                                    erros.add(new ErroSemantico(cmd.getLinha(), "Linha: " + cmd.getLinha() + " - Valor '" + cmd.getParam().get(j).getId() + "' não é do tipo " + met.getParametros().get(j).getTipo()));
                                                }
                                                break;
                                        }
                                    } else if (cmd.getParam().get(j).getTipo().equals("var")){
                                        boolean aux = false;
                                       for (Constante c : ctes){
                                            if (c.getNome().equals(cmd.getParam().get(j).getId())){
                                                aux = true;
                                                if (!c.getTipo().equals(met.getParametros().get(j).getTipo())){
                                                   erros.add(new ErroSemantico(cmd.getLinha(), "Linha: " + cmd.getLinha() + " - Constante '" + c.getNome() + "' não é do tipo " + met.getParametros().get(j).getTipo()));
                                                }
                                            }
                                        }
                                       if (!aux){
                                           for (Variavel v : m.getParametros()){
                                               if (v.getNome().equals(cmd.getParam().get(j).getId())){
                                                   aux = true;
                                                   verificarVetores(v.isVetor(),v.isMatriz(),cmd.getParam().get(j));
                                                   if (!v.getTipo().equals(met.getParametros().get(j).getTipo())){
                                                       erros.add(new ErroSemantico(cmd.getLinha(), "Linha: " + cmd.getLinha() + " - Variavel '" + v.getNome() + "' não é do tipo " + met.getParametros().get(j).getTipo()));
                                                   }
                                               }
                                           }
                                           if (!aux){
                                               for (Variavel v : m.getVariaveis()){
                                                   if (v.getNome().equals(cmd.getParam().get(j).getId())){
                                                        aux = true;
                                                        verificarVetores(v.isVetor(),v.isMatriz(),cmd.getParam().get(j));
                                                        if (!v.getTipo().equals(met.getParametros().get(j).getTipo())){
                                                            erros.add(new ErroSemantico(cmd.getLinha(), "Linha: " + cmd.getLinha() + " - Variavel '" + v.getNome() + "' não é do tipo " + met.getParametros().get(j).getTipo()));
                                                        }
                                                   }
                                               }
                                               
                                               if (!aux){
                                                   erros.add(new ErroSemantico(cmd.getLinha(), "Linha: " + cmd.getLinha() + " - Variavel '" + cmd.getParam().get(j).getId() + "' nao existe"));
                                               }
                                           }
                                       }
                                    } else if (cmd.getParam().get(j).getTipo().equals("met")){
                                        System.out.println("ok");
                                    }
                                }
                            }
                        }
                    }
                    if (!exist){
                        erros.add(new ErroSemantico(cmd.getLinha(), "Linha: " + cmd.getLinha() + " - Metodo '" + cmd.getId() + "' nao existe"));
                    }
                }
                else if (cmd.getTipo().equals("atribuicao")){
                    String nome = cmd.getId();
                    String tipo = "";
                    boolean exist = false;
                    for (Constante c : ctes){
                        if (c.getNome().equals(nome)){
                            exist = true;
                        }
                        
                    }
                    if (exist){
                        erros.add(new ErroSemantico(cmd.getLinha(), "Linha: " + cmd.getLinha() + " - Nao pode fazer atribuicao em constante"));
                    }else{
                        exist = false;
                        for (Variavel v : m.getParametros()){
                            if (v.getNome().equals(nome)){
                                exist = true;
                                tipo = v.getTipo();
                            }
                        }
                        if (!exist){
                            for (Variavel v : m.getVariaveis()){
                                if (v.getNome().equals(nome)){
                                    exist = true;
                                    verificarVetores(v.isVetor(), v.isMatriz(), cmd);
                                    tipo = v.getTipo();
                                }
                            }
                            if (!exist){
                                erros.add(new ErroSemantico(cmd.getLinha(), "Linha: " + cmd.getLinha() + " - Variavel '" + cmd.getId() + "' nao existe"));
                            }
                        }
                        if (exist){
                            for (Comando c : cmd.getParam()){
                                if (!c.getTipo().equals(tipo)){
                                    if (c.getTipo().equals("inc") || c.getTipo().equals("inc2")){
                                        boolean e = false;
                                        for (Constante cte : ctes){
                                            if (cte.getNome().equals(c.getId())){
                                                erros.add(new ErroSemantico(cmd.getLinha(), "Linha: " + cmd.getLinha() + " - Nao pode fazer atribuicao em constante"));
                                                e = true;
                                            }
                                        }
                                        
                                        for (Variavel v : m.getParametros()){
                                            if (v.getNome().equals(c.getId())){
                                                e = true;
                                                if (v.getTipo().equals("texto") || v.getTipo().equals("boleano")){
                                                    erros.add(new ErroSemantico(cmd.getLinha(), "Linha: " + cmd.getLinha() + " - Nao pode fazer incrmento em '" + v.getTipo() + "'"));
                                                }else{
                                                    if (!v.getTipo().equals(tipo)){
                                                        erros.add(new ErroSemantico(cmd.getLinha(), "Linha: " + cmd.getLinha() + " - Só pode haver operando do tipo '" + tipo + "' nessa expressao"));
                                                    }
                                                }
                                            }
                                        }
                                        
                                        for (Variavel v : m.getVariaveis()){
                                            if (v.getNome().equals(c.getId())){
                                                e = true;
                                                verificarVetores(v.isVetor(), v.isMatriz(), c);
                                                if (v.getTipo().equals("texto") || v.getTipo().equals("boleano")){
                                                    erros.add(new ErroSemantico(cmd.getLinha(), "Linha: " + cmd.getLinha() + " - Nao pode fazer incrmento em '" + v.getTipo() + "'"));
                                                }else{
                                                    if (!v.getTipo().equals(tipo)){
                                                        erros.add(new ErroSemantico(cmd.getLinha(), "Linha: " + cmd.getLinha() + " - Só pode haver operando do tipo '" + tipo + "' nessa expressao"));
                                                    }
                                                }
                                            }
                                        }
                                        
                                        if (!e){
                                            erros.add(new ErroSemantico(cmd.getLinha(), "Linha: " + cmd.getLinha() + " - Variavel '" + c.getId() + "' nao existe"));
                                        }
                                        
                                    } else if (c.getTipo().equals("id")){
                                        boolean e = false;
                                        for (Constante c2 : ctes){
                                           if (c2.getNome().equals(c.getId())){
                                               e = true;
                                               if (!c2.getTipo().equals(tipo)){
                                                   erros.add(new ErroSemantico(cmd.getLinha(), "Linha: " + cmd.getLinha() + " - A variavel '" + c2.getNome() + "' não é do tipo '" + tipo + "'"));
                                               }
                                           } 
                                        }
                                        if (!e){
                                            for (Variavel v2 : m.getParametros()){
                                                if (v2.getNome().equals(c.getId())){
                                                    e = true;
                                                    if (!v2.getTipo().equals(tipo)){
                                                        erros.add(new ErroSemantico(cmd.getLinha(), "Linha: " + cmd.getLinha() + " - A variavel '" + v2.getNome() + "' não é do tipo '" + tipo + "'"));
                                                    }
                                                }
                                            }
                                            
                                            if (!e){
                                                for (Variavel v2 : m.getVariaveis()){
                                                    if (v2.getNome().equals(c.getId())){
                                                        e = true;
                                                        verificarVetores(v2.isVetor(), v2.isMatriz(), c);
                                                        if (!v2.getTipo().equals(tipo)){
                                                            erros.add(new ErroSemantico(cmd.getLinha(), "Linha: " + cmd.getLinha() + " - A variavel '" + v2.getNome() + "' não é do tipo '" + tipo + "'"));
                                                        }
                                                    }
                                                }
                                                if (!e){
                                                    erros.add(new ErroSemantico(cmd.getLinha(), "Linha: " + cmd.getLinha() + " - Variavel '" + c.getId() + "' nao existe"));
                                                }
                                            }
                                        }
                                    }else{
                                        erros.add(new ErroSemantico(cmd.getLinha(), "Linha: " + cmd.getLinha() + " - Só pode haver operando do tipo '" + tipo + "' nessa expressao"));
                                    }
                                }
                            }
                            if (tipo.equals("texto")){
                                for (String s : cmd.getOp()){
                                    if (!s.equals("+")){
                                        erros.add(new ErroSemantico(cmd.getLinha(), "Linha: " + cmd.getLinha() + " - A unica operação com textos permita é a adição"));
                                    }
                                }
                            }
                            
                        }
                    }
                }
            }
        }
    }

    private void verificarVetores(boolean vetor, boolean matriz, Comando cmd) {
        if (vetor == cmd.isVetor()) {
            if (matriz == cmd.isMatriz()) {

            } else {
                if (matriz) {
                    erros.add(new ErroSemantico(cmd.getLinha(), "Linha: " + cmd.getLinha() + " - A variavel '" + cmd.getId() + "' tem que ser acessada como matriz"));
                } else {
                    erros.add(new ErroSemantico(cmd.getLinha(), "Linha: " + cmd.getLinha() + " - A variavel '" + cmd.getId() + "' nao pode ser acessada como matriz"));
                }
            }
        } else {
            if (matriz) {
                erros.add(new ErroSemantico(cmd.getLinha(), "Linha: " + cmd.getLinha() + " - A variavel '" + cmd.getId() + "' tem que ser acessada como matriz"));
            } else {
                if (vetor) {
                    erros.add(new ErroSemantico(cmd.getLinha(), "Linha: " + cmd.getLinha() + " - A variavel '" + cmd.getId() + "' tem que ser acessada como vetor"));
                } else if (!vetor) {
                    if (cmd.isMatriz()) {
                        erros.add(new ErroSemantico(cmd.getLinha(), "Linha: " + cmd.getLinha() + " - A variavel '" + cmd.getId() + "' nao pode ser acessada como matriz"));
                    } else {
                        erros.add(new ErroSemantico(cmd.getLinha(), "Linha: " + cmd.getLinha() + " - A variavel '" + cmd.getId() + "' nao pode ser acessada como vetor"));
                    }
                }
            }
        }
    }

    private void verificarSobrescrita(ArrayList<Metodo> lista) {
        Metodo m;
        ArrayList<Metodo> clone = (ArrayList<Metodo>) lista.clone();
        while (!clone.isEmpty()) {
            m = clone.remove(0);
            for (Metodo aux : clone) {
                if (m.getNome().equals(aux.getNome())) {
                    if (m.getRetorno().equals(aux.getRetorno())) {
                        if (m.getParametros().size() == aux.getParametros().size()) {
                            if (m.getParametros().size() > 0) {
                                boolean same = false;
                                for (int i = 0; i < m.getParametros().size(); i++) {
                                    if (m.getParametros().get(i).getTipo().equals(aux.getParametros().get(i).getTipo())) {
                                        same = true;
                                    } else {
                                        same = false;
                                        i = m.getParametros().size();
                                    }
                                }
                                if (same) {
                                    erros.add(new ErroSemantico(aux.getLinha(), "Linha: " + aux.getLinha() + " - Não é permitido a sobrescrita do metodo '" + aux.getNome() + "'"));
                                }
                            } else {
                                erros.add(new ErroSemantico(aux.getLinha(), "Linha: " + aux.getLinha() + " - Não é permitido a sobrescrita do metodo '" + aux.getNome() + "'"));
                            }
                        }
                    }
                }
            }
        }
    }

    private void verificarVar(ArrayList<Metodo> lista, ArrayList<Constante> ctes) {
        ArrayList<String> citados = new ArrayList<>();
        for (Metodo m : lista) {
            for (Variavel v : m.getVariaveis()) {
                if (v.getTipo().equals("vazio")) {
                    erros.add(new ErroSemantico(v.getLinha(), "Linha: " + v.getLinha() + " - A variavel '" + v.getNome() + "' não pode ser do tipo vazio"));
                }

                for (Constante c : ctes) {
                    if (v.getNome().equals(c.getNome())) {
                        erros.add(new ErroSemantico(v.getLinha(), "Linha: " + v.getLinha() + " - Nome da variavel '" + v.getNome() + "' já foi declarada como constante"));
                    }
                }

                if (Collections.frequency(m.getParametros(), v) > 1) {
                    if (citados.contains(v.getNome())) {
                        erros.add(new ErroSemantico(v.getLinha(), "Linha: " + v.getLinha() + " - O nome do parametro '" + v.getNome() + "' ja esta em uso"));
                    } else {
                        citados.add(v.getNome());
                    }
                }

                for (Variavel param : m.getParametros()) {
                    if (v.getNome().equals(param.getNome())) {
                        erros.add(new ErroSemantico(v.getLinha(), "Linha: " + v.getLinha() + " - A variavel '" + v.getNome() + "' já foi declarada nos parametros do metodo"));
                    }
                }
            }
        }
    }

    private void verificarMetodoParam(ArrayList<Metodo> lista, ArrayList<Constante> ctes) {
        ArrayList<String> citados = new ArrayList<>();

        for (Metodo m : lista) {
            for (Variavel v : m.getParametros()) {
                if (v.getTipo().equals("vazio")) {
                    erros.add(new ErroSemantico(v.getLinha(), "Linha: " + v.getLinha() + " - O parametro '" + v.getNome() + "' não pode ser do tipo vazio"));
                }

                for (Constante c : ctes) {
                    if (v.getNome().equals(c.getNome())) {
                        erros.add(new ErroSemantico(v.getLinha(), "Linha: " + v.getLinha() + " - Nome do parametro '" + v.getNome() + "' já foi declarado como constante"));
                    }
                }
                if (Collections.frequency(m.getParametros(), v) > 1) {
                    if (citados.contains(v.getNome())) {
                        erros.add(new ErroSemantico(v.getLinha(), "Linha: " + v.getLinha() + " - O nome do parametro '" + v.getNome() + "' ja esta em uso"));
                    } else {
                        citados.add(v.getNome());
                    }
                }
            }
        }
    }

    private void verificarMetodoPrincipal(ArrayList<Metodo> lista) {
        int quantidade = 0;
        for (Metodo m : lista) {
            if (m.getNome().equals("principal")) {
                quantidade++;
                if (!m.getRetorno().equals("vazio")) {
                    erros.add(new ErroSemantico(m.getLinha(), "Linha: " + m.getLinha() + " - O retorno do método principal tem que ser vazio"));
                }
                if (!m.getParametros().isEmpty()) {
                    erros.add(new ErroSemantico(m.getLinha(), "Linha: " + m.getLinha() + " - O método principal não pode ter parametros"));
                }
                if (quantidade > 1) {
                    erros.add(new ErroSemantico(m.getLinha(), "Linha: " + m.getLinha() + " - O método principal já foi declarado"));
                }
            }

        }
        if (quantidade == 0) {
            erros.add(new ErroSemantico(0, "Está faltando o método principal"));
        }

    }

    public void verificarConstantes(ArrayList<Constante> lista) {
        verificarConstantesIguais(lista);
        verificarConstanteVazias(lista);
        verificarTipoConstante(lista);
    }

    private void verificarConstantesIguais(ArrayList<Constante> lista) {
        ArrayList<Constante> aux = (ArrayList<Constante>) lista.clone();
        while (!aux.isEmpty()) {
            Constante c = aux.remove(aux.size() - 1);
            int ocorrencias = Collections.frequency(aux, c);
            if (ocorrencias > 0) {
                erros.add(new ErroSemantico(c.getLinha(), "Linha: " + c.getLinha() + " - O nome da constante '" + c.getNome() + "' ja esta em uso"));
            }
        }
    }

    private void verificarConstanteVazias(ArrayList<Constante> lista) {
        for (Constante c : lista) {
            if (c.getTipo().equals("vazio")) {
                erros.add(new ErroSemantico(c.getLinha(), "Linha: " + c.getLinha() + " - Nao e permitido declarar constantes com do tipo 'vazio'"));
            }
        }
    }

    private void verificarTipoConstante(ArrayList<Constante> lista) {
        ArrayList<Constante> tipos = new ArrayList<>();
        for (Constante c : lista) {
            switch (c.getTipo()) {
                case "inteiro":
                    try {
                        int i = Integer.parseInt(c.getValor());
                    } catch (Exception ex) {
                        erros.add(new ErroSemantico(c.getLinha(), "Linha: " + c.getLinha() + " - Constante '" + c.getNome() + "' esta recebendo um valor incompativel com sua tipagem"));
                    }
                    break;
                case "real":
                    try {
                        float i = Float.parseFloat(c.getValor());
                    } catch (Exception ex) {
                        erros.add(new ErroSemantico(c.getLinha(), "Linha: " + c.getLinha() + " - Constante '" + c.getNome() + "' esta recebendo um valor incompativel com sua tipagem"));
                    }
                    break;
                case "boleano":
                    if (!automatos.isTipoBoleano(c.getValor())) {
                        erros.add(new ErroSemantico(c.getLinha(), "Linha: " + c.getLinha() + " - Constante '" + c.getNome() + "' esta recebendo um valor incompativel com sua tipagem"));
                    }
                    break;
                case "texto":
                    if (!c.getValor().matches("\"(.)*\"")) {
                        erros.add(new ErroSemantico(c.getLinha(), "Linha: " + c.getLinha() + " - Constante '" + c.getNome() + "' esta recebendo um valor incompativel com sua tipagem"));
                    }
                    break;
            }
        }
    }
}
