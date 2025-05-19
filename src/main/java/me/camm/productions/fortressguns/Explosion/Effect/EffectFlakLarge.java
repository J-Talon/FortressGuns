package me.camm.productions.fortressguns.Explosion.Effect;

import me.camm.productions.fortressguns.Explosion.Abstract.ExplosionEffect;
import me.camm.productions.fortressguns.Explosion.Abstract.ExplosionFG;
import org.bukkit.*;
import org.jetbrains.annotations.Nullable;


public class EffectFlakLarge extends ExplosionEffect<Double> {

    @Override
    public void preMutation(ExplosionFG explosion, @Nullable Double context) {

        World world = explosion.getWorld();
        double x,y,z;
        x = explosion.getX();
        y = explosion.getY();
        z = explosion.getZ();

        Location loc = new Location(world, x,y,z );
        final Color DARK_GRAY = Color.fromRGB(60,60,60);
        final Color BLACK = Color.BLACK;

        int effectIntensity = context == null ? 1 : (int)(context * 30);

        Particle.DustTransition transition = new Particle.DustTransition(DARK_GRAY,BLACK,30);

        world.spawnParticle(Particle.SQUID_INK,loc,effectIntensity,0,0,0,0.4f);
        world.spawnParticle(Particle.SMOKE_LARGE,loc,effectIntensity,0,0,0,0.1,null,true);
        world.spawnParticle(Particle.REDSTONE,loc,effectIntensity,0.7,0.7,0.7,0,transition,true);


        world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS,4,1);
        world.playSound(loc, Sound.ENTITY_FIREWORK_ROCKET_BLAST, SoundCategory.BLOCKS,4,(float)Math.random());
        world.playSound(loc, Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, SoundCategory.BLOCKS,2.8f,2);
        world.playSound(loc, Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, SoundCategory.BLOCKS,4f,2);
        world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS,4f,0);
    }
}
