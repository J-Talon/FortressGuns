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


public class NapalmSpark extends AbstractNapalm implements ProjectileFG  {


    protected Artillery source;
    public NapalmSpark(World world, double locX, double locY, double locZ, @Nullable EntityPlayer shooter, Artillery source) {
        super(world, locX, locY, locZ, shooter);
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
        return true;
    }

    @Override
    public boolean onBlockHit(Vec3D exactHitPosition, EnumDirection blockFace, BlockPosition hitBlock) {
        Location impact = new Location(
                bukkitWorld,
                exactHitPosition.getX(),
                exactHitPosition.getY(),
                exactHitPosition.getZ()
        );

        int radius = 10;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    // sphere
                    if (x * x + y * y + z * z > radius * radius)
                        continue;

                    Location fireLocation = impact.clone().add(x, y, z);

                    if (!fireLocation.getBlock().getType().isAir())
                        continue;

                    Location below = fireLocation.clone().subtract(0, 1, 0);

                    if (!below.getBlock().getType().isSolid())
                        continue;

                    fireLocation.getBlock().setType(org.bukkit.Material.FIRE);
                }
            }
        }

        for (org.bukkit.entity.Entity entity : bukkitWorld.getNearbyEntities(impact, radius, radius, radius)) { // entity handling

            if (!(entity instanceof org.bukkit.entity.LivingEntity living))
                continue;

            // Make sure it is actually inside the spherical radius
            if (living.getLocation().distanceSquared(impact) > radius * radius)
                continue;

            // 10 seconds of fire
            living.setFireTicks(200);
        }

        this.die();
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
