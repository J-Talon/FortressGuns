package me.camm.productions.fortressguns.Core.Artillery.Projectiles.Flare;

import me.camm.productions.fortressguns.Core.Artillery.Projectiles.Abstract.ProjectileSeeking;
import me.camm.productions.fortressguns.Core.Artillery.Projectiles.Abstract.ProjectileType;
import me.camm.productions.fortressguns.Core.Artillery.Projectiles.Missile.AbstractRocket;
import me.camm.productions.fortressguns.Core.Artillery.Projectiles.Missile.HeatseekingMissile;
import me.camm.productions.fortressguns.Util.Config.ConfigManager;
import me.camm.productions.fortressguns.Util.Config.ConfigurationHolder;
import me.camm.productions.fortressguns.Util.Config.Wrapper.Projectile.SectionFlare;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SimpleFlare extends AbstractFlare implements ProjectileType.ISimpleFlare, ConfigurationHolder<SectionFlare> {

    private static final ItemStack SPRITE = new ItemStack(Items.rA);

    private static double directionVariance = 0.5;
    private static double successChance = 0.37; // % chance per missile to affect it ~ a 90% chance for a spread of missiles
    private static int lifespanTicks = 300;
    private static double radius = 50.0;
    private static boolean configApplied = false;


    private Vector DIRECTION;

    private int lifespan;

    private static final double MAX_SPEED = 1;
    private final Set<ProjectileSeeking> affectedMissiles = new HashSet<>();

    public SimpleFlare(org.bukkit.World world, double x, double y, double z, Player shooter) {
        super(world, x, y, z, shooter);


        if (!configApplied) {
            ConfigManager manager = ConfigManager.getInstance();
            setStaticDefaults(manager.getFlare());
        }

        this.lifespan = lifespanTicks;

        if (shooter != null) {
            Location loc = shooter.getLocation();
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


    @Override
    public void setStaticDefaults(@Nullable SectionFlare data) {
        configApplied = true;
        if (data == null) return;

        directionVariance = data.getVariance();
        radius = data.getFlareEffectRange();
        successChance = data.getFlareSuccess();
        lifespanTicks = data.getLifespan().intValue();
    }





    public int getLifespan() {
        return lifespan;
    }

    public void setLifespan(int lifespan) {
        this.lifespan = lifespan;
    }

    @Override
    public void tick() {
        if (getMot().f() == 0 && DIRECTION != null) {
            setMot(new Vec3D(
                    DIRECTION.getX() * MAX_SPEED,
                    DIRECTION.getY() * MAX_SPEED,
                    DIRECTION.getZ() * MAX_SPEED
            ));
        }

        super.tick();

        if (lifespan-- < 0) {
            this.die();
            return;
        }

        this.C = true;

        Location flareLocation = this.getBukkitEntity().getLocation();

        for (AbstractRocket missile : HeatseekingMissile.getActiveMissiles()) {

            if (!missile.isAlive()) {
                continue;
            }

            double distanceSquared =
                    missile.getBukkitEntity().getLocation().distanceSquared(flareLocation);

            if (distanceSquared > radius * radius) {
                continue;
            }

            if (!(missile instanceof ProjectileSeeking seeking)) continue;

            if (!affectedMissiles.add(seeking)) {
                continue;
            }

            if (rand.nextDouble() <= successChance) {
                seeking.setTarget(this.getBukkitEntity());

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
    public boolean onEntityHit(Entity hitEntity, Vector entityPosition) {
        // had an issue where player would kill their own flare so yeah
        //don't pp collisions if not needed!
        return false;
    }

    @Override
    public boolean onBlockHit(Vector exactHitPosition, BlockFace blockFace, Block hitBlock) {
        this.die();
        return true;
    }

    @Override
    public float getHitDamage() {
        return 0;
    }

}
