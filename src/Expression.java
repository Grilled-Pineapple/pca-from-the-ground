import java.util.*;

public class Expression extends TreeMap<Integer, Expression.Term> {

    public static class Term {
        int power;
        double coefficient;

        public Term(int pow, double coef) {
            power = pow;
            coefficient = coef;
        }

        public void add(double coef) {
            coefficient += coef;
        }

        public void add(Term t) {
            if (t.power != power) {
                throw new IllegalArgumentException("ensure powers are the same!");
            }
            coefficient += t.coefficient;
        }
    }

    public Expression(){
        super();
    }

    public Expression(List<Double> termList){
        super();
        int currPow = 0;
        for (double d: termList) {
            put(currPow, new Term(currPow, d));
            currPow++;
        }
    }

    public void addTerm(Term t) {
        addTerm(t.power, t.coefficient);
    }

    public void addTerm(int pow, double coef) {
        if (coef != 0) {
            if (!containsKey(pow)) {
                put(pow, new Term(pow,0));
            }
            get(pow).add(coef);
        }
    }

    public Expression multiply(double d) {
        Expression retval = new Expression();
        for (Term t: values()) {
            retval.addTerm(t.power, t.coefficient*d);
        }
        return retval;
    }

    public static Expression add(List<Expression> inputs){
        Expression retval = new Expression();
        for (Expression e: inputs) {
            for (Term t: e.values()) {
                retval.addTerm(t);
            }
        }
        return retval;
    }

    public static Expression multiply(Expression expr1, Expression expr2) {
        Expression retval = new Expression();
        for (Term t: expr1.values()) {
            for (Term t2: expr2.values()){
                retval.addTerm(t.power+t2.power, t.coefficient*t2.coefficient);
            }
        }
        return retval;
    }

    public double calculate(double x) {
        double total = 0;
        for (Term t: values()) {
            total += Math.pow(x, t.power)*t.coefficient;
        }
        return total;
    }

    public Expression derivative() {
        Expression d = new Expression();
        for (Term t: values()) {
            d.addTerm(t.power-1, t.coefficient*t.power);
        }
        return d;
    }

    public List<Double> solvePositive(){
        Expression e = copy();
        List<Double> roots = new ArrayList<>();
        boolean keepGoing = true;
        while (keepGoing && e.power() > 0) {
            try {
                double root = e.findRoot();
                roots.add(root);
                if (root > 1) {
                    e = e.removeRootFromBottom(root);
                } else {
                    e = e.removeRoot(root);
                }

            } catch (Exception ex) {
                keepGoing = false;
                System.out.println(roots.size()+" roots found; error thrown!");
                ex.printStackTrace(System.out);
            }
        }
        return roots;
    }

    private double findRoot() {
        double roodimentary = findBisectionIntervalPositive(largestCoefficient()*Config.initLowerBoundMult,
                largestCoefficient());
        return newtonMethod(roodimentary-Config.bisectionTolerance,
                roodimentary+Config.bisectionTolerance*2, derivative());
        //adds a bit of wiggle room to both sides to prevent errors
    }
    //TODO: ADD MAX RECURDEPTH

    private double newtonMethod(double lb, double ub, Expression d) {
        int attempts;
        Double retval = null;
        Random r = new Random();
        double tolerance = Config.minRootToleranceProportion;
        while (retval == null && tolerance < Config.maxRootToleranceProportion) {
            attempts = 0;
            while (retval == null && attempts < Config.maxNewtonAttempts) {
                double nextCurr = lb+(ub-lb)*r.nextDouble();
                retval = newtonHelper(lb, ub, nextCurr , d, 0, tolerance);
                attempts++;
            }
            tolerance *= 10;
        }
        if (retval == null) {
            if (tolerance > Config.maxRootToleranceProportion) {
                throw new RuntimeException("root precision too low!");
            }
            throw new RuntimeException("code done sank like the titanic");
        }
        return retval;
    }

    private Double newtonHelper(double lb, double ub, double curr, Expression d, int attempts, double tolerance) {
        double next = curr-(calculate(curr)/d.calculate(curr));
        if (next < lb || next > ub || attempts > Config.maxNewtonDepth) {
            return null;
        }
        if (Math.abs(curr-next) <= Math.abs(curr)*tolerance) {
            return next;
        }
        return newtonHelper(lb, ub, next, d, attempts+1, tolerance);
    }

