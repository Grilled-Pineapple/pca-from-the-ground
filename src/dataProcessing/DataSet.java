package dataProcessing;

import java.util.ArrayList;
import java.util.List;

public class DataSet {


    //using nested classes is probably better here
    public static class Entry{
        public final double data;
        public final Day day;
        public final Field field;
        public Double demeaned;
        public Entry(double dat, Day da, Field fiel){
            data = dat;
            day = da;
            field = fiel;
        }
        public void setDemeaned(double demesne) {
            demeaned = demesne;
        }

        @Override
        public String toString() {
            if (demeaned != null) {
                return data + " {"+demeaned+"}";
            }
            return String.valueOf(data);
        }
    }

    public static class Group<T> extends ArrayList<Entry> {
        public T header;

        public Group(T everybodyLoves) {
            super();
            header = everybodyLoves;
        }

        @Deprecated
        public void setEntries(List<Double> debug) {
            for (double d: debug) {
                add(new Entry(d, null, null));
            }
        }

        @Override
        public String toString() {
            return header.toString() + ": " + super.toString();
        }
    }

    public static class Day extends Group<Integer> {
        public Day(Integer in) {
            super(in);
        }
    }

    public static class Field extends Group<String> {
        public Field(String st) {
            super(st);
        }

        public void zScorify(){
            double tot = 0;
            for (Entry entry: this) {
                tot += entry.data;
            }
            double mean = tot/this.size();

            tot = 0;
            for (Entry entry: this) {
                tot += Math.pow(entry.data - mean, 2);
            }
            tot /= this.size() - 1;
            double sd = Math.sqrt(tot);

            for (Entry entry: this) {
                entry.setDemeaned((entry.data - mean)/sd);
            }
        }
    }
}
