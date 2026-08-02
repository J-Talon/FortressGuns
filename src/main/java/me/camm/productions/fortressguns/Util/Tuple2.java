package me.camm.productions.fortressguns.Util;

public class Tuple2<A,B> {

    private A a;
    private B b;

    public Tuple2(A a, B b) {
        this.a = a;
        this.b = b;
    }

    //yes I know technically tuples aren't supposed to have a set function
    //cause they're supposed to be final... so really this should be called a
    //duet or something. I'm bad at names so I just call it a tuple.
    public A getA() {
        return a;
    }

    public void setA(A a) {
        this.a = a;
    }

    public B getB() {
        return b;
    }

    public void setB(B b) {
        this.b = b;
    }

    public String toString() {
        return "<"+a+", "+b+">";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Tuple2<?,?>)) {
            return super.equals(obj);
        }

        try {
            Tuple2<A, B> tup = (Tuple2<A, B>) obj;
            return tup.a == this.a && tup.b == this.b;
        }
        catch (ClassCastException e) {
            return false;
        }

    }
}
