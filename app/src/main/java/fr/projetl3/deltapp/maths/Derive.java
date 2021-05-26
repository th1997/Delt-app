package fr.projetl3.deltapp.maths;

import org.mariuszgromada.math.mxparser.Expression;

public class Derive {

    private Expression expression;

    public Derive(String expr){
        this.expression = new Expression(expr);
    }

    public String toString(){
        return "Dérivé de : " + expression.getExpressionString() + " = " +  expression.calculate();
    }


}
