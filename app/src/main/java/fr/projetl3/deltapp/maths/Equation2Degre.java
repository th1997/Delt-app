package fr.projetl3.deltapp.maths;


public class Equation2Degre {
    String equation;
    long[] tab;


    public Equation2Degre(String eq){
        equation = "";
        tab = new long[3];
        construireTabEquation(eq);
        construireEquationSimplifier();
    }

    private void StringToTab(String str, long[] tab){
        long val;
        String tmp;
        if(str.contains("x2")){
            tmp = str.replaceAll("x2", "");
            if(str.contains("x")){ System.out.println("Erreur string tmp"); return;}// Erreur string temporaire
            try {
                val = Long.parseLong(tmp);
                tab[2] += val;
            } catch (Exception e) {
                System.out.println(e.getMessage()); return;
            }
        } else if(str.contains("x")){
            tmp = str.replaceAll("x", "");
            try {
                val = Long.parseLong(tmp);
                tab[1] += val;
            } catch (Exception e) {
                System.out.println(e.getMessage()); return;
            }
        } else {
            try {
                val = Long.parseLong(str);
                tab[0] += val;
            } catch (Exception e) {
                System.out.println(e.getMessage()); return;
            }
        }
    }

    private void construireTabEquation(String eq){
        //
        String equation = eq.replaceAll(" ", "");
        int count = (int) equation.chars().filter(ch -> ch == '=').count();
        // Requis 1 seules égalité
        String tmp = "";
        if(equation.contains("=") && count == 1){
            String[] split = equation.split("=", 2);
            String before = split[0] + "=";
            String after = split[1];

            for(int i = 0; i < before.length(); i++){
                if(before.charAt(i) == '+'){
                    StringToTab(tmp, tab);
                    tmp = "";
                } else if(before.charAt(i) == '-' && i != 0){
                    StringToTab(tmp, tab);
                    tmp = "-";
                } else if(before.charAt(i) == '='){
                    StringToTab(tmp, tab);
                } else { //
                    tmp = tmp.concat(String.valueOf(before.charAt(i)));
                    if(i == before.length()-1){
                        StringToTab(tmp, tab);
                    }
                }
            }
            tmp = "";
            for(int i = 0; i < after.length(); i++){
                if(after.charAt(i) == '+'){
                    StringToTab(tmp, tab);
                    tmp = "";
                } else if(after.charAt(i) == '-' && i != 0){
                    StringToTab(tmp, tab);
                    tmp = "-";
                } else if(after.charAt(i) == '='){
                    StringToTab(tmp, tab);
                } else { //
                    tmp = tmp.concat(String.valueOf(after.charAt(i)));
                    if(i == after.length()-1){
                        StringToTab(tmp, tab);
                    }
                }
            }
        } else {
            System.out.println("Erreur");
        }

    }

    private void construireEquationSimplifier(){
        long a = tab[2], b = tab[1], c = tab[0];
        equation = equation.concat(a + "x² ");
        if(b > 0){equation = equation.concat("+" + b + "x "); } else {equation = equation.concat(b + " ");}
        if(c > 0){equation = equation.concat("+" + c + " "); } else {equation = equation.concat(c + " ");}
        equation = equation.concat("= 0");

    }

    public String result(){
        long a, b, c, delta;
        String x1, x2;
        String res;
        a = tab[2]; b = tab[1]; c = tab[0];
        delta = (long) Math.pow(b, 2) - (4 * a * c);
        if(delta == 0){
            res = "X1= -" + b + " / " + (2*a); // + "\nApprox X1 = " + String.valueOf(deuxa)
        } else if(delta > 0){
            if(Math.sqrt((double) delta) == (int) Math.sqrt((double) delta)){
                x1 = "X1 = " + (long) (-b + Math.sqrt((double) delta)) + " / " + (2 * a); //  + "\nApprox X1 = " +String.valueOf((-b-Math.sqrt(delta))/2*a)
                x2 = "X2 = " + (long) (-b - Math.sqrt((double) delta)) + " / " + (2 * a); //  + "\nApprox X2 = " +String.valueOf((-b+Math.sqrt(delta))/2*a)
                res = x1 + "\n" + x2;
            } else {
                x1 = "X1 = [" + -b + " + (V" + delta + ")] / " + (2 * a); //  + "\nApprox X1 = " +String.valueOf((-b-Math.sqrt(delta))/2*a)
                x2 = "X2 = [" + -b + " - (V" + delta + ")] / " + (2 * a); //  + "\nApprox X2 = " +String.valueOf((-b+Math.sqrt(delta))/2*a)
                res = x1 + "\n" + x2;
            }
        } else {
            res = "Si vous n'avez pas encore vu les nombres imaginaires, il n'y a pas de solution pour cette équation";
        }
        return res;
    }

    @Override
    public String toString(){
        return equation;
    }

}

