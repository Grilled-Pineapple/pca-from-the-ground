package linalgEngine;

public class Config {
    public static int maxPrintSize = 100;
    public static char eqSymbol = 'λ';
    public static double precision = 0.0000000001; //might need to lower this to detect nullspace
    public static double maxZeroThresholdNullspace = 0.001;
    public static double orthogonalityThreshold = 0.0001;
    public static double normThreshold = 0.00001;
    public static double bisectionTolerance = 0.1;
    public static int maxFBSIPDepth = 1000;
    public static int maxNewtonDepth = 100;
    public static int maxNewtonAttempts = 20;
    public static double minRootToleranceProportion = 0.000000000000000000001;
    public static double maxRootToleranceProportion = 0.01;
    public static double minLowerBoundMult = 0.00000000000000001;
    public static double bisectIterationMult = 0.95;

}
