package fr.projetl3.deltapp.maths;
import org.jetbrains.annotations.NotNull;
import org.mariuszgromada.math.mxparser.*;

public class CalculBasique {
    private Expression expression;
    private String expr;
    private double result;

    public CalculBasique(String calc){
        this.expression = new Expression(calc);
        expr = expression.getExpressionString();
        result = expression.calculate();
    }

    public String getExpr() {
        return expr;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public double getResult() {
        return result;
    }

    public void setResult(double result) {
        this.result = result;
    }

    @NotNull
    public String toString(){
        return "Resultat de : " + expression.getExpressionString() + " = " +  expression.calculate();
    }

}
