import java.util.List;

public class Main {
    void main(String[] args){
        Expression two = new Expression(List.of(3.0,-4.0,1.0));
        System.out.println(two);
        System.out.println(two.removeRootFromBottom(1));
        System.out.println(two.removeRootFromBottom(3));

        Expression fifteen = new Expression(List.of(-206077622.362618,
                1156212342.682105,
                -2907706183.496825,
                4344957172.470668,
                -4311819646.971616,
                3008627600.896246,
                -1524312852.37848,
                570873309.140097,
                -159320263.476545,
                33133548.078592,
                -5093599.845728,
                568564.226368,
                -44623.846092,
                2325.918936,
                -72.035108,
                1.0
        ));
        System.out.println(fifteen.solvePositive());

        Expression truncation = new Expression(List.of(6.48e-07,
                -0.000203,
                0.025644,
                -1.699745,
                64.757444,
                -1457.279644,
                19159.435777,
                -140930.370395,
                541422.380489,
                -1000649.919246,
                827266.828553,
                -267573.784403,
                23072.257037,
                -373.965,
                1.0
        ));
        System.out.println(truncation.solvePositive());

        Matrix REtest = new Matrix (6,6);
        REtest.set(new double[]{1, 2, -1, 3, 1, 2,
                2, 5, 1, 8, 4, 5,
                1, 3, 3, 8, 5, 2,
                3, 8, 2, 17, 11, 8,
                1, 1, -4, -1, -3, 2,
                4, 10, 3, 19, 10, 9});
        System.out.println(REtest.nullVector());

        Matrix test = new Matrix (6,3);
        test.set(new double[]{2, 0, 1,
                1, 3, 0,
                0, 1, 4,
                2, 1, 1,
                -1, 2, 0,
                0, -1, 3});
        Matrix ata = Matrix.multiply(test.transpose(),test);
        System.out.println(ata);
        System.out.println(ata.charPolynomial());
        System.out.println(ata.charPolynomial().solvePositive());

        System.out.println("done!");

    }
}
