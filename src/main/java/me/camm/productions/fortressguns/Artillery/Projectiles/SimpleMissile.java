package me.camm.productions.fortressguns.Artillery.Projectiles;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.Artillery.Entities.Components.Component;
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
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class SimpleMissile extends EntityArrow implements ArtilleryProjectile {

    private final Player shooter;
    private Entity target;
    private final org.bukkit.World bukkitWorld;
    private final Construct source;

    private int fuelUsed;
    private Vec3D direction;

    private final double[] keyPoint;


    private long lastKeyTime;

    private static final long NEXT_KEY_TIME = 1000;

    private static final double INTERCEPT_DIST_SQUARED = 16;

    private static final double ACCELERATION = 0.2;
    private static final double MAX_SPEED_SQUARED = 144;
    private static final double DIST_ENGAGE_SQUARED = 3600;
   /// private static final double DIST_ENGAGE;

    private static final double KEY_DIST = 50;

    private int age;
    private static final int FUEL = 600;  // 30 sec
    private static final int PRIME = 10; //1/2 sec

    private final Vec3D initialVelocity;
    private boolean hadTarget;

    private static final Random rand;
    private static final ItemStack item;


    static {
        org.bukkit.inventory.ItemStack bukkitVer = new org.bukkit.inventory.ItemStack(Material.LEVER);
        ItemMeta meta = bukkitVer.getItemMeta();
        meta.setDisplayName("Rocket");
        bukkitVer.setItemMeta(meta);
        item = CraftItemStack.asNMSCopy(bukkitVer);
        rand = new Random();
    }

    public SimpleMissile(EntityTypes<? extends EntityArrow> entitytypes, double x, double y, double z, World world, @Nullable Player shooter, Artillery source) {
        super(entitytypes, x, y, z, world);
        this.shooter = shooter;
        fuelUsed = 0;
        bukkitWorld = world.getWorld();
        direction = null;
        this.source = source;
        this.setNoGravity(false);
        keyPoint = new double[3];
        lastKeyTime = System.currentTimeMillis();
        age = 0;

        Vector initial = Construct.eulerToVec(source.getAim());

        initialVelocity = new Vec3D(initial.getX(),initial.getY(),initial.getZ());
        hadTarget = false;

    }


    public void setTarget(Entity target) {
        this.target = target;
        hadTarget = true;
    }

    @Override
    public void explode(@Nullable MovingObjectPosition pos) {
        //explode the thing here
        if (pos == null) {
            playExplosionEffects(new Location(bukkitWorld, locX(), locY(), locZ()));
            return;
        }

        if (pos instanceof MovingObjectPositionBlock) {
            BlockPosition blockPos = ((MovingObjectPositionBlock) pos).getBlockPosition();
            Block hitBlock = bukkitWorld.getBlockAt(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            if (hitBlock.getType().isAir())
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

    public void playExplosionEffects(Location hitLoc) {
        EntityPlayer nmsEntity = shooter == null ? null : ((CraftPlayer)shooter).getHandle();

        World nmsWorld = ((CraftWorld)bukkitWorld).getHandle();
        nmsWorld.createExplosion(nmsEntity,hitLoc.getX(), hitLoc.getY(), hitLoc.getZ(),getStrength(),false, Explosion.Effect.c);
        this.die();
    }

    private void playExplosionEffects(MovingObjectPosition pos) {
        Vec3D hitLoc = pos.getPos();
        playExplosionEffects(new Location(bukkitWorld,hitLoc.getX(),hitLoc.getY(),hitLoc.getZ()));
    }


    @Override
    public void a(MovingObjectPosition pos) {
    explode(pos);
    }


    public void tick() {

        super.tick();

        if (age < PRIME) {
            age ++;

            if (age >= PRIME) {
                setGlowingTag(true);
                setNoGravity(true);
            }

            return;
        }

        //unfinished


        if (fuelUsed >= FUEL) {
            this.setNoGravity(false);
            return;
        }

        fuelUsed ++;

        Location loc = new Location(bukkitWorld, locX(), locY(), locZ());
        Vec3D lookDir = getMot();
        lookDir = lookDir.e();  ///e() --> multiply(-1)

        bukkitWorld.spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE,loc,0,lookDir.getX(), lookDir.getY(),lookDir.getZ(),0.2);
        bukkitWorld.spawnParticle(Particle.FLAME,loc,0,lookDir.getX(), lookDir.getY(),lookDir.getZ(),0.2);

        bukkitWorld.playSound(loc, Sound.ITEM_ARMOR_EQUIP_LEATHER,SoundCategory.BLOCKS,1,0.1f);


        if (target == null) {

            if (direction == null) {

                if (hadTarget)
                    direction = getMot().d();
                else
                    direction = initialVelocity;
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
            direction = direction.a(ACCELERATION);
            setMot(motion.add(direction.getX(),direction.getY(),direction.getZ()));
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
        double distSquared = targetLoc.distanceSquared(missileLoc);

        Vec3D mot = getMot();
        Vector missileVel = new Vector(mot.getX(),mot.getY(),mot.getZ());
        if (distSquared <= INTERCEPT_DIST_SQUARED) {
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
        boolean onTarget = false;
        if (missileLoc.distanceSquared(targetLoc) < DIST_ENGAGE_SQUARED) {

            nextLoc = targetLoc;
            onTarget = true;


        }
        else{
            nextLoc = key;

        }

        Vector toTarget = nextLoc.subtract(missileLoc).toVector().normalize();

        toTarget.multiply(ACCELERATION * 2);  //arbitrary

        Vec3D motion = getMot();
        if (missileVel.dot(toTarget) > 0.5 && onTarget) {  //also arbitrary
            motion = motion.a(0.5d);
            motion = motion.add(toTarget.getX() * 2, toTarget.getY() * 2, toTarget.getZ() * 2);
        }
        else
            motion = motion.add(toTarget.getX(), toTarget.getY(), toTarget.getZ());

        setMot(motion);
        this.C = true;



    }



    @Override
    protected ItemStack getItemStack() {
        return item;
    }
}
