package me.camm.productions.fortressguns.Artillery.Projectiles.Modifier;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;

public interface IModifier {
    void activate();

    default void playMuzzleEffects(org.bukkit.World bukkitWorld, Location explosion) {

    }

    default void playFlakEffects(org.bukkit.World bukkitWorld, Location explosion){
        bukkitWorld.spawnParticle(Particle.FLASH,explosion,3,0,0,0,0);
        bukkitWorld.spawnParticle(Particle.SMOKE_LARGE,explosion,50,0.1,0.1,0.1,0.2f);
        bukkitWorld.spawnParticle(Particle.SQUID_INK,explosion,50,0.1,0.1,0.1,0.2f);
        bukkitWorld.spawnParticle(Particle.FLAME,explosion,50,0.1,0.1,0.1,0.1f);

        bukkitWorld.createExplosion(explosion,4,true, false);
        //explode and make a sound
        bukkitWorld.playSound(explosion, Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS,1,2);
        bukkitWorld.playSound(explosion, Sound.ENTITY_ENDER_DRAGON_FLAP, SoundCategory.BLOCKS,1,0.2f);

    }

    default void playExplosionEffects(org.bukkit.World bukkitWorld, Location explosion){
        bukkitWorld.spawnParticle(Particle.FLASH,explosion,3,0,0,0,0);
        bukkitWorld.spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE,explosion,30,0,0,0,0.3);
        bukkitWorld.createExplosion(explosion, 4f);
    }
}
