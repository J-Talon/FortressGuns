package me.camm.productions.fortressguns.Artillery.Entities.Abstract;

import org.bukkit.Chunk;

import java.util.Set;

public interface Construct {

    void spawn();

    void setChunkLoaded(boolean loaded);
    Set<Chunk> getLoaders();

    void unload(boolean drop, boolean explode);
    boolean inValid();
}
