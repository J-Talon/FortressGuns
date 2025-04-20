package me.camm.productions.fortressguns.Explosions.Abstract;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.item.EntityTNTPrimed;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentProtection;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.RayTrace;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameters;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.bukkit.craftbukkit.v1_17_R1.event.CraftEventFactory;


import java.util.*;

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


    protected float getSeenPercent(Entity entity) {
        AxisAlignedBB axisalignedbb = entity.getBoundingBox();
        Vec3D vec3d = new Vec3D(x,y,z);

        /*
        n2 - n1 is the length of a bounding box side
        d = max x  a = min x
        e = max y  b = min y
        f = max z  c = min z

         */
        double ratioX = 1.0 / ((axisalignedbb.d - axisalignedbb.a) * 2.0 + 1.0);
        double ratioY = 1.0 / ((axisalignedbb.e - axisalignedbb.b) * 2.0 + 1.0);
        double ratioZ = 1.0 / ((axisalignedbb.f - axisalignedbb.c) * 2.0 + 1.0);

        double d3 = (1.0 - Math.floor(1.0 / ratioX) * ratioX) / 2.0;
        double d4 = (1.0 - Math.floor(1.0 / ratioZ) * ratioZ) / 2.0;

        if (ratioX >= 0.0 && ratioY >= 0.0 && ratioZ >= 0.0) {
            int hits = 0;
            int total = 0;

            ///okay I think what they're doing here is a series of raytraces
            //so basically doing scanning and determining the ratio of what
            //hits or not
            for(float f = 0.0F; f <= 1.0F; f = (float)((double)f + ratioX)) {
                for(float f1 = 0.0F; f1 <= 1.0F; f1 = (float)((double)f1 + ratioY)) {
                    for(float f2 = 0.0F; f2 <= 1.0F; f2 = (float)((double)f2 + ratioZ)) {

                        //d = Math.lerp()
                        double d5 = MathHelper.d(f, axisalignedbb.a, axisalignedbb.d);
                        double d6 = MathHelper.d(f1, axisalignedbb.b, axisalignedbb.e);
                        double d7 = MathHelper.d(f2, axisalignedbb.c, axisalignedbb.f);


                        Vec3D vec3d1 = new Vec3D(d5 + d3, d6, d7 + d4);
                        if (entity.t.rayTrace(new RayTrace(vec3d1, vec3d, RayTrace.BlockCollisionOption.a, RayTrace.FluidCollisionOption.a, entity)).getType() == MovingObjectPosition.EnumMovingObjectType.a) {
                            ++hits;
                        }

                        ++total;
                    }
                }
            }

            return (float)hits / (float)total;
        } else {
            return 0.0F;
        }
    }


    protected DamageSource getDamageSource() {
        return DamageSource.explosion(stupid);
    }




    protected void damageEntity(Entity affected) {

        if (affected.cx()) {  //cx = boolean ignoreExplosion()
            return;
        }

        double distanceRatio = Math.sqrt(affected.e(source)) / (double) (radius * 2);
        if (distanceRatio > 1)
            return;

        double entityExposure = getSeenPercent(affected);

        // I think the logic here is that we want to multiply the exposure rate by the distance
        // cause then damage also scales with distance
        double distExposure = (1.0 - distanceRatio) * entityExposure;

        float damage = (float) ((int) ((distExposure * distExposure + distExposure) / 2.0 * 7.0 * radius + 1.0));

        if (damage == 0)
            return;

        double diffX = affected.locX() - x;
        double diffY = (affected instanceof EntityTNTPrimed ? affected.locY() :  affected.getHeadY()) - y;
        double diffZ = affected.locZ() - z;

        double distance = Math.sqrt(diffX * diffX + diffY * diffY + diffZ * diffZ);
        if (distance == 0.0) {
            return;
        }

        //normalizing the direction
        diffX /= distance;
        diffY /= distance;
        diffZ /= distance;

        CraftEventFactory.entityDamage = source;   ///j = the entity creating the explosion
        affected.forceExplosionKnockback = false;

        //b = damage source
        boolean wasDamaged = affected.damageEntity(getDamageSource(), damage);

        CraftEventFactory.entityDamage = null;

        //this won't affect falling blocks or TNT
        if (wasDamaged || affected.forceExplosionKnockback) {
            double dampened = distExposure;
            if (affected instanceof EntityLiving) {

                //a  = explosion knockback after dampening from protection
                dampened = EnchantmentProtection.a((EntityLiving)  affected, distExposure);
            }

            affected.setMot((affected.getMot().add(diffX * dampened, diffY * dampened, diffZ * dampened)));
            //wait why is there no motionChanged = true here...???

        }
    }



    public void processBlocks(Collection<BlockPosition> blocks) {

        Iterator<BlockPosition> iterator = blocks.iterator();
        /*
        So the thing about this list is that it seems like it's unused
        but no, it does actually serve a purpose

        the idea here is that when we're merging we want to ensure we're not doing any
        unnecessary stuff, so by having a reference list, we can do that.
         */
        ObjectArrayList<Pair<ItemStack, BlockPosition>> tempBlockMergeReference = new ObjectArrayList<>();


        while(iterator.hasNext()) {
            BlockPosition position;
            IBlockData data;
            net.minecraft.world.level.block.Block nmsBlock;

            do {
                //get the next block which isn't air
                position = iterator.next();
                data = this.world.getType(position);
                nmsBlock = data.getBlock();

            } while(data.isAir() && iterator.hasNext());

            calculateDrops(tempBlockMergeReference,position,data,nmsBlock,radius);
        }

        //adding entities to world
        for (Pair<ItemStack, BlockPosition> pair: tempBlockMergeReference) {
            net.minecraft.world.level.block.Block.a(world,pair.getSecond(),pair.getFirst());
        }
    }



    protected void calculateDrops(ObjectArrayList<Pair<ItemStack, BlockPosition>> trackingList, BlockPosition blockPos, IBlockData data, net.minecraft.world.level.block.Block block, float yield) {

        BlockPosition immutablePos = blockPos.immutableCopy();
        //block a = drop from explosion
        if (block.a(stupid) && this.world instanceof WorldServer) {
            TileEntity tileentity = data.isTileEntity() ? this.world.getTileEntity(blockPos) : null;

            LootTableInfo.Builder itemDropCalculator = (new LootTableInfo.Builder((WorldServer)this.world)).
                    a(this.world.w).set(LootContextParameters.f, Vec3D.a(blockPos)).set(LootContextParameters.i, ItemStack.b).
                    setOptional(LootContextParameters.h, tileentity).setOptional(LootContextParameters.a, source);

            if (destroysBlocks || yield < 1.0F) {
                itemDropCalculator.set(LootContextParameters.j, 1.0F / yield);
            }

            ///blockdata.a = get drops
            data.a(itemDropCalculator).forEach((itemstack) -> {
                mergeItems(trackingList, itemstack, immutablePos);
            });
        }

        ///setTypeAndData = setBlock
        this.world.setTypeAndData(blockPos, Blocks.a.getBlockData(), 3);

        //block.wasExploded is basically the reaction for the exploded block
        block.wasExploded(this.world, blockPos, stupid);
    }



    protected void mergeItems(ObjectArrayList<Pair<ItemStack, BlockPosition>> items, ItemStack mergeWith, BlockPosition mergePos) {

        final int MAX_STACK = 16;

        if (mergeWith.isEmpty()) {
            return;
        }

        for(int slot = 0; slot < items.size(); ++slot) {
            Pair<ItemStack, BlockPosition> pair = items.get(slot);
            ItemStack current = pair.getFirst();

            //a = can be merged
            if (EntityItem.a(current, mergeWith)) {
                //a  = merge
                ItemStack mergeResult = EntityItem.a(current, mergeWith, MAX_STACK);

                items.set(slot, Pair.of(mergeResult,pair.getSecond()));

                //if all of the items from mergeWith fit into the resulting item, making mergeWith have size 0
                if (mergeWith.isEmpty()) {
                    return;
                }
            }
        }

        items.add(Pair.of(mergeWith, mergePos));

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
