package me.camm.productions.fortressguns.Explosion.Abstract;

import me.camm.productions.fortressguns.Util.Tuple2;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Explosion;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;

public abstract class ExplosionFunctional extends ExplosionFG {

    protected boolean destructive;
    protected Explosion stupid;

    protected float radius;
    protected Entity source;

    public ExplosionFunctional(double x, double y, double z, World world, float radius, boolean destructive, Entity source) {
        super(x,y,z, world);
        this.radius = radius;
        this.destructive = destructive;
        this.source = source;
        stupid = new Explosion(null, ((CraftEntity)source).getHandle(),0,0,0,0);
    }


    protected DamageSource getDamageSource() {
        return DamageSource.explosion(stupid);
    }



    protected void damageEntity(Entity affected, double exposure) {

        if (affected.isInvulnerable()) {
            return;
        }

        Vector position = new Vector(x,y,z);
        double distanceSquared = affected.getLocation().toVector().distanceSquared(position);

        double falloff = damageFalloff(distanceSquared);
        falloff *= exposure;


        net.minecraft.world.entity.Entity nms = ((CraftEntity)affected).getHandle();
        nms.damageEntity(getDamageSource(), (float)falloff);

        Vector knockback = affected.getLocation().toVector().subtract(position);
        if (knockback.lengthSquared() == 0)
            return;

        if (!(affected instanceof Attributable attributable))
            return;

        AttributeInstance instance = attributable.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
        double resistance = (instance == null ? 0 : instance.getBaseValue());
        resistance = Math.min(1, resistance); //in case some bloke has a value more than 1


        knockback.normalize();
        knockback.multiply(falloff/getMaxDamage()).multiply((1 - resistance) * 1.5f);


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

    public float getRadius() {
        return radius;
    }


    /*
     *
     * @param distanceSquared Distance to target
     * @return The damage as a function of distance [0 - maxDamage]
     */
    public abstract double damageFalloff(double distanceSquared);


}
