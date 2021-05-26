package fr.projetl3.deltapp.maths;
import org.jetbrains.annotations.NotNull;
import org.mariuszgromada.math.mxparser.*;

public class CalculBasique {
    private Expression expression;

    public CalculBasique(String calc){
        this.expression = new Expression(calc);
    }


    @NotNull
    public String toString(){
        return "Resultat de : " + expression.getExpressionString() + " = " +  expression.calculate();
    }

}
