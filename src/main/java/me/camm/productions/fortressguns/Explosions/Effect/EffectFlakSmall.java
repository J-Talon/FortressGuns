package me.camm.productions.fortressguns.Explosions.Effect;

import me.camm.productions.fortressguns.Explosions.Abstract.EffectContext;
import me.camm.productions.fortressguns.Explosions.Abstract.ExplosionEffect;
import me.camm.productions.fortressguns.Explosions.Abstract.ExplosionFG;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;

public class EffectFlakSmall extends ExplosionEffect<Integer> {

    @Override
    public void preMutation(ExplosionFG explosion, EffectContext<Integer> context) {
        World world = explosion.getWorld();
        Location loc = new Location(world, explosion.getX(), explosion.getY(), explosion.getZ());
        world.playSound(loc, Sound.ENTITY_FIREWORK_ROCKET_BLAST, SoundCategory.BLOCKS,1,0);
        world.spawnParticle(Particle.SMOKE_LARGE,loc,10,0.1,0.1,0.1,0,null,true);
    }

    @Override
    public void postMutation(ExplosionFG explosion, EffectContext<Integer> context) {

    }
}
