package me.camm.productions.fortressguns.Artillery.Projectiles.LightShell;


import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ArtilleryProjectile;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileExplosive;
import me.camm.productions.fortressguns.Explosion.ExplosionFactory;
import me.camm.productions.fortressguns.Util.DamageSource.GunSource;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.projectile.IProjectile;
import net.minecraft.world.level.World;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.bukkit.entity.Explosive;
import org.bukkit.entity.Projectile;


public class StandardLightShell extends LightShell {

    private static float hitDamage = 5;


    public StandardLightShell(World world, double x, double y, double z, EntityPlayer human, Artillery source) {
        super(world, x, y, z, human, source);
    }

    @Override
    public boolean onEntityHit(Entity hitEntity, Vec3D entityPosition) {
        this.a(GameEvent.I, this.getShooter());

        if (shooter != null) {
            DamageSource source = GunSource.gunShot(shooter,this);
            hitEntity.damageEntity(source, getHitDamage());
        }
        else
            hitEntity.damageEntity(DamageSource.n, getHitDamage()); //generic damage

        //hurt ticks I believe this is
        if (hitEntity instanceof EntityLiving) {
            hitEntity.W = 0;
            this.remove();
            return true;
        }

        if (hitEntity instanceof ProjectileExplosive) {
            ((ProjectileExplosive) hitEntity).explode(null);
            this.remove();
            return true;
        }

        if (hitEntity instanceof ArtilleryProjectile || hitEntity instanceof IProjectile) {
            float otherWeight = (hitEntity instanceof ArtilleryProjectile) ? ((ArtilleryProjectile) hitEntity).getWeight() : 0.1f;
            Vec3D thisDeflection = hitEntity.getPositionVector().a(getPositionVector()).d();  /// subtract
            Vec3D otherDeflection = thisDeflection.a(-1F);
            otherDeflection = otherDeflection.a(getWeight());
            hitEntity.setMot(hitEntity.getMot().add(otherDeflection.getX(), otherDeflection.getY(), otherDeflection.getZ()));

            thisDeflection = thisDeflection.a(otherWeight);
            setMot(getMot().add(thisDeflection.getX(), thisDeflection.getY(), thisDeflection.getZ()));
            return false;
        }


        org.bukkit.entity.Entity bukkit = hitEntity.getBukkitEntity();
        org.bukkit.World bukkitWorld = getWorld().getWorld();

        if (bukkit instanceof Explosive) {

            Entity bukkitShooter = null;

            if (bukkit instanceof Projectile) {
                try {
                    bukkitShooter = (Entity)(((Projectile) bukkit).getShooter());
                }
                catch (ClassCastException ignored) {
                }
            }
            bukkitWorld.createExplosion(new Location(bukkitWorld, entityPosition.getX(), entityPosition.getY(), entityPosition.getZ()),
                    ((Explosive) bukkit).getYield(), ((Explosive) bukkit).isIncendiary(),true,
                    (org.bukkit.entity.Entity) bukkitShooter);
            hitEntity.die();
            remove();
            return true;

        }
        remove();
        return true;
    }

    @Override
    public boolean onBlockHit(Vec3D exactHitPosition, EnumDirection blockFace, BlockPosition hitBlock) {
        return super.onBlockHit(exactHitPosition, blockFace, hitBlock);
    }


    @Override
    public void onWaterEnter() {
        ExplosionFactory.smallSplashExplosion(bukkitWorld(),locX(), locY(), locZ());
    }

    @Override
    public float getWeight() {
        return 0.005F;
    }



    public static void setHitDamage(float hitDamage) {
        StandardLightShell.hitDamage = hitDamage;
    }


    @Override
    public float getHitDamage() {
        return hitDamage;
    }
}
