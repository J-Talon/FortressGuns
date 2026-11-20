package me.camm.productions.fortressguns.Artillery.Projectiles.Napalm;


import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileFG;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileType;
import me.camm.productions.fortressguns.Explosion.ExplosionFactory;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import org.bukkit.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import org.bukkit.World;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;


import javax.annotation.Nullable;


public class NapalmShell extends AbstractNapalm implements ProjectileType.INapalmShell {


    protected Artillery source;
    private World world;
    private Player shooter;
    public NapalmShell(World world, double locX, double locY, double locZ, @Nullable Player shooter, Artillery source) {
        super(world, locX, locY, locZ, shooter, source);
        this.source = source;
        this.world = world;
        this.shooter = shooter;
        setCritical(true);
    }

    @Override
    public void tick() {
        super.tick();
    }


    // I stole this from lightshell and have no idea what it does
//    @Override
//    protected boolean a(Entity entity) {
//
//        if (!entity.isSpectator() && entity.isAlive() && !(entity.getEntityType() == EntityTypes.w)) {
//            Entity entity1 = shooter;
//            return entity1 == null || !entity1.isSameVehicle(entity);
//        } else {
//            return false;
//        }
//    }

    @Override
    public boolean onEntityHit(Entity hitEntity, Vector entityPosition) {
        Location loc = this.getBukkitEntity().getLocation();
        ExplosionFactory.get().napalmExplosion(world, shooter, loc.getX(), loc.getY(), loc.getZ(), 25);
        remove();
        return false;
    }

    @Override
    public boolean onBlockHit(Vector exactHitPosition, BlockFace blockFace, Block hitBlock) {
        Location loc = this.getBukkitEntity().getLocation();
        ExplosionFactory.get().napalmExplosion(world, shooter, loc.getX(), loc.getY(), loc.getZ(), 25);
        remove();
        return false;
    }


    @Override
    public void onWaterEnter() {
        Vec3D motion = getMot();
        ExplosionFactory.get().largeSplashExplosion(world, locX()-(0.25* motion.getX()), locY()-(0.25*motion.getY()), locZ()-(0.25*motion.getZ()));
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
