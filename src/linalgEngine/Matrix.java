package linalgEngine;

import java.util.*;

public class Matrix {
    public double[][] contents; //stores array contents

    public Matrix(int h, int w) { //initializes an hxw matrix
        contents = new double[h][w];
    }

    public void set(int row, int column, double entry) { //ZERO INDEXED!
        contents[row][column] = entry;
    }

    public void set(double[] stuff) { //inserts a list of elements into the matrix. top to bottom first
        if (stuff.length != width()*height()){
            throw new IllegalArgumentException("one element per cell!");
        }
        int idx = 0;
        for (int i = 0; i < height(); i++) {
            for (int j = 0; j < width(); j++) {
                contents[i][j] = stuff[idx];
                idx++;
            }
        }
    }

    public void set(List<Double> stuff) { //inserts a list of elements into the matrix. top to bottom first
        if (stuff.size() != width()*height()){
            throw new IllegalArgumentException("one element per cell!");
        }
        int idx = 0;
        for (int i = 0; i < height(); i++) {
            for (int j = 0; j < width(); j++) {
                contents[i][j] = stuff.get(idx);
                idx++;
            }
        }
    }

    public void setRow(Vector v, int row) {
        if (v.size() != width()) {
            throw new IllegalArgumentException("mismatched dimensions: "+v.size()+" and "+width());
        }
        for (int i = 0; i < v.size(); i++) {
            set(row, i, v.get(i));
        }
    }

    public void setColumn(Vector v, int col) {
        if (v.size() != height()) {
            throw new IllegalArgumentException("mismatched dimensions: "+v.size()+" and "+height());
        }
        for (int i = 0; i < v.size(); i++) {
            set(i, col, v.get(i));
        }
    }

    public void swapRows(int r1, int r2) {
        Vector v1 = new Vector(this, r1, 0);
        Vector v2 = new Vector(this, r2, 0);
        setRow(v1, r2);
        setRow(v2, r1);
    }

    public Matrix transpose() {
        Matrix result = new Matrix(width(), height());
        for (int i = 0; i < height(); i++) {
            for (int j = 0; j < width(); j++){
                result.set(j, i, contents[i][j]);
            }
        }
        return result;
    }

    public Matrix slice(int axis, int start, int end) {//start incl, end excl, zero-indexed
        //0 cuts horizontally, 1 cuts vertically
        if (axis == 0) {
            Matrix result = new Matrix(end-start, width());
            for (int i = start; i < end; i++) {
                for (int j = 0; j < width(); j++) {
                    result.set(i-start,j,contents[i][j]);
                }
            }
            return result;
        } else if (axis == 1) {
            Matrix result = new Matrix(height(), end-start);
            for (int i = 0; i < height(); i++) {
                for (int j = start; j < end; j++) {
                    result.set(i,j-start,contents[i][j]);
                }
            }
            return result;
        } else {
            throw new IllegalArgumentException("0 for width, 1 for height");
        }
    }

    public List<Eigenpair> findEigenpairsPositive() {
        List<Double> eigenvalues = charPolynomial().solvePositive();
        List<Eigenpair> eigenpairs = new ArrayList<>();
        for (double ev: eigenvalues) {
            Vector solVector = sum(this, identity(width()).multiply(-ev)).nullVector(true);
            eigenpairs.add(new Eigenpair(ev, solVector.normalized()));
            //it is assumed that in SVD, duplicate pairs are extremely unlikely
        }
        eigenpairs.sort(new ReverseEigenComparator());
        return eigenpairs;
    }

    private static class ReverseEigenComparator implements Comparator<Eigenpair> {
        @Override
        public int compare(Eigenpair o1, Eigenpair o2) {
            return -o1.compareTo(o2);
        }
    }

    public List<Vector> nullSpace() {
        return nullSpace(Config.precision);
    }

    private List<Vector> nullSpace(double threshold){ //extracts ALL nullspace vectors
        Matrix m = rowEchelon();
        List<Vector> valid = new ArrayList<>();
        int mostPivot = width();
        valid.add(Vector.zeroes(width())); //duplicate vector every time a new freedom opportunity comes up

        for (int i = height()-1; i >= 0; i--) {

            Vector v = new Vector(m, i, 0);
            while (i > 0 && v.leftmostNonZeroIndex(threshold) == null) {
                i--;
                v = new Vector(m,i,0);
            }

            if (i == 0) {
                v = new Vector(m,i,0);
                if (v.leftmostNonZeroIndex() == null) {
                    i--;
                }
            }

            if (i >= 0) {
                while (mostPivot-v.leftmostNonZeroIndex() > 1) {
                    valid.getLast().set(mostPivot-1, 1.0);
                    valid.add(Vector.zeroes(width()));
                    mostPivot--;
                }
                mostPivot = v.leftmostNonZeroIndex();

                for (Vector vee: valid) { //heh
                    double dot = Vector.dot(vee, v);
                    vee.set(mostPivot,-dot/v.get(mostPivot));
                }
            }
        }
        valid.removeLast();

        List<Vector> norm = new ArrayList<>();

        for (Vector vee: valid) {
            norm.add(vee.normalized());
        }

        return norm;
    }

    public Vector nullVector(boolean hasNullspace){ //extracts one nullspace vector
        double attemptThreshold = Config.precision;
        List<Vector> k = new ArrayList<>();
        while ((hasNullspace ||attemptThreshold < Config.maxZeroThresholdNullspace*10) && k.isEmpty()) {
            k = nullSpace(attemptThreshold);
            attemptThreshold *= 10;
        }
        return k.getFirst();
    }

