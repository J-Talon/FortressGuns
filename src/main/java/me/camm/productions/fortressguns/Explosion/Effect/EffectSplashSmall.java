package me.camm.productions.fortressguns.Explosion.Effect;

import me.camm.productions.fortressguns.Explosion.Abstract.ExplosionEffect;
import me.camm.productions.fortressguns.Explosion.Abstract.ExplosionFG;
import me.camm.productions.fortressguns.Explosion.Explosions.Ambient.ExplosionSplash;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

public class EffectSplashSmall extends ExplosionEffect<Double>  {

    @Override
    public void preMutation(ExplosionFG explosion, @Nullable Double context) {
        World w = explosion.getWorld();
        Location loc = new Location(w, explosion.getX(), explosion.getY(), explosion.getZ());

        int count = (int)(30 * (context == null ? 1 : context));
        int dustCount = (int)(100 * (context == null ? 1 : context));
        BlockData concrete = Material.WHITE_CONCRETE.createBlockData();
        w.spawnParticle(Particle.WATER_SPLASH, loc,count,0.1, 1, 0.1);
        w.spawnParticle(Particle.FALLING_DUST, loc,dustCount,0.2, 1, 0.2, 1,concrete,true);
        w.playSound(loc, Sound.ENTITY_PLAYER_SPLASH,1,1);
        w.playSound(loc,Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1,2);
    }
}
