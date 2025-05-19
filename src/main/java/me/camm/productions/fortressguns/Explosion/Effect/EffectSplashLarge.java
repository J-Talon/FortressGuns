package me.camm.productions.fortressguns.Explosion.Effect;

import me.camm.productions.fortressguns.Explosion.Abstract.ExplosionEffect;
import me.camm.productions.fortressguns.Explosion.Abstract.ExplosionFG;
import org.bukkit.*;
import org.jetbrains.annotations.Nullable;

public class EffectSplashLarge extends ExplosionEffect<Double> {

    @Override
    public void preMutation(ExplosionFG explosion, @Nullable Double context) {
        World world = explosion.getWorld();
        Location loc = new Location(world, explosion.getX(), explosion.getY(), explosion.getZ());


        int count = (int)(30 * (context == null ? 1: context));
        world.spawnParticle(Particle.WATER_BUBBLE,loc,count,2,0,2,1);

        int countFalling = (int)(200 * (context == null ? 1: context));
        world.spawnParticle(Particle.FALLING_DUST,loc, countFalling,0.7, 0.7, 0.7, 1, Material.WHITE_CONCRETE.createBlockData(),true );

        world.playSound(loc,Sound.ENTITY_PLAYER_SPLASH_HIGH_SPEED,0,1);
        world.spawnParticle(Particle.CLOUD,loc, countFalling, 0.7, 0.7, 0.7,0.01);
    }
}
