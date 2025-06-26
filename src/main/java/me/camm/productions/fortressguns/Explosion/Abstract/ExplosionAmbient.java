package me.camm.productions.fortressguns.Explosion.Abstract;

import org.bukkit.World;

public abstract class ExplosionAmbient extends ExplosionFG {


    public ExplosionAmbient(double x, double y, double z, World world) {
        super(x,y,z,world);
    }

    @Override
    public void perform() {

    }
}
