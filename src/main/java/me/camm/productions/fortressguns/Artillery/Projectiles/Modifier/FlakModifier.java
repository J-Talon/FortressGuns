package me.camm.productions.fortressguns.Artillery.Projectiles.Modifier;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.FortressGuns;

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

        playFlakEffects(bukkitWorld, explosion);

        Block block = explosion.getBlock();
        final Material mat = block.getType();
        if (Artillery.isFlashable(block))
            block.setType(Material.LIGHT);


      //  bukkitWorld.createExplosion(explosion,4,true,false);

        //create explosion fragments.
        new BukkitRunnable(){

            public void run(){
                    block.setType(mat);
                    bukkitWorld.spawnParticle(Particle.SQUID_INK, explosion, 30, 0.1, 0.1, 0.1, 0.2f);
            }
        }.runTaskLater(FortressGuns.getInstance(),1);
    }
}
