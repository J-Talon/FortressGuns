package me.camm.productions.fortressguns.Artillery.Projectiles;



import net.minecraft.world.entity.EntityTypes;


import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.Vec3D;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;

import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;


public class ExplosiveShell extends StandardShell {

    private static Random rand = new Random();
    private Vector spewDir = null;
    private Location center = null;

    public ExplosiveShell(EntityTypes<? extends EntityArrow> entitytypes, double d0, double d1, double d2, World world, @Nullable Player shooter) {
        super(entitytypes, d0, d1, d2, world, shooter);
    }


    @Override
    public float getStrength() {
         return 4f;
    }

    @Override
    public void explode(@Nullable MovingObjectPosition pos) {

        if (pos == null) {
            super.explode((MovingObjectPosition) null);
            return;
        }

        Vec3D backstep = stepBack(pos,this);

        Vec3D currentPos = this.getPositionVector();

        Vec3D blastDir = currentPos.a(backstep);  ///currentPos - hitLoc
        blastDir = blastDir.d().e();


        RayTraceResult centerPoint = bukkitWorld.rayTraceBlocks(
                new Location(bukkitWorld, currentPos.getX(), currentPos.getY(), currentPos.getZ())
                , new Vector(blastDir.getX(), blastDir.getY(), blastDir.getZ()),
                10,
                FluidCollisionMode.NEVER);

        if (centerPoint == null) {
            super.explode(pos);
            return;
        }

        Block bukkitBlock = centerPoint.getHitBlock();
        BlockFace face = centerPoint.getHitBlockFace();

        if (bukkitBlock == null || face == null) {
            super.explode(pos);
            return;
        }

        spewDir = face.getDirection();
        center = centerPoint.getHitPosition().add(spewDir.clone().multiply(-3)).toLocation(bukkitWorld);
        super.explode(pos);
    }

    @Override
    public void playExplosionEffects(Location explosion){
        bukkitWorld.spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE,explosion,100,0,0,0,0.5);
        bukkitWorld.spawnParticle(Particle.CLOUD,explosion,100,0,0,0,0.5);
    }

    @Override
    public void postExplosion(EntityExplodeEvent event) {
        super.postExplosion(event);

        if (spewDir == null) {
            return;
        }

        double SQRT_PI = Math.sqrt(Math.PI);


        List<Block> blocks = event.blockList();

        for (Block b: blocks) {
            Location loc = b.getLocation();
            Vector direction = loc.clone().subtract(center).toVector();
            Vector velocity = direction.add(spewDir).normalize();

            double xSquared = loc.distanceSquared(center);
            double magnitude = (1 / 1.3 * SQRT_PI) * Math.pow(Math.E,-0.2f * xSquared) - (0.003 * xSquared) + 0.3;

            velocity.multiply(magnitude);
            FallingBlock block = bukkitWorld.spawnFallingBlock(loc,b.getBlockData());
            block.setVelocity(velocity);
        }
    }
}
