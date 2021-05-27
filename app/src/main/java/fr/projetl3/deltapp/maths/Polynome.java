package fr.projetl3.deltapp.maths;



import org.mariuszgromada.math.mxparser.Expression;

import java.util.HashMap;

public class Polynome {
    private String expression;
    private HashMap<Integer, Double> coefficientPolynome;
    private int degres;

    public Polynome(String expr){
        expression = expr;
        coefficientPolynome = new HashMap<>();
        construireTabEquation();
    }

    private void construireTabEquation() {
        //
        String equation = expression.replaceAll(" ", "");
        int count = (int) equation.chars().filter(ch -> ch == '=').count();

        // Requis 1 seules égalité
        String tmp = "";

        if (count == 1) {
            String[] split = equation.split("=", 2);
            String before = split[0] + "=";
            String after = split[1];

            for (int i = 0; i < before.length(); i++) {
                if (before.charAt(i) == '+') {
                    StringToTab(tmp, false);
                    tmp = "";
                } else if (before.charAt(i) == '-' && i != 0) {
                    StringToTab(tmp, false);
                    tmp = "-";
                } else if (before.charAt(i) == '=') {
                    StringToTab(tmp, false);
                    tmp = "";
                } else { //
                    tmp = tmp.concat(String.valueOf(before.charAt(i)));
                    if (i == before.length() - 1)
                        StringToTab(tmp, false);
                }
            }

            for (int i = 0; i < after.length(); i++) {
                if (after.charAt(i) == '+') {
                    StringToTab(tmp, true);
                    tmp = "";
                } else if (after.charAt(i) == '-' && i != 0) {
                    StringToTab(tmp, true);
                    tmp = "-";
                } else if (after.charAt(i) == '=') {
                    StringToTab(tmp, true);
                } else {
                    tmp = tmp.concat(String.valueOf(after.charAt(i)));
                    if (i == after.length() - 1)
                        StringToTab(tmp, true);
                }
            }
        }
    }

    private void StringToTab(String str, boolean negatif){
        double val,  lastval; int pui;
        String tmp = ""; String[] split = {};
        Expression exp;

        if(str.contains("x") || str.contains("X")){
            tmp = str.replaceAll("X", "x");
            split = tmp.split("x", 2);

            if(split[0].isEmpty()){
                val = 1;
            }else {
                exp = new Expression(split[0]);
                val = exp.calculate();
            }

            if(split[1].isEmpty()){
                pui = 1;
            } else {
                pui = Integer.parseInt(split[1]);
            }
        } else {
            exp = new Expression(str);
            val = exp.calculate();
            pui = 0;

        }
        if(val != 0){
            if(negatif)
                val *= -1;
            if(coefficientPolynome.containsKey(pui)){
                lastval = coefficientPolynome.get(pui);
                coefficientPolynome.put(pui,lastval + val);}
            else {
                coefficientPolynome.put(pui,val);
            }
            System.out.println("Ajout de " + val + " à la puissance " + pui) ;
        }
        if(pui >= degres){
            degres = pui;
        }
    }

    public int getDegres() {
        return degres;
    }

    public HashMap<Integer, Double> getCoefficientPolynome() {
        return coefficientPolynome;
    }
}





