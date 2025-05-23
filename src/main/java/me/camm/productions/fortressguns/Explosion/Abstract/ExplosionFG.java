package me.camm.productions.fortressguns.Explosion.Abstract;

import me.camm.productions.fortressguns.Util.Tuple2;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.item.EntityTNTPrimed;

import net.minecraft.world.item.enchantment.EnchantmentProtection;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.RayTrace;

import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.event.CraftEventFactory;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;


import java.util.*;

import static me.camm.productions.fortressguns.Util.MathLib.linearInterpolate;

public abstract class ExplosionFG {


    protected double x,y,z;
    protected World world;
    protected float radius;
    protected Entity source;
    protected static Random rand = new Random();
    protected boolean destroysBlocks;
    protected static Explosion stupid = new Explosion(null, null,0,0,0,0);

    public ExplosionFG(double x, double y, double z, World world, float radius, Entity source, boolean destructive) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.radius = radius;
        this.source = source;
        this.destroysBlocks = destructive;
    }
    /*
    Ideally this should be the sequence of events:
    get blocks to mutate
    get entities to damage
    preMutation
    mutate entities
    mutate blocks
    postMutation

    Pre-mutation may occur before getting the blocks and/or entities. Doesn't matter because it should
    not be a mutating operation
     */

    protected DamageSource getDamageSource() {
        return DamageSource.explosion(stupid);
    }



    protected void damageEntity(Entity affected, double exposure) {

        net.minecraft.world.entity.Entity nms = ((CraftEntity)affected).getHandle();
        net.minecraft.world.entity.Entity sourceNMS = ((CraftEntity)affected).getHandle();

        if (nms.cx()) {  //cx = boolean ignoreExplosion()
            return;
        }

        double distanceRatio = Math.sqrt(nms.e(sourceNMS)) / (double) (radius * 2);
        if (distanceRatio > 1)
            return;

        // I think the logic here is that we want to multiply the exposure rate by the distance
        // cause then damage also scales with distance
        double distExposure = (1.0 - distanceRatio) * exposure;

        float damage = (float) ((int) ((distExposure * distExposure + distExposure) / 2.0 * 7.0 * radius + 1.0));

        if (damage == 0)
            return;


        Location loc = affected.getLocation();
        double diffX = loc.getX() - x;
        double diffY = (nms instanceof EntityTNTPrimed ? loc.getY() :  nms.getHeadY()) - y;
        double diffZ = loc.getZ() - z;

        double distance = Math.sqrt(diffX * diffX + diffY * diffY + diffZ * diffZ);
        if (distance == 0.0) {
            return;
        }

        //normalizing the direction
        diffX /= distance;
        diffY /= distance;
        diffZ /= distance;

        CraftEventFactory.entityDamage = sourceNMS;   ///j = the entity creating the explosion
        nms.forceExplosionKnockback = false;

        //b = damage source
        boolean wasDamaged = nms.damageEntity(getDamageSource(), damage);

        CraftEventFactory.entityDamage = null;

        //this won't affect falling blocks or TNT
        if (wasDamaged || nms.forceExplosionKnockback) {
            double dampened = distExposure;
            if (nms instanceof EntityLiving) {

                //a  = explosion knockback after dampening from protection
                dampened = EnchantmentProtection.a((EntityLiving)  nms, distExposure);
            }

            nms.setMot((nms.getMot().add(diffX * dampened, diffY * dampened, diffZ * dampened)));
            //wait why is there no motionChanged = true here...???

        }
    }


    protected void processDrops(Collection<Block> blocks) {

        Iterator<Block> iterator = blocks.iterator();

        Map<Material, List<Tuple2<ItemStack, Block>>> droppedItems = new HashMap<>();

        while(iterator.hasNext()) {
            Block next = iterator.next();
            Material mat = next.getType();

            if (mat.isAir() || mat == Material.TNT) {
                blockReaction(next);
                continue;
            }

            if (next.getBlockData() instanceof Container) {
                next.breakNaturally();
                continue;
            }
            mergeDrops(next, droppedItems);
            next.setType(Material.AIR);
        }
        dropItems(droppedItems);
    }

    protected void dropItems(Map<Material, List<Tuple2<ItemStack, Block>>> droppedItems) {
        for (List<Tuple2<ItemStack, Block>> positions: droppedItems.values()) {
            for (Tuple2<ItemStack, Block> items: positions) {
                world.dropItem(items.getB().getLocation(), items.getA());
            }
        }
    }



    protected void blockReaction(Block block) {
        BlockPosition position = new BlockPosition(block.getX(), block.getY(), block.getZ());
        net.minecraft.world.level.World worldNMS = ((CraftWorld)world).getHandle();
        net.minecraft.world.level.block.Block blockNMS = worldNMS.getType(position).getBlock();
        blockNMS.wasExploded(worldNMS, position, stupid);
    }

    protected void mergeDrops(Block block, Map<Material, List<Tuple2<ItemStack, Block>>> drops) {
        final ItemStack MIDPOINT = new ItemStack(Material.IRON_PICKAXE);
        final int MAX_STACK = 16;

        Collection<ItemStack> loot = block.getDrops(MIDPOINT);
        for (ItemStack stack: loot) {
            if (rand.nextFloat() > 0.25f) {
                continue;
            }
            List<Tuple2<ItemStack, Block>> current = drops.getOrDefault(stack.getType(), new ArrayList<>());

            boolean added = false;
            for (Tuple2<ItemStack, Block> tuple: current) {
                ItemStack residing = tuple.getA();

                if (stack.getAmount() == 0) {
                    added = true;
                    break;
                }

                if (!residing.isSimilar(stack))
                    continue;

                if (residing.getMaxStackSize() == residing.getAmount())
                    continue;

                int input = Math.min(MAX_STACK - residing.getAmount(), stack.getAmount());

                if (input <= 0)
                    continue;

                stack.setAmount(stack.getAmount() - input);
                residing.setAmount(residing.getAmount() + input);
            }

            if (!added) {
                current.add(new Tuple2<>(stack, block));
            }

        }
    }



    public abstract void perform();

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public World getWorld() {
        return world;
    }

    public float getRadius() {
        return radius;
    }

    public Entity getSource() {
        return source;
    }
}
