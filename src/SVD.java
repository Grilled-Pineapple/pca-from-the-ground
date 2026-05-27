import java.util.ArrayList;
import java.util.List;

public class SVD {
    public Matrix u;
    public Matrix z;
    public Matrix v;
    public SVD(Matrix a) {
        if (a.height() >= a.width()) {
            Matrix ata = Matrix.multiply(a.transpose(), a);
            List<Eigenpair> epl = ata.findEigenpairsPositive();
            z = new Matrix(a.height(), a.width());
            for (int i = 0; i < epl.size(); i++) {
                z.set(i,i,Math.sqrt(epl.get(i).eigenvalue));
            }

            List<Vector> vees = new ArrayList<>();
            //COME ON DOWN TO V-TOWER!!!!
            List<Vector> uuse = new ArrayList<>();
            for (Eigenpair e: epl) {
                vees.add(e.eigenvector);
            }
            for (Vector v: vees) {
                Vector u = new Vector(Matrix.multiply(a, v.matrixify(1)),0,1);
                uuse.add(u.normalized());
            }
            vees = Vector.fillBasis(vees);
            uuse = Vector.fillBasis(uuse);

            u = new Matrix(a.height(), a.height());
            for (int i = 0; i < a.height(); i++) {
                u.setColumn(uuse.get(i), i);
            }
            v = new Matrix(a.width(), a.width());
            for (int i = 0; i < a.width(); i++) {
                v.setColumn(vees.get(i), i);
            }

        } else {
            Matrix aat = Matrix.multiply(a, a.transpose());
            List<Eigenpair> epl = aat.findEigenpairsPositive();

            z = new Matrix(a.height(), a.width());
            for (int i = 0; i < epl.size(); i++) {
                z.set(i, i, Math.sqrt(epl.get(i).eigenvalue));
            }

            List<Vector> vees = new ArrayList<>();
            //WHERE WE HELP TALENT FLOWER
            List<Vector> uuse = new ArrayList<>();
            for (Eigenpair e : epl) {
                uuse.add(e.eigenvector);
            }
            for (Vector u : uuse) {
                Vector v = new Vector(Matrix.multiply(a.transpose(), u.matrixify(1)), 0, 1);
                vees.add(v.normalized());
            }
            vees = Vector.fillBasis(vees);
            uuse = Vector.fillBasis(uuse);

            u = new Matrix(a.height(), a.height());
            for (int i = 0; i < a.height(); i++) {
                u.setColumn(uuse.get(i), i);
            }
            v = new Matrix(a.width(), a.width());
            for (int i = 0; i < a.width(); i++) {
                v.setColumn(vees.get(i), i);
            }
        }
        System.out.println(u.round(3));
        System.out.println(z.round(3));
        System.out.println(v.round(3));

        System.out.println(a);

        System.out.println(Matrix.multiply(Matrix.multiply(u, z), v.transpose()).round(8));
    }
}
