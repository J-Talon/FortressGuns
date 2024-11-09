package me.camm.productions.fortressguns.Artillery.Projectiles.LightShell;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Projectiles.ArtilleryProjectile;
import me.camm.productions.fortressguns.Artillery.Projectiles.ProjectileExplosive;
import me.camm.productions.fortressguns.Util.DamageSource.GunSource;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntityFireball;
import net.minecraft.world.entity.projectile.EntitySnowball;
import net.minecraft.world.entity.projectile.IProjectile;
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
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftMagicNumbers;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.awt.*;


public abstract class LightShell extends EntitySnowball implements ArtilleryProjectile {


    @Nullable
    protected EntityHuman gunOperator;
    private static final ItemStack SPRITE = new ItemStack(Items.pD);

    protected Artillery source;





    //world may be null
    public LightShell(@Nullable World world, double x, double y, double z, @Nullable EntityHuman human, Artillery source) {
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

        int damage = 10;

        Entity hit = position.getEntity();


        if (gunOperator != null) {
            DamageSource source = GunSource.gunShot(gunOperator);
            hit.damageEntity(source, damage);
        }
        else
            hit.damageEntity(DamageSource.n, damage); //generic damage

        //hurt ticks I believe this is
        if (hit instanceof EntityLiving) {
            hit.W = 0;
        }
        else  if (hit instanceof ProjectileExplosive) {
            ((ProjectileExplosive)hit).explode(null);
        }
        else if (hit instanceof IProjectile) {

            if (hit instanceof EntityFireball) {
                org.bukkit.World world = getWorld().getWorld();
                Vec3D pos = position.getPos();
                EntityFireball ball = ((EntityFireball)hit);
                Entity shooter = ball.getShooter();

                world.createExplosion(new Location(world, pos.getX(), pos.getY(), pos.getZ()), ball.bukkitYield,
                        ball.isIncendiary,true, shooter == null ? null: shooter.getBukkitEntity());
            }
            hit.die();
        }

        preHit(position);
        this.die();
    }



    @Override
    protected void a(MovingObjectPosition hit) {

        MovingObjectPosition.EnumMovingObjectType movingobjectposition_enummovingobjecttype = hit.getType();
        if (movingobjectposition_enummovingobjecttype == MovingObjectPosition.EnumMovingObjectType.c) {
            this.a((MovingObjectPositionEntity)hit);
        } else if (movingobjectposition_enummovingobjecttype == MovingObjectPosition.EnumMovingObjectType.b) {
            this.a((MovingObjectPositionBlock)hit);
        }

        if (movingobjectposition_enummovingobjecttype != MovingObjectPosition.EnumMovingObjectType.a) {
            this.a(GameEvent.I, this.getShooter());
        }

        if (hit instanceof MovingObjectPositionBlock) {

            Vec3D motion = getMot();

            org.bukkit.World bukkit = getWorld().getWorld();
            BlockPosition position = ((MovingObjectPositionBlock) hit).getBlockPosition();
            Block block = bukkit.getBlockAt(position.getX(),position.getY(), position.getZ());

            Material mat = block.getType();

            motion = motion.d().a(0.1f);

            if (!mat.isAir()) {
                Location loc = new Location(bukkit, position.getX(), position.getY(), position.getZ());
                bukkit.spawnParticle(Particle.BLOCK_CRACK,loc, 10, 0, 0, 0, block.getBlockData());

                net.minecraft.world.level.block.Block nms = CraftMagicNumbers.getBlock(mat);

                int color = nms.s().al;
                if (color == 0) {
                    color = 12895428; // light gray color rgb(196, 196, 196)
                }

                Particle.DustOptions options = new Particle.DustOptions(org.bukkit.Color.fromRGB(color),1);
                bukkit.spawnParticle(Particle.REDSTONE,new Location(bukkit, locX(), locY(), locZ()),
                        30,0.3,0.3,0.3,1,options);
            }

            if (mat == Material.TNT) {
                block.setType(Material.AIR);
                TNTPrimed primed = bukkit.spawn(new Location(bukkit, position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5),
                        TNTPrimed.class);
                motion = motion.d().a(0.1f);  //mult -0.1 and normalize
                primed.setVelocity(new Vector(motion.getX(), motion.getY(), motion.getZ()));
            }
            else {
                float hardness = mat.getHardness();
                if (hardness < Material.DIRT.getHardness() && hardness >= 0) {
                    block.breakNaturally();
                }
            }
        }

        preHit(hit);
        this.die();
    }


    @Override
    public void a(byte var0) {};

}
