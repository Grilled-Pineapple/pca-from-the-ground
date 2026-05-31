package linalgEngine;

import java.util.ArrayList;
import java.util.List;

public class Vector extends ArrayList<Double> {
    public Vector(Matrix m, int idx, int axis) {
        //0 cuts horizontally, 1 cuts vertically
        if (axis == 0) {
            for (int j = 0; j < m.width(); j++) {
                add(m.contents[idx][j]);
            }
        } else if (axis == 1) {
            for (int i = 0; i < m.height(); i++) {
                add(m.contents[i][idx]);
            }
        } else {
            throw new IllegalArgumentException("0 for width, 1 for height");
        }
    }

    public Vector() {
        super();
    }

    public static Vector sum(Vector v1, Vector v2) {
        if (v1.size() != v2.size()) {
            throw new IllegalArgumentException("vector input sizes must match!");
        }
        Vector v3 = new Vector();
        for (int i = 0; i < v1.size(); i++) {
            v3.add(v1.get(i)+v2.get(i));
        }
        return v3;
    }

    public static Vector multiply(Vector vector, double scalar) {
        Vector v1 = new Vector();
        for (double d: vector) {
            v1.add(d*scalar);
        }
        return v1;
    }

    public static Vector zeroes(int size){
        Vector v = new Vector();
        v.setZeroes(size);
        return v;
    }

    public Vector copy(){
        Vector rv = new Vector();
        rv.addAll(this);
        return rv;
    }

    public Vector normalized(){
        double norm = norm();
        Vector retVec = new Vector();
        for (Double d: this) {
            retVec.add(d/norm);
        }
        return retVec;
    }

    public boolean isNormal(){
        return Math.abs(norm()-1) < Config.normThreshold;
    }

    public boolean isZero() {
        return Math.abs(norm()) < Config.normThreshold;
    }

    public Matrix matrixify(int axis) {
        //0: horizontal (row), 1: vertical (col)
        if (axis == 0) {
            Matrix m = new Matrix(1, size());
            m.setRow(this, 0);
            return m;
        } else if (axis == 1) {
            Matrix m = new Matrix(size(), 1);
            m.setColumn(this, 0);
            return m;
        } else {
            throw new IllegalArgumentException("axis not supported");
        }
    }

    private Vector truncate(int idx) { //shaves off all entries before idx (exclusive)
        Vector revec = new Vector();
        for (int i = idx; i < size(); i++) {
            revec.add(get(i));
        }
        return revec;
    }

    public static List<Vector> fillBasis(List<Vector> orthogonals){
        //maintains order! hopefully.
        if (!orthonormal(orthogonals)) {
            throw new IllegalArgumentException("orthonormalize vectors first!");
        }
        Matrix tp = new Matrix(orthogonals.size(),orthogonals.getFirst().size());
        int i = 0;
        for (Vector v: orthogonals) {
            tp.setRow(v, i);
            i++;
        }

        List<Vector> retval = new ArrayList<>();
        retval.addAll(orthogonals);
        retval.addAll(tp.nullSpace());
        return retval;
    }

    public boolean isZeroPast(int idx) {
        return Math.abs(truncate(idx).norm()) < Config.normThreshold;
    }

    public double norm(){
        double result = 0;
        for (double d: this) {
            result += d*d;
        }
        return Math.sqrt(result);
    }

    public static double dot(Vector v1, Vector v2) {
        if (v1.size() != v2.size()) {
            throw new IllegalArgumentException("vector input sizes must match!");
        }
        double result = 0;
        for (int i = 0; i<v1.size(); i++) {
            result += v1.get(i)*v2.get(i);
        }
        return result;
    }

    public static boolean orthogonal(Vector v1, Vector v2) {
        return dot(v1, v2) < v1.norm()*v2.norm()*Config.orthogonalityThreshold;
    }

    public static boolean orthonormal(List<Vector> orthonormals) {

        for (int i = 0; i < orthonormals.size(); i++) {
            for (int j = i+1; j < orthonormals.size(); j++) {
                if (!orthogonal(orthonormals.get(i), orthonormals.get(j))) {
                    return false;
                }
            }
        }
        for (Vector v: orthonormals) {
            if (!v.isNormal()) {
                return false;
            }
        }
        return true;
    }

    public void setZeroes(int n) {
        clear();
        for (int i = 0; i < n; i++) {
            add(0.0);
        }
    }

    public Integer leftmostNonZeroIndex() {
        return leftmostNonZeroIndex(Config.precision);
    }

    public Integer leftmostNonZeroIndex(double threshold) {
        int idx = 0;
        while (Math.abs(get(idx)) < threshold) {
            idx++;
            if (idx >= size()) {
                return null;
            }
        }
        return idx;
    }
}
