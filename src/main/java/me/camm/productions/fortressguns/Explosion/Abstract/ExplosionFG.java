package me.camm.productions.fortressguns.Explosion.Abstract;

import me.camm.productions.fortressguns.Util.Tuple2;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Explosion;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;


import java.util.*;

import static me.camm.productions.fortressguns.Util.MathLib.linearInterpolate;

public abstract class ExplosionFG {


    protected double x,y,z;
    protected World world;
    protected float radius;
    protected Entity source;
    protected static Random rand = new Random();
    protected boolean destroysBlocks;
    protected Explosion stupid;

    public ExplosionFG(double x, double y, double z, World world, float radius, Entity source, boolean destructive) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.radius = radius;
        this.source = source;
        this.destroysBlocks = destructive;
        stupid = new Explosion(null, ((CraftEntity)source).getHandle(),0,0,0,0);;
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

        if (affected.isInvulnerable()) {
            return;
        }

        Vector position = new Vector(x,y,z);
        float maxDamage = getMaxDamage();
        double distanceSquared = affected.getLocation().toVector().distanceSquared(position);

        double falloff = getFalloff(distanceSquared);
        falloff *= exposure;

        maxDamage *= falloff;

        net.minecraft.world.entity.Entity nms = ((CraftEntity)affected).getHandle();
        nms.damageEntity(getDamageSource(), maxDamage);

        Vector knockback = affected.getLocation().toVector().subtract(position);
        if (knockback.lengthSquared() == 0)
            return;

        knockback.normalize();
        knockback.multiply(falloff).multiply(1.5f);

        System.out.println("knockback:"+knockback);
        System.out.println("affected vel before:"+affected.getVelocity());
        System.out.println("falloff:"+falloff);

        Vector velocity = affected.getVelocity().add(knockback);

        affected.setVelocity(velocity);
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

            if (next.getState() instanceof Container) {
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

                ItemStack stack = items.getA();
                if (stack.getAmount() <= 0 || stack.getType().isAir())
                    continue;

                world.dropItem(items.getB().getLocation(),stack);
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

            Material type = stack.getType();
            if (!type.isItem() || !type.isBlock()) {
                continue;
            }

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
                drops.put(stack.getType(),current);
            }

        }
    }



    public abstract void perform();

    public abstract float getMaxDamage();


    //returns the damage as a function of distance [0-maxDamage]
    public abstract double getFalloff(double distanceSquared);

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
