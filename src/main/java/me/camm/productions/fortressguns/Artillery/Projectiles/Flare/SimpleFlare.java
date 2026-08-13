package me.camm.productions.fortressguns.Artillery.Projectiles.Flare;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileFG;
import me.camm.productions.fortressguns.Artillery.Projectiles.Missile.SimpleMissile;
import me.camm.productions.fortressguns.Explosion.ExplosionFactory;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Handlers.MissileLockNotifier;
import me.camm.productions.fortressguns.Util.Math.MathFG;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SimpleFlare extends AbstractFlare {
    private static final double directionVariance = 0.5;

    private static final double successChance = 0.9; // % chance per missile to affect it
    private final Vector direction;
    private int lifespan;
    private double radius = 50.0;

    private static final double MAX_SPEED = 0.5;
    private final Set<SimpleMissile> affectedMissiles = new HashSet<>();

    public SimpleFlare(World world, double x, double y, double z, EntityPlayer shooter) {
        super(world, x, y, z, shooter);

        this.lifespan = 30 * 20;

        if (shooter != null) {
            Player player = shooter.getBukkitEntity();
            Location loc = player.getLocation();
            Vector forward = loc.getDirection();
            Location upLoc = loc.clone();
            upLoc.setPitch(loc.getPitch() - 90.0F);
            Vector up = upLoc.getDirection().normalize(); // all this to get left/right stuff lol

            forward = forward.normalize();
            Vector relativeHorizontal = up.getCrossProduct(forward);
            relativeHorizontal.normalize();
            relativeHorizontal = relativeHorizontal.multiply((rand.nextFloat() - 0.5) * 2 * directionVariance);
            this.direction = forward.add(relativeHorizontal).normalize();
        } else {
            this.direction = new Vector(1, 0, 1).normalize();
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (lifespan-- < 0) {
            this.die();
            return;
        }

        // Get current velocity
        Vec3D motion = getMot();

        // Forward acceleration
        motion = motion.add(
                direction.getX() * MAX_SPEED,
                direction.getY() * MAX_SPEED,
                direction.getZ() * MAX_SPEED
        );

        // Gravity
        motion = motion.add(0, -0.05, 0);

        // Limit speed
        if (motion.g() > MAX_SPEED * MAX_SPEED) {
            motion = motion.d().a(MAX_SPEED);
        }

        setMot(motion);
        this.C = true;

        org.bukkit.World bukkitWorld = this.getBukkitEntity().getWorld();
        Location flareLocation = this.getBukkitEntity().getLocation();

        for (org.bukkit.entity.Entity entity :
                bukkitWorld.getNearbyEntities(flareLocation, radius, radius, radius)) {

            if (!(entity instanceof org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity craftEntity)) {
                continue;
            }

            net.minecraft.world.entity.Entity nmsEntity = craftEntity.getHandle();

            if (!(nmsEntity instanceof SimpleMissile missile)) {
                continue;
            }

            double distanceSquared = entity.getLocation().distanceSquared(flareLocation);

            if (distanceSquared > radius * radius) {
                continue;
            }

//            if (!affectedMissiles.add(missile)) {
//                continue;
//            }

            if (this.rand.nextDouble() <= successChance) {
                missile.setTarget(this.getBukkitEntity());
            }
        }

        // insert effects here
    }

    @Override
    public boolean onEntityHit(Entity hitEntity, Vec3D entityPosition) {
        return false;
    }

    @Override
    public boolean onBlockHit(Vec3D exactHitPosition,
                              EnumDirection blockFace,
                              BlockPosition hitBlock) {
        return false;
    }

    @Override
    public float getHitDamage() {
        return 0;
    }
}
