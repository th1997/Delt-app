package fr.projetl3.deltapp.maths;


import android.widget.Toast;

import fr.projetl3.deltapp.Accueil;

public class Equation2Degre {
    String equation;

    ///////////////////////////////////////////////////////
    private Polynome polynome;

    public Equation2Degre(String eq){
        equation = "";
        polynome = new Polynome(eq);
        construireEquationSimplifier();

    }
    ///////////////////////////////////////////////////////

    public String result(){
        double   a = 0, b = 0, c = 0, delta;
        String x1, x2;
        String res;
        if(polynome.getCoefficientPolynome().containsKey(2)){
            a     = polynome.getCoefficientPolynome().get(2);
        }
        if(polynome.getCoefficientPolynome().containsKey(1)){
            b     = polynome.getCoefficientPolynome().get(1);
        }
        if(polynome.getCoefficientPolynome().containsKey(0)){
            c     = polynome.getCoefficientPolynome().get(0);
        }
        delta = (long) Math.pow(b, 2) - (4 * a * c);

        if(delta == 0)
            //res = "X1= -" + b + " / " + (2*a);
            res = "X1= " +(-b/(2*a)); //+ "\nApprox X1 = " + String.valueOf(deuxa)
        else if(delta > 0){
            if(Math.sqrt((double) delta) == (int) Math.sqrt((double) delta)){
                x1 = "X1 = " + (long) (-b + Math.sqrt((double) delta)) + " / " + (2 * a); //  + "\nApprox X1 = " +String.valueOf((-b-Math.sqrt(delta))/2*a)
                x2 = "X2 = " + (long) (-b - Math.sqrt((double) delta)) + " / " + (2 * a); //  + "\nApprox X2 = " +String.valueOf((-b+Math.sqrt(delta))/2*a)
            } else {
                x1 = "X1 = [" + -b + " + (V" + delta + ")] / " + (2 * a); //  + "\nApprox X1 = " +String.valueOf((-b-Math.sqrt(delta))/2*a)
                x2 = "X2 = [" + -b + " - (V" + delta + ")] / " + (2 * a); //  + "\nApprox X2 = " +String.valueOf((-b+Math.sqrt(delta))/2*a)
            }
            res = x1 + "\n" + x2;
        } else
            res = "Si vous n'avez pas encore vu les nombres imaginaires, il n'y a pas de solution pour cette équation";

        return res;
    }

    private void construireEquationSimplifier(){
        double a = 0, b = 0, c = 0;
        if(polynome.getCoefficientPolynome().containsKey(2)){
            a     = polynome.getCoefficientPolynome().get(2);
        }
        if(polynome.getCoefficientPolynome().containsKey(1)){
            b     = polynome.getCoefficientPolynome().get(1);
        }
        if(polynome.getCoefficientPolynome().containsKey(0)){
            c     = polynome.getCoefficientPolynome().get(0);
        }
        equation = equation.concat(a + "x² ");

        if(b > 0){equation = equation.concat("+" + b + "x "); } else {equation = equation.concat(b + "x ");}
        if(c > 0){equation = equation.concat("+" + c + " "); } else {equation = equation.concat(c + " ");}

        equation = equation.concat("= 0");
    }

    public Polynome getPolynome() {
        return polynome;
    }

    @Override
    public String toString(){
        return equation;
    }
}

