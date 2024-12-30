package me.camm.productions.fortressguns.Artillery.Projectiles.HeavyShell;


import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Util.Explosions.ShellExplosion;
import net.minecraft.world.entity.EntityTypes;


import net.minecraft.world.entity.item.EntityFallingBlock;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.Vec3D;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import org.bukkit.block.Container;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_17_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftVector;

import org.bukkit.entity.Player;

import org.bukkit.event.entity.CreatureSpawnEvent;

import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;



public class ExplosiveHeavyShell extends StandardHeavyShell {



    private static float hitDamage = 10;
    private static float explosionPower = 4;


    private Vector spewDir = null;
    private Location center = null;

    public ExplosiveHeavyShell(EntityTypes<? extends EntityArrow> entitytypes, double d0, double d1, double d2, World world, @Nullable Player shooter) {
        super(entitytypes, d0, d1, d2, world, shooter);
    }


    @Override
    public float getHitDamage() {
        return hitDamage;
    }

    public static void setHitDamage(float hitDamage) {
        ExplosiveHeavyShell.hitDamage = hitDamage;
    }

    @Override
    public float getExplosionPower() {
        return explosionPower;
    }

    public static void setExplosionPower(float explosionPower) {
        ExplosiveHeavyShell.explosionPower = explosionPower;
    }

    @Override
    public void preHit(@Nullable MovingObjectPosition pos) {

        if (pos == null) {
            super.preHit((MovingObjectPosition) null);
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
            super.preHit(pos);
            return;
        }

        Block bukkitBlock = centerPoint.getHitBlock();
        BlockFace face = centerPoint.getHitBlockFace();

        if (bukkitBlock == null || face == null) {
            super.preHit(pos);
            return;
        }

        spewDir = face.getDirection();
        center = centerPoint.getHitPosition().add(spewDir.clone().multiply(-3)).toLocation(bukkitWorld);
        super.preHit(pos);
    }


    @Override
    public void modifyExplosion(ShellExplosion explosion) {

        if (spewDir == null) {
            return;
        }

        double SQRT_PI = Math.sqrt(Math.PI);

        List<Block> blocks = explosion.getBukkitBlocksBroken();

        for (Block bukkitBlock: blocks) {

            Location loc = bukkitBlock.getLocation();
            double xSquared = loc.distanceSquared(center);
            double magnitude = (1 / 1.3 * SQRT_PI) * Math.pow(Math.E,-0.2f * xSquared) - (0.003 * xSquared) + 0.3;
            if (magnitude <= 0.1f) {
                continue;
            }

            if (bukkitBlock.getState() instanceof Container) {
                continue;
            }

            Collection<ItemStack> drops = bukkitBlock.getDrops();
            if (drops.isEmpty())
                continue;

            IBlockData res = null;
            boolean dropsItems = false;

            for (ItemStack stack: drops) {
                Material next = stack.getType();
                if (next.isBlock() && !next.isAir() && next.getBlastResistance() > 0) {
                    net.minecraft.world.level.block.Block nms = CraftMagicNumbers.getBlock(next);
                    res = nms.getBlockData();
                    break;
                }
                else if (next.isItem()) {
                    dropsItems = true;
                }
            }

            if (dropsItems || res == null)
                continue;


            Vector direction = loc.clone().subtract(center).toVector();
            Vector velocity = direction.add(spewDir).normalize();
            velocity.multiply(magnitude);

            EntityFallingBlock entity = new EntityFallingBlock(getWorld(),loc.getX(), loc.getY(),loc.getZ(),res);
            entity.b = 1;
            getWorld().addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
            entity.setMot(CraftVector.toNMS(velocity));


        }
    }
}
