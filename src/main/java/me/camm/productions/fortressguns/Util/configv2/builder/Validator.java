package me.camm.productions.fortressguns.Util.configv2.builder;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface Validator<T> {

    abstract void validate(@NotNull T in);

    default void notNull(Object o, ConfigField field) throws IllegalArgumentException {
        if (o == null) throw new IllegalArgumentException(field.toString()+" not defined or invalid type");
    }

    default void inRange(int low, int high, Double v, ConfigField field) {
        notNull(v, field);
        if (v >= low && v <= high) return;
        throw new IllegalArgumentException(field.toString()+" ("+v+") not in range ["+low+", "+high+"]");
    }

    default void moreThan(double low, Double v, ConfigField field) {
        notNull(v, field);
        if (v > low) return;
        throw new IllegalArgumentException(field.toString() +" ("+v+") not more than "+low);
    }

    default void moreThan(double low, Long v, ConfigField field) {
        notNull(v, field);
        if (v > low) return;
        throw new IllegalArgumentException(field.toString() +" ("+v+") not more than "+low);
    }

    default void moreThan(double low, Integer v, ConfigField field) {
        notNull(v, field);
        if (v > low) return;
        throw new IllegalArgumentException(field.toString() +" ("+v+") not more than "+low);
    }


    default void atLeast(double low, Double v, ConfigField field) {
        notNull(v, field);
        if (v >= low) return;
        throw new IllegalArgumentException(field.toString() +"("+v+") not at least >= "+low);
    }

    default void atLeast(int low, Long v, ConfigField field) {
        notNull(v, field);
        if (v >= low) return;
        throw new IllegalArgumentException(field.toString() +"("+v+") not at least >= "+low);
    }

}
