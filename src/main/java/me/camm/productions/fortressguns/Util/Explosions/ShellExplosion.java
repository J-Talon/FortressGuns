package me.camm.productions.fortressguns.Util.Explosions;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import me.camm.productions.fortressguns.Artillery.Projectiles.ProjectileExplosive;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.item.EntityTNTPrimed;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentProtection;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.BlockFireAbstract;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameters;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.bukkit.craftbukkit.v1_17_R1.event.CraftEventFactory;


import javax.annotation.Nullable;
import java.util.*;

public class ShellExplosion extends Explosion {


    private static final ExplosionDamageCalculator DEFAULT_CALCULATOR = new ExplosionDamageCalculator();  //
    protected ExplosionDecoration decoration;
    protected World world;   //f
    protected double locX, locY, locZ;    ///g,h,i
    protected List<BlockPosition> brokenBlocks;  //n
    protected float explosionRadius;   //k  (yield)
    protected Explosion.Effect effect;
    ///a = none, b = break, c = destroy

    protected boolean setFire;

    protected Random random;   //e
    
    protected ExplosionDamageCalculator calculator;   //m

    private final Map<EntityHuman, Vec3D> affectedPlayers;  ///o

    protected boolean destroysBlocks;

    protected ProjectileExplosive explosive;



    //literally the only reason why we're inheriting is so that the rest of the game
    //doesn't think we're doing something completely unfamiliar
    //the superclass Explosion is very inheritance unfriendly cause it uses
    //private variables for everything
    ///this is from NMS, partially remapped by me
    public ShellExplosion(World world, @Nullable Entity source, double locX, double locY, double locZ,
                          float explosionRadius, ExplosionDecoration decoration, ProjectileExplosive explosive, boolean destroysBlocks) {

        super(world, source, null, null, locX, locY, locZ, explosionRadius, false, Effect.a);
        this.decoration = decoration;

        this.world = world;
        this.locX = locX;
        this.locY = locY;
        this.locZ = locZ;
        this.explosionRadius = explosionRadius < 0 ? 0 : explosionRadius;
        brokenBlocks = new ArrayList<>();
        this.random = new Random();

        this.calculator = this.getCalculator(source);
        affectedPlayers = new HashMap<>();
        this.destroysBlocks = destroysBlocks;
        this.explosive = explosive;


    }

    public Map<EntityHuman, Vec3D> c() {
        return this.affectedPlayers;
    }

    public void clearBlocks() {
        this.brokenBlocks.clear();
    }

    public List<BlockPosition> getBlocks() {
        return this.brokenBlocks;
    }

    private ExplosionDamageCalculator getCalculator(@Nullable Entity entity) {
        return entity == null ? DEFAULT_CALCULATOR : new ExplosionDamageCalculatorEntity(entity);
    }


    public World getWorld() {
        return world;
    }

    public double getLocX() {
        return locX;
    }

    public double getLocY() {
        return locY;
    }

    public double getLocZ() {
        return locZ;
    }

    public float getExplosionRadius() {
        return explosionRadius;
    }


    public void playExplosion() {
        a();
        a(true);
        decoration.postExplosion(this);
    }


    //a = explode()
    public void a() {
        affectBlocks();
        affectEntities();
    }



    protected void affectEntities() {


        float explosionDiameter = this.explosionRadius * 2.0F;
        int minX = MathHelper.floor(locX - (double)explosionDiameter - 1.0);
        int maxX = MathHelper.floor(locX + (double)explosionDiameter + 1.0);

        int minY = MathHelper.floor(locY - (double)explosionDiameter - 1.0);
        int maxY = MathHelper.floor(locY + (double)explosionDiameter + 1.0);

        int minZ = MathHelper.floor(locZ - (double)explosionDiameter - 1.0);
        int maxZ = MathHelper.floor(locZ + (double)explosionDiameter + 1.0);


        ///min x,y,z max x,y,z
        List<Entity> affectedEntities = this.world.getEntities(this.j, new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ));
        Vec3D explosionSource = new Vec3D(locX, locY, locZ);

