package me.camm.productions.fortressguns.Explosion.Explosions.Ambient;

import me.camm.productions.fortressguns.Explosion.Abstract.ExplosionAmbient;
import me.camm.productions.fortressguns.Explosion.Abstract.ExplosionFG;
import me.camm.productions.fortressguns.Explosion.Effect.EffectSplashSmall;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public class ExplosionSplash extends ExplosionAmbient {

    ///gotta fix this and the large one cause if source is null it crashes
    public ExplosionSplash(double x, double y, double z, World world) {
        super(x, y, z, world);
    }

    @Override
    public void perform() {
        EffectSplashSmall small = new EffectSplashSmall();
        small.preMutation(this, null);
    }
}
