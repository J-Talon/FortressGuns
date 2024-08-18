package me.camm.productions.fortressguns.Artillery.Projectiles;


import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import net.minecraft.world.phys.MovingObjectPosition;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.Set;

/*
Models a missile which is made of multiple entities
 */
public class ComplexMissile extends Construct implements ArtilleryProjectile {


    @Override
    public boolean spawn() {
        return false;
    }


    @Override
    public void explode(MovingObjectPosition pos) {

    }

    @Override
    public void postExplosion(EntityExplodeEvent event) {

    }

    @Override
    public float getStrength() {
        return 0;
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
