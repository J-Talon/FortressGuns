package me.camm.productions.fortressguns.Explosion.Abstract;


public class EffectContext<T> {
    T context;

    public EffectContext(T context) {
        this.context = context;
    }

    public T getContext() {
        return context;
    }
}
