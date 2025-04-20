package me.camm.productions.fortressguns.Artillery.Projectiles.HeavyShell;


import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ArtilleryProjectile;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileArrowFG;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileExplosive;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.EntityEnderman;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;



import javax.annotation.Nullable;


public abstract class HeavyShell extends ProjectileArrowFG implements ProjectileExplosive {


    protected Artillery source;
    public HeavyShell(World world, double locX, double locY, double locZ, @Nullable EntityPlayer shooter, Artillery source) {
        super(world, locX, locY, locZ, shooter);
        this.source = source;
    }


    ///code to allow projectile-projectile collisions
    @Override
    protected boolean a(Entity entity) {

        if (!entity.isSpectator() && entity.isAlive() && !(entity.getEntityType() == EntityTypes.w)) {
            Entity entity1 = shooter;
            return entity1 == null || !entity1.isSameVehicle(entity);
            //so either the bullet has no shooter, and they're not stacked and not enderman
            //removed the check isInteractable()
        } else {
            return false;
        }
    }

    @Override
    public boolean onEntityHit(Entity hitEntity, Vec3D entityPosition) {
        if (hitEntity.getEntityType() == EntityTypes.w) {
            return false;
        }

        if (hitEntity instanceof ArtilleryProjectile) {
            ((ArtilleryProjectile) hitEntity).remove();
        }

        return true;
    }

    @Override
    public void remove() {
        this.die();
    }

    @Override
    public void inactiveTick() {
        explode(getPositionVector());
    }
}
