package me.camm.productions.fortressguns.Explosion.Abstract;


import org.jetbrains.annotations.Nullable;

//The idea with the context is that we can use it to better fine tune the amount of
//particles or parameters that the explosion has
//I.e direction and/or particle amount/ other effects
/*
    Ideally this should be the sequence of events:
    get blocks to mutate
    get entities to damage
    preMutation
    mutate entities
    mutate blocks
    postMutation

    Pre-mutation may occur before getting the blocks and/or entities. Doesn't matter because it should
    not be a mutating operation
 */
public abstract class ExplosionEffect<T> {


    //before the breaking of blocks and damaging entities
    public abstract void preMutation(ExplosionFG explosion, @Nullable T context);

    //after blocks are broken and entities are damaged
    public void postMutation(ExplosionFG explosion) {}
}
