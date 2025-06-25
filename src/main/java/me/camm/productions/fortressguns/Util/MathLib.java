package me.camm.productions.fortressguns.Util;

import org.bukkit.util.Vector;

import java.util.Random;

public class MathLib {

    static Random rand = new Random();

    public static double linearInterpolate(double increment, double start, double end) {
        return start + increment * (end - start);
    }


    public static double randomDouble() {
        return rand.nextDouble();
    }


    public static Vector getOrthogonal(Vector other) {
        double denom = Double.NaN;
        double linComb = Double.NaN;  ///linear combination method to get orthagonal
        double mult1, mult2;
        mult1 = rand.nextDouble() - 0.5;
        mult2 = rand.nextDouble() - 0.5;

        //A is orthagonal to B if A dot B = 0
        /*
        A = {a1,a2,a3}
        B = {b1, b2, b3}
        A ' B means:

        a1 * b1 + a2 * b2 + a3 * b3 = 0
        where mult1 and mult2 serve as either b1, b2, or b3 depending on the situation

        We're basically solving for either b1, b2, or b3 depending on what a1, a2, and a3 are
         */

        if (other.getX() != 0) {
            denom = other.getX();
            linComb = other.getY() * mult1 - other.getZ() * mult2;
        }
        else if (other.getY() != 0) {
            denom = other.getY();
            linComb = -other.getX() * mult1 - other.getZ() * mult2;
        }
        else if (other.getZ() != 0) {
            denom = other.getZ();
            linComb = -other.getX() * mult1 - other.getY() * mult1;
        }

        if (Double.isNaN(denom) && Double.isNaN(linComb))
            return new Vector(0,0,0);
        else return new Vector(mult1, mult2, linComb / denom).normalize();

    }


}
