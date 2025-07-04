package me.camm.productions.fortressguns.Artillery.Projectiles.LightShell;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileExplosive;
import me.camm.productions.fortressguns.Explosion.ExplosionFactory;
import me.camm.productions.fortressguns.Util.DamageSource.GunSource;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;
import org.jetbrains.annotations.Nullable;


/**
 * @author CAMM
 */
public class FlakLightShell extends LightShell implements ProjectileExplosive
{

    private static float hitDamage = 10;
    private org.bukkit.entity.Entity ignore;


    //(EntityTypes<? extends EntityArrow> entitytypes, double d0, double d1, double d2, World world)
    public FlakLightShell(World world, double x, double y, double z, EntityPlayer human, Artillery source) {
        super(world,x,y,z, human, source);
        ignore = null;
    }


    @Override
    public boolean onEntityHit(Entity hitEntity, Vec3D entityPosition) {
        ignore = hitEntity.getBukkitEntity();

        DamageSource source = GunSource.gunShot(shooter, this);
        hitEntity.damageEntity(source,getHitDamage());

        double length = this.getMot().f();
        final double FACTOR = 1.5;

        if (length == 0) {
            explode(getPositionVector());
            return true;
        }
        Vec3D motion = this.getMot();
        motion = motion.a(FACTOR/length);

        Vec3D velocity = hitEntity.getMot().e(motion);
        hitEntity.setMot(velocity);
        explode(getPositionVector());
        return true;
    }

    @Override
    public boolean onBlockHit(Vec3D exactHitPosition, EnumDirection blockFace, BlockPosition hitBlock) {
        explode(exactHitPosition);
        return true;
    }


    @Override
    public float getHitDamage() {
        return hitDamage;
    }

    public static void setHitDamage(float hitDamage) {
        FlakLightShell.hitDamage = hitDamage;
    }


    @Override
    public float getExplosionPower() {
        return 1;
    }


    @Override
    public float getWeight() {
        return 0.015F;
    }


    @Override
    public void explode(@Nullable Vec3D hit) {

        double x,y,z;
        if (hit == null) {
            Vec3D pos = getPositionVector();
            x = pos.getX();
            y = pos.getY();
            z = pos.getZ();
        }
        else {
            x = hit.getX();
            y = hit.getY();
            z = hit.getZ();
        }

        ExplosionFactory.flakLightExplosion(bukkitWorld(),getBukkitEntity(), shooter.getBukkitEntity(),x,y,z, ignore);
    }
}
