package me.camm.productions.fortressguns.Artillery.Projectiles;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Artillery.Entities.Components.Component;
import me.camm.productions.fortressguns.Util.DamageSource.GunSource;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.MovingObjectPositionEntity;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class SimpleMissile extends EntityArrow implements ArtilleryProjectile {

    private final Player shooter;
    private Entity target;
    private org.bukkit.World bukkitWorld;
    private Construct source;

    private int fuelUsed;
    private Vec3D direction;

    private double[] keyPoint;


    private long lastKeyTime;

    private static final long NEXT_KEY_TIME = 5000;

    private static final double INTERCEPT_DIST_SQUARED = 49;

    private static final double ACCELERATION = 0.1;
    private static final double MAX_SPEED_SQUARED = 45;
    private static final double DIST_ENGAGE_SQUARED = 900;
    private static final double DIST_ENGAGE;

    private static final double KEY_DIST = 1;
    private static final double MAX_SPEED;



    private static final int FUEL = 400;  // 20 sec


    private static Random rand;
    private static final ItemStack item;
    static {
        org.bukkit.inventory.ItemStack bukkitVer = new org.bukkit.inventory.ItemStack(Material.LEVER);
        ItemMeta meta = bukkitVer.getItemMeta();
        meta.setDisplayName("Rocket");
        bukkitVer.setItemMeta(meta);
        item = CraftItemStack.asNMSCopy(bukkitVer);
        MAX_SPEED = Math.sqrt(MAX_SPEED_SQUARED);
        rand = new Random();
        DIST_ENGAGE = Math.sqrt(DIST_ENGAGE_SQUARED);
    }

    public SimpleMissile(EntityTypes<? extends EntityArrow> entitytypes, double x, double y, double z, World world, @Nullable Player shooter, Construct source) {
        super(entitytypes, x, y, z, world);
        this.shooter = shooter;
        fuelUsed = 0;
        bukkitWorld = world.getWorld();
        direction = null;
        this.source = source;
        this.setNoGravity(true);
        setGlowingTag(true);
        keyPoint = new double[3];
        lastKeyTime = System.currentTimeMillis();
    }


    public void setTarget(Entity target) {
        this.target = target;
    }

    @Override
    public void explode(@Nullable MovingObjectPosition pos) {
        //explode the thing here
        if (pos == null)
            return;


        if (pos instanceof MovingObjectPositionBlock) {
            BlockPosition blockPos = ((MovingObjectPositionBlock) pos).getBlockPosition();
            Block b = bukkitWorld.getBlockAt(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            if (b.getType().isAir())
                return;
        }


        if (!(pos instanceof MovingObjectPositionEntity)) {
            playExplosionEffects(pos);
            return;
        }

        MovingObjectPositionEntity hit = (MovingObjectPositionEntity) pos;
        net.minecraft.world.entity.Entity hitEntity = hit.getEntity();


        if (!(hitEntity instanceof Component)) {
            playExplosionEffects(pos);
            return;
        }

        Component body = (Component)hitEntity;
        if (!source.equals(body.getBody())) {
            playExplosionEffects(pos);
        }


    }


    public float getStrength() {
        return 4f;
    }

    private void playExplosionEffects(MovingObjectPosition pos) {

        EntityPlayer nmsEntity = shooter == null ? null : ((CraftPlayer)shooter).getHandle();

        Vec3D hitLoc = getHitLoc(pos, this);
        World nmsWorld = ((CraftWorld)bukkitWorld).getHandle();
        Explosion e = nmsWorld.createExplosion(nmsEntity,hitLoc.getX(), hitLoc.getY(), hitLoc.getZ(),getStrength(),false, Explosion.Effect.c);
        e.a();
        e.a(true);
        this.die();
    }


    @Override
    public void a(MovingObjectPosition pos) {
    explode(pos);
    }


    public void tick() {

        //unfinished
        super.tick();

        if (fuelUsed >= FUEL) {
            this.setNoGravity(false);
            return;
        }

        fuelUsed ++;

        Location loc = new Location(bukkitWorld, locX(), locY(), locZ());
        bukkitWorld.spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE,loc,1,0,0,0,0.1);
        bukkitWorld.spawnParticle(Particle.FLAME,loc,1,0,0,0,0.1);
        bukkitWorld.playSound(loc, Sound.ITEM_ARMOR_EQUIP_LEATHER,SoundCategory.BLOCKS,1,0.1f);


        if (target == null) {

            if (direction == null) {
                direction = getMot().d();
            }
            flyNormally();

        }
        else {
            flyToTarget(loc);
            direction = null;
        }


    }


    public void flyNormally() {
        Vec3D motion = getMot();
        double magnitudeSquared = motion.g();
        if (magnitudeSquared < MAX_SPEED_SQUARED) {
            Vec3D motionMultiplied = direction.a(Math.sqrt(magnitudeSquared) + ACCELERATION);
            setMot(motionMultiplied);
        }
        else {
            setMot(direction.a(MAX_SPEED));
        }
        this.C = true;
    }


    public void flyToTarget(Location missileLoc) {

        if (target.isDead() || !target.isValid()) {
            target = null;
            return;
        }


        Location targetLoc = target.getLocation().clone();
        Vector targetMot = target.getVelocity();

        if (targetLoc.distanceSquared(missileLoc) <= INTERCEPT_DIST_SQUARED) {
            explode(null);
            return;
        }


        long currentTime = System.currentTimeMillis();
        Location key = null;
        if (currentTime > lastKeyTime + NEXT_KEY_TIME) {
            lastKeyTime = currentTime;

            double x,y,z;
            x = rand.nextDouble(KEY_DIST * 2);
            x -= (x / 2);

            y = rand.nextDouble(KEY_DIST * 2);
            y -= (y / 2);

            z = rand.nextDouble(KEY_DIST * 2);
            z -= (z / 2);

            keyPoint[0] = x;
            keyPoint[1] = y;
            keyPoint[2] = z;
        }

        key = new Location(bukkitWorld, keyPoint[0] + targetLoc.getX() + targetMot.getX(),
                keyPoint[1] + targetLoc.getY() + targetMot.getY(),
                keyPoint[2] + targetLoc.getZ() + targetMot.getZ());


        Location nextLoc;
        if (missileLoc.distanceSquared(targetLoc) < DIST_ENGAGE_SQUARED) {
            nextLoc = targetLoc;
        }
        else{
            nextLoc = key;
        }

        Vector toTarget = nextLoc.subtract(missileLoc).toVector().normalize();
        toTarget.multiply(ACCELERATION * 2);  //arbitrary

        Vec3D motion = getMot();
        motion = motion.add(toTarget.getX(), toTarget.getY(), toTarget.getZ());

        setMot(motion);
        this.C = true;



    }



    @Override
    protected ItemStack getItemStack() {
        return item;
    }
}
