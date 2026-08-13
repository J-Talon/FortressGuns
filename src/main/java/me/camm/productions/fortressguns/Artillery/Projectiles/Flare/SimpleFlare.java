package me.camm.productions.fortressguns.Artillery.Projectiles.Flare;

import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileFG;
import me.camm.productions.fortressguns.Artillery.Projectiles.Missile.SimpleMissile;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;

public class SimpleFlare extends AbstractFlare implements ProjectileFG {
    private static final double directionVariance = 0.5;
    private static final ItemStack SPRITE = new ItemStack(Items.rA);

    private static final double successChance = 0.9; // % chance per missile to affect it
    private final Vector DIRECTION;
    private int lifespan;
    private final double RADIUS = 50.0;

    private static final double MAX_SPEED = 1;
    private final Set<SimpleMissile> affectedMissiles = new HashSet<>();

    public SimpleFlare(World world, double x, double y, double z, EntityPlayer shooter) {
        super(world, x, y, z, shooter);

        this.lifespan = 15 * 20;

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
            this.DIRECTION = forward.add(relativeHorizontal).normalize();
        } else {
            this.DIRECTION = new Vector(1, 0, 1).normalize();
        }
        setItem(SPRITE);
        setMot(new Vec3D(
                DIRECTION.getX() * MAX_SPEED,
                DIRECTION.getY() * MAX_SPEED,
                DIRECTION.getZ() * MAX_SPEED
        ));
    }

    @Override
    public void tick() {
        super.tick();

        if (lifespan-- < 0) {
            end();
            return;
        }

        // Get current velocity
        Vec3D motion = getMot();

        // Gravity
//        motion = motion.add(0, 0.0002, 0);

        setMot(motion);
        this.C = true;

        org.bukkit.World bukkitWorld = this.getBukkitEntity().getWorld();
        Location flareLocation = this.getBukkitEntity().getLocation();

        for (org.bukkit.entity.Entity entity :
                bukkitWorld.getNearbyEntities(flareLocation, RADIUS, RADIUS, RADIUS)) {

            if (!(entity instanceof org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity craftEntity)) {
                continue;
            }

            net.minecraft.world.entity.Entity nmsEntity = craftEntity.getHandle();

            if (!(nmsEntity instanceof SimpleMissile missile)) {
                continue;
            }

            double distanceSquared = entity.getLocation().distanceSquared(flareLocation);

            if (distanceSquared > RADIUS * RADIUS) {
                continue;
            }

            if (!affectedMissiles.add(missile)) {
                continue;
            }

            if (this.rand.nextDouble() <= successChance) {
                missile.setTarget(this.getBukkitEntity());
            }
        }

        // Flare particle effects
        org.bukkit.World world = this.getBukkitEntity().getWorld();
        Location loc = this.getBukkitEntity().getLocation();

        world.spawnParticle(
                Particle.FLAME,
                loc,
                3,
                0.08, 0.08, 0.08,
                0.01
        );
        world.spawnParticle(
                Particle.FLASH,
                loc,
                3,
                0.08, 0.08, 0.08,
                0.01
        );

        world.spawnParticle(
                Particle.FIREWORKS_SPARK,
                loc,
                2,
                0.05, 0.05, 0.05,
                0.005
        );
    }

    @Override
    public boolean onEntityHit(Entity hitEntity, Vec3D entityPosition) {
        end();
        return false;
    }

    @Override
    public boolean onBlockHit(Vec3D exactHitPosition,
                              EnumDirection blockFace,
                              BlockPosition hitBlock) {
        end();
        return false;
    }

    private void end() {
        this.getBukkitEntity().remove();
        this.affectedMissiles.clear();
        this.die();
    }

    @Override
    public float getHitDamage() {
        return 0;
    }
}
