package me.camm.productions.fortressguns.Explosion.Effect;

import org.bukkit.*;

///for the effects that don't need to be part of a true explosion
//essentially creating the visuals of an explosion but no mutations
public class EffectExplosionStandalone {
    public static void explodeArtillery(Location loc, World world){
        world.spawnParticle(Particle.SMOKE_LARGE,loc,30,0,0,0,0.3);
        world.spawnParticle(Particle.FLAME,loc, 10,0,0,0,0.3);
        world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.HOSTILE,1F,1F);
        world.spawnParticle(Particle.EXPLOSION_HUGE,loc, 3,0,0,0,0);
    }


}
