package me.camm.productions.fortressguns.Explosion.Effect;

import me.camm.productions.fortressguns.Explosion.Abstract.EffectContext;
import me.camm.productions.fortressguns.Explosion.Abstract.ExplosionEffect;
import me.camm.productions.fortressguns.Explosion.Abstract.ExplosionFG;
import org.bukkit.*;
import org.jetbrains.annotations.Nullable;

public class EffectFlakSmall extends ExplosionEffect<Double> {

    @Override
    public void preMutation(ExplosionFG explosion, @Nullable Double context) {
        World world = explosion.getWorld();
        Location loc = new Location(world, explosion.getX(), explosion.getY(), explosion.getZ());
        world.playSound(loc, Sound.ENTITY_FIREWORK_ROCKET_BLAST, SoundCategory.BLOCKS,1,0);

        int intensity = context == null ? 1 : (int)(10 * context);
        world.spawnParticle(Particle.SMOKE_LARGE,loc,intensity,0.1,0.1,0.1,0,null,true);
    }
}
