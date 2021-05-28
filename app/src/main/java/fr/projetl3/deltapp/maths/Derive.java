package fr.projetl3.deltapp.maths;

import java.util.ArrayList;

public class Derive {

    private String expression; // Contient l'expression entré par l'utilisateur
    private String symbole; // Contient le symbole de dérivation
    private String result; // Contient le résultat dérivé
    private ArrayList<String> list; // Contient la liste des expression
    private ArrayList<String> listDerive; // Contient la liste des parties dérivé
    private ArrayList<String> complexe; // Contient toutes les fonctions "complexes" exemple fonction cosinus

    // Constructeur sans symbole
    public Derive(String expression){
        this.expression = expression;
        this.list = new ArrayList<>();
        this.listDerive = new ArrayList<>();
        getSymboleFromExpression();
        generateList();
        setComplexeList();
        derive();
    }

    // Constructeur qui prend un paramètre symbole pour forcer l'utilisation d'un symbole
    public Derive(String expression, String symbole){
        this.expression = expression;
        this.list = new ArrayList<>();
        this.listDerive = new ArrayList<>();
        this.symbole = symbole;
        generateList();
        setComplexeList();
        derive();
    }

    // Va générer la liste des fonctions complexes
    private void setComplexeList() {
        this.complexe = new ArrayList<>();
        complexe.add("cos");
        complexe.add("sin");
        complexe.add("tan");
        complexe.add("sqrt");
        complexe.add("exp");
        complexe.add("ln");
        complexe.add("log");
    }

    // Va récupérer le symbole de l'expression
    private void getSymboleFromExpression(){
        String[] split = expression.split("=");
        if(split.length > 1 && split[0].length() > 0 && split[1].length() > 0){
            String part1 = split[0];
            String part2 = split[1];
            if(part1.startsWith("der(") && part1.endsWith(")")){
                part1 = part1.replace("der(", ""); part1 = part1.replace(")", "");
                if(part1.length() == 1){symbole = part1;} else {symbole = "x";}
            }
            this.expression = part2;
        } else {
            symbole = "x";
        }
    }

    // Retourne la liste des dérivé
    public ArrayList<String> getListDerive(){
        return listDerive;
    }

    // Retourne la liste des expression
    public ArrayList<String> getList(){
        return list;
    }

    // Retourne le symbole de dérivation
    public String getSymbole(){
        return symbole;
    }

    // Va générer la liste des expressions grâce à l'expression entrée par l'utilisateur
    private void generateList(){
        String tmp = "";
        int popen = 0;
        for(int i = 0; i < expression.length(); i++){
            if(expression.charAt(i) == '+' && popen == 0){
                list.add(tmp);
                tmp = "";
            } else if(expression.charAt(i) == '-' && popen == 0 && i != 0){
                list.add(tmp);
                tmp = "-";
            } else {
                if(expression.charAt(i) == '('){popen++;} else if(expression.charAt(i) == ')'){popen--;}
                tmp = tmp.concat(String.valueOf(expression.charAt(i)));
                if(i == expression.length() - 1){
                    list.add(tmp);
                }
            }
        }
        result = list.toString();
    }

    // Va générer la liste des dérivé grâce à la liste des expressions en dérivant chaque partie.
    private void derive(){
        String str;
        for(int i = 0; i < list.size(); i++){
            str = fprime(list.get(i));
            if(!str.isEmpty()){ listDerive.add(str); }

        }
        result = result.concat("\n" + listDerive.toString());
    }

    // Va retourner vrai si l'expression entré en paramètre contient une des fonctions complexes
    private boolean stringContainsComplexe(String f){
        for(int i = 0; i < complexe.size(); i++){
            if(f.contains(complexe.get(i))){
                return true;
            }
        }
        return false;
    }

