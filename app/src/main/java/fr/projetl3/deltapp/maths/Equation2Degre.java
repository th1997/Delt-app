package fr.projetl3.deltapp.maths;

import org.jetbrains.annotations.NotNull;

public class Equation2Degre {
    private String equation;
    private Polynome polynome;

    public Equation2Degre(String eq){
        equation = "";
        polynome = new Polynome(eq);
        construireEquationSimplifier();

    }

    public String getEquation() {
        return equation;
    }

    public void setEquation(String equation) {
        this.equation = equation;
    }

    public void setPolynome(Polynome polynome) {
        this.polynome = polynome;
    }

    public String getResult(){
        double   a = 0, b = 0, c = 0, delta;
        CalculBasique x1, x2;
        String res;

        if(polynome.getCoefficientPolynome().containsKey("2")){
            a     = polynome.getCoefficientPolynome().get("2");
        }
        if(polynome.getCoefficientPolynome().containsKey("1")){
            b     = polynome.getCoefficientPolynome().get("1");
        }
        if(polynome.getCoefficientPolynome().containsKey("0")){
            c     = polynome.getCoefficientPolynome().get("0");
        }

        if(polynome.getDegres() == 1){
            res = "X= " +((-1*c)/b);
        }
        else{
            delta = (long) Math.pow(b, 2) - (4 * a * c);

            if(delta == 0)
                res = "X= " +(-b) +" / " +(2*a) +" = " +(-b/(2*a));
            else if(delta > 0){
                x1 = new CalculBasique("(" +(-b) +"+" +Math.sqrt(delta) +")/" +(2 * a));
                x2 = new CalculBasique("(" +(-b) +"-" +Math.sqrt(delta) +")/" +(2 * a));
                res = "X1 = " +(-b) +" -√" +(Math.pow(b,2)-4*a*c) +" / " +(2*a) +" = " +x1.getResult() + "\nX2 = " +(-b) +" +√" +(Math.pow(b,2)-4*a*c) +" / " +(2*a) +" = " +x2.getResult();
            } else{
                String X1;
                String X2;

                double numerateur = Math.sqrt(-1 * delta) / 2 * a;
                if(numerateur > 0){
                    X1 = (-b/2*a) +"+" +numerateur +"i";
                    X2 = (-b/2*a) +"-" +numerateur +"i";
                }else{
                    X1 = (-b/2*a) +"+" +(-numerateur) +"i";
                    X2 = (-b/2*a) +numerateur +"i";
                }
                res = "X1 = " +(-b) +" -√" +(Math.pow(b,2)-4*a*c) +"i / " +(2*a) +" = " +X1 +"\nX2 = " +(-b) +" +√" +(Math.pow(b,2)-4*a*c) +"i / " +(2*a) +" = " +X2;
            }
        }

        return res;
    }

    private void construireEquationSimplifier(){
        double a = 0, b = 0, c = 0;
        if(polynome.getCoefficientPolynome().containsKey("2")){
            a     = polynome.getCoefficientPolynome().get("2");
        }
        if(polynome.getCoefficientPolynome().containsKey("1")){
            b     = polynome.getCoefficientPolynome().get("1");
        }
        if(polynome.getCoefficientPolynome().containsKey("0")){
            c     = polynome.getCoefficientPolynome().get("0");
        }

        if(a == 1){
            equation = equation.concat("x² ");
        }else if(a == -1){
            equation = equation.concat("-x² ");
        }else
            equation = equation.concat(simplifierDec(a,"x² "));

        if(b == 1)
            equation = equation.concat("+x ");
        else if(b == -1)
            equation = equation.concat("-x ");
        else
            equation = equation.concat(simplifierDec(b,"x "));

        equation = equation.concat(simplifierDec(c," "));
        equation = equation.concat("= 0");
    }

    private String simplifierDec(double d,String deg){
        if(deg.equals("x² ")){
            if(((long)d) != d){
                return d +deg;
            } else
                return ((long)d) +deg;
        }else{
            if(d > 0)
                if(((long)d) != d){
                    return "+" +d +deg;
                } else
                    return "+" +((long)d) +deg;
            else
                if(((long)d) != d){
                    return d +deg;
                } else
                    return ((long)d) +deg;
        }
    }

    public Polynome getPolynome() {
        return polynome;
    }

    @NotNull
    @Override
    public String toString(){
        return equation;
    }
}

