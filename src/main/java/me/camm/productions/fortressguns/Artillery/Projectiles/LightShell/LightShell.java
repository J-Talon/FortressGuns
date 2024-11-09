package me.camm.productions.fortressguns.Artillery.Projectiles.LightShell;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Projectiles.ArtilleryProjectile;
import me.camm.productions.fortressguns.Artillery.Projectiles.ProjectileExplosive;
import me.camm.productions.fortressguns.Util.DamageSource.GunSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
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
import org.jetbrains.annotations.Nullable;


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
        if (!entity.isSpectator() && entity.isAlive()) {
            Entity entity1 = gunOperator;
            return entity1 == null || !entity1.isSameVehicle(entity);

            //so either the bullet has no shooter, or they're not stacked
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

        preTerminate(position);
        this.die();
    }



    @Override
    protected void a(MovingObjectPosition var0) {

        MovingObjectPosition.EnumMovingObjectType movingobjectposition_enummovingobjecttype = var0.getType();
        if (movingobjectposition_enummovingobjecttype == MovingObjectPosition.EnumMovingObjectType.c) {
            this.a((MovingObjectPositionEntity)var0);
        } else if (movingobjectposition_enummovingobjecttype == MovingObjectPosition.EnumMovingObjectType.b) {
            this.a((MovingObjectPositionBlock)var0);
        }

        if (movingobjectposition_enummovingobjecttype != MovingObjectPosition.EnumMovingObjectType.a) {
            this.a(GameEvent.I, this.getShooter());
        }


        preTerminate(var0);

        this.die();
    }


    @Override
    public void a(byte var0) {}

}
