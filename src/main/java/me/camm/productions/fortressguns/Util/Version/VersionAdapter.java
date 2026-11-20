package me.camm.productions.fortressguns.Util.Version;

public abstract class VersionAdapter<T,C> {

    public abstract void provide(T data, C context);

    public abstract Version getTarget();
}
