package me.camm.productions.fortressguns.Util;

public class DataHelper {


    public static double stringToDouble(String in, double defaultVal) {
        try {
            return Double.parseDouble(in);
        }
        catch (NumberFormatException e) {
            return defaultVal;
        }
    }


}
