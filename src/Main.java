import java.util.List;

public class Main {
    public void main(String[] args){
        Matrix myTrix = new Matrix(2,2);
        myTrix.add(new double[] {2, 0, 0, 1});
        System.out.println(Matrix.multiply(myTrix,myTrix)); //expecting {1,1,0,0} again

        Expression myExpr = new Expression(List.of(2.0,-3.0,1.0));
        Expression myExpr2 = new Expression(List.of(6.0,-5.0,1.0));
        Expression testExpr = new Expression(List.of(720.0, -1764.0, 1624.0, -735.0, 175.0, -21.0, 1.0));

        System.out.println(myExpr.calculate(1200));

        System.out.println(myExpr.removeRoot(1));

        System.out.println(testExpr.solvePositive());

        System.out.println("done!");

    }
}