    private record Interval(double lower, double upper) {
        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof Interval i) {
                return Math.abs(i.lower-lower) < Config.precision &&
                        Math.abs(i.upper-upper) < Config.precision;
            }
            return false;
        }
        @Override
        public int hashCode(){
            return (int) Math.round(lower*1000+upper);
        }
    }

    private double findBisectionIntervalPositive(double j, double k) {
        if (j>k) {
            throw new IllegalArgumentException("bruh I KNEW it");
        }
        Double output = null;
        double low = j;
        double high = k;

        while (output == null && low > k*Config.minLowerBoundMult) {
            Queue<Interval> attempts = new LinkedList<>();
            Set<Interval> tried = new HashSet<>();
            boolean thereIsSomethingLeftForUs = true;
            attempts.add(new Interval(low, high));
            int ops = 0;
            while (output == null && ops<Config.maxFBSIPDepth && thereIsSomethingLeftForUs) {
                Interval ivl = attempts.remove();

                output = bisectRootPositive(ivl);
                Interval i1 = new Interval(ivl.lower, ivl.upper + (ivl.upper - ivl.lower) / 5);
                Interval i2 = new Interval(ivl.lower, ivl.upper - (ivl.upper - ivl.lower) / 3);

                if (!tried.contains(i1) && i1.upper < high) {
                    attempts.add(i1);
                    tried.add(i1);
                }
                if (!tried.contains(i2)) {
                    attempts.add(i2);
                    tried.add(i2);
                }

                thereIsSomethingLeftForUs = !attempts.isEmpty();
                ops++;
            }
            high = low;
            low *= Config.iterationLowerBoundMult;
        }
        if (output == null) {
            throw new RuntimeException("Root not found. :(");
        }
        return output;
    }

    private Double bisectRootPositive(Interval ivl) {
        return bisectRootPositive(ivl.lower, ivl.upper);
    }

    private Double bisectRootPositive(double j, double k) {
        if (j>k) {
            throw new IllegalArgumentException("bruh I KNEW it");
        }
        if (k-j < Config.bisectionTolerance) {
            if (calculate(j)*calculate(k) <= 0) {
                return j;
            }
            return null; //accounts for hitting interval edge
        }
        if (calculate(j)*calculate(k) <= 0) {
            if (calculate(j)*calculate((j+k)/2) <= 0) {
                return bisectRootPositive(j, (j+k)/2);
            }
            return bisectRootPositive((j+k)/2,k);
        }
        return null;
    }

    public Expression removeRoot(double root) { //divides the expression by x-root
        int curPow = power();
        Expression d = new Expression();
        double curTerm;
        double quotient = 0;
        while (curPow > 0) {
            curTerm = get(curPow).coefficient+quotient;
            d.addTerm(curPow-1, curTerm);
            quotient = curTerm * root;
            curPow --;
        }
        return d;
    }

    public Expression removeRootFromBottom(double root) { //suggested by Gemini (written by me)
        //I guess it makes sense that this reduces the error
        //because all future roots are smaller and thus are less affected by
        //changes to the most significant coefficient
        int curPow = 0;
        Expression d = new Expression();
        double tsTerm;
        double upQuotient = 0;
        while (curPow < power()) {
            tsTerm = -get(curPow).coefficient/root + upQuotient/root;
            d.addTerm(curPow, tsTerm);
            upQuotient = tsTerm;
            curPow ++;
        }
        return d;
    }

    private Expression copy(){
        Expression c = new Expression();
        for (Term t: values()) {
            c.addTerm(t);
        }
        return c;
    }

    public int power() {
        int k = lastKey();
        while (get(k).coefficient < Config.precision) {
            k = lowerKey(k);
        }
        return k;
    }

    public double largestCoefficient() {
        double retval = 0;
        for (Term t:values()) {
            retval = Math.max(Math.abs(t.coefficient), retval);
        }
        return retval;
    }

    @Override
    public String toString(){
        StringBuilder retval = new StringBuilder();
        for (int pow: keySet()) {
            Term t = get(pow);
            if (t.coefficient < 0) {
                if (retval.length() > 2){
                    retval.delete(retval.length()-2, retval.length());
                }
                retval.append("- ");
            }
            String s = String.valueOf(Math.abs(t.coefficient));
            if (s.length() > 4) {
                retval.append(s, 0, 4);
            } else {
                retval.append(s);
            }
            if (t.power != 0) {
                retval.append(Config.eqSymbol);
                if (t.power != 1) {
                    retval.append("^");
                    retval.append(t.power);
                }
            }
            retval.append(" + ");
        }
        retval.delete(retval.length()-2, retval.length());
        return retval.toString();
    }

    //DEBUG!
    @Deprecated
    public List<Double> solvePositiveTopDown(){
        Expression e = copy();
        List<Double> roots = new ArrayList<>();
        boolean keepGoing = true;
        while (keepGoing && e.power() > 0) {
            try {
                double root = e.findRoot();
                roots.add(root);
                e = e.removeRoot(root);
            } catch (Exception ex) {
                keepGoing = false;
                System.out.println(roots.size()+" roots found; error thrown!");
                ex.printStackTrace(System.out);
            }
        }
        return roots;
    }
}
