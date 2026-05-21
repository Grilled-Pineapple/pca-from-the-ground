public class Main {
    public void main(String[] args){
        Matrix myTrix = new Matrix(2,2);
        myTrix.add(new double[] {2, 0, 0, 2});
        System.out.println(Matrix.multiply(myTrix,myTrix)); //expecting {1,1,0,0} again
    }
}