    // Va dérivé et renvoyer l'expression passé en paramètre
    private String fprime(String f){
        String[] split;
        String str = "", u, v;
        Derive dr;
        int pui;
        String coeff;
        if(f.contains(symbole)){
            if(stringContainsComplexe(f)){
                if(f.startsWith("(") && f.endsWith(")")){
                    f = f.substring(1, f.length() - 1);
                    String uprime, vprime;
                    if(f.contains("/") && !f.contains("*")){ // u*v -> [((u' * v) - (u * v')) / v²]
                        split = f.split("/", 2);
                        if(split[0].isEmpty() || split[1].isEmpty()){
                            // THROW SYNTAX
                        } else {
                            u = split[0]; v = split[1];
                            dr = new Derive(u, symbole); uprime = dr.getResult();
                            dr = new Derive(v, symbole); vprime = dr.getResult();
                            if(uprime.isEmpty()){ uprime = "0"; }
                            if(vprime.isEmpty()){ vprime = "0"; }
                            if(isDouble(uprime) && Double.parseDouble(uprime) == 1){ str = str.concat("[" + v + " - "); }
                            else if(isDouble(uprime) && Double.parseDouble(uprime) == -1){ str = str.concat("[-(" + v + ") - "); }
                            else if(isDouble(uprime) && Double.parseDouble(uprime) == 0){ str = str.concat("["); }
                            else { str = str.concat("[(" + uprime + " * " + v + ") - "); }

                            if(isDouble(vprime) && Double.parseDouble(vprime) == 1){ str = str.concat(u); }
                            else if(isDouble(vprime) && Double.parseDouble(vprime) == -1){ str = str.concat("-(" + u + ")"); }
                            else { str = str.concat("(" + u + " * " + vprime + ")"); }

                            str = str.concat(" / (" + v + ")²]");
                        }
                    } else if(f.contains("*") && !f.contains("/")){ // u*v -> (u' * v) + (u * v')
                        split = f.split("\\*", 2);
                        if(split[0].isEmpty() || split[1].isEmpty()){
                            // THROW SYNTAX
                        } else {
                            u = split[0]; v = split[1];
                            dr = new Derive(u, symbole); uprime = dr.getResult();
                            dr = new Derive(v, symbole); vprime = dr.getResult();
                            if(uprime.isEmpty()){ uprime = "0"; } if(vprime.isEmpty()){ vprime = "0"; }
                            if(isDouble(uprime) && Double.parseDouble(uprime) == 1){ str = str.concat("[" + v + " + "); }
                            else if(isDouble(uprime) && Double.parseDouble(uprime) == -1){ str = str.concat("[-(" + v + ") + "); }
                            else if(isDouble(uprime) && Double.parseDouble(uprime) == 0){ str = str.concat("["); }
                            else { str = str.concat("[(" + uprime + " * " + v + ") + "); }

                            if(isDouble(vprime) && Double.parseDouble(vprime) == 1){ str = str.concat(u + "]"); }
                            else if(isDouble(vprime) && Double.parseDouble(vprime) == -1){ str = str.concat("-(" + u + ")]"); }
                            else { str = str.concat("(" + u + " * " + vprime + ")]"); }
                        }
                    } else {
                        str = fprime(f);
                    }
                } else {
                    // Fonction cos(x)
                    if(f.startsWith("cos(") && f.endsWith(")")){
                        f = f.substring(4, f.length() - 1);
                        dr = new Derive(f, symbole);
                        u = dr.getResult();
                        if(isInteger(u) && Integer.parseInt(u) == 1){ str = "-sin(" + f + ")"; }
                        else { str = "[-sin(" + f + ") * " + dr.getResult() + "]"; }

                    }  else if(f.startsWith("-cos(") && f.endsWith(")")){
                        f = f.substring(5, f.length() - 1);
                        dr = new Derive(f, symbole);
                        u = dr.getResult();
                        if(isInteger(u) && Integer.parseInt(u) == 1){ str = "sin(" + f + ")"; }
                        else { str = "[sin(" + f + ") * " + dr.getResult() + "]"; }

                    } else if(f.startsWith("sin(") && f.endsWith(")")){
                        f = f.substring(4, f.length() - 1);
                        dr = new Derive(f, symbole);
                        u = dr.getResult();
                        if(isInteger(u) && Integer.parseInt(u) == 1){ str = "cos(" + f + ")"; }
                        else { str = "[cos(" + f + ") * " + dr.getResult() + "]"; }

                    } else if(f.startsWith("-sin(") && f.endsWith(")")){
                        f = f.substring(5, f.length() - 1);
                        dr = new Derive(f, symbole);
                        u = dr.getResult();
                        if(isInteger(u) && Integer.parseInt(u) == 1){ str = "-cos(" + f + ")"; }
                        else if(isDouble(u) && Double.parseDouble(u) == -1){ str = "cos(" + f + ")"; }
                        else { str = "[-cos(" + f + ") * " + dr.getResult() + "]"; }

                    } else if(f.startsWith("tan(") && f.endsWith(")")){ // u'.(1+tan²'u))
                        f = f.substring(4, f.length() - 1);
                        dr = new Derive(f, symbole);
                        u = dr.getResult();
                        if(isDouble(u) &&  Double.parseDouble(u) == 1){ str = "(1+tan²(" + f + "))"; }
                        else if(isDouble(u) && Double.parseDouble(u) == -1){ str = "-(1+tan²(" + f + "))"; }
                        else { str = "[" + u + " * (1+tan²(" + f + "))]"; }

                    } else if(f.startsWith("sqrt(") && f.endsWith(")")){
                        f = f.substring(5, f.length() - 1);
                        dr = new Derive(f, symbole);
                        u = dr.getResult();
                        if(isDouble(u) && Double.parseDouble(u) == 1){ str = "[1 / 2 * sqrt(" + f + "))]"; }
                        else if(isDouble(u) && Double.parseDouble(u) == -1){ str = "[-1 / (2 * sqrt(" + f + "))]"; }
                        else { str = "[("+u+") / (2 * sqrt(" +f+"))]"; }

                    } else if(f.startsWith("exp(") && f.endsWith(")")){ // (e^{u})'=u' * e^{u}}
                        f = f.substring(4, f.length() - 1);
                        dr = new Derive(f, symbole);
                        u = dr.getResult();
                        if(isDouble(u) && Double.parseDouble(u) == 1){ str = "exp("+f+")"; }
                        else if(isDouble(u) && Double.parseDouble(u) == -1){ str = "-exp("+f+")"; }
                        else { str = "[" +u+ " * exp("+f+")]"; }
                    } else if(f.startsWith("ln(") && f.endsWith(")")){ // ln(u) = u'/u
                        f = f.substring(3, f.length() - 1);
                        dr = new Derive(f, symbole);
                        u = dr.getResult();
                        if(isDouble(u) && Double.parseDouble(u) == 1){ str = "[1/"+f+"]"; }
                        else if(isDouble(u) && Double.parseDouble(u) == -1){ str = "[-1/"+f+"]"; }
                        else { str = "["+u+"/"+f+"]"; }
                    } else if(f.startsWith("log(") && f.endsWith(")")){
                        f = f.substring(4, f.length() - 1);
                        dr = new Derive("ln("+f+")", symbole);
                        u = dr.getResult();
                        split = u.split("/");
                        split[1] = split[1].substring(0, split[1].length() - 1).concat(" * ln(10))]");
                        str = split[0] + " / (" + split[1];
                    }
                }
            } else {
                try {
                    // On sépare partie coefficient et puissance
                    split = f.split(symbole, 2);
                    // On vérifie que le coefficient existe sinon on part du principe qu'elle vaut 1
                    if(split[0].isEmpty()){ coeff = "1"; }
                    else if(split[0].startsWith("-")){
                        split[0] = split[0].substring(1);
                        if(split[0].isEmpty()){ coeff = "-1";}
                        else { coeff = String.valueOf(-1 * Integer.parseInt(split[0]));}
                    } else if(split[0].startsWith("(") && split[0].endsWith(")")){
                        split[0] = split[0].substring(1, split[0].length() - 1);
                        if(split[0].contains("/")){
                            coeff = split[0];
                        } else {
                            throw new Exception("Erreur syntaxe division : (a/b)");
                        }
                    } else { coeff = String.valueOf(Double.parseDouble(split[0])); }
                    // On vérifie que la puissance existe sinon on part du principe qu'elle vaut 1
                    if(split[1].isEmpty()){ pui = 1; } else { pui = Integer.parseInt(split[1]); }
                    // On vérifie que la puissance et positive sinon on lance une erreur de syntaxe, puissance négative -> 1/x
                    if(pui < 1){ /* Throw syntax error */}
                    // Si la puissance vaut 1 on retourne une valeur sans x
                    if(pui == 1){ str = coeff; }
                    // Si la puissance vaut 2 on retourne une valeur avec un x sans puissance après car x puissance 1 = x
                    else if(pui == 2){
                        if(coeff.contains("/")){ str = fractionModifier(coeff, pui); }
                        else { str = Double.parseDouble(coeff) * pui + symbole; }
                    }
                    // Sinon on retourne nouveau coefficient + symbole + (puissance-1)
                    else {

                        if(coeff.contains("/")){ str = fractionModifier(coeff, pui) + symbole + (pui-1);; }
                        else { str = Double.parseDouble(coeff) * pui + symbole+ (pui-1); }
                    }
                } catch(Exception e){
                    // Throw syntax error exception
                }
            }
        }
        return str;
    }

