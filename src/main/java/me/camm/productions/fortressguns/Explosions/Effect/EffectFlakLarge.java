package me.camm.productions.fortressguns.Explosions.Effect;

import me.camm.productions.fortressguns.Explosions.Abstract.EffectContext;
import me.camm.productions.fortressguns.Explosions.Abstract.ExplosionEffect;
import me.camm.productions.fortressguns.Explosions.Abstract.ExplosionFG;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;

public class EffectFlakLarge extends ExplosionEffect<Integer> {

    @Override
    public void preMutation(ExplosionFG explosion, EffectContext<Integer> context) {

        CraftWorld world = explosion.getWorld().getWorld();
        double x,y,z;
        x = explosion.getX();
        y = explosion.getY();
        z = explosion.getZ();

        Location loc = new Location(world, x,y,z );
        final Color DARK_GRAY = Color.fromRGB(60,60,60);
        final Color BLACK = Color.BLACK;

        Particle.DustTransition transition = new Particle.DustTransition(DARK_GRAY,BLACK,30);

        world.spawnParticle(Particle.SQUID_INK,loc,30,0,0,0,0.4f);
        world.spawnParticle(Particle.SMOKE_LARGE,loc,30,0,0,0,0.1,null,true);
        world.spawnParticle(Particle.REDSTONE,loc,30,0.7,0.7,0.7,0,transition,true);


        world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS,4,1);
        world.playSound(loc, Sound.ENTITY_FIREWORK_ROCKET_BLAST, SoundCategory.BLOCKS,4,(float)Math.random());
        world.playSound(loc, Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, SoundCategory.BLOCKS,2.8f,2);
        world.playSound(loc, Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, SoundCategory.BLOCKS,4f,2);
        world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS,4f,0);
    }

    @Override
    public void postMutation(ExplosionFG explosion, EffectContext<Integer> context) {

    }
}
