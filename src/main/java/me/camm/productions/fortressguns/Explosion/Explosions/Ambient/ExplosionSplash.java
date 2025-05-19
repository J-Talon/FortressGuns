package me.camm.productions.fortressguns.Explosion.Explosions.Ambient;

import me.camm.productions.fortressguns.Explosion.Abstract.ExplosionFG;
import me.camm.productions.fortressguns.Explosion.Effect.EffectSplashSmall;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public class ExplosionSplash extends ExplosionFG {

    public ExplosionSplash(double x, double y, double z, World world) {
        super(x, y, z, world, 0, null, false);
    }

    @Override
    public void perform() {
        EffectSplashSmall small = new EffectSplashSmall();
        small.preMutation(this, null);
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
