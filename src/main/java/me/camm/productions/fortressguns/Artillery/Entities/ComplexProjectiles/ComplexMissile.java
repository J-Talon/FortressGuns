package me.camm.productions.fortressguns.Artillery.Entities.ComplexProjectiles;


import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import org.bukkit.Chunk;

import java.util.Set;

/*
Models a missile which is made of multiple entities
 */
public class ComplexMissile implements Construct {


    @Override
    public void spawn() {

    }

    @Override
    public void setChunkLoaded(boolean loaded) {

    }

    @Override
    public Set<Chunk> getLoaders() {
        return null;
    }

    @Override
    public void unload(boolean drop, boolean explode) {

    }

    @Override
    public boolean inValid() {
        return false;
    }
}
