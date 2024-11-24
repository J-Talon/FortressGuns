package me.camm.productions.fortressguns.Util.DataLoading.Validator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface Validator<T> {
    public boolean validate(@NotNull T in);


     default float getFloat(@Nullable Object in) {

         if (in == null) {
             return Float.NaN;
         }

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
        return Float.NaN;
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
}
