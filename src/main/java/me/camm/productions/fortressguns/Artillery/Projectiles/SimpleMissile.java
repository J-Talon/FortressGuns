package me.camm.productions.fortressguns.Artillery.Projectiles;

import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Artillery;
import me.camm.productions.fortressguns.Artillery.Entities.Abstract.Construct;
import me.camm.productions.fortressguns.FortressGuns;
import me.camm.productions.fortressguns.Handlers.MissileLockNotifier;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class SimpleMissile extends EntityArrow implements ArtilleryProjectile, ProjectileExplosive {

    private final Player shooter;
    private Entity target;
    private final org.bukkit.World bukkitWorld;
    private final Construct source;

    private int fueledFlightAge;
    private Vec3D direction;

    private static final double ACCELERATION = 0.2;
    private static final double MAX_ACCELERATION = 0.3;
    private static final double MAX_SPEED_SQUARED = 6; //slightly faster than max spd elytra
    private static final double MAX_SPEED;
    private static final double ORBIT_DIST = 17;
    private static final int DIST_EXPLODE_SQUARED = 16;

    private int readyTime;
    private static final int FUEL = 600;  //
    private static final int PRIME = 10; //1/2 sec

    private final Vec3D initialVelocity;
    private boolean hadTarget;

    private static final Random rand;
    private static final ItemStack item;
    private final double sineOffset;

    private float initialXRot, initialYRot;
    private Vector targetOrthogonal;

    private final MissileLockNotifier notifier;



    static {
        org.bukkit.inventory.ItemStack bukkitVer = new org.bukkit.inventory.ItemStack(Material.LEVER);
        ItemMeta meta = bukkitVer.getItemMeta();
        meta.setDisplayName("Rocket");
        bukkitVer.setItemMeta(meta);
        item = CraftItemStack.asNMSCopy(bukkitVer);
        rand = new Random();
        MAX_SPEED = Math.sqrt(MAX_SPEED_SQUARED);
    }

    public SimpleMissile(EntityTypes<? extends EntityArrow> entitytypes, double x, double y, double z, World world, @Nullable Player shooter, Artillery source) {
        super(entitytypes, x, y, z, world);
        this.shooter = shooter;
        fueledFlightAge = 0;
        bukkitWorld = world.getWorld();
        direction = null;
        this.source = source;
        readyTime = 0;
        Vector initial = Construct.eulerToVec(source.getAim());
        initialVelocity = new Vec3D(initial.getX(),initial.getY(),initial.getZ());
        hadTarget = false;

        sineOffset = rand.nextDouble() * Math.PI * 2; // 2PI = period of sine function
        initialYRot = initialXRot = Float.NaN;
        notifier = MissileLockNotifier.get(FortressGuns.getInstance());


        //velocity is in blocks/tick

    }


    public void setTarget(Entity target) {
        this.target = target;
        if (target instanceof Player) {
            notifier.addNotification(target.getUniqueId());
        }

        hadTarget = true;
    }

    @Override
    public void preHit(@Nullable MovingObjectPosition pos) {
        //explode the thing here
        if (pos == null) {
            explode(null);
            return;
        }

        if (pos instanceof MovingObjectPositionBlock) {
            BlockPosition blockPos = ((MovingObjectPositionBlock) pos).getBlockPosition();
            Block hitBlock = bukkitWorld.getBlockAt(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            if (hitBlock.getType().isAir())
                return;
        }
        explode(pos.getPos());
    }


    public float getDamageStrength() {
        return 4f;
    }

    public void explode(@Nullable Vec3D hit) {

        if (target != null && target instanceof Player) {
            notifier.exitNotification(target.getUniqueId());
        }
        Location explosionLoc;
        org.bukkit.World world = getWorld().getWorld();
        if (hit == null) {
            explosionLoc = new Location(world, locX(), locY(), locZ());
            world.createExplosion(explosionLoc,getDamageStrength());
        }
        else {
            explosionLoc = new Location(world, hit.getX(), hit.getY(), hit.getZ());
            world.createExplosion(explosionLoc, getDamageStrength(), false, true, shooter);
        }
        world.spawnParticle(Particle.EXPLOSION_HUGE,explosionLoc,1,0,0,0,0,null, true);

        this.die();
    }



    @Override
    public void a(MovingObjectPosition pos) {
    preHit(pos);
    }


    public void playEffects(Location loc) {

        Vec3D lookDir = getMot();
        lookDir = lookDir.e();  ///e() --> multiply(-1)

        //0.00015x^{2}

        //maybe this should be an option
        // I know some peeps want the small explode particles instead
//        final ParticleParam PARAM = Particles.v;  //end rod particle
//        bukkitWorld.getPlayers().forEach(player -> {
//
//            double distSquared = loc.distanceSquared(player.getLocation());
//            double glow = Math.max((-0.0008 * distSquared) + 5, 1);
//
//            PacketPlayOutWorldParticles packet;
//            EntityPlayer nms = ((CraftPlayer)player).getHandle();
//
//            for (double angle = 0; angle < 2*Math.PI; angle += Math.PI / 2) {
//                Location next = loc.clone();
//                double z = Math.cos(angle);
//                double x = -Math.sin(angle);
//
//                for (int num = 0; num < glow; num ++) {
//                    packet = new PacketPlayOutWorldParticles(PARAM, true, next.getX(),next.getY(), next.getZ(), (float)9.9E5, 0,0,1,0);
//                    nms.b.sendPacket(packet);
//                    next.add(x * 0.1 * num,0,z * 0.1 * num); /// 0.1 is arbitrary dist
//                }
//            }
//
//            for (int num = 0; num < glow; num ++) {
//                Location next = loc.clone();
//
//                next.add(0,0.1 * num,0);
//                packet = new PacketPlayOutWorldParticles(PARAM, true, next.getX(),next.getY(), next.getZ(), (float)9.9E5, 0,0,1,0);
//                nms.b.sendPacket(packet);
//
//                next.add(0,-0.2 * num,0);
//                packet = new PacketPlayOutWorldParticles(PARAM, true, next.getX(),next.getY(), next.getZ(), (float)9.9E5, 0,0,1,0);
//                nms.b.sendPacket(packet);
//            }
//
//        });


        //some people like this more???
        bukkitWorld.spawnParticle(Particle.EXPLOSION_LARGE,loc,0,0,0,0,1,null, true);

        bukkitWorld.spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE,loc,0,lookDir.getX(), lookDir.getY(),lookDir.getZ(),0.2,null,true);
        bukkitWorld.spawnParticle(Particle.FLAME,loc,0,lookDir.getX(), lookDir.getY(),lookDir.getZ(),0.2,null, true);
        bukkitWorld.playSound(loc, Sound.ITEM_ARMOR_EQUIP_LEATHER,SoundCategory.BLOCKS,1,0.1f);


    }



    public void tick() {
        super.tick(); /// <<<< issue here???

        if (readyTime < PRIME) {
            readyTime++;

            if (readyTime >= PRIME) {
                setNoGravity(true);
            }
            return;
        }



        if (fueledFlightAge >= FUEL) {
            this.setNoGravity(false);

            if (target != null) {
                notifier.exitNotification(target.getUniqueId());
            }

            return;
        }

        fueledFlightAge++;
        Location loc = new Location(bukkitWorld, locX(), locY(), locZ());
        playEffects(loc);

        if (target == null) {
            if (direction == null) {

                if (hadTarget)
                    direction = getMot().d();
                else
                    direction = initialVelocity;
            }

            setMot(new Vec3D(0,0,0));
            flyNormally();
        }
        else {
            flyToTarget(loc);
            direction = null;
        }


    }


    public void flyNormally() {
        Vec3D motion = getMot();
        double magnitudeSquared = motion.g(); // length squared
        if (magnitudeSquared >= MAX_SPEED_SQUARED) {  //if >= than max speed
            //a = multiply()
            Vec3D motionMultiplied = direction.a(MAX_SPEED);
            setMot(motionMultiplied);
        }
        else {  //accelerate
            direction = direction.a(MAX_ACCELERATION + 1);
            motion = motion.add(direction.getX(),direction.getY(),direction.getZ());
            setMot(motion);
        }

        //motion changed
        this.C = true;


    }


    public void flyToTarget(Location missileLoc) {

        if (target instanceof Player && (!(((Player) target).isGliding()))) {
            notifier.removeNotification(target.getUniqueId());
        }

        if (target.isDead() || !target.isValid() || (target.getWorld() != bukkitWorld)) {

            notifier.removeNotification(target.getUniqueId());
            target = null;
            return;
        }

        Location targetLoc = target.getLocation();

        if (targetLoc.distanceSquared(missileLoc) <= DIST_EXPLODE_SQUARED) {
            preHit(null);
            return;
        }

        float yaw = targetLoc.getYaw();
        float pitch = targetLoc.getPitch();

        EulerAngle targetFacing = new EulerAngle(pitch, yaw, 0);
        Vector targetFacingVec = Construct.eulerToVec(targetFacing);

        //turbulence calculations
        //arbitrary values from artistic standpoint
        double targetSpeed = target.getVelocity().length();  //sqrt here
        double turbulence = Math.pow(Math.E, -17 * (targetSpeed - 1.6) * (targetSpeed - 1.6));
        double acceleration = 0.1 * (targetSpeed - 0.2) * (targetSpeed - 0.85);




        ////orthogonal calculations
        ///0.03 is period
        double offset = Math.sin(0.03 * fueledFlightAge + sineOffset) * ORBIT_DIST;

        Vector targetLocVec = targetLoc.toVector();

        if (Float.isNaN(initialXRot) && Float.isNaN(initialYRot)) {
            initialYRot = yaw;
            initialXRot = pitch;
        }



        Vector trackingLocation;

        if (offset == 0) {
            targetOrthogonal = null;
           trackingLocation = targetLocVec;
        }
        else {
            if (targetOrthogonal == null)
                targetOrthogonal = getOrthogonal(targetFacingVec);
            double diffYaw = Math.toRadians(yaw - initialYRot);
            double diffPitch = Math.toRadians(pitch - initialXRot);

            Vector nextOrtho = targetOrthogonal.clone().rotateAroundY(diffYaw).rotateAroundX(diffPitch); //
            trackingLocation = targetLocVec.clone().add(nextOrtho.multiply(offset * turbulence));
        }

        Vector travelTowards = trackingLocation.subtract(missileLoc.toVector());
        travelTowards.normalize();   //sqrt here

        travelTowards.multiply(Math.min(ACCELERATION + acceleration, MAX_ACCELERATION));  //arbitrary

        Vec3D motion = getMot();

        double originalMotionPercent = -Math.abs(0.15 * (targetSpeed-1.5)) +0.8;
        double accAmount = (1 - originalMotionPercent) + 1;
        travelTowards.multiply(accAmount);

        motion.a(originalMotionPercent);  //0.8
        motion = motion.add(travelTowards.getX(), travelTowards.getY(), travelTowards.getZ());

        if (motion.g() > MAX_SPEED_SQUARED) {
            motion = motion.d().a(MAX_SPEED);  //sqrt here
        }


        setMot(motion);
        this.C = true;
    }








    private Vector getOrthogonal(Vector other) {
        double denom = Double.NaN;
        double linComb = Double.NaN;
        double mult1, mult2;
        mult1 = rand.nextDouble() - 0.5;
        mult2 = rand.nextDouble() - 0.5;

        if (other.getX() != 0) {
           denom = other.getX();
           linComb = other.getY() * mult1 - other.getZ() * mult2;
        }
        else if (other.getY() != 0) {
            denom = other.getY();
            linComb = -other.getX() * mult1 - other.getZ() * mult2;
        }
        else if (other.getZ() != 0) {
            denom = other.getZ();
            linComb = -other.getX() * mult1 - other.getY() * mult1;
        }

        if (Double.isNaN(denom) && Double.isNaN(linComb))
            return new Vector(0,0,0);
        else return new Vector(mult1, mult2, linComb / denom).normalize();

    }


    @Override
    protected ItemStack getItemStack() {
        return item;
    }


    public static int getFuelTicks(){
        return FUEL;
    }
}
