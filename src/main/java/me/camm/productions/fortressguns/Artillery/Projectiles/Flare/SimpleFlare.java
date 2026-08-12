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
    private static final double VERTICAL_VARIANCE = 10.0;

    private static final double successChance = 0.5; // % chance per missile to affect it
    private final Vec3D direction;
    private int lifespan;

    private static final double ACCELERATION = 0.2;
    private static final double MAX_SPEED = 1;
    private final Set<SimpleMissile> affectedMissiles = new HashSet<>();

    public SimpleFlare(World world, double x, double y, double z, @Nullable EntityPlayer shooter, Vec3D guideDirection) {
        super(world, x, y, z, shooter);

        this.lifespan = 10 * 20;

        if (shooter != null) {
            // Random vertical offset: -10° to +10°
            double pitchOffset = (Math.random() * 2.0 - 1.0) * VERTICAL_VARIANCE;

            float yaw = shooter.getYRot();
            float pitch = (float)(shooter.getXRot() + pitchOffset);

            // Convert modified Euler angles back into a direction vector
            Vector direction = MathFG.eulerToVec(new EulerAngle(pitch, yaw, 0));

            this.direction = new Vec3D(direction.getX(), direction.getY(), direction.getZ()).d();
        } else {
            this.direction = guideDirection.d();
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

        // Accelerate in the direction the flare was fired
        double speedSquared = motion.g();

        if (speedSquared < MAX_SPEED * MAX_SPEED) {

            // Add acceleration in the desired direction
            motion = motion.add(
                    direction.getX() * ACCELERATION,
                    direction.getY() * ACCELERATION,
                    direction.getZ() * ACCELERATION
            );

            // Don't exceed maximum speed
            if (motion.g() > MAX_SPEED * MAX_SPEED) {
                motion = motion.d().a(MAX_SPEED);
            }

            setMot(motion);
        }

        // Tell Minecraft that the entity's movement changed
        this.C = true;

        org.bukkit.World bukkitWorld = this.getBukkitEntity().getWorld();
        Location flareLocation = this.getBukkitEntity().getLocation();

        double radius = 100.0;
        for (org.bukkit.entity.Entity entity :
                bukkitWorld.getNearbyEntities(flareLocation, radius, radius, radius)) {

            if (!(entity instanceof SimpleMissile missile)) {
                continue;
            }

            double distanceSquared = entity.getLocation().distanceSquared(flareLocation);

            if (distanceSquared > radius * radius) {
                continue;
            }

            // Only affect this missile once
            if (!affectedMissiles.add(missile)) {
                continue;
            }

            if (this.rand.nextDouble() < successChance) {
                missile.setTarget(this.getBukkitEntity());
            }
        }
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
