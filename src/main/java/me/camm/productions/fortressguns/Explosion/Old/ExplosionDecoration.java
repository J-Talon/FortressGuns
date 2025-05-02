package me.camm.productions.fortressguns.Explosion.Old;


import me.camm.productions.fortressguns.FortressGuns;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;



import java.util.Collection;

@FunctionalInterface
public interface ExplosionDecoration {
    void decorate(ShellExplosion explosion);

    default void postExplosion(ShellExplosion explosion) {

    }

    static class StandardDecoration implements ExplosionDecoration {
        @Override
        public void decorate(ShellExplosion explosion) {
            CraftWorld world = explosion.getWorld().getWorld();
            double x,y,z;
            x = explosion.getLocX();
            y = explosion.getLocY();
            z = explosion.getLocZ();

            Location loc = new Location(world, x,y,z );

            final Color LIGHT_GRAY = Color.fromRGB(120,120,120);
            final Color DARK_GRAY = Color.fromRGB(60,60,60);


            world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS,4,0.2f);
            world.playSound(loc, Sound.ENTITY_ENDER_DRAGON_FLAP, SoundCategory.BLOCKS,4,0.2f);
            world.playSound(loc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.BLOCKS,0.5f,0);

            world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE,loc,50,0,0,0,0.5,null, true);
            world.spawnParticle(Particle.SMOKE_LARGE,loc,60,0,0,0,0.3);
            world.spawnParticle(Particle.SMOKE_LARGE,loc,60,1,1,1,0.2,null,true);
            world.spawnParticle(Particle.FLASH,loc,5,0,0,0,0,null,true);

            Particle.DustTransition transition = new Particle.DustTransition(LIGHT_GRAY,DARK_GRAY,30);
            world.spawnParticle(Particle.REDSTONE,loc,70,1.7,2,1.7,1,transition);

        }


        public void postExplosion(ShellExplosion explosion) {
            World bukkitWorld = explosion.getWorld().getWorld();
            BlockData LIGHT = Material.LIGHT.createBlockData();
            BlockData AIR = Material.AIR.createBlockData();
            Collection<Player> players = bukkitWorld.getPlayers();
            Location loc = new Location(bukkitWorld,explosion.getLocX(),explosion.getLocY(),explosion.getLocZ());


            bukkitWorld.setBlockData(loc, LIGHT);
            new BukkitRunnable() {
                public void run() {
                    bukkitWorld.setBlockData(loc,AIR);
                }
            }.runTaskLater(FortressGuns.getInstance(), 5);

//
//            players.forEach(player ->{player.sendBlockChange(loc,LIGHT);});
//            new BukkitRunnable(){
//                public void run() {
//                    players.forEach(player->{
//                        player.sendBlockChange(loc, loc.getBlock().getBlockData());
//                    });
//
//                }}.runTaskLater(FortressGuns.getInstance(),5);

        /*
        So the issue with this currently is the fact that we're trying to set
        light levels in an explosion, which is pretty gosh damn annoying cause
        the placing and breaking of blocks changes the light levels and creates
        unpredictable results.

        there isn't a really good way around this because of how the lighting
        works in vanilla MC unless you look deeeeeeeep into the lighting engine


         */
        }

    }


    static class LargeFlakDecoration implements ExplosionDecoration {

        @Override
        public void decorate(ShellExplosion explosion) {
            CraftWorld world = explosion.getWorld().getWorld();
            double x,y,z;
            x = explosion.getLocX();
            y = explosion.getLocY();
            z = explosion.getLocZ();

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
    }




    static class SmallFlakExplosion implements ExplosionDecoration {
        @Override
        public void decorate(ShellExplosion explosion) {
            CraftWorld world = explosion.getWorld().getWorld();
            Location loc = new Location(world, explosion.getLocX(), explosion.getLocY(), explosion.getLocZ());
            world.playSound(loc, Sound.ENTITY_FIREWORK_ROCKET_BLAST, SoundCategory.BLOCKS,1,0);
            world.spawnParticle(Particle.SMOKE_LARGE,loc,10,0.1,0.1,0.1,0,null,true);
        }
    }



}



