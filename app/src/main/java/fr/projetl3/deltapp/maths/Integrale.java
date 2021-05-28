package fr.projetl3.deltapp.maths;


import org.mariuszgromada.math.mxparser.Expression;

public class Integrale {

    private Expression integrale;
    private String expr;
    private Double result;

    public Integrale(String integrale) throws SyntaxException {
        try {
            this.integrale = new Expression(integrale);
            result = this.integrale.calculate();
            expr = this.integrale.getExpressionString();
        } catch (Exception e){
            throw new SyntaxException("Le programme indique qu'une erreur de syntaxe a été envoyé, veuillez vérifier la syntaxe requise pour les intégrales.");
        }
    }


    public String getExpr() {
        return expr;
    }

    public void setExpr(String expr) {
        this.expr = expr;
    }

    public void setResult(Double result) {
        this.result = result;
    }

    public Double getResult() {
        return result;
    }
}
