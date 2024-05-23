package me.camm.productions.fortressguns.Artillery.Projectiles;


import me.camm.productions.fortressguns.Util.Tracer;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;

import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;

import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import java.util.Random;
import java.util.Set;




public class ExplosiveShell extends StandardShell {



    private static int NUM_TRACES = 15;
    private static Random rand = new Random();

    public ExplosiveShell(EntityTypes<? extends EntityArrow> entitytypes, double d0, double d1, double d2, World world, @Nullable Player shooter) {
        super(entitytypes, d0, d1, d2, world, shooter);
    }


    @Override
    public float getStrength() {
         return 4f;
    }

    @Override
    public void explode(MovingObjectPosition pos) {

        Vec3D look = this.getLookDirection();
        Vector input =  new Vector(look.getX(), look.getY(), look.getZ());


        Vec3D hitLoc = getHitLoc(pos,this);


        Location loc = new Location(bukkitWorld, hitLoc.getX(), hitLoc.getY(), hitLoc.getZ());
        Vector locVec = loc.toVector();

        for (int traces = 0; traces < NUM_TRACES; traces ++)
        {
            double[] comps = rand.doubles(3,0,1).toArray();
            Vector dir = new Vector(comps[0] - 0.5, comps[1] - 0.5, comps[2] - 0.5).normalize();
            Tracer tracer =  new Tracer(dir, locVec, bukkitWorld);
            Set<Tuple<Block, Double>> broken = tracer.getBrokenBlocks();

            for (Tuple<Block, Double> tup: broken) {
                double dist = tup.b();
                Block block = tup.a();

                Location spawn = block.getLocation().add(0.5,0.5,0.5);
                FallingBlock fallingBlock = bukkitWorld.spawnFallingBlock(spawn, block.getBlockData());
                fallingBlock.setHurtEntities(true);
                block.setType(Material.AIR);

                Vector vel = spawn.toVector().subtract(loc.toVector()).normalize();
                vel.multiply(Math.max(0.1f,dist * dist - 9));
                vel.add(input.clone().multiply(3-dist));

                fallingBlock.setVelocity(vel);

            }
        }

        playExplosionEffects(loc);
        super.explode(pos);
    }

    private void playExplosionEffects(Location explosion){
        bukkitWorld.spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE,explosion,100,0,0,0,0.5);
        bukkitWorld.spawnParticle(Particle.CLOUD,explosion,100,0,0,0,0.5);
    }


}
