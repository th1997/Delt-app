package fr.projetl3.deltapp.maths;


import org.mariuszgromada.math.mxparser.Expression;

public class Integrale {

    private Expression integrale;
    private Double result;

    public Integrale(String integrale) throws SyntaxException {
        try {
            this.integrale = new Expression("int("+ integrale + ")");
            result = this.integrale.calculate();
        } catch (Exception e){
            throw new SyntaxException("Le programme indique qu'une erreur de syntaxe a été envoyé, veuillez vérifier la syntaxe requise pour les intégrales.");
        }
    }

    public Expression getIntegrale() {
        return integrale;
    }

    public Double getResult() {
        return result;
    }
}
