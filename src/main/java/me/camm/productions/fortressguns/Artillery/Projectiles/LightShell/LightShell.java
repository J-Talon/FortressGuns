package me.camm.productions.fortressguns.Artillery.Projectiles.LightShell;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Projectiles.ArtilleryProjectile;
import me.camm.productions.fortressguns.Artillery.Projectiles.ProjectileExplosive;
import me.camm.productions.fortressguns.Util.DamageSource.GunSource;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntitySnowball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;

import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.MovingObjectPositionEntity;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftMagicNumbers;
import org.bukkit.entity.Explosive;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;


public abstract class LightShell extends EntitySnowball implements ArtilleryProjectile {


    @Nullable
    protected EntityHuman gunOperator;
    private static final ItemStack SPRITE = new ItemStack(Items.pD);

    protected Artillery source;





    //world may be null
    public LightShell(@Nullable World world, double x, double y, double z, @Nullable EntityPlayer human, Artillery source) {
        super(world,x,y,z);
        gunOperator = human;
        this.source = source;
        setItem(SPRITE);

    }

    ///code to allow projectile-projectile collisions
    @Override
    protected boolean a(Entity entity) {

        if (!entity.isSpectator() && entity.isAlive() && !(entity.getEntityType() == EntityTypes.w)) {
            Entity entity1 = gunOperator;
            return entity1 == null || !entity1.isSameVehicle(entity);
            //so either the bullet has no shooter, and they're not stacked and not enderman
            //removed the check isInteractable()
        } else {
            return false;
        }
    }


    @Override
    protected void a(MovingObjectPositionEntity position) {



        Entity hit = position.getEntity();


        if (gunOperator != null) {
            DamageSource source = GunSource.gunShot(gunOperator,this);
            hit.damageEntity(source, getHitDamage());
        }
        else
            hit.damageEntity(DamageSource.n, getHitDamage()); //generic damage

        //hurt ticks I believe this is
        if (hit instanceof EntityLiving) {
            hit.W = 0;
        }
        else  if (hit instanceof ProjectileExplosive) {
            ((ProjectileExplosive)hit).explode(null);
        }
        else {
            org.bukkit.entity.Entity bukkit = hit.getBukkitEntity();
            org.bukkit.World bukkitWorld = getWorld().getWorld();
            Vec3D pos = position.getPos();

            if (bukkit instanceof Explosive) {

                Entity bukkitShooter = null;

                if (bukkit instanceof Projectile) {
                    try {
                        bukkitShooter = (Entity)(((Projectile) bukkit).getShooter());
                    }
                    catch (ClassCastException ignored) {
                    }
                }
                bukkitWorld.createExplosion(new Location(bukkitWorld, pos.getX(), pos.getY(), pos.getZ()),
                        ((Explosive) bukkit).getYield(), ((Explosive) bukkit).isIncendiary(),true,
                        (org.bukkit.entity.Entity) bukkitShooter);
            }

            hit.die();
        }

        preHit(position);
       // this.die();
    }



    @Override
    protected void a(MovingObjectPosition hit) {


        MovingObjectPosition.EnumMovingObjectType objectType = hit.getType();
        if (objectType == MovingObjectPosition.EnumMovingObjectType.c) {
            this.a((MovingObjectPositionEntity)hit);
        } else if (objectType == MovingObjectPosition.EnumMovingObjectType.b) {
            this.a((MovingObjectPositionBlock)hit);
        }

        if (objectType != MovingObjectPosition.EnumMovingObjectType.a) {
            this.a(GameEvent.I, this.getShooter());
        }

        if (hit instanceof MovingObjectPositionBlock) {

            org.bukkit.World bukkit = getWorld().getWorld();
            BlockPosition position = ((MovingObjectPositionBlock) hit).getBlockPosition();
            Block block = bukkit.getBlockAt(position.getX(),position.getY(), position.getZ());

            Material mat = block.getType();
            Collection<org.bukkit.inventory.ItemStack> drops = block.getDrops();
            for (org.bukkit.inventory.ItemStack item: drops)
            {
                Material itemMat = item.getType();
                if (itemMat.isBlock() && (!itemMat.isAir())) {
                    mat = itemMat;
                    break;
                }
            }

            Vec3D vec = hit.getPos();
            Vec3D mot = getMot().e().d().a(0.7);
            //0.7 is an arbitrary artistic decision
            //we're basically normalizing, reversing the vector and multiplying it by 0.7
            //to determine where we should spawn particles for an effect

            vec = vec.e(mot);
            Location effectLoc = new Location(bukkit, vec.getX(), vec.getY(), vec.getZ());

            if (!mat.isAir()) {


                bukkit.spawnParticle(Particle.BLOCK_CRACK,effectLoc, 30, 0.1, 0.1, 0.1,1, block.getBlockData());

                net.minecraft.world.level.block.Block nms = CraftMagicNumbers.getBlock(mat);


                int color = nms.s().al;
                if (color == 0) {
                    color = 12895428; // light gray color rgb(196, 196, 196)
                }

                Particle.DustOptions options = new Particle.DustOptions(org.bukkit.Color.fromRGB(color),1);
                bukkit.spawnParticle(Particle.REDSTONE,effectLoc, 17,0.3,0.3,0.3,1,options);
            }

            if (mat == Material.TNT) {
                block.setType(Material.AIR);
                TNTPrimed primed = bukkit.spawn(new Location(bukkit, position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5),
                        TNTPrimed.class);

                primed.setFuseTicks(0);
            }
            else {
                float hardness = mat.getHardness();
                if (hardness < Material.DIRT.getHardness() && hardness >= 0) {
                    block.breakNaturally();
                }
                bukkit.playSound(effectLoc,block.getBlockData().getSoundGroup().getHitSound(), SoundCategory.BLOCKS,1,0);
            }
        }

        preHit(hit);
    }


    //This was code in the superclass which basically showed snowball particles
    //we don't want snowball particles so this goes blank
    @Override
    public void a(byte var0) {};

}
