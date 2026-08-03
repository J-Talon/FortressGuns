package me.camm.productions.fortressguns.Util.Math;

public class IntTuple2 {

    int a, b;
    public IntTuple2(int a, int b) {
        this.a = a;
        this.b = b;
    }

    public void setA(int a) {
        this.a = a;
    }

    public int getA() {
        return a;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IntTuple2 o) {
            return o.a == this.a && o.b == this.b;
        }
        return super.equals(obj);
    }


    @Override
    public String toString() {
        return "<"+a+", "+b+">";
    }
}
