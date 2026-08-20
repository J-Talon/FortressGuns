package me.camm.productions.fortressguns.interact;

import org.jetbrains.annotations.Nullable;

public interface InteractionBehaviour<T> {


    public abstract boolean accept(T item);

    public @Nullable default IBHandle getHandle() {return null;}


}
