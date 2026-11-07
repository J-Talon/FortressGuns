package me.camm.productions.fortressguns.Util;

public abstract class Adapter<T,C> {

    public abstract void provide(T data, C context);

    public abstract Class<?> getTarget();
}
