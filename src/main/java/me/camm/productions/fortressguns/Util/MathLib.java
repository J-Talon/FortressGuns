package me.camm.productions.fortressguns.Util;

public class MathLib {

    public static double linearInterpolate(double increment, double start, double end) {
        return start + increment * (end - start);
    }


}
