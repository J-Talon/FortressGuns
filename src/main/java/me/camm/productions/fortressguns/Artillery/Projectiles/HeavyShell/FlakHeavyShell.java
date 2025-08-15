package me.camm.productions.fortressguns.Artillery.Projectiles.HeavyShell;


import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileExplosive;
import me.camm.productions.fortressguns.Explosion.ExplosionFactory;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.World;

import net.minecraft.world.phys.Vec3D;


import javax.annotation.Nullable;

public class FlakHeavyShell extends HeavyShell implements ProjectileExplosive {


    private static float hitDamage = 10;
    private static float explosionPower = 4;


    int flightTime;
    double explodeTime;

    public FlakHeavyShell(World world, double d0, double d1, double d2, @Nullable EntityPlayer shooter, Artillery source) {
        super(world, d0, d1, d2, shooter, source);
        flightTime = 0;
        explodeTime = 1;
    }

    @Override
    public float getWeight() {
        return 0;
    }


    @Override
    public float getHitDamage() {
        return hitDamage;
    }

    public static void setHitDamage(float hitDamage) {
        FlakHeavyShell.hitDamage = hitDamage;
    }

    @Override
    public float getExplosionPower() {
        return explosionPower;
    }

    public static void setExplosionPower(float explosionPower) {
        FlakHeavyShell.explosionPower = explosionPower;
    }

    public void setExplodeTime(double time) {
        this.explodeTime = time;
    }

    public void setTerminus(Entity target) {

        if (target != null) {

            double speed = this.getMot().f();
            double delX = target.locX() - locX();
            double delY = target.locY() - locY();
            double delZ = target.locZ() - locZ();

            double distance = Math.sqrt(delX * delX + delY * delY + delZ * delZ);
            explodeTime = (int)(distance / speed);
        }

    }



    @Override
    public void tick(){
        super.tick();

        if (flightTime > explodeTime) {
            explode(null);
            return;
        }

        flightTime ++;
    }


    @Override
    public void explode(@Nullable Vec3D hit) {
        this.die();
        if (hit == null)
            ExplosionFactory.flakHeavyExplosion(bukkitWorld,this.getBukkitEntity(),locX(),locY(),locZ(), getExplosionPower());
        else
            ExplosionFactory.flakHeavyExplosion(bukkitWorld,this.getBukkitEntity(),hit.getX(),hit.getY(),hit.getZ(), getExplosionPower());
    }


    @Override
    public boolean onBlockHit(Vec3D exactHitPosition, EnumDirection blockFace, BlockPosition hitBlock) {
        explode(exactHitPosition);
        return true;
    }


    @Override
    public boolean onEntityHit(Entity hitEntity, Vec3D entityPosition) {

        if (super.onEntityHit(hitEntity, entityPosition)) {
            return true;
        }

        explode(getPositionVector());
        return true;
    }



}
