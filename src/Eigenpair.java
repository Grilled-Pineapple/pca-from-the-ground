public class Eigenpair implements Comparable<Eigenpair> {
    public double eigenvalue;
    public Vector eigenvector;
    public Eigenpair(double d, Vector v) {
        eigenvalue = d;
        eigenvector = v;
    }
    @Override
    public String toString(){
        return eigenvalue + "-> " + eigenvector + "\n";
    }

    @Override
    public int compareTo(Eigenpair o) {
        if (eigenvalue > o.eigenvalue) { //this avoids same-int rounding doubles becoming "equal"
            return 1;
        } else if (Math.abs(eigenvalue-o.eigenvalue) < Config.precision) {
            return 0;
        } else {
            return -1;
        }
    }
}
