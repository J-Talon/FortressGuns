package me.camm.productions.fortressguns.Explosion.Effect;

import me.camm.productions.fortressguns.Explosion.Abstract.ExplosionEffect;
import me.camm.productions.fortressguns.Explosion.Abstract.ExplosionFG;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Util.MathLib;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;


public class EffectMissile extends ExplosionEffect<Double> {

    @FunctionalInterface
   private interface EffectGroup {
        void perform (World world, double x, double y, double z, double percent);


        class TickSpark implements EffectGroup {
            @Override
            public void perform(World world, double x, double y, double z, double percent) {
                Location loc = new Location(world,x,y,z);
                world.spawnParticle(Particle.ELECTRIC_SPARK,loc,(int)(percent * 20), 0,0,0,1,null,false);
                world.spawnParticle(Particle.END_ROD,loc,(int)(percent * 20), 0,0,0,1, null, true);
                world.spawnParticle(Particle.FLASH, loc, (int)(percent * 5), 0,0,0,0.3, null, true);
                //world.playSound(loc, Sound.ENTITY_BLAZE_SHOOT, 1,2);
            }
        }

        class TickExpansion implements EffectGroup {

            @Override
            public void perform(World world, double x, double y, double z, double percent) {
                Location loc = new Location(world,x,y,z);
                world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE,loc, (int) (percent * 15),0.1, 0.1, 0.1, 0.1, null, false);
                world.spawnParticle(Particle.SMOKE_LARGE,loc, (int) (percent * 40),0.1, 0.1, 0.1, 0.3, null, true);
                world.spawnParticle(Particle.SMOKE_NORMAL, loc, (int) (percent * 10), 0.1,0.1,0.1,0.5, null, false);

                Particle.DustTransition dustTransition = new Particle.DustTransition(Color.fromRGB(255, 137, 25), Color.fromRGB(0,0,0), 30.0F);
                world.spawnParticle(Particle.DUST_COLOR_TRANSITION,loc,(int) (percent * 20),0.5, 0.5, 0.5,0.3,dustTransition,true);
                world.playSound(loc,Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR,3,0);
            }
        }


        class TickSmoke implements EffectGroup {

            @Override
            public void perform(World world, double x, double y, double z, double percent) {
                Location loc = new Location(world, x,y,z);
                world.spawnParticle(Particle.SMOKE_LARGE, loc,(int) (percent * 45),0,0,0,0.3,null,false);

                Particle.DustTransition dustTransition = new Particle.DustTransition(Color.fromRGB(255, 209, 25), Color.fromRGB(0,0,0), 30.0F);
                world.spawnParticle(Particle.DUST_COLOR_TRANSITION,loc,(int) (percent * 50),1.2, 1.2, 1.2,0.3,dustTransition,true);
            }
        }

        class TickRecession implements EffectGroup {

            @Override
            public void perform(World world, double x, double y, double z, double percent) {
                Location loc = new Location(world, x,y,z);
                Particle.DustOptions options = new Particle.DustOptions(Color.fromRGB(25,25,25),30f);
                world.spawnParticle(Particle.REDSTONE,loc,(int) (percent * 20), 1,1,1,1,options, true);
                world.spawnParticle(Particle.SMOKE_LARGE,loc, (int) (percent * 50), 0,0,0,0.3,null, false);
            }
        }

    }

    private static final EffectGroup[] tickParams;

    static {
        tickParams = new EffectGroup[]{
                new EffectGroup.TickSpark(),
                new EffectGroup.TickExpansion(),
                new EffectGroup.TickSmoke(),
                new EffectGroup.TickRecession()
        };
    }



    @Override
    public void preMutation(ExplosionFG explosion, @Nullable Double context) {

        int numTrails = (int)( MathLib.randomDouble() * 8) + 6;


        Vector[] trails = new Vector[numTrails];
        World world = explosion.getWorld();
        double x,y,z;
        x = explosion.getX();
        y = explosion.getY();
        z = explosion.getZ();
        Vector explosionVec = new Vector(x,y,z);

        double percent = (context == null ? 1 : context);

        for (int i = 0; i < numTrails; i ++) {
            double vX = MathLib.randomDouble() - MathLib.randomDouble();
            double vY = MathLib.randomDouble() - MathLib.randomDouble();
            double vZ = MathLib.randomDouble() - MathLib.randomDouble();


            trails[i] = new Vector(vX, (vY + 0.75), vZ).multiply(1.25);  //magic numbers due to artistic choice

        }

        Color from = Color.fromRGB(255, 137, 25);
        Color to = Color.fromRGB(0,0,0);
        float size = 3f;
        final int TICKS = 30;

        float dec = (size / TICKS);



        Vector acceleration = new Vector(0,-0.05,0);
        Vector accumulatedAcc = new Vector(0,0,0);

        new BukkitRunnable() {
            int tick = 0;
            public void run() {

                Particle.DustTransition dustTransition = new Particle.DustTransition(from, to, (size - (dec * tick)));
                if (tick < tickParams.length) {
                    EffectGroup group = tickParams[tick];
                    group.perform(world, x,y,z,percent);
                }

                int finished = 0;
                for (Vector v: trails) {

                    Location vecLoc = explosionVec.clone().add(v.clone().multiply(tick)).toLocation(world);

                    if (world.getBlockAt(vecLoc).isEmpty()) {
                        world.spawnParticle(Particle.DUST_COLOR_TRANSITION, vecLoc, (int) (percent * 3), 0,0,0, 0, dustTransition, true);
                        v.add(acceleration);
                    }
                    else finished ++;
                }

                if (tick > TICKS || finished >= numTrails)
                    cancel();

                tick ++;
                accumulatedAcc.add(acceleration);
            }
        }.runTaskTimer(FortressGuns.getInstance(),0,1);
    }


    @Override
    public void postMutation(ExplosionFG explosion) {

        BlockData lightData = Material.LIGHT.createBlockData();
        Levelled levelled = (Levelled)lightData;;

        World world = explosion.getWorld();
        Location loc = new Location(world, explosion.getX(), explosion.getY(), explosion.getZ());
        BlockData locData = loc.getBlock().getBlockData();

        for (Player player: world.getPlayers()) {
            player.sendBlockChange(loc, lightData);
        }

        new BukkitRunnable() {

            int light = 15;
            @Override
            public void run() {

                if (light > 0) {
                    light --;
                    levelled.setLevel(light);
                    for (Player player: world.getPlayers()) {
                        player.sendBlockChange(loc, levelled);
                    }
                }
                else {
                    for (Player player : world.getPlayers()) {
                        player.sendBlockChange(loc, locData);
                    }
                    cancel();
                }

            }
        }.runTaskTimer(FortressGuns.getInstance(),5,3);

    }
}
