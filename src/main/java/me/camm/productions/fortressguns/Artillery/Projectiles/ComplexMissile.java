package me.camm.productions.fortressguns.Artillery.Projectiles;


import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import org.bukkit.Chunk;

import java.util.Set;

/*
Models a missile which is made of multiple entities
 */
public class ComplexMissile extends Construct {


    @Override
    public void spawn() {

    }




    @Override
    public void setChunkLoaded(boolean loaded) {

    }

    @Override
    public Set<Chunk> getOccupiedChunks() {
        return null;
    }

    @Override
    public void unload(boolean drop, boolean explode) {

    }

    @Override
    public boolean isInvalid() {
        return false;
    }

    public void setLocation(double x, double y, double z){

    }
}