    public Matrix rowEchelon(){
        Matrix m = copy();
        int bonusZeroes = 0;
        for (int complete = 0; complete < Math.min(height(),width()); complete++) {
            Vector save = new Vector(m,complete,0);
            while (complete+bonusZeroes < height() &&
                    new Vector(m, complete + bonusZeroes, 1).isZeroPast(complete)) {
                bonusZeroes++;
            }
            if (complete + bonusZeroes < height()) {
                for (int j = complete; j < height()-1; j++) {
                    Vector one = save;
                    while (j < height()-1 && m.contents[j+1][complete+bonusZeroes] == 0) {
                        j++;
                    }
                    if (j < height()-1) {
                        Vector two = new Vector(m, j+1, 0);
                        save = two;
                        two = Vector.multiply(two, -one.get(complete+bonusZeroes)
                                /two.get(complete+bonusZeroes));
                        two = Vector.sum(one, two);
                        m.setRow(two, j+1);
                        if (two.isZero()) {
                            m.swapRows(j+1, height()-1);
                            j--;
                        }
                    }
                }
            }
        }
        return m;
    }

    public Expression charPolynomial(){
        if (!isSquare()) {
            throw new IllegalArgumentException("Must be a square matrix!");
        }
        return multiply(-1).charPolynomial(identity(height()));
    }

    private Expression charPolynomial(Matrix lambdas) {
        if (height() == 1){
            return new Expression(List.of(contents[0][0],lambdas.contents[0][0]));
        }
        List<Expression> exprList = new ArrayList<>();
        for (int i = 0; i < height(); i++) {
            Expression ts = new Expression(List.of(contents[i][0],lambdas.contents[i][0]));
            exprList.add(Expression.multiply(
                    ts,minor(i, 0).charPolynomial(lambdas.minor(i,0))).multiply(
                            Math.pow(-1,i)));
        }
        return Expression.add(exprList);
    }

    public Matrix minor(int row, int col){ //index of removal
        Matrix retval = new Matrix(height()-1, width()-1);
        for (int i = 0; i < height()-1; i++) {
            for (int j = 0; j < width()-1; j++) {
                int p = i;
                int q = j;
                if (i >= row) {
                    p++;
                }
                if (j >= col) {
                    q++;
                }
                retval.contents[i][j] = contents[p][q];
            }
        }
        return retval;
    }

    private Matrix multiply(double k){ //scales by k
        Matrix retval = new Matrix(height(), width());
        for (int i = 0; i < height(); i++) {
            for (int j = 0; j < width(); j++) {
                retval.contents[i][j] = k*contents[i][j];
            }
        }
        return retval;
    }

    public static Matrix sum(Matrix a, Matrix b) { //might make a dynamic method to do this too
        if (a.width() != b.width() || a.height() != b.height()) {
            throw new IllegalArgumentException("must be same shape to sum!");
        }
        Matrix result = new Matrix(a.height(), a.width());
        for (int i = 0; i < a.height(); i++) {
            for (int j = 0; j < a.width(); j++) {
                result.contents[i][j] = a.contents[i][j]+b.contents[i][j];
            }
        }
        return result;
    }

    public static Matrix multiply(Matrix a, Matrix b) {
        if (a.width() != b.height()) {
            throw new IllegalArgumentException("non-matching output/input dimensions!");
        }
        Matrix result = new Matrix(a.height(), b.width());
        for (int i = 0; i < a.height(); i++) {
            for (int j = 0; j < b.width(); j++) {
                result.set(i,j,Vector.dot(new Vector(a,i,0), new Vector(b,j,1)));
            }
        }
        return result;
    }

    public static Matrix identity(int n){
        Matrix retval = new Matrix(n, n);
        for (int i = 0; i < n; i++){
            retval.contents[i][i] = 1;
        }
        return retval;
    }

    public Matrix copy(){
        Matrix copy = new Matrix(height(), width());
        for (int i = 0; i < height(); i++) {
            for (int j = 0; j < width(); j++) {
                copy.set(i,j,contents[i][j]);
            }
        }
        return copy;
    }

    //TODO: Override Equals + Hashcode

    public boolean isSquare() {
        return height() == width();
    }

    @Override
    public String toString() { //for ease of printing
        StringBuilder construct = new StringBuilder();
        for (int i = 0; i < Math.min(height(), Config.maxPrintSize); i++) {
            construct.append("[");
            for (int j = 0; j < Math.min(width(), Config.maxPrintSize); j++) {
                construct.append(String.valueOf(contents[i][j])); //TODO: ALIGN LENGTH
                construct.append(" ");
            }
            construct.deleteCharAt(construct.length()-1);
            if (width() > Config.maxPrintSize) {
                construct.append("... ");
            }
            construct.append("]");
            construct.append("\n");
        }
        if (height() > Config.maxPrintSize) {
            construct.append("[ ... ]");
            construct.append("\n");
        }
        return construct.toString();
    }

    public int height(){
        return contents.length;
    }

    public int width() {
        return contents[0].length;
    }

    public Matrix round(int sf){
        Matrix copy = new Matrix(height(), width());
        for (int i = 0; i < height(); i++) {
            for (int j = 0; j < width(); j++) {
                copy.set(i,j,round(contents[i][j], sf));
            }
        }
        return copy;
    }

    private double round(double num, int sf) {
        num = num*Math.pow(10,sf);
        num = (double) Math.round(num);
        num = num/Math.pow(10,sf);
        return num;
    }
}
