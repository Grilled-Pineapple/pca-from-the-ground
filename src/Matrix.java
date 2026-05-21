import java.util.List;

public class Matrix {
    public double[][] contents; //stores array contents

    public Matrix(int h, int w) { //initializes an hxw matrix
        contents = new double[h][w];
    }

    public void add(int row, int column, double entry) { //ZERO INDEXED!
        contents[row][column] = entry;
    }

    public void add(double[] stuff) { //inserts a list of elements into the matrix. top to bottom first
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

    public Matrix transpose() {
        Matrix result = new Matrix(width(), height());
        for (int i = 0; i < height(); i++) {
            for (int j = 0; j < width(); j++){
                result.add(j, i, contents[i][j]);
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
                    result.add(i-start,j,contents[i][j]);
                }
            }
            return result;
        } else if (axis == 1) {
            Matrix result = new Matrix(height(), end-start);
            for (int i = 0; i < height(); i++) {
                for (int j = start; j < end; j++) {
                    result.add(i,j-start,contents[i][j]);
                }
            }
            return result;
        } else {
            throw new IllegalArgumentException("0 for width, 1 for height");
        }
    }

    public static Matrix multiply(Matrix a, Matrix b) {
        if (a.width() != b.height()) {
            throw new IllegalArgumentException("invalid matrix!");
        }
        Matrix result = new Matrix(a.height(), b.width());
        for (int i = 0; i < a.height(); i++) {
            for (int j = 0; j < b.width(); j++) {
                result.add(i,j,Vector.dot(new Vector(a,i,0), new Vector(b,j,1)));
            }
        }
        return result;
    }

    //TODO: Override Equals + Hashcode

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
}
