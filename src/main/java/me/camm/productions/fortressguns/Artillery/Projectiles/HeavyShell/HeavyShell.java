package me.camm.productions.fortressguns.Artillery.Projectiles.HeavyShell;


import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ArtilleryProjectile;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileArrowFG;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileExplosive;
import me.camm.productions.fortressguns.Explosion.ExplosionFactory;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.EntityEnderman;
import net.minecraft.world.entity.projectile.EntityFireball;
import net.minecraft.world.entity.projectile.IProjectile;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Fireball;


import javax.annotation.Nullable;


public abstract class HeavyShell extends ProjectileArrowFG  {


    protected Artillery source;
    public HeavyShell(World world, double locX, double locY, double locZ, @Nullable EntityPlayer shooter, Artillery source) {
        super(world, locX, locY, locZ, shooter);
        this.source = source;
        setCritical(true);
    }


    ///code to allow projectile-projectile collisions
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
        if (hitEntity.getEntityType() == EntityTypes.w) {   //enderman
            return false;
        }

        if (hitEntity instanceof ProjectileExplosive) {
            ((ProjectileExplosive) hitEntity).explode(null);
            return true;
        }

        //deflect
        if (hitEntity instanceof IProjectile) {
            Vec3D current = this.getPositionVector();
            Vec3D other = hitEntity.getPositionVector();
            float otherWeight = 0.05f;

            Vec3D thisDeflection;
            thisDeflection = other.a(current).d();  /// subtract, normalize


            if (hitEntity instanceof ArtilleryProjectile) {
                otherWeight = ((ArtilleryProjectile) hitEntity).getWeight();
            }

            Location loc = new Location(bukkitWorld, current.getX(), current.getY(), current.getZ());
            bukkitWorld.spawnParticle(Particle.ELECTRIC_SPARK,loc,5,0,0,0,1);
            bukkitWorld.playSound(loc, Sound.BLOCK_BELL_USE,2,1);

            Vec3D otherDeflection = thisDeflection.a(-1F);
            otherDeflection = otherDeflection.a(getWeight());
            hitEntity.setMot(hitEntity.getMot().add(otherDeflection.getX(), otherDeflection.getY(), otherDeflection.getZ()));

            thisDeflection = thisDeflection.a(otherWeight);
            setMot(getMot().add(thisDeflection.getX(), thisDeflection.getY(), thisDeflection.getZ()));
            return false;
        }

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
    public void inactiveTick() {
        remove();
    }

    @Override
    public float getWeight() {
        return 0.1F;
    }



}
