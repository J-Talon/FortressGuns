package me.camm.productions.fortressguns.Artillery.Projectiles.Modifier;

import me.camm.productions.fortressguns.Artillery.Artillery;
import me.camm.productions.fortressguns.FortressGuns;
import net.minecraft.world.damagesource.DamageSource;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

/*
flak modifier for flak shots
 */
public class FlakModifier implements IModifier
{


    //location of explosion
    private final Location explosion;

    public FlakModifier(Location explosion) {
        this.explosion = explosion;
    }

    //activating the shot
    @Override
    public void activate() {

        World bukkitWorld = explosion.getWorld();
        if (bukkitWorld==null)
            return;
        //create flak particles
        bukkitWorld.spawnParticle(Particle.FLASH,explosion,3,0,0,0,0);
        bukkitWorld.spawnParticle(Particle.SMOKE_LARGE,explosion,50,0.1,0.1,0.1,0.2f);
        bukkitWorld.spawnParticle(Particle.SQUID_INK,explosion,50,0.1,0.1,0.1,0.2f);
        bukkitWorld.spawnParticle(Particle.FLAME,explosion,50,0.1,0.1,0.1,0.1f);
        Block block = explosion.getBlock();
        final Material mat = block.getType();
        if (Artillery.isFlashable(block))
            block.setType(Material.LIGHT);

       bukkitWorld.createExplosion(explosion,4,true, false);
        //explode and make a sound
        bukkitWorld.playSound(explosion, Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS,1,2);
        bukkitWorld.playSound(explosion, Sound.ENTITY_ENDER_DRAGON_FLAP, SoundCategory.BLOCKS,1,0.2f);
      //  bukkitWorld.createExplosion(explosion,4,true,false);

        //create explosion fragments.
        new BukkitRunnable(){
            int time = 0;
            boolean spent = false;
            public void run(){

                if (!spent) {
                    block.setType(mat);
                    spent = true;
                }


                if (time < 16) {
                    time++;
                    bukkitWorld.spawnParticle(Particle.SMOKE_LARGE, explosion, 30, 0.1, 0.1, 0.1, 0.1f);
                }
                else {
                   cancel();
                }
            }
        }.runTaskTimer(FortressGuns.getInstance(),2,5);
    }
}