    // Va modifier la fraction avant une valeur x pour intégrer la puissance
    private String fractionModifier(String coeff, int pui) throws Exception {
        String str = "";
        if(coeff.contains("/")){
            String split[] = coeff.split("/", 2);
            if(isDouble(split[0])){
                Long val = Long.parseLong(split[0]) * pui;
                str = "(" + val + "/" + split[1]+")";
            } else {
                str = "( " + split[0] + " * " + pui + " / " + split[1];
            }
        } else {
            throw new Exception("Syntax error");
        }
        return str;
    }

    // Va retourner le résultat de la dérivation sous forme de chaîne de caractère
    public String getResult() {
        String result = "";
        for(int i = 0; i < listDerive.size(); i++){
            if(!listDerive.get(i).isEmpty()){
                if(listDerive.get(i).startsWith("-") && i != 0){ result = result.concat(listDerive.get(i).substring(1));}
                else { result = result.concat(listDerive.get(i)); }
                if(i < listDerive.size() - 1){
                    if(listDerive.get(i+1).startsWith("-")){ result = result.concat(" - "); } else { result = result.concat(" + ");  }
                }
            }
        }
        return result;
    }

    // Retourne la fonction f sous forme de chaîne de caractère
    public String getFonction(){
        String fonction = "";
        for(int i = 0; i < list.size(); i++){
            if(!list.get(i).isEmpty()){
                if(list.get(i).startsWith("-") && i != 0){ fonction = fonction.concat(list.get(i).substring(1));}
                else { fonction = fonction.concat(list.get(i)); }
                if(i < list.size() - 1){
                    if(list.get(i+1).startsWith("-")){ fonction = fonction.concat(" - "); } else { fonction = fonction.concat(" + ");  }
                }
            }
        }
        return fonction;
    }

    // Vérifie si la chaîne de caractère est un entier ou non
    private boolean isInteger(String str){
        try {
            Integer.parseInt(str);
            return true;
        } catch(Exception e){
            return false;
        }
    }

    // Vérifie si la chaîne de caractère est un réel ou non
    private boolean isDouble(String str){
        try {
            Double.parseDouble(str);
            return true;
        } catch(Exception e){
            return false;
        }
    }

}





