package me.camm.productions.fortressguns.Artillery.Projectiles.Flare;

import me.camm.productions.fortressguns.Artillery.Projectiles.Abstract.ProjectileFG;
import me.camm.productions.fortressguns.Artillery.Projectiles.Missile.AbstractRocket;
import me.camm.productions.fortressguns.Artillery.Projectiles.Missile.HeatseekingMissile;
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

    private static final double successChance = 0.37; // % chance per missile to affect it ~ a 90% chance for a spread of missiles
    private final Vector DIRECTION;
    private int lifespan;
    private final double RADIUS = 50.0;

    private static final double MAX_SPEED = 1;
    private final Set<AbstractRocket> affectedMissiles = new HashSet<>();

    public SimpleFlare(World world, double x, double y, double z, EntityPlayer shooter) {
        super(world, x, y, z, shooter);

        this.lifespan = 10 * 20;
        setNoGravity(true);

        if (shooter != null) {
            Player player = shooter.getBukkitEntity();
            Location loc = player.getLocation();
            Vector forward = loc.getDirection();
            Location upLoc = loc.clone();
            upLoc.setPitch(loc.getPitch() - 90.0F);
            Vector up = upLoc.getDirection().normalize(); // all this to get left/right stuff lol

            forward = forward.normalize();
            up = up.normalize();
            Vector relativeHorizontal = up.getCrossProduct(forward);
            relativeHorizontal.normalize();
            relativeHorizontal = relativeHorizontal.multiply((rand.nextFloat() - 0.5) * 2 * directionVariance);
            up = up.multiply((rand.nextFloat() - 0.5) * 2 * directionVariance);
            this.DIRECTION = forward.add(relativeHorizontal).add(up);
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

    public SimpleFlare(World world, double x, double y, double z, double dx, double dy, double dz) {
        super(world, x, y, z, null);

        this.lifespan = 15 * 20;
        setNoGravity(true);

        DIRECTION = new Vector(dx, dy, dz);
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
            this.die();
            return;
        }

        // Get current velocity
        Vec3D motion = getMot();

        // Gravity
        motion = motion.add(0, -0.01, 0);

        setMot(motion);
        this.C = true;

        Location flareLocation = this.getBukkitEntity().getLocation();

        for (AbstractRocket missile : HeatseekingMissile.getActiveMissiles()) {

            if (!missile.isAlive()) {
                continue;
            }

            double distanceSquared =
                    missile.getBukkitEntity().getLocation().distanceSquared(flareLocation);

            if (distanceSquared > RADIUS * RADIUS) {
                continue;
            }

            if (!affectedMissiles.add(missile)) {
                continue;
            }

            if (rand.nextDouble() <= successChance) {
                missile.setTarget(this.getBukkitEntity());

            }
        }

        // flare particle effects
        org.bukkit.World world = this.getBukkitEntity().getWorld();
        Location loc = this.getBukkitEntity().getLocation();

        world.spawnParticle(
                Particle.FLAME,
                loc,
                3,
                0.08, 0.08, 0.08,
                0.01
        );
        world.spawnParticle(Particle.FLASH, loc, 1);

        world.spawnParticle(
                Particle.REDSTONE,
                loc,
                10,
                0.08, 0.08, 0.08,
                0.01,
                new Particle.DustOptions(Color.RED, 1.5f)
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
        return false;
        // had an issue where player would kill their own flare so yeah
        //don't pp collisions if not needed!
    }

    @Override
    public boolean onBlockHit(Vec3D exactHitPosition,
                              EnumDirection blockFace,
                              BlockPosition hitBlock) {
        this.die();
        return true;
    }

    @Override
    public float getHitDamage() {
        return 0;
    }
}
