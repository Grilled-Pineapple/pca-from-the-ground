public class SVD {
    public Matrix u;
    public Matrix z;
    public Matrix v;
    public SVD(Matrix a) {
        if (a.height() >= a.width()) {
            Matrix ata = Matrix.multiply(a.transpose(), a);
            ata.findEigenpairsPositive();
        }
    }
}
