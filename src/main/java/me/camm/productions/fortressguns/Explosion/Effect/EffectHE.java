package me.camm.productions.fortressguns.Explosion.Effect;

import me.camm.productions.fortressguns.Explosion.Abstract.ExplosionEffect;
import me.camm.productions.fortressguns.Explosion.Abstract.ExplosionFG;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Util.Tuple2;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;
import java.util.Collection;

public class EffectHE extends ExplosionEffect<Tuple2<Collection<Material>, Double>> {
    @Override
    public void preMutation(ExplosionFG explosion, @Nullable Tuple2<Collection<Material>, Double> context) {


        World world = explosion.getWorld();
        double x,y,z;
        x = explosion.getX();
        y = explosion.getY();
        z = explosion.getZ();

        Location loc = new Location(world, x,y,z );


        double intensityPercent = context == null ? 1 : context.getB();

        final Color LIGHT_GRAY = Color.fromRGB(120,120,120);
        final Color DARK_GRAY = Color.fromRGB(60,60,60);

        world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS,4,0.2f);
        world.playSound(loc, Sound.ENTITY_ENDER_DRAGON_FLAP, SoundCategory.BLOCKS,4,0.2f);
        world.playSound(loc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.BLOCKS,0.5f,0);

        world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE,loc,(int)(50 * intensityPercent),0,0,0,0.5,null, true);
        world.spawnParticle(Particle.SMOKE_LARGE,loc,(int)(60 * intensityPercent),0,0,0,0.3);
        world.spawnParticle(Particle.SMOKE_LARGE,loc,(int)(60 * intensityPercent),1,1,1,0.2,null,true);
        world.spawnParticle(Particle.FLASH,loc,(int)(5 * intensityPercent),0,0,0,0,null,true);

        Particle.DustTransition transition = new Particle.DustTransition(LIGHT_GRAY,DARK_GRAY,30);
        world.spawnParticle(Particle.REDSTONE,loc,(int)(70 * intensityPercent),1.7,2,1.7,1,transition);
    }

    @Override
    public void postMutation(ExplosionFG explosion) {
        World bukkitWorld = explosion.getWorld();
        BlockData LIGHT = Material.LIGHT.createBlockData();
        BlockData AIR = Material.AIR.createBlockData();
        Location loc = new Location(bukkitWorld,explosion.getX(),explosion.getY(),explosion.getZ());

        if (!bukkitWorld.getBlockAt(loc).getType().isAir())
            return;

        bukkitWorld.setBlockData(loc, LIGHT);

        new BukkitRunnable() {
            public void run() {
                bukkitWorld.setBlockData(loc,AIR);
            }
        }.runTaskLater(FortressGuns.getInstance(), 5);
    }
}
