package me.camm.productions.fortressguns.Explosion.Explosions.Ambient;

import me.camm.productions.fortressguns.Explosion.Abstract.ExplosionEffect;
import me.camm.productions.fortressguns.Explosion.Abstract.ExplosionFG;
import me.camm.productions.fortressguns.Explosion.AllocatorFunction.Entity.AllocatorVanillaE;
import me.camm.productions.fortressguns.Explosion.Effect.EffectSplashLarge;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class ExplosionSplashLarge extends ExplosionFG {


    public ExplosionSplashLarge(double x, double y, double z, World world) {
        super(x, y, z, world, 0,null, false);
    }

    @Override
    public void perform() {
        ExplosionEffect<Double> effect = new EffectSplashLarge();
        effect.preMutation(this, 1d);
    }

    @Override
    public float getMaxDamage() {
        return 0;
    }

    @Override
    public double getFalloff(double distanceSquared) {
        return 0;
    }
}
