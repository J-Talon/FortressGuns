package me.camm.productions.fortressguns.Util.vfx;

import me.camm.productions.fortressguns.FortressGuns;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class FG_VFX {

    public static void constructDestroy(Location loc, World world){
        world.spawnParticle(Particle.SMOKE_LARGE,loc,30,0,0,0,0.3);
        world.spawnParticle(Particle.FLAME,loc, 10,0,0,0,0.3);
        world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.HOSTILE,1F,1F);
        world.spawnParticle(Particle.EXPLOSION_HUGE,loc, 3,0,0,0,0);
    }


    public static void gunFire(Location muzzle, World world) {
        world.spawnParticle(Particle.SMOKE_LARGE,muzzle.getX(),muzzle.getY(), muzzle.getZ(),30,0,0,0,0.2);
        world.spawnParticle(Particle.FLASH,muzzle.getX(),muzzle.getY(), muzzle.getZ(),1,0,0,0,0.2);

        world.playSound(muzzle, Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR,SoundCategory.BLOCKS,2,0.2f);
        world.playSound(muzzle, Sound.ENTITY_LIGHTNING_BOLT_THUNDER,SoundCategory.BLOCKS,0.2f,0.2f);
        world.playSound(muzzle, Sound.ITEM_ARMOR_EQUIP_GENERIC,SoundCategory.BLOCKS,1f,0.2f);
    }



    public static void createFlash(Location origin, World world) {

        List<Player> players = world.getPlayers();
        BlockData lightData = Material.LIGHT.createBlockData();
        BlockData airData = Material.AIR.createBlockData();
        final Location flash = origin.clone();

        world.spawnParticle(Particle.FLASH,origin.getX(),origin.getY(), origin.getZ(),1,0,0,0,0.2);


        players.forEach(player -> player.sendBlockChange(flash, lightData));


        new BukkitRunnable() {
            public void run() {
                players.forEach(player -> player.sendBlockChange(flash, airData));

            }
        }.runTaskLater(FortressGuns.getInstance(), 5);


    }





}
