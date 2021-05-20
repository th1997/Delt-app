package fr.projetl3.deltapp.maths;


import android.widget.Toast;

import fr.projetl3.deltapp.Accueil;

public class Equation2Degre {
    Accueil instance;
    String equation;
    long[] tab;


    public Equation2Degre(String eq, Accueil instanceAccueil){
        equation = "";
        tab = new long[3];
        instance = instanceAccueil;
        construireTabEquation(eq);
        construireEquationSimplifier();
    }

    private void StringToTab(String str, long[] tab, boolean negatif){
        long val;
        String tmp;
        if(str.contains("x2")){
            tmp = str.replaceAll("x2", ""); tmp = tmp.replaceAll("X2", "");
            if(tmp.contains("x")){ Toast.makeText(instance, "Erreur " + tmp, Toast.LENGTH_SHORT).show(); return;}// Erreur string temporaire
            try {
                val = Long.parseLong(tmp);
                if(negatif){val *= -1;}
                tab[2] += val;
                Toast.makeText(instance, "Ajout de " + val + "dans x2", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(instance, e.getMessage(), Toast.LENGTH_SHORT).show(); return;
            }
        } else if(str.contains("x")){
            tmp = str.replaceAll("x", ""); tmp = tmp.replaceAll("X", "");
            try {
                val = Long.parseLong(tmp);
                if(negatif){val *= -1;}
                tab[1] += val;
                Toast.makeText(instance, "Ajout de " + val + "dans x", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(instance, e.getMessage(), Toast.LENGTH_SHORT).show();; return;
            }
        } else {
            try {
                val = Long.parseLong(str);
                if(negatif){val *= -1;}
                tab[0] += val;
                Toast.makeText(instance, "Ajout de " + val, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(instance, e.getMessage(), Toast.LENGTH_SHORT).show();; return;
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
                    StringToTab(tmp, tab, false);
                    tmp = "";
                } else if(before.charAt(i) == '-' && i != 0){
                    StringToTab(tmp, tab, false);
                    tmp = "-";
                } else if(before.charAt(i) == '='){
                    StringToTab(tmp, tab, false);
                    tmp = "";
                } else { //
                    tmp = tmp.concat(String.valueOf(before.charAt(i)));
                    if(i == before.length()-1){
                        StringToTab(tmp, tab, false);
                    }
                }
            }
            for(int i = 0; i < after.length(); i++){
                if(after.charAt(i) == '+'){
                    StringToTab(tmp, tab, true);
                    tmp = "";
                } else if(after.charAt(i) == '-' && i != 0){
                    StringToTab(tmp, tab, true);
                    tmp = "-";
                } else if(after.charAt(i) == '='){
                    StringToTab(tmp, tab, true);
                } else { //
                    tmp = tmp.concat(String.valueOf(after.charAt(i)));
                    if(i == after.length()-1){
                        StringToTab(tmp, tab, true);
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
        if(b > 0){equation = equation.concat("+" + b + "x "); } else {equation = equation.concat(b + "x ");}
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

