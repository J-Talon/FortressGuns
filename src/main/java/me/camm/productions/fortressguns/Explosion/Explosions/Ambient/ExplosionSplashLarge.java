package me.camm.productions.fortressguns.Explosion.Explosions.Ambient;

import me.camm.productions.fortressguns.Explosion.Abstract.ExplosionEffect;
import me.camm.productions.fortressguns.Explosion.Abstract.ExplosionFG;
import me.camm.productions.fortressguns.Explosion.Effect.EffectSplashLarge;
import org.bukkit.World;

public class ExplosionSplashLarge extends ExplosionFG {


    public ExplosionSplashLarge(double x, double y, double z, World world) {
        super(x, y, z, world);
    }

    @Override
    public void perform() {
        ExplosionEffect<Double> effect = new EffectSplashLarge();
        effect.preMutation(this, 1d);
    }

}
