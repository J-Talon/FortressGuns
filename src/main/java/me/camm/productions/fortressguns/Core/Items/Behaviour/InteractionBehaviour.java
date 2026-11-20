package me.camm.productions.fortressguns.Core.Items.Behaviour;

import org.jetbrains.annotations.Nullable;

public interface InteractionBehaviour<T> {


    public abstract boolean accept(T item);

    public @Nullable default IBHandle getHandle() {return null;}

}