        for (Entity affected : affectedEntities) {

            if (affected.cx()) {  //cx = boolean ignoreExplosion()
                continue;
            }

            //e = distance squared

            ////if distance ratio <= 1, that means whatever it is, is inside the explosion radius
            double distanceRatio = Math.sqrt(affected.e(explosionSource)) / (double) explosionDiameter;

            if (distanceRatio > 1.0) {
                continue;
            }
            double diffX = affected.locX() - locX;
            double diffY = (affected instanceof EntityTNTPrimed ? affected.locY() :  affected.getHeadY()) - locY;
            double diffZ = affected.locZ() - locZ;

            double distance = Math.sqrt(diffX * diffX + diffY * diffY + diffZ * diffZ);
            if (distance == 0.0) {
                continue;
            }

            //normalizing the direction
            diffX /= distance;
            diffY /= distance;
            diffZ /= distance;

            ///a = getSeenPercent
            //so basically how much of the entity is exposed
            double entityExposure = getSeenPercent(explosionSource, affected);

            // I think the logic here is that we want to multiply the exposure rate by the distance
            // cause then damage also scales with distance
            double distExposure = (1.0 - distanceRatio) * entityExposure;

            float damage = (float) ((int) ((distExposure * distExposure + distExposure) / 2.0 * 7.0 * (double) explosionDiameter + 1.0));

            if (damage == 0)
                continue;

            CraftEventFactory.entityDamage = this.j;   ///j = the entity creating the explosion
            affected.forceExplosionKnockback = false;

            //b = damage source
            boolean wasDamaged = affected.damageEntity(this.b(), damage);

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

                if (affected instanceof EntityHuman entityhuman) {

                    //abilities.b = flying
                    if (!entityhuman.isSpectator() && (!entityhuman.isCreative() || !entityhuman.getAbilities().b)) {
                        this.affectedPlayers.put(entityhuman, new Vec3D(diffX * distExposure, diffY * distExposure, diffZ * distExposure));
                    }
                }

            }
        }
    }


    //extracted code from NMS
    protected void affectBlocks() {

        if (this.explosionRadius < 0.1F || !destroysBlocks) {
            return;
        }


        //create a new explosion game event
        this.world.a(this.j, GameEvent.v, new BlockPosition(locX, locY, locZ));
        Set<BlockPosition> brokenBlocks = new HashSet<>();


        //okay so this draws a 3D shape that looks a bit like the infamous wave function
        //I'm gonna call it a bump for now
        int wallX;
        int wallY;
        for(int wallZ = 0; wallZ < 16; ++wallZ) {
            for(wallX = 0; wallX < 16; ++wallX) {
                for(wallY = 0; wallY < 16; ++wallY) {

                    if (!(wallZ == 0 || wallZ == 15 || wallX == 0 || wallX == 15 || wallY == 0 || wallY == 15)) {
                        continue;
                    }

                    //x,y,z coordinates
                    //this initially makes a hollow cube
                    //n / 15 * 2 - 1 gives the coordinates of the wall
                    double bumpX = ((float)wallZ / 15.0F * 2.0F - 1.0F);
                    double bumpY = ((float)wallX / 15.0F * 2.0F - 1.0F);
                    double bumpZ = ((float)wallY / 15.0F * 2.0F - 1.0F);

                    double length = Math.sqrt(bumpX * bumpX + bumpY * bumpY + bumpZ * bumpZ);

                    //performing a transformation
                    //which transforms the cube into a bump
                    bumpX /= length;
                    bumpY /= length;
                    bumpZ /= length;
                    //w = random
                    float power = this.explosionRadius * (0.7F + this.world.w.nextFloat() * 0.6F);

                    //direction of an explosion fragment
                    double vectorX = locX;
                    double vectorY = locY;
                    double vectorZ = locZ;

                    ///this expansion factor is probably from
                    //the game devs testing what value is the best for expansion without
                    //accidently missing a block or checking a block more than once
                    final double EXPANSION = 0.30000001192092896;

                    //air resistance affecting the explosion
                    final double AIR_RESIST = 0.22500001F;

                    while (power > 0) {
                        BlockPosition block = new BlockPosition(vectorX, vectorY, vectorZ);

                        //is in world bounds
                        if (!this.world.isValidLocation(block)) {
                            break;
                        }

                        IBlockData iblockdata = this.world.getType(block);
                        Fluid fluid = this.world.getFluid(block);

                        //calculator.a = getBlockExplosionResistance
                        Optional<Float> blockResistance = this.calculator.a(this, this.world, block, iblockdata, fluid);

                        if (blockResistance.isPresent()) {
                            power -= (blockResistance.get() + 0.3F) * 0.3F;
                        }

                        //a = shouldBlockExplode
                        if (power > 0.0F && this.calculator.a(this, this.world, block, iblockdata, power)) {
                            brokenBlocks.add(block);
                        }

                        //expanding the vectors of the bump
                        vectorX += bumpX * EXPANSION;
                        vectorY += bumpY * EXPANSION;
                        vectorZ += bumpZ * EXPANSION;

                        power -= AIR_RESIST;
                    }
                }
            }
        }

        this.brokenBlocks.addAll(brokenBlocks);
        explosive.modifyExplosion(this);
    }



    public List<Block> getBukkitBlocksBroken() {
        List<Block> blocks = new ArrayList<>();
        org.bukkit.World bukkit = world.getWorld();
        for (BlockPosition pos: brokenBlocks) {
            Block block = bukkit.getBlockAt(pos.getX(), pos.getY(), pos.getZ());
            if (block.getType().isAir())
                continue;

            blocks.add(block);
        }
        return blocks;
    }



    public static float getSeenPercent(Vec3D vec3d, Entity entity) {
        AxisAlignedBB axisalignedbb = entity.getBoundingBox();


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



    //finalize explosion
    public void a(boolean showParticles) {

        decoration.decorate(this);

        if (!destroysBlocks) {
            return;
        }

        Collections.shuffle(this.brokenBlocks, this.world.w);
        Iterator<BlockPosition> iterator = this.brokenBlocks.iterator();


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

            enterDropCalculation(tempBlockMergeReference,position,data,nmsBlock,explosionRadius);
        }


        //adding entities to world???
        for (Pair<ItemStack, BlockPosition> pair: tempBlockMergeReference) {
            net.minecraft.world.level.block.Block.a(world,pair.getSecond(),pair.getFirst());
        }
    }



    protected void enterDropCalculation(ObjectArrayList<Pair<ItemStack, BlockPosition>> trackingList, BlockPosition blockPos, IBlockData data, net.minecraft.world.level.block.Block block, float yield) {

        BlockPosition immutablePos = blockPos.immutableCopy();
        this.world.getMethodProfiler().enter("explosion_blocks");

        //block a = drop from explosion
        if (block.a(this) && this.world instanceof WorldServer) {
            TileEntity tileentity = data.isTileEntity() ? this.world.getTileEntity(blockPos) : null;

            LootTableInfo.Builder itemDropCalculator = (new LootTableInfo.Builder((WorldServer)this.world)).
                    a(this.world.w).set(LootContextParameters.f, Vec3D.a(blockPos)).set(LootContextParameters.i, ItemStack.b).
                    setOptional(LootContextParameters.h, tileentity).setOptional(LootContextParameters.a, this.j);

            if (destroysBlocks || yield < 1.0F) {
                itemDropCalculator.set(LootContextParameters.j, 1.0F / yield);
            }

            ///blockdata.a = get drops
            data.a(itemDropCalculator).forEach((itemstack) -> {
                mergeNext(trackingList, itemstack, immutablePos);
            });
        }

        ///setTypeAndData = setBlock
        this.world.setTypeAndData(blockPos, Blocks.a.getBlockData(), 3);

        //block.wasExploded is basically the reaction for the exploded block
        block.wasExploded(this.world, blockPos, this);
        this.world.getMethodProfiler().exit();
    }





    protected void setFire(){
        if (this.setFire) {
            for (BlockPosition current : this.brokenBlocks) {

                if (this.random.nextInt(3) == 0 &&
                        this.world.getType(current).isAir() &&
                        this.world.getType(current.down()).i(this.world, current.down()) &&
                        !CraftEventFactory.callBlockIgniteEvent(this.world, current.getX(), current.getY(), current.getZ(), this).isCancelled()) {

                    this.world.setTypeUpdate(current, BlockFireAbstract.a(this.world, current));
                }
            }
        }
    }



    //merge item stacks?
    protected static void mergeNext(ObjectArrayList<Pair<ItemStack, BlockPosition>> items, ItemStack mergeWith, BlockPosition mergePos) {

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



}
