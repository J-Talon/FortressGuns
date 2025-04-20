package me.camm.productions.fortressguns.Explosions.Abstract;


public class EffectContext<T> {
    T context;

    public EffectContext(T context) {
        this.context = context;
    }

    public T getContext() {
        return context;
    }
}
