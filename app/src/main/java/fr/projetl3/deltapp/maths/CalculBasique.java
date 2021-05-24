package fr.projetl3.deltapp.maths;
import org.mariuszgromada.math.mxparser.*;

public class CalculBasique {
    Expression expression;

    public CalculBasique(String calc){
        this.expression = new Expression(calc);
    }

    public String result(){
        return "Resultat de : " + expression.getExpressionString() + " = " +  expression.calculate();
    }

}
