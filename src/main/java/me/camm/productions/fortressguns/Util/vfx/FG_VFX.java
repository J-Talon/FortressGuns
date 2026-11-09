package me.camm.productions.fortressguns.Util.vfx;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Components.Component;
import me.camm.productions.fortressguns.FortressGuns;
import net.minecraft.network.protocol.game.PacketPlayOutPosition;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FG_VFX {

    protected final static double [] offsetX;
    protected final static double [] offsetZ;


    static {
        final int LEN = 12;
        offsetX = new double[LEN];
        offsetZ = new double[LEN];

        int slot = 0;
        for (double rotation = 0; rotation < 2*Math.PI; rotation += (2*Math.PI / LEN)) {
            double z = Math.cos(rotation);
            double x = -Math.sin(rotation);
            offsetX[slot] = x;
            offsetZ[slot] = z;

            slot ++;
        }
    }


    public static void shockwaveParticles(Component pivot) {

        Location initial = pivot.getCenterPoint().add(0,-pivot.getPositionalHeight(),0);
        World world = pivot.getBukkitWorld();

        for (int len = 0; len < offsetX.length; len ++) {
            world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, initial,0,offsetX[len],0,offsetZ[len],0.3f);

        }
    }



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





    public static void shakeAnimation(List<Player> vibrateFor, Artillery artillery, int ticks) {


        Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> flags = new HashSet<>(Arrays.asList(
                PacketPlayOutPosition.EnumPlayerTeleportFlags.a,
                PacketPlayOutPosition.EnumPlayerTeleportFlags.b,
                PacketPlayOutPosition.EnumPlayerTeleportFlags.c,
                PacketPlayOutPosition.EnumPlayerTeleportFlags.d));

        final double MULT = 5;
        double offset = Math.sin(Math.PI + (0.5d * ticks * Math.PI)) * Math.max(-0.12 * ticks + 1, 0) * MULT;
        if (offset != 0) {
            artillery.setVibrationOffsetY(offset);
            for (Player observer : vibrateFor) {

                if (observer.getVehicle() != null)
                    continue;

                double magnitude = observer.getLocation().distanceSquared(artillery.getCurrentLocation());
                double baseSquared = artillery.getBaseLength();
                baseSquared *= baseSquared;
                baseSquared *= 1.25;

                magnitude = (1d/(baseSquared*baseSquared) * magnitude);
                magnitude *= -magnitude;
                magnitude += 1;

                magnitude = Math.max(0, magnitude);
                if (magnitude == 0)
                    continue;

                offset *= magnitude;

                EntityPlayer nms = ((CraftPlayer)observer).getHandle();

                PacketPlayOutPosition packet = new PacketPlayOutPosition(0,0,0,
                        0,
                        nms.getXRot() + ( (float) offset),
                        flags, 1, true);

                nms.b.sendPacket(packet);
            }
        }
        else {
            artillery.setVibrationOffsetY(0);
        }
    }





}
