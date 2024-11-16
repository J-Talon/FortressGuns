package me.camm.productions.fortressguns.Util.DataLoading.Validator;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface Validator<T> {
    public T validate(@NotNull Object in);


     default Float getFloat(@NotNull Object in) {
        float value;
        if (in instanceof Integer) {
            value = (Integer)in;
            return value;
        }
        else if (in instanceof Float) {
            return (Float) in;
        }
        else if (in instanceof Double) {
            double temp = (Double) in;
            return (float)temp;
        }
        return null;

    }

    default Integer getInt(@NotNull Object in) {
         int value;
         if (in instanceof Long) {
             long l = (Long)in;
             value = (int)l;
             return value;
         }

         if (in instanceof Integer) {
             return (Integer) in;
         }
         return null;
    }


    //dont nest the classes if you do you'll be SORRY

    static class ValidateIntPositive implements Validator<Integer> {
        @Override
        public Integer validate(@NotNull Object in) {
            try {

                Integer value = getInt(in);

                if (value == null)
                    return null;

                if (value > 0)
                    return value;
                return null;
            }
            catch (ClassCastException e) {
                e.printStackTrace();
                return null;
            }
        }
    }



    static class ValidateIntUnsigned implements Validator<Integer> {

        @Override
        public Integer validate(@NotNull Object in) {
            try {

                Integer value = getInt(in);
                if (value == null)
                    return null;

                if (value >= 0) {
                    return value;
                }
                return null;

            }
            catch (ClassCastException e) {
                return null;
            }
        }
    }


    static class ValidateJam implements Validator<Float> {

        @Override
        public Float validate(@NotNull Object in) {
            try {


                Float value = getFloat(in);
                if (value == null)
                    return null;


                if (value >= 0 && value <= 1) {
                    return value;
                }
                return null;
            }
            catch (ClassCastException e) {
                return null;
            }
        }
    }

    static class ValidateHeat implements Validator<Float> {

        @Override
        public Float validate(@NotNull Object in) {
            try {
                Float value = getFloat(in);
                if (value == null)
                    return null;

                if (value >= 0 && value <= 100) {
                    return value;
                }
                return null;
            }
            catch (ClassCastException e) {
                return null;
            }
        }
    }
}
