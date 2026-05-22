public class SVD {
    public Matrix u;
    public Matrix z;
    public Matrix v;
    public SVD(Matrix a) {
        Matrix ata = Matrix.multiply(a, a.transpose());
    }
}
