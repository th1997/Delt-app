package fr.projetl3.deltapp.maths;

import android.widget.Toast;

import org.mariuszgromada.math.mxparser.Expression;

import java.util.ArrayList;

public class Polynome {
    private String expression;

    public ArrayList<Double> getCoefficientPolynome() {
        return coefficientPolynome;
    }

    private ArrayList<Double> coefficientPolynome;

    public Polynome(String expr){
        expression = expr;
        coefficientPolynome = new ArrayList<Double>();
        construireTabEquation();
        System.out.println(this.getCoefficientPolynome().get(0));
        System.out.println(this.getCoefficientPolynome().get(1));
        System.out.println(this.getCoefficientPolynome().get(2));
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
        long val = 0;
        String tmp = "";
        Expression expr = null;

        if(str.contains("x") || str.contains("X")){
            tmp = str.replaceAll("X", "x");
            String[] split = tmp.split("x", 2);

            expr = new Expression(split[0]);
            coefficientPolynome.add(expr.calculate());
            val = Long.parseLong(split[0]);

        } else {
            val = Long.parseLong(str);

        }
        if(negatif)
            val *= -1;
        coefficientPolynome.set(coefficientPolynome.size()-1,coefficientPolynome.get(coefficientPolynome.size()-1) + val);
    }
}
