package me.camm.productions.fortressguns.Artillery.Projectiles.HeavyShell;


import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileFG;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileExplosive;
import me.camm.productions.fortressguns.Explosion.ExplosionFactory;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;
import org.jetbrains.annotations.Nullable;




public class HeavyShellHE extends HeavyShell implements ProjectileExplosive {



    private static float hitDamage = 10;
    private static float explosionPower = 4;


//    private Vector spewDir = null;
//    private Location center = null;

    public HeavyShellHE(World world, double d0, double d1, double d2, @Nullable EntityPlayer shooter, Artillery source) {
        super(world, d0, d1, d2, shooter, source);
    }


    @Override
    public float getHitDamage() {
        return hitDamage;
    }

    public static void setHitDamage(float hitDamage) {
        HeavyShellHE.hitDamage = hitDamage;
    }

    @Override
    public float getExplosionPower() {
        return explosionPower;
    }

    public static void setExplosionPower(float explosionPower) {
        HeavyShellHE.explosionPower = explosionPower;
    }

    @Override
    public boolean onBlockHit(Vec3D exactHitPosition, EnumDirection blockFace, BlockPosition hitBlock) {
        explode(exactHitPosition);
        return true;
    }

    @Override
    public boolean onEntityHit(Entity hitEntity, Vec3D entityPosition) {
        super.onEntityHit(hitEntity, entityPosition);

        if (hitEntity instanceof ProjectileFG) {
            ((ProjectileFG) hitEntity).remove();
        }

        explode(getPositionVector());
        return true;
    }

    @Override
    public void explode(@Nullable Vec3D hit) {

        //we'll worry about this in the future but
        //for a better explosion use a penetration value.
        //so if you trace and you find air, then go forwards. else go backwards.
        if (hit == null)
            ExplosionFactory.heavyShellExplosion(bukkitWorld,this.getBukkitEntity(), locX(), locY(), locZ(), getExplosionPower());
        else
            ExplosionFactory.heavyShellExplosion(bukkitWorld,this.getBukkitEntity(), hit.getX(), hit.getY(), hit.getZ(), getExplosionPower());
    remove();
    }


    @Override
    public float getWeight() {
        return 0.5F;
    }


    @Override
    public void remove() {
        this.die();
    }
}
