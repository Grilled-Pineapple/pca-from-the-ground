import java.util.ArrayList;

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

    public void setZeroes(int n) {
        clear();
        for (int i = 0; i < n; i++) {
            add(0.0);
        }
    }
}
