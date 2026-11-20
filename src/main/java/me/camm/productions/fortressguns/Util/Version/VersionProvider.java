package me.camm.productions.fortressguns.Util.Version;

import org.jetbrains.annotations.Nullable;

public abstract class VersionProvider<T, C> {

    public abstract @Nullable T provide(C context);

    public abstract Version getTarget();

}
