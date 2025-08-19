package me.camm.productions.fortressguns.Util;

public class Tuple3<A,B,C> {

    private A a;
    private B b;
    private C c;

    public Tuple3(A a, B b, C c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public A getA() {
        return a;
    }

    public B getB() {
        return b;
    }

    public C getC() {
        return c;
    }


    //yes I know technically tuples aren't supposed to have a set function
    //cause they're supposed to be final... so really this should be called a
    //triplet or something. I'm bad at names so I just call it a tuple.
    public void setA(A a) {
        this.a = a;
    }

    public void setB(B b) {
        this.b = b;
    }

    public void setC(C c) {
        this.c = c;
    }
}
