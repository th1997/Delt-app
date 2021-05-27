package fr.projetl3.deltapp.maths;

import java.util.ArrayList;

public class Derive {

    private String expression; // Contient l'expression entré par l'utilisateur
    private String symbole; // Contient le symbole de dérivation
    private String result; // Contient le résultat dérivé
    private ArrayList<String> list; // Contient la liste des expression
    private ArrayList<String> listDerive; // Contient la liste des parties dérivé
    private ArrayList<String> complexe; // Contient toutes les fonctions "complexes" exemple fonction cosinus

    // Constructeur
    public Derive(String expression){
        this.expression = expression;
        this.list = new ArrayList<>();
        this.listDerive = new ArrayList<>();
        getSymboleFromExpression();
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
        complexe.add("(");
        complexe.add(")");
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
        String str = "";
        int coeff, pui;
        if(f.contains(symbole)){
            if(stringContainsComplexe(f)){
                if(f.startsWith("(") && f.endsWith(")")){
                    // Fonction composer
                } else {
                    // Fonction cos(x)
                    if(f.startsWith("cos(") && f.endsWith(")")){
                        f = f.substring(4, f.length() - 1);
                        Derive dr = new Derive(f);
                        String u = dr.getResult();
                        if(isInteger(u) && Integer.parseInt(u) == 1){ str = "-sin(" + f + ")"; }
                        else { str = "[-sin(" + f + ") * " + dr.getResult() + "]"; }
                    }  else if(f.startsWith("-cos(") && f.endsWith(")")){
                        f = f.substring(5, f.length() - 1);
                        Derive dr = new Derive(f);
                        String u = dr.getResult();
                        if(isInteger(u) && Integer.parseInt(u) == 1){ str = "sin(" + f + ")"; }
                        else { str = "[sin(" + f + ") * " + dr.getResult() + "]"; }
                    } else if(f.startsWith("sin(") && f.endsWith(")")){
                        f = f.substring(4, f.length() - 1);
                        Derive dr = new Derive(f);
                        String u = dr.getResult();
                        if(isInteger(u) && Integer.parseInt(u) == 1){ str = "cos(" + f + ")"; }
                        else { str = "[cos(" + f + ") * " + dr.getResult() + "]"; }
                    } else if(f.startsWith("-sin(") && f.endsWith(")")){
                        f = f.substring(5, f.length() - 1);
                        Derive dr = new Derive(f);
                        String u = dr.getResult();
                        if(isInteger(u) && Integer.parseInt(u) == 1){ str = "-cos(" + f + ")"; }
                        else { str = "[-cos(" + f + ") * " + dr.getResult() + "]"; }
                    }
                }
            } else {
                try {
                    // On sépare partie coefficient et puissance
                    split = f.split(symbole, 2);
                    // On vérifie que le coefficient existe sinon on part du principe qu'elle vaut 1
                    if(split[0].isEmpty()){ coeff = 1; } else { coeff = Integer.parseInt(split[0]); }
                    // On vérifie que la puissance existe sinon on part du principe qu'elle vaut 1
                    if(split[1].isEmpty()){ pui = 1; } else { pui = Integer.parseInt(split[1]); }
                    // On vérifie que la puissance et positive sinon on lance une erreur de syntaxe, puissance négative -> 1/x
                    if(pui < 1){ /* Throw syntax error */}
                    // Si la puissance vaut 1 on retourne une valeur sans x
                    if(pui == 1){ str = String.valueOf(coeff * pui);}
                    // Si la puissance vaut 2 on retourne une valeur avec un x sans puissance après car x puissance 1 = x
                    else if(pui == 2){ str = (coeff * pui) + symbole;}
                    // Sinon on retourne nouveau coefficient + symbole + (puissance-1)
                    else {str =  (coeff * pui) + symbole + (pui-1); }
                } catch(Exception e){
                    // Throw syntax error exception
                }
            }
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

    private boolean isInteger(String str){
        try {
            Integer.parseInt(str);
            return true;
        } catch(Exception e){
            return false;
        }
    }

}

