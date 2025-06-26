package me.camm.productions.fortressguns.Explosion.Abstract;

import org.bukkit.Material;
import org.bukkit.World;



import java.util.*;

public abstract class ExplosionFG {


    protected double x,y,z;
    protected World world;
    protected static Random rand = new Random();

    public ExplosionFG(double x, double y, double z, World world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
    }


    public abstract void perform();

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

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public World getWorld() {
        return world;
    }

}
