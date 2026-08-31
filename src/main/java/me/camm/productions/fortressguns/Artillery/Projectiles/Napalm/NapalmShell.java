package me.camm.productions.fortressguns.Artillery.Projectiles.Napalm;


import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileFG;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileArrowFG;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileExplosive;
import me.camm.productions.fortressguns.Artillery.Projectiles.HeavyShell.HeavyShell;
import me.camm.productions.fortressguns.Explosion.ExplosionFactory;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.projectile.IProjectile;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;


import javax.annotation.Nullable;


public class NapalmShell extends HeavyShell {


    protected Artillery source;
    public NapalmShell(World world, double locX, double locY, double locZ, @Nullable EntityPlayer shooter, Artillery source) {
        super(world, locX, locY, locZ, shooter, source);
        this.source = source;
        setCritical(true);
    }


    // I stole this from lightshell and have no idea what it does
    @Override
    protected boolean a(Entity entity) {

        if (!entity.isSpectator() && entity.isAlive() && !(entity.getEntityType() == EntityTypes.w)) {
            Entity entity1 = shooter;
            return entity1 == null || !entity1.isSameVehicle(entity);
        } else {
            return false;
        }
    }

    @Override
    public boolean onEntityHit(Entity hitEntity, Vec3D entityPosition) {
        // arson arson arson arson ARSON!

        // explode

        // send mini-napalms and ignite fire
        return true;
    }

    @Override
    public boolean onBlockHit(Vec3D exactHitPosition, EnumDirection blockFace, BlockPosition hitBlock) {
        return false;
    }


    @Override
    public void onWaterEnter() {
        Vec3D motion = getMot();
        ExplosionFactory.largeSplashExplosion(bukkitWorld,locX()-(0.25* motion.getX()), locY()-(0.25*motion.getY()), locZ()-(0.25*motion.getZ()));
        remove();
    }

    @Override
    public void remove() {
        this.die();
    }

    @Override
    public float getHitDamage() {
        return 0;
    }

    @Override
    public void inactiveTick() {
        remove();
    }

    @Override
    public float getWeight() {
        return 0.1F;
    }



}
