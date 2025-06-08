package me.camm.productions.fortressguns.Explosion.Effect;

import me.camm.productions.fortressguns.Explosion.Abstract.ExplosionEffect;
import me.camm.productions.fortressguns.Explosion.Abstract.ExplosionFG;
import me.camm.productions.fortressguns.Util.Tuple2;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import static me.camm.productions.fortressguns.Util.MathLib.getOrthogonal;


public class EffectHE extends ExplosionEffect<Tuple2<Double, Vector>> {

    private Location mutation;

    //in: vector: direction of motion
    //in: double: effect intensity
    @Override
    public void preMutation(ExplosionFG explosion, @Nullable Tuple2<Double, Vector> context) {


        World world = explosion.getWorld();
        double x,y,z;
        x = explosion.getX();
        y = explosion.getY();
        z = explosion.getZ();

        Location loc = new Location(world, x,y,z );
        Location ground = loc.clone();

        double intensityPercent = context == null ? 1 : context.getA();
        Vector step;
        Vector direction;

        if (context == null) {
            step = new Vector(0,0,0);
            direction = new Vector(0,1,0);
            ground.add(0,0.25,0);
        }
        else {
            step = context.getB().clone().normalize();
            direction = step.clone();
            ground.add(direction.clone().multiply(-0.5));
        }

        step.multiply(-1);
        step.multiply(3);
        loc.add(step);
        mutation = loc;

        final Color LIGHT_GRAY = Color.fromRGB(120,120,120);
        final Color DARK_GRAY = Color.fromRGB(60,60,60);

        world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS,2,0);
        world.playSound(loc, Sound.ENTITY_ENDER_DRAGON_FLAP, SoundCategory.BLOCKS,2,0);
        world.playSound(loc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.BLOCKS,0.5f,0);

        world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE,loc,(int)(50 * intensityPercent),0,0,0,0.5,null, true);
        world.spawnParticle(Particle.SMOKE_LARGE,loc,(int)(60 * intensityPercent),0,0,0,0.3);
        world.spawnParticle(Particle.SMOKE_LARGE,loc,(int)(60 * intensityPercent),1,1,1,0.2,null,true);
        world.spawnParticle(Particle.FLASH,loc,(int)(5 * intensityPercent),0,0,0,0,null,true);

        Particle.DustTransition transition = new Particle.DustTransition(LIGHT_GRAY,DARK_GRAY,30);
        world.spawnParticle(Particle.REDSTONE,loc,(int)(70 * intensityPercent),1.7,2,1.7,1,transition);

        Vector orthogonal = getOrthogonal(direction);
        final double ANGLE_INC = 10;
        for (double current = 0; current < 360; current += ANGLE_INC) {
            orthogonal.rotateAroundNonUnitAxis(direction,current);
            world.spawnParticle(Particle.CLOUD,ground,0,orthogonal.getX(), orthogonal.getY(), orthogonal.getZ(),1);
        }

        World bukkitWorld = explosion.getWorld();
        BlockData LIGHT = Material.LIGHT.createBlockData();

        if (!bukkitWorld.getBlockAt(mutation).getType().isAir())
            return;

        for (Player player : bukkitWorld.getPlayers()) {
            player.sendBlockChange(loc, LIGHT);
        }
    }

    @Override
    public void postMutation(ExplosionFG explosion) {
        World bukkitWorld = explosion.getWorld();
        BlockData AIR = Material.AIR.createBlockData();

        for (Player player: bukkitWorld.getPlayers()) {
            player.sendBlockChange(mutation,AIR);
        }
    }
}
