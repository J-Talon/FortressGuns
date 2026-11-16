package me.camm.productions.fortressguns.Util.String;

public class StringHelper {


    public static double stringToDouble(String in, double defaultVal) {
        try {
            return Double.parseDouble(in);
        }
        catch (NumberFormatException e) {
            return defaultVal;
        }
    }


}
